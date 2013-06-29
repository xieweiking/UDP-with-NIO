package server;

import java.nio.ByteBuffer;

public interface RequestHandler {

    ByteBuffer onRequest(ByteBuffer receivedBuff, RequestContext ctx) throws Throwable;

}
