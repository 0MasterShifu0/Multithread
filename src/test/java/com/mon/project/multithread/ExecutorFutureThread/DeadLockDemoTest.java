package com.mon.project.multithread.ExecutorFutureThread;

import org.junit.Test;

import java.util.concurrent.*;


public class DeadLockDemoTest {
    private Object resource1 = new Object();//资源 1
    private Object resource2 = new Object();//资源 2

    @Test
    public void testInterruptedDemoTask() throws InterruptedException, ExecutionException {
        ExecutorService exec = Executors.newCachedThreadPool(); //缓冲线程池

        Future<Target> future1 = exec.submit(new GetResourceAsc());
        Future<Target> future2 = exec.submit(new GetResourceDesc());
        future1.get();
        future1.cancel(true);
        future2.get();
        future2.cancel(true);
        //TimeUnit.SECONDS.sleep(4); //给足时间让启动起来，但又不足以让其完成
        System.out.println("sleep end.");
//        boolean cancelResult1 = future.cancel(true); //true表示，如果已经运行，则中断
//        boolean cancelResult2 = future2.cancel(false);
//
//        System.out.println("cancelResult1:" + cancelResult1);
//        System.out.println("cancelResult2：" + cancelResult2);
//
//        try {
//            Target target = future2.get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void testCompletableFutureCancel() throws InterruptedException, ExecutionException {
        ExecutorService exec = Executors.newCachedThreadPool(); //缓冲线程池
        CompletableFuture cf1 = CompletableFuture.supplyAsync(() ->
                {
                    try {
                        synchronized (resource1) {
                            System.out.println(Thread.currentThread() + "get resource1");

                            TimeUnit.SECONDS.sleep(1);

                            System.out.println(Thread.currentThread() + "waiting get resource2");
                            int i =0;
                            while (i<8){
                                TimeUnit.SECONDS.sleep(1);
                                System.out.println(Thread.currentThread()+" i : "+(i++));
                            }
                            synchronized (resource2) {
                                System.out.println(Thread.currentThread() + "get resource2");
                            }
                        }
                    } catch (InterruptedException e) {
                        System.out.println(Thread.currentThread() + "Thread is Interrupted.");
                    }
                    return null;
                }
        );
        CompletableFuture cf2 = CompletableFuture.supplyAsync(() ->
                {
                    try {
                        synchronized (resource2) {
                            System.out.println(Thread.currentThread() + "get resource2");
                            TimeUnit.SECONDS.sleep(1);
                            System.out.println(Thread.currentThread() + "waiting get resource1");
                            int i =0;
                            while (i<8){
                                TimeUnit.SECONDS.sleep(1);
                                System.out.println(Thread.currentThread()+" i : "+(i++));
                            }
                            synchronized (resource1) {
                                System.out.println(Thread.currentThread() + "get resource1");
                            }
                        }
                    } catch (InterruptedException e) {
                        System.out.println(Thread.currentThread() + "Thread is Interrupted.");
                    }
                    return null;
                }
        );
        try {
            cf1.get(3,TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            //cf1.cancel(true);
            System.out.println( "thread1.cancel end. true");
            e.printStackTrace();
        }
        try {
            cf2.get(3,TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            //cf2.cancel(true);
            System.out.println( "thread2.cancel end. true");
            e.printStackTrace();
        }
        System.out.println("sleep end.");
//        boolean cancelResult1 = future.cancel(true); //true表示，如果已经运行，则中断
//        boolean cancelResult2 = future2.cancel(false);
//
//        System.out.println("cancelResult1:" + cancelResult1);
//        System.out.println("cancelResult2：" + cancelResult2);
//
//        try {
//            Target target = future2.get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }


    @Test
    public void testSynchronizedInterrupted() throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool(); //缓冲线程池
        Object resource1 = new Object();//资源 1
        Object resource2 = new Object();//资源 2
        Future<Boolean> future1 =
                exec.submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        try {
                            synchronized (resource1) {
                                System.out.println(Thread.currentThread() + "get resource1");

                                TimeUnit.SECONDS.sleep(1);

                                System.out.println(Thread.currentThread() + "waiting get resource2");
                                int i =0;
                                while (i<8){
                                    TimeUnit.SECONDS.sleep(1);
                                    System.out.println(Thread.currentThread()+" i : "+(i++));
                                }
                                synchronized (resource2) {
                                    System.out.println(Thread.currentThread() + "get resource2");
                                }
                            }
                        } catch (InterruptedException e) {
                            System.out.println(Thread.currentThread() + "Thread is Interrupted.");
                        }
                        return null;
                    }
                });

        Future<Boolean> future2 =
                exec.submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        try {
                            synchronized (resource2) {
                                System.out.println(Thread.currentThread() + "get resource2");
                                TimeUnit.SECONDS.sleep(1);
                                System.out.println(Thread.currentThread() + "waiting get resource1");
                                int i =0;
                                while (i<8){
                                    TimeUnit.SECONDS.sleep(1);
                                    System.out.println(Thread.currentThread()+" i : "+(i++));
                                }
                                synchronized (resource1) {
                                    System.out.println(Thread.currentThread() + "get resource1");
                                }
                            }
                        } catch (InterruptedException e) {
                            System.out.println(Thread.currentThread() + "Thread is Interrupted.");
                        }
                        return null;
                    }
                });
        try {
            future1.get(3,TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            future1.cancel(true);
            System.out.println( "thread1.cancel end. true");
            e.printStackTrace();
        }
        try {
            future1.get(3,TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            future1.cancel(true);
            System.out.println( "thread1.cancel end. true 2");
            e.printStackTrace();
        }
        System.out.println( "thread1.interrup end.");
    }

}