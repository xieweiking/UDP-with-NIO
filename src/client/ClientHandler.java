package client;

import java.nio.ByteBuffer;

import core.AbstractHandler;

public abstract class ClientHandler extends AbstractHandler<ClientHandlerContext> {

    public ClientHandler() {
    }
    
    protected ByteBuffer doInput(final ByteBuffer receivedBuff, final ClientHandlerContext ctx) throws Throwable {
        return this.onResponse(receivedBuff, ctx);
    }
    
    protected void doOutput(final ByteBuffer outBuff, final ClientHandlerContext ctx) throws Throwable {
        ctx.getChannel().write(outBuff);
    }

    protected abstract ByteBuffer onResponse(ByteBuffer receivedBuff, ClientHandlerContext ctx) throws Throwable;

}
