package arpc.nio;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends ByteArrayOutputStream {
    protected ByteBuffer byteBuffer;

    public ByteBufferOutputStream(ByteBuffer bffr) {
        this.byteBuffer = bffr;
        this.buf = byteBuffer.array();
    }

    public synchronized void write(int b) {
        super.write(b);
        this.byteBuffer.position(this.count);
    }

    public synchronized void write(byte[] b, int off, int len) {
        super.write(b, off, len);
        this.byteBuffer.position(this.count);
    }
}
