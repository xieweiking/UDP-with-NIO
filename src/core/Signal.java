package core;

public abstract class Signal extends Throwable {

    private static final long serialVersionUID = -8306813483064905024L;

    public static final Signal FINISH = new FINISH(), SHUTDOWN = new SHUTDOWN(), CONTINUE = new CONTINUE();

    public final String        label;

    public Signal(final String label) {
        this.label = label;
    }

    public static JUMP jumpTo(final String handler) {
        return new JUMP(handler);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.label == null) ? 0 : this.label.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Signal other = (Signal) obj;
        if (this.label == null) {
            if (other.label != null) {
                return false;
            }
        }
        else if (!this.label.equals(other.label)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Signal [label=" + this.label + "]";
    }

    private static class CONTINUE extends Signal {

        private static final long serialVersionUID = 7634556486047924405L;

        private CONTINUE() {
            super("CONTINUE");
        }

    }

    private static class FINISH extends Signal {

        private static final long serialVersionUID = -1391301442731061554L;

        private FINISH() {
            super("FINISH");
        }

    }

    private static class SHUTDOWN extends Signal {

        private static final long serialVersionUID = -4812487991644654952L;

        private SHUTDOWN() {
            super("SHUTDOWN");
        }

    }

    public static class JUMP extends Signal {

        private static final long serialVersionUID = 4308582408239023856L;

        public static final int   LIMIT            = 256;

        public final String       handler;

        public JUMP(final String handlerName) {
            super("JUMP");
            this.handler = handlerName;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + ((this.handler == null) ? 0 : this.handler.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final JUMP other = (JUMP) obj;
            if (this.handler == null) {
                if (other.handler != null) {
                    return false;
                }
            }
            else if (!this.handler.equals(other.handler)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "JUMP [handler=" + this.handler + "]";
        }

    }

}
