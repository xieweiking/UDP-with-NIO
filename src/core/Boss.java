package core;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Boss<WCTX extends WorkerContext<?, BOSS>,
                           HANDLER extends Handler<WCTX>,
                           BCTX extends BossContext<HANDLER>,
                           BOSS extends Boss<WCTX, HANDLER, BCTX, BOSS>> implements Runnable {

    protected final Logger logger;
    protected final BCTX context;
    protected final Selector selector;

    public Boss(final String host, final int port, final BCTX context) throws IOException {
        this.context = context;
        this.selector = Selector.open();
        final DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.register(this.selector, SelectionKey.OP_READ);
        this.context.collectChannel(channel);
        this.logger = this.context.getLogger(this.getClass());
        this.init(channel, host, port);
    }

    protected abstract void init(DatagramChannel channel, String host, int port) throws IOException;

    public BCTX getContext() {
        return this.context;
    }

    public void destroy() {
        if (this.isAlive()) {
            this.context.destroy();
            this.logger.info(this.context.getName() + " has destroyed.");
        }
    }

    public boolean isDestroyed() {
        return this.context.isDestroyed();
    }

    public boolean isAlive() {
        return !this.isDestroyed();
    }

    public void run() {
        while (this.isAlive()) {
            try {
                if (this.selector.select(300) > 0) {
                    for (final Iterator<SelectionKey> itr = this.selector.selectedKeys().iterator(); this.isAlive()
                                                                                                     && itr.hasNext(); itr.remove()) {
                        final SelectionKey sk = itr.next();
                        if (sk.isValid() && sk.isReadable()) {
                            final SelectableChannel channel = sk.channel();
                            if (channel instanceof DatagramChannel) {
                                this.context.workerExecute(this.createWorker((DatagramChannel) channel));
                            }
                        }
                    }
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Can NOT dispatch.", ex);
            }
        }
    }

    protected abstract Worker<WCTX, HANDLER, BCTX, BOSS> createWorker(DatagramChannel channel) throws IOException;

    protected void start() {
        for (int i = 0; i < this.context.getBossThreadCount(); i++) {
            this.context.bossExecute(this);
        }
    }

}
