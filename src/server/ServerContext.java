package server;

import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import core.BossContext;

public class ServerContext extends BossContext<ServerHandler> {

    protected final Map<SocketAddress, DatagramChannel> clientChannel;

    public ServerContext(final String name, final int buffSize, final Integer bossThread, final Integer workerThread) {
        super(name, buffSize, bossThread, workerThread);
        this.clientChannel = new ConcurrentHashMap<SocketAddress, DatagramChannel>();
    }

    public ServerContext(final String name, final int buffSize) {
        this(name, buffSize, null, null);
    }

    public ServerContext(final String name) {
        this(name, BossContext.LAN_BUFFER_SIZE, null, null);
    }

    public ServerContext(final int buffSize) {
        this("Server", buffSize, null, null);
    }

    public ServerContext() {
        this(BossContext.LAN_BUFFER_SIZE);
    }

    @Override
    public ServerContext addHandler(final String name, final ServerHandler handler) {
        super.addHandler(name, handler);
        return this;
    }

    @Override
    protected void destroy() {
        this.clientChannel.clear();
        super.destroy();
    }

}
