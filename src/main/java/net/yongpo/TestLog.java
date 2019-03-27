package net.yongpo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by p0po on 2019/3/14 0014.
 */
public class TestLog {
    static final Logger logger = LoggerFactory.getLogger("disk.writter");
    static ExecutorService e = Executors.newFixedThreadPool(1);

    public static void main(String[] args) {

/*
        e.submit(new Runnable() {
            public void run() {
                while (true){
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    MyTriggeringPolicy.CONSUMER_FREE = true;
                }
            }
        });
*/

        while (true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info(System.getProperties().toString());
        }
    }
}
