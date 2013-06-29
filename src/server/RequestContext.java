package server;

import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

import core.WorkerContext;

public class RequestContext extends WorkerContext<ServerContext, Server> {

    private final SocketAddress clientAddr;

    public RequestContext(final Server server, final DatagramChannel channel, final SocketAddress clientAddr, final ServerContext context) {
        super(server, channel, context);
        this.clientAddr = clientAddr;
    }

    public SocketAddress getClientAddr() {
        return this.clientAddr;
    }

}
