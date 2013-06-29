import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import server.RequestHandler;
import server.Server;
import server.ServerContext;
import server.ServerHandlerContext;
import client.Client;
import client.ClientContext;
import client.ClientHandlerContext;
import client.ResponseHandler;
import core.Signal;

public class Test {

    private static final Charset CHARSET = Charset.defaultCharset();

    public static void main(final String... args) throws Exception {
        Server.listen(5057, new ServerContext("你妹").addHandler("停鸡", new RequestHandler() {

            public ByteBuffer onRequest(final ByteBuffer received, final ServerHandlerContext ctx) throws Throwable {
                if (received.remaining() == 16 && received.getLong() == 0 && received.getLong() == 0) {
                    ctx.getBoss().multicast(CHARSET.encode("好歌一生伴着你"));
                    throw Signal.SHUTDOWN;
                }
                throw Signal.jumpTo("唱");
            }

        }).addHandler("唱", new RequestHandler() {

            public ByteBuffer onRequest(final ByteBuffer received, final ServerHandlerContext ctx) throws Throwable {
                final String lyric = CHARSET.decode(received).toString();
                System.out.println("我哥: " + lyric);
                return CHARSET.encode("好歌献给你～".equals(lyric) ? "愿你藏心里～" : "我活在歌声里");
            }

        }));

        Client.connect(5057, new ClientContext("我哥").addHandler("唱", new ResponseHandler() {

            public ByteBuffer onResponse(final ByteBuffer received, final ClientHandlerContext ctx) throws Throwable {
                final String lyric = CHARSET.decode(received).toString();
                System.out.println("你妹: " + lyric);
                if ("愿你藏心里～".equals(lyric)) {
                    return CHARSET.encode("惟愿为你解去愁闷");
                }
                else {
                    ctx.getBossContext().workerExecute(new Runnable() {

                        public void run() {
                            try {
                                Thread.sleep(100);
                            }
                            catch (final Exception ignored) {
                            }
                            ctx.getBoss().destroy();
                        }

                    });
                    return ctx.wrapToBuffer(new byte[16]);
                }
            }

        })).send(CHARSET.encode("好歌献给你～"));
    }

}
