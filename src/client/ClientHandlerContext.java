package client;
import java.nio.channels.DatagramChannel;

import core.WorkerContext;

public class ClientHandlerContext extends WorkerContext<ClientContext, Client> {

    public ClientHandlerContext(final Client client, final DatagramChannel channel, final ClientContext context) {
        super(client, channel, context);
    }

}
