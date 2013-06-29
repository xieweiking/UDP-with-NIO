package core;

public interface ErrorHandler<WCTX extends WorkerContext<?, ?>> {

    void onError(final Throwable t, final WCTX ctx);

}
