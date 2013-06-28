package core;

import java.nio.ByteBuffer;

public interface Handler<WCTX extends WorkerContext<?, ?>> {

    void handle(final ByteBuffer receivedBuff, final WCTX ctx) throws Throwable;

    void onError(final Throwable t, final WCTX ctx);

}
