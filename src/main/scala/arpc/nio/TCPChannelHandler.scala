package nio

import serialization.{Deserializer, Serializer}

trait TCPChannelHandler[T] extends ChannelWriter[T] with StreamReader[T] {
  this: Serializer[T] with Deserializer[T] =>
}