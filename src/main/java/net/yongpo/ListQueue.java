package net.yongpo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by p0po on 2019/3/19 0019.
 */

public class ListQueue {
    Logger logger = LoggerFactory.getLogger(ListQueue.class);
    String fileName;
    List<String> list = new ArrayList<String>();
    AtomicInteger position = new AtomicInteger(-1);
    AtomicInteger size = new AtomicInteger(0);

    public synchronized int getAndIncrementPosition() {
        int p = position.incrementAndGet();
        if (p + 1 > this.size.get()) {
            return -999;
        }
        return p;
    }

    public int getSize() {
        return size.get();
    }

    public void setSize(int size) {
        this.size = new AtomicInteger(size);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public int getPosition() {
        return this.position.get();
    }

    public void setPosition(int position) {
        this.position = new AtomicInteger(position);
    }
}
