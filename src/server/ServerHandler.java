package server;

import java.nio.ByteBuffer;

import core.AbstractHandler;

public abstract class ServerHandler extends AbstractHandler<ServerHandlerContext> {

    public ServerHandler() {
    }

    protected ByteBuffer doInput(final ByteBuffer receivedBuff, final ServerHandlerContext ctx) throws Throwable {
        return this.onRequest(receivedBuff, ctx);
    }

    protected void doOutput(final ByteBuffer outBuff, final ServerHandlerContext ctx) throws Throwable {
        ctx.getChannel().send(outBuff, ctx.getClientAddr());
    }

    protected abstract ByteBuffer onRequest(ByteBuffer receivedBuff, ServerHandlerContext ctx) throws Throwable;

}