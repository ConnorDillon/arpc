import java.nio.channels.SelectionKey

package object nio {
  type ChannelHandlerFactory = (SelectionKey, SingleThreadedExecutor) => ChannelReader[_] with ChannelWriter[_]
}