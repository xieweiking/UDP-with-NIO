package server;

import java.nio.ByteBuffer;

public interface RequestHandler {

    ByteBuffer onRequest(ByteBuffer receivedBuff, ServerHandlerContext ctx) throws Throwable;

}
