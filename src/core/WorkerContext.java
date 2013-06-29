package core;

import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.Map;

public abstract class WorkerContext<BCTX extends BossContext<?>, BOSS extends Boss<?, ?, BCTX, BOSS>> {

    private final BOSS                boss;
    private final DatagramChannel     channel;
    private final BCTX                context;
    private final Map<String, Object> attributes;

    public WorkerContext(final BOSS boss, final DatagramChannel channel, final BCTX context) {
        this.boss = boss;
        this.channel = channel;
        this.context = context;
        this.attributes = new HashMap<String, Object>();
    }

    public BOSS getBoss() {
        return this.boss;
    }

    public DatagramChannel getChannel() {
        return this.channel;
    }

    public BCTX getBossContext() {
        return this.context;
    }

    public boolean hasAttribute(final String key) {
        return this.attributes.containsKey(key);
    }

    public boolean hasAttribute(final String key, final Class<?> clazz) {
        return this.hasAttribute(key) && clazz.isAssignableFrom(this.getAttribute(key).getClass());
    }

    public Object getAttribute(final String key) {
        return this.attributes.get(key);
    }

    public <T> T getAttribute(final String key, final Class<T> clazz) {
        if (this.hasAttribute(key, clazz)) {
            return clazz.cast(this.getAttribute(key));
        }
        return null;
    }

    public Object setAttribute(final String key, final Object value) {
        return this.attributes.put(key, value);
    }

    public Object removeAttribute(final String key) {
        return this.attributes.remove(key);
    }

    public ByteBuffer createBuffer() {
        return ByteBuffer.allocate(this.getBossContext().getBufferSize());
    }

    public ByteBuffer wrapToBuffer(final byte[] array) {
        return ByteBuffer.wrap(array, 0, Math.min(array.length, this.getBossContext().getBufferSize()));
    }

    protected void destroy() {
        this.attributes.clear();
    }

}
