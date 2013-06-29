package server;

import java.nio.ByteBuffer;

import core.AbstractHandler;

public abstract class ServerHandler extends AbstractHandler<RequestContext> implements RequestHandler, RequestErrorHandler {

    public ServerHandler() {
    }

    protected ByteBuffer doInput(final ByteBuffer receivedBuff, final RequestContext ctx) throws Throwable {
        return this.onRequest(receivedBuff, ctx);
    }

    protected void doOutput(final ByteBuffer outBuff, final RequestContext ctx) throws Throwable {
        ctx.getChannel().send(outBuff, ctx.getClientAddr());
    }

}
