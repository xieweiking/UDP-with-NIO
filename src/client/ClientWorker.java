package client;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

import core.Worker;

public class ClientWorker extends Worker<ResponseContext, ClientHandler, ClientContext, Client> {

    public ClientWorker(final Client client, final DatagramChannel channel) throws IOException {
        super(client, channel);
    }

    protected SocketAddress receiveData(final DatagramChannel channel) throws IOException {
        while (this.receiveBuff.hasRemaining() && channel.read(this.receiveBuff) > 0);
        return null;
    }

    protected ResponseContext createHandlerContext(final Client client, final DatagramChannel channel, final SocketAddress addr) {
        return new ResponseContext(client, channel, this.context);
    }

}
