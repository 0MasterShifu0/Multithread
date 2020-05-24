package com.mon.project.multithread.ExecutorFutureThread;

import org.junit.Test;

import java.util.concurrent.*;


public class FutureCancelDemoTest {

    @Test
    public void testFutureCancel() throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool(); //缓冲线程池

        Future<Target> future = exec.submit(new DemoTask());
        TimeUnit.SECONDS.sleep(2); //给足时间让启动起来，但又不足以让其完成
        boolean cancelResult1 = future.cancel(true); //true表示，如果已经运行，则中断

        Future<Target> future2 = exec.submit(new DemoTask());
        TimeUnit.SECONDS.sleep(2);
        boolean cancelResult2 = future2.cancel(false);

        System.out.println("cancelResult1:" + cancelResult1);
        System.out.println("cancelResult2：" + cancelResult2);

        try {
            Target target = future2.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInterruptedDemoTask() throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool(); //缓冲线程池

        Future<Target> future = exec.submit(new InterruptedDemoTask());
        TimeUnit.SECONDS.sleep(2); //给足时间让启动起来，但又不足以让其完成
        boolean cancelResult1 = future.cancel(true); //true表示，如果已经运行，则中断

        Future<Target> future2 = exec.submit(new InterruptedDemoTask());
        TimeUnit.SECONDS.sleep(2);
        boolean cancelResult2 = future2.cancel(false);

        System.out.println("cancelResult1:" + cancelResult1);
        System.out.println("cancelResult2：" + cancelResult2);

//        try {
//            Target target = future2.get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }

}