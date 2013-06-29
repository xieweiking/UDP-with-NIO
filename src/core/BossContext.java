package core;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public abstract class BossContext<HANDLER extends Handler<?>> {

    public static final int WAN_BUFFER_SIZE = 576 - 8 - 20, MAX_BUFFER_SIZE = 65535 - 8 - 20,
        LAN_BUFFER_SIZE = 1500 - 8 - 20;

    protected final String name;
    protected final int buffSize;
    protected final ExecutorService bossExecutor, workerExecutor;
    protected final Set<DatagramChannel> channelCollector;
    protected final int bossThreadCount;
    protected final Map<String, HANDLER> handlers;
    private boolean destroyed = false;

    public BossContext(final String name, final int buffSize, Integer bossThread, final Integer workerThread) {
        this.name = name;
        this.buffSize = buffSize;
        final GroupedThreadFactory bossFactory = new GroupedThreadFactory(name, "Boss"), workerFactory = new GroupedThreadFactory(name, "Worker");
        this.bossThreadCount = (bossThread == null ? 1 : bossThread);
        this.bossExecutor = Executors.newFixedThreadPool(this.bossThreadCount, bossFactory);
        this.workerExecutor = (workerThread == null ? Executors.newCachedThreadPool(workerFactory)
                                                   : Executors.newFixedThreadPool(workerThread, workerFactory));
        this.channelCollector = new HashSet<DatagramChannel>();
        this.handlers = new LinkedHashMap<String, HANDLER>();
    }

    public String getName() {
        return this.name;
    }

    public BossContext<HANDLER> addHandler(final String name, final HANDLER handler) {
        synchronized (this.handlers) {
            if (!this.handlers.containsKey(name) && handler != null) {
                this.handlers.put(name, handler);
            }
        }
        return this;
    }

    public Map<String, HANDLER> getHandlers() {
        return this.handlers;
    }

    protected synchronized void destroy() {
        this.workerExecutor.shutdown();
        this.bossExecutor.shutdown();
        for (final Iterator<DatagramChannel> itr = this.channelCollector.iterator(); itr.hasNext(); itr.remove()) {
            final DatagramChannel ch = itr.next();
            try {
                if (ch.isOpen()) {
                    ch.close();
                }
            }
            catch (final IOException ignored) {
            }
        }
        this.handlers.clear();
        this.channelCollector.clear();
        this.destroyed = true;
    }

    public synchronized boolean isDestroyed() {
        return this.destroyed;
    }

    protected int getBossThreadCount() {
        return this.bossThreadCount;
    }

    public synchronized void collectChannel(final DatagramChannel channel) {
        this.channelCollector.add(channel);
    }

    protected int getBufferSize() {
        return this.buffSize;
    }

    public void workerExecute(final Runnable r) {
        if (!this.workerExecutor.isShutdown()) {
            this.workerExecutor.execute(r);
        }
    }

    protected void bossExecute(final Runnable r) {
        if (!this.bossExecutor.isShutdown()) {
            this.bossExecutor.execute(r);
        }
    }

    public Logger getLogger(final Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }

}
