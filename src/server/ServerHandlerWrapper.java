package server;

import java.nio.ByteBuffer;

public class ServerHandlerWrapper extends ServerHandler {

    private final RequestHandler handler;

    public ServerHandlerWrapper(final RequestHandler handler) {
        this.handler = handler;
    }

    public ByteBuffer onRequest(final ByteBuffer receivedBuff, final RequestContext ctx) throws Throwable {
        return this.handler.onRequest(receivedBuff, ctx);
    }

    @Override
    public void onError(final Throwable t, final RequestContext ctx) {
        if (this.handler instanceof RequestErrorHandler) {
            ((RequestErrorHandler) this.handler).onError(t, ctx);
        }
        else {
            super.onError(t, ctx);
        }
    }

}
