package net.yongpo;

/**
 * Created by p0po on 2019/3/15 0015.
 */
public interface DiskMq {
    boolean add(Object o);

    boolean offer(Object o);

    Object remove();

    Object poll();

    String peek();
}
