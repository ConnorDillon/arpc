package arpc.nio

class SingleThreadedExecutorPool(size: Int) {
  protected val pool = Array.fill[SingleThreadedExecutor](size)(new SingleThreadedExecutor)
  
  protected var next = 0
  
  def get: SingleThreadedExecutor = {
    val exec = pool(next)
    next = (next + 1) % (size + 1)
    exec
  }
}