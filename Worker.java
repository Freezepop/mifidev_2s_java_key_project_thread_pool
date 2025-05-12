import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Worker extends Thread {
    private final BlockingQueue<Runnable> queue;
    private final CustomThreadPool pool;
    private final long keepAliveTime;
    private final TimeUnit unit;

    public Worker(CustomThreadPool pool, BlockingQueue<Runnable> queue, long keepAliveTime, TimeUnit unit) {
        this.queue = queue;
        this.pool = pool;
        this.keepAliveTime = keepAliveTime;
        this.unit = unit;
    }

    @Override
    public void run() {
        try {
            while (!pool.isShutdown()) {
                Runnable task = queue.poll(keepAliveTime, unit);
                if (task != null) {
                    System.out.println("[Worker] " + getName() + " executes " + task);
                    try {
                        task.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (pool.shouldTerminate(this)) {
                        System.out.println("[Worker] " + getName() + " idle timeout, stopping.");
                        break;
                    }
                }
            }
        } catch (InterruptedException ignored) {
        } finally {
            System.out.println("[Worker] " + getName() + " terminated.");
            pool.removeWorker(this);
        }
    }
}
