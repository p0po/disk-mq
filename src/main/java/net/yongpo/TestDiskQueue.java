package net.yongpo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by p0po on 2019/3/18 0018.
 */
public class TestDiskQueue {
    static ExecutorService e = Executors.newFixedThreadPool(1);

    public static void main(String[] args) {
        final DiskMq diskMq = new DiskMqImpl("/export/data/", "test");

        e.submit(new Runnable() {
            public void run() {
                while (true) {
                    long start = System.nanoTime();
                    Object o = diskMq.peek();
                    long stop = System.nanoTime();
                    System.out.println("cost :" + (stop - start) + " ws");

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        });

        while (true) {
            diskMq.add(System.getenv());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }


    }
}
