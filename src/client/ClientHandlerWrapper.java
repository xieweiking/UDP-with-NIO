package client;

import java.nio.ByteBuffer;

public class ClientHandlerWrapper extends ClientHandler {

    private final ResponseHandler handler;

    public ClientHandlerWrapper(final ResponseHandler handler) {
        this.handler = handler;
    }

    public ByteBuffer onResponse(final ByteBuffer receivedBuff, final ClientHandlerContext ctx) throws Throwable {
        return this.handler.onResponse(receivedBuff, ctx);
    }

    @Override
    public void onError(final Throwable t, final ClientHandlerContext ctx) {
        if (this.handler instanceof ResponseErrrorHandler) {
            ((ResponseErrrorHandler) this.handler).onError(t, ctx);
        }
        else {
            super.onError(t, ctx);
        }
    }

}
