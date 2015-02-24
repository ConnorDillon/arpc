package arpc.nio

import java.io.ByteArrayInputStream
import java.nio.ByteBuffer

class ByteBufferInputStream(buffer: ByteBuffer) extends ByteArrayInputStream(buffer.array()) {
  override def read(): Int = {
    val out = super.read()
    buffer.position(pos)
    out
  }

  override def read(b: Array[Byte], off: Int, len: Int): Int = {
    val out = super.read(b, off, len)
    buffer.position(pos)
    out
  }
}
