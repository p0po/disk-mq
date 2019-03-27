package net.yongpo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.io.Files;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by p0po on 2019/3/15 0015.
 */
public class DiskMqImpl implements DiskMq {
    private static final Logger writter = LoggerFactory.getLogger("disk.writter");
    private static final Logger log = LoggerFactory.getLogger(DiskMqImpl.class);
    private static final ListQueue LIST_QUEUE = new ListQueue();

    private static String DATA_PATH;
    private static String DATA_TO_CONSUME_PATH;
    private static String DATA_TO_DEL_PATH;

    public DiskMqImpl(String dataDir, String dbName) {
        if (StringUtils.isBlank(dataDir)) {
            throw new RuntimeException("dataDir 不能为空");
        }

        if (!dataDir.endsWith("/")) {
            throw new RuntimeException("dataDir 需要以：" + File.separator + " 结尾");
        }

        if (StringUtils.isBlank(dbName)) {
            throw new RuntimeException("dbName 不能为空");
        }

        if (dbName.contains(File.separator)) {
            throw new RuntimeException("dbName不能包含字符：" + File.separator);
        }

        DATA_PATH = dataDir + dbName.trim() + File.separator + "new";
        DATA_TO_CONSUME_PATH = dataDir + dbName.trim() + File.separator + "to-consume";
        DATA_TO_DEL_PATH = dataDir + dbName.trim() + File.separator + "to-del";
    }

    private synchronized static List<File> listFileName(String dir) {
        List<File> list = new ArrayList<File>();
        File file = new File(dir);
        if (file.isDirectory()) {
            return Arrays.asList(file.listFiles());
        }
        return list;
    }

    private synchronized static void move(File file, String desDir, String fileName) {
        File toDir = new File(desDir);
        if (!toDir.exists()) {
            toDir.mkdirs();
        }

        fileName = (fileName == null) ? file.getName() : fileName;

        if (file.exists()) {
            try {
                Files.move(file, new File(toDir.getAbsolutePath() + File.separator + fileName));
                //log.info("move {} to {}",file.getAbsolutePath(),toDir.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean add(Object o) {
        try {
            writter.info(JSON.toJSONString(o, SerializerFeature.WriteClassName));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean offer(Object o) {
        try {
            writter.info(JSON.toJSONString(o, SerializerFeature.WriteClassName));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void removeFile(String fileName) {
        if (fileName != null) {
            LIST_QUEUE.setFileName(null);
            move(new File(DATA_TO_CONSUME_PATH + File.separator + fileName), DATA_TO_DEL_PATH + File.separator + DateTime.now().toString("yyyy-MM-dd"), null);
        }
    }

    /**
     * 这里串行执行，因为不串行在切文件的首位部分会出问题，目前没有解决
     * 经过测试发现，加与不加synchronized对性能好像没有太多影响
     *
     * @return
     */
    private synchronized String peekOne() {
        List<String> memoryList = LIST_QUEUE.getList();
        if (memoryList != null) {
            int pos = LIST_QUEUE.getAndIncrementPosition();
            if (pos == -999) {
                removeFile(LIST_QUEUE.getFileName());
                DATA_READY.compareAndSet(true, false);
            } else if (pos >= 0) {
                log.info("pos/size={}/{} [{}]", pos, LIST_QUEUE.getSize(), LIST_QUEUE.getFileName());
                String result = memoryList.get(pos);
                return result;
            }
        }

        return null;
    }

    private synchronized void fetchData() {
        if (DATA_READY.get()) {
            return;
        }
        List<File> fileList = listFileName(DATA_TO_CONSUME_PATH);

        if (CollectionUtils.isEmpty(fileList)) {
            MyTriggeringPolicy.CONSUMER_FREE = true;
        }

        while (CollectionUtils.isEmpty(fileList)) {
            fileList = listFileName(DATA_TO_CONSUME_PATH);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (CollectionUtils.isNotEmpty(fileList)) {
            Collections.sort(fileList, new Comparator<File>() {
                public int compare(File o1, File o2) {
                    return Long.valueOf(o1.getName().substring(5)).compareTo(Long.valueOf(o2.getName().substring(5)));
                }
            });
            File file = fileList.get(0);

            LIST_QUEUE.setFileName(file.getName());
            try {
                List<String> list = Files.readLines(file, Charset.forName("UTF-8"));
                if (CollectionUtils.isNotEmpty(list) && DATA_READY.compareAndSet(false, true)) {
                    LIST_QUEUE.setList(list);
                    LIST_QUEUE.setPosition(-1);
                    LIST_QUEUE.setSize(list.size());
                } else if (CollectionUtils.isEmpty(list)) {
                    removeFile(file.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*dirList = listFileName(DATA_PATH);

        for(File file:dirList){
            //System.out.println("args = [" + file.getName() + "]");
            if(file.getName().startsWith("read")){
                move(file,DATA_TO_CONSUME_PATH, null);
            }
        }*/
    }

    final static AtomicBoolean DATA_READY = new AtomicBoolean(false);

    public String peek() {
        String o = null;
        if (DATA_READY.get()) {
            o = peekOne();
        } else {
            fetchData();
        }
        return o;
    }

    public Object remove() {
        return null;
    }

    public Object poll() {
        Object o1 = peek();
        Object o2 = remove();
        return o1;
    }

    public static void main(String[] args) {
        List<String> fileList = Arrays.asList("read16", "read15", "read15", "read13", "read11", "read17");
        Collections.sort(fileList, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return Long.valueOf(o1.substring(5)).compareTo(Long.valueOf(o2.substring(5)));
            }
        });
        System.out.println("args = [" + fileList.get(0) + "]");
    }

}
