package core;

import java.util.concurrent.ThreadFactory;

public class GroupedThreadFactory implements ThreadFactory {

    private final ThreadGroup threadGroup;
    private final String threadNamePrefix;
    private int counter = 0;

    public GroupedThreadFactory(final String groupName, final String threadNamePrefix) {
        this(new ThreadGroup(groupName), threadNamePrefix);
    }

    public GroupedThreadFactory(final String threadNamePrefix) {
        this(Thread.currentThread().getThreadGroup(), threadNamePrefix);
    }

    public GroupedThreadFactory(final ThreadGroup group, final String threadNamePrefix) {
        this.threadGroup = group;
        this.threadNamePrefix = group.getName() + " - " + threadNamePrefix;
    }

    public Thread newThread(final Runnable r) {
        this.counter++;
        return new Thread(this.threadGroup, r, this.threadNamePrefix + " #" + this.counter);
    }

}
