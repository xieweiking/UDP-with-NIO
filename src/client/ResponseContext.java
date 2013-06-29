package client;
import java.nio.channels.DatagramChannel;

import core.WorkerContext;

public class ResponseContext extends WorkerContext<ClientContext, Client> {

    public ResponseContext(final Client client, final DatagramChannel channel, final ClientContext context) {
        super(client, channel, context);
    }

}
