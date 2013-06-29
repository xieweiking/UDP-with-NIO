package client;

import java.nio.ByteBuffer;

public interface ResponseHandler {

    ByteBuffer onResponse(ByteBuffer receivedBuff, ResponseContext ctx) throws Throwable;

}
