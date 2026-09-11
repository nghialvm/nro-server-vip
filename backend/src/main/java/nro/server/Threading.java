package nro.server;



import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class Threading {

    private static ThreadFactory namedFactory(String prefix) {
        AtomicInteger counter = new AtomicInteger(1);
        return runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY);
            t.setName(prefix + "-" + counter.getAndIncrement());
            return t;
        };
    }

    public static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            0,
            Integer.MAX_VALUE,
            30L, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            namedFactory("nro")
    );

    public static final ScheduledThreadPoolExecutor SCHEDULER = new ScheduledThreadPoolExecutor(
            4,
            namedFactory("nro")
    );

    public static final ThreadPoolExecutor WORKER = new ThreadPoolExecutor(
            4,
            Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            namedFactory("nro")
    );

    public static final ThreadPoolExecutor SAVE_EXECUTOR = new ThreadPoolExecutor(
            2,
            Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            namedFactory("nro")
    );

    public static final ThreadPoolExecutor NETWORK_EXECUTOR = new ThreadPoolExecutor(
            4,
            Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            namedFactory("nro")
    );

    public static final ScheduledThreadPoolExecutor AUTO_SAVE_SCHEDULER = new ScheduledThreadPoolExecutor(
            2,
            namedFactory("nro")
    );

    public static final ThreadPoolExecutor LOG_EXECUTOR = new ThreadPoolExecutor(
            1,
            Integer.MAX_VALUE,
            30L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            namedFactory("nro")
    );

    public static void SLEEP(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void autoShutdownExecutor(ThreadPoolExecutor executor, long idleTimeoutMillis) {
        SCHEDULER.scheduleWithFixedDelay(() -> {
            if (executor.getActiveCount() == 0 && executor.getQueue().isEmpty()) {
                executor.shutdown();
            }
        }, idleTimeoutMillis, idleTimeoutMillis, TimeUnit.MILLISECONDS);
    }

    static {
        EXECUTOR.allowCoreThreadTimeOut(true);
        WORKER.allowCoreThreadTimeOut(true);
        SCHEDULER.setKeepAliveTime(60L, TimeUnit.SECONDS);
        SCHEDULER.allowCoreThreadTimeOut(true);
        SAVE_EXECUTOR.allowCoreThreadTimeOut(true);
        NETWORK_EXECUTOR.allowCoreThreadTimeOut(true);
        AUTO_SAVE_SCHEDULER.setKeepAliveTime(60L, TimeUnit.SECONDS);
        AUTO_SAVE_SCHEDULER.allowCoreThreadTimeOut(true);
        LOG_EXECUTOR.allowCoreThreadTimeOut(true);
        autoShutdownExecutor(EXECUTOR, 60_000);
        autoShutdownExecutor(WORKER, 60_000);
        autoShutdownExecutor(SAVE_EXECUTOR, 60_000);
        autoShutdownExecutor(NETWORK_EXECUTOR, 60_000);
        autoShutdownExecutor(LOG_EXECUTOR, 60_000);
    }

    public static ThreadPoolExecutor createNewExecutor() {
        ThreadPoolExecutor newExecutor = new ThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                30L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                namedFactory("nro")
        );
        newExecutor.allowCoreThreadTimeOut(true);
        autoShutdownExecutor(newExecutor, 60_000);
        return newExecutor;
    }

    public static void Autogc() {
        Threading.SCHEDULER.scheduleAtFixedRate(() -> {
            System.gc();
        }, 0, 5, TimeUnit.MINUTES);
    }
}