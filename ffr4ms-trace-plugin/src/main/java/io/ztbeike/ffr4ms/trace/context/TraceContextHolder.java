package io.ztbeike.ffr4ms.trace.context;

/**
 * 保持trace上下文信息
 */
public class TraceContextHolder {

    /**
     * 单例的TraceContextHolder对象
     */
    private static volatile TraceContextHolder holder = null;

    /**
     * 保存线程私有的trace上下文
     */
    private final ThreadLocal<TraceContext> threadLocal = new ThreadLocal<>();

    private TraceContextHolder() {
    }

    /**
     * 获取单例TraceContextHolder对象
     * @return 单例的TraceContextHolder对象
     */
    public static TraceContextHolder getInstance() {
        if (holder == null) {
            synchronized (TraceContextHolder.class) {
                if (holder == null) {
                    holder = new TraceContextHolder();
                }
            }
        }
        return holder;
    }

    public void set(TraceContext context) {
        this.threadLocal.set(context);
    }

    /**
     * 获取当前线程的trace上下文
     * @return 当前线程的trace上下文
     */
    public TraceContext get() {
        return this.threadLocal.get();
    }

    /**
     * 清除线程trace上下文
     */
    public void remove() {
        this.threadLocal.remove();
    }

}
