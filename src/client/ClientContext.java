package client;

import core.BossContext;

public class ClientContext extends BossContext<ClientHandler> {

    public ClientContext(final String name, final int buffSize, final Integer bossThread,
                         final Integer workerThread) {
        super(name, buffSize, bossThread, workerThread);
    }

    public ClientContext(final String name, final int buffSize) {
        this(name, buffSize, null, null);
    }

    public ClientContext(final String name) {
        this(name, BossContext.LAN_BUFFER_SIZE, null, null);
    }

    public ClientContext(final int buffSize) {
        this("Client", buffSize, null, null);
    }

    public ClientContext() {
        this(BossContext.LAN_BUFFER_SIZE);
    }

    @Override
    public ClientContext addHandler(final String name, final ClientHandler handler) {
        super.addHandler(name, handler);
        return this;
    }

}
