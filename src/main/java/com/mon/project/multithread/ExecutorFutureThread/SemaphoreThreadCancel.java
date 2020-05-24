package com.mon.project.multithread.ExecutorFutureThread;

import com.mon.project.multithread.ExecutorFactory;

import java.util.concurrent.*;

public class SemaphoreThreadCancel {
    // https://www.cnblogs.com/alipayhutu/archive/2012/06/20/2556091.html
    // Future, future.cancel()可以删除同步阻塞任务
    /** 信号量 */
    private Semaphore semaphore = new Semaphore(0); // 1
    /** 线程池 */
    private ThreadPoolExecutor pool = new ThreadPoolExecutor(3, 5, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3));
    //private ExecutorService  pool = ExecutorFactory.executor;
    /** Future */
    private Future<String> future ;

    public void test() {

        future = ExecutorFactory.executor.submit(new Callable<String>() {

            @Override
            public String call() {
                String result = null;
                try {
                    // 同步阻塞获取信号量
                    semaphore.acquire();
                    result = "ok";
                } catch (InterruptedException e) {
                    result = "interrupted";
                }
                return result;
            }
        });

        String result = "timeout";
        try {
            // 等待3s
            result = future.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("超时异常");
        }

        // 删除线程池中任务
        boolean cancelResult = future.cancel(true);

        System.out.println("result is " + result);
        System.out.println("删除结果：" + cancelResult);
        System.out.println("当前active线程数：" + pool.getActiveCount());
        test();
    }
}
