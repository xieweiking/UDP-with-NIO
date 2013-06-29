package core;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Map;
import java.util.logging.Logger;

public abstract class Worker<WCTX extends WorkerContext<?, BOSS>,
                             HANDLER extends Handler<WCTX>,
                             BCTX extends BossContext<HANDLER>,
                             BOSS extends Boss<WCTX, HANDLER, BCTX, BOSS>> implements Runnable {

    protected final Logger logger;
    protected final BOSS boss;
    protected final BCTX context;
    protected final WCTX workerCtx;
    protected final ByteBuffer receiveBuff;

    public Worker(final BOSS boss, final DatagramChannel channel) throws IOException {
        this.boss = boss;
        this.context = boss.getContext();
        this.receiveBuff = ByteBuffer.allocate(this.context.getBufferSize());
        final SocketAddress addr = this.receiveData(channel);
        this.receiveBuff.flip();
        this.context.collectChannel(channel);
        this.logger = this.context.getLogger(this.getClass());
        this.workerCtx = this.createHandlerContext(boss, channel, addr);
    }

    protected abstract SocketAddress receiveData(DatagramChannel channel) throws IOException;

    protected abstract WCTX createHandlerContext(BOSS boss, DatagramChannel channel, SocketAddress addr);

    public void run() {
        if (this.boss.isAlive()) {
            Signal sig;
            String targetHandler = null;
            int jumpCount = 0;
            do {
                sig = null;
                final Map<String, HANDLER> handlers = this.context.getHandlers();
                synchronized (handlers) {
                    for (final Map.Entry<String, HANDLER> entry : handlers.entrySet()) {
                        final String name = entry.getKey();
                        if (targetHandler == null || targetHandler.equals(name)) {
                            if (targetHandler != null) {
                                targetHandler = null;
                            }
                            final HANDLER handler = entry.getValue();
                            try {
                                handler.handle(this.receiveBuff.duplicate(), this.workerCtx);
                            }
                            catch (final Throwable t) {
                                if (t instanceof Signal) {
                                    sig = (Signal) t;
                                }
                                else {
                                    handler.onError(t, this.workerCtx);
                                }
                            }
                            if (!Signal.CONTINUE.equals(sig)) {
                                if (sig instanceof Signal.JUMP) {
                                    targetHandler = ((Signal.JUMP) sig).handler;
                                    jumpCount++;
                                    this.logger.info("Handler is jumping from '" + name + "' to '" + targetHandler + "'.");
                                }
                                break;
                            }
                        }
                    }
                }
                if (sig == null && targetHandler != null && this.boss.isAlive()) {
                    this.logger.severe("No handler named '" + targetHandler + "'!");
                }
            }
            while (sig instanceof Signal.JUMP && jumpCount < Signal.JUMP.LIMIT);
            if (Signal.SHUTDOWN.equals(sig)) {
                this.boss.destroy();
            }
            else if (sig instanceof Signal.JUMP) {
                this.logger.severe("Too many jumps! The max jumps is limited at " + Signal.JUMP.LIMIT + "!");
            }
        }
        this.workerCtx.destroy();
    }

}
