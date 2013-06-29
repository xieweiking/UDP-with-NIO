package client;

import java.nio.ByteBuffer;

public interface ResponseHandler {

    ByteBuffer onResponse(ByteBuffer receivedBuff, ClientHandlerContext ctx) throws Throwable;

}
