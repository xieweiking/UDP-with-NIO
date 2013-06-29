package client;

import java.nio.ByteBuffer;

import core.AbstractHandler;

public abstract class ClientHandler extends AbstractHandler<ResponseContext> implements ResponseHandler, ResponseErrrorHandler {

    public ClientHandler() {}

    protected ByteBuffer doInput(final ByteBuffer receivedBuff, final ResponseContext ctx) throws Throwable {
        return this.onResponse(receivedBuff, ctx);
    }

    protected void doOutput(final ByteBuffer outBuff, final ResponseContext ctx) throws Throwable {
        ctx.getChannel().write(outBuff);
    }

}
