package server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import core.Boss;

public class Server extends Boss<RequestContext, ServerHandler, ServerContext, Server> {

    protected Server(final String host, final int port, final ServerContext context) throws IOException {
        super(host, port, context);
    }

    public void multicast(final ByteBuffer buff) throws IOException {
        final Set<SocketAddress> removing = new HashSet<SocketAddress>();
        for (final Map.Entry<SocketAddress, DatagramChannel> entry : this.context.clientChannel.entrySet()) {
            final SocketAddress addr = entry.getKey();
            final DatagramChannel channel = entry.getValue();
            if (!channel.isOpen()) {
                removing.add(addr);
            }
            else {
                this.singlecast(addr, channel, buff);
            }
        }
        for (final SocketAddress addr : removing) {
            this.context.clientChannel.remove(addr);
        }
        buff.clear();
    }

    public void singlecast(final String host, final int port, final ByteBuffer buff) throws IOException {
        final InetSocketAddress addr = new InetSocketAddress(host, port);
        DatagramChannel channel = this.context.clientChannel.get(addr);
        if (channel == null) {
            channel = DatagramChannel.open();
            this.context.collectChannel(channel);
            this.context.clientChannel.put(addr, channel);
        }
        this.singlecast(addr, channel, buff);
        buff.clear();
    }

    protected void singlecast(final SocketAddress addr, DatagramChannel channel, final ByteBuffer buff) throws IOException {
        if (channel.isOpen()) {
            if (!channel.isConnected()) {
                channel = channel.connect(addr);
            }
            buff.mark();
            while (channel.isOpen() && buff.hasRemaining()) {
                channel.write(buff);
            }
            buff.reset();
        }
    }

    protected void init(final DatagramChannel channel, final String host, final int port) throws IOException {
        final DatagramSocket socket = channel.socket();
        socket.bind(new InetSocketAddress(host, port));
        this.logger.info(this.context.getName() + " at port[" + port + "] has started.");
    }

    protected ServerWorker createWorker(final DatagramChannel channel) throws IOException {
        return new ServerWorker(this, channel);
    }

    public static Server listen(final String host, final int port, final ServerContext context) throws IOException {
        final Server server = new Server(host, port, context);
        server.start();
        return server;
    }

    public static Server listen(final int port, final ServerContext context) throws IOException {
        return listen(InetAddress.getLocalHost().getHostAddress(), port, context);
    }

}
