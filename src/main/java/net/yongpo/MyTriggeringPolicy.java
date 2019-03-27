package net.yongpo;

import ch.qos.logback.core.rolling.TriggeringPolicyBase;
import ch.qos.logback.core.util.DefaultInvocationGate;
import ch.qos.logback.core.util.InvocationGate;

import java.io.File;

/**
 * Created by p0po on 2019/3/18 0018.
 */
public class MyTriggeringPolicy<E> extends TriggeringPolicyBase<E> {

    public static volatile boolean CONSUMER_FREE = false;

    //public static final long DEFAULT_MAX_FILE_SIZE = 10 * 1024 * 1024; // 1 MB
    public static final long DEFAULT_MAX_FILE_SIZE = 64 * 1024 * 1024; // 100 MB


    InvocationGate invocationGate = new DefaultInvocationGate();

    public boolean isTriggeringEvent(File activeFile, Object event) {
        long now = System.currentTimeMillis();
        if (invocationGate.isTooSoon(now))
            return false;

        if (MyTriggeringPolicy.CONSUMER_FREE) {
            MyTriggeringPolicy.CONSUMER_FREE = false;
            return activeFile.length() >= 0;
        } else {
            return activeFile.length() >= DEFAULT_MAX_FILE_SIZE;
        }
    }
}
