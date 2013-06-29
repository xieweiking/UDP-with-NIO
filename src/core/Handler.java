package core;

import java.nio.ByteBuffer;

public interface Handler<WCTX extends WorkerContext<?, ?>> extends ErrorHandler<WCTX> {

    void handle(final ByteBuffer receivedBuff, final WCTX ctx) throws Throwable;

}
