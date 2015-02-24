package arpc

import scala.concurrent.ExecutionContext

package object utils {
  def execute(fn: () => Unit)(implicit ec: ExecutionContext): Unit = ec.execute(new Runnable {
    def run(): Unit = fn()
  })
}
