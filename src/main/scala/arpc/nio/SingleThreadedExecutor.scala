package arpc.nio

import java.util.concurrent.{ThreadPoolExecutor, TimeUnit, LinkedBlockingQueue}

class SingleThreadedExecutor extends ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue())
