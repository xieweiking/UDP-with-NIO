package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import core.Boss;

public class Client extends Boss<ResponseContext, ClientHandler, ClientContext, Client> {

    private DatagramChannel channel;

    protected Client(final String host, final int port, final ClientContext context) throws IOException {
        super(host, port, context);
    }

    public void send(final ByteBuffer buff) throws IOException {
        if (this.channel.isOpen() && this.channel.isConnected()) {
            if (buff.position() != 0) {
                buff.flip();
            }
            while (buff.hasRemaining()) {
                this.channel.write(buff);
            }
            buff.clear();
        }
    }

    protected void init(final DatagramChannel channel, final String host, final int port) throws IOException {
        this.channel = channel;
        channel.connect(new InetSocketAddress(host, port));
        this.logger.info(this.context.getName() + " has connected to server[" + host + ":" + port + "].");
    }

    protected ClientWorker createWorker(final DatagramChannel channel) throws IOException {
        return new ClientWorker(this, channel);
    }

    public static Client connect(final String host, final int port, final ClientContext context) throws IOException {
        final Client client = new Client(host, port, context);
        client.start();
        return client;
    }

    public static Client connect(final int port, final ClientContext context) throws IOException {
        return connect(InetAddress.getLocalHost().getHostAddress(), port, context);
    }

}
