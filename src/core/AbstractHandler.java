package core;

import java.io.IOException;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Level;

public abstract class AbstractHandler<WCTX extends WorkerContext<?, ?>> implements Handler<WCTX> {

    public AbstractHandler() {
    }

    public void handle(final ByteBuffer receivedBuff, final WCTX ctx) throws Throwable {
        final DatagramChannel channel = ctx.getChannel();
        final ByteBuffer outBuff = channel.isOpen() ? this.doInput(receivedBuff, ctx) : null;
        if (outBuff == null) {
            throw Signal.CONTINUE;
        }
        if (outBuff.position() != 0) {
            outBuff.flip();
        }
        while (channel.isOpen() && outBuff.hasRemaining()) {
            this.doOutput(outBuff, ctx);
        }
        outBuff.clear();
    }

    protected abstract ByteBuffer doInput(ByteBuffer receivedBuff, WCTX ctx) throws Throwable;

    protected abstract void doOutput(ByteBuffer outBuff, WCTX ctx) throws Throwable;

    protected void jumpTo(final String handler) throws Signal.JUMP {
        throw new Signal.JUMP(handler);
    }

    public void onError(final Throwable t, final WCTX ctx) {
        final DatagramChannel channel = ctx.getChannel();
        final DatagramSocket socket = channel.socket();
        ctx.getBossContext().getLogger(this.getClass()).log(Level.SEVERE, "Error occured while handling channel to socket["
                                                                             + socket.getInetAddress()
                                                                             + ":"
                                                                             + socket.getPort() + "]!", t);
        try {
            channel.close();
        }
        catch (final IOException ignored) {
        }
    }

}
