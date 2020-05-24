package com.mon.project.multithread.ExecutorFutureThread;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.mon.project.multithread.ExecutorFutureThread.DeadLockDemo.resource1;
import static com.mon.project.multithread.ExecutorFutureThread.DeadLockDemo.resource2;

public class DeadLockDemo {
    public static Object resource1 = new Object();//资源 1
    public static Object resource2 = new Object();//资源 2

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
                try {
                    synchronized (resource1) {
                        System.out.println(Thread.currentThread() + "get resource1");

                            TimeUnit.SECONDS.sleep(1);

                        System.out.println(Thread.currentThread() + "waiting get resource2");
                        synchronized (resource2) {
                            System.out.println(Thread.currentThread() + "get resource2");
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread() + "Thread is Interrupted.");
                }
            }, "线程 1");
        thread1.start();

        Thread thread2 = new Thread(() -> {
                try {
                synchronized (resource2) {
                    System.out.println(Thread.currentThread() + "get resource2");
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(Thread.currentThread() + "waiting get resource1");
                    synchronized (resource1) {
                        System.out.println(Thread.currentThread() + "get resource1");
                    }
                }
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread() + "Thread is Interrupted.");
                }
            }, "线程 2");
        thread2.start();
        TimeUnit.SECONDS.sleep(6);
        System.out.println( "thread1.interrup start.");
        thread1.interrupt();
        thread2.interrupt();
        System.out.println( "thread1.interrup result : "+thread1.isInterrupted());
        System.out.println( "thread1.interrup end.");
    }
}


    class GetResourceAsc implements Callable<Target> { //任务
        // 测试睡眠的中断 http://m.mamicode.com/info-detail-2436786.html
        private static int counter = 0;
        private final int id = counter++;

        @Override
        public Target call() throws Exception {
            return getTargetCallAsc();
        }

        public Target getTargetCallAsc() {
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
            return new Target();
        }

        @Override
        public String toString() {
            return "Task[" + id + "]";
        }
    }

class GetResourceDesc implements Callable<Target> { //任务
    // 测试睡眠的中断 http://m.mamicode.com/info-detail-2436786.html
    private static int counter = 0;
    private final int id = counter++;

    @Override
    public Target call() throws Exception {
        return getTargetCallDesc();
    }

    public Target getTargetCallDesc() {
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
        return new Target();
    }

    @Override
    public String toString() {
        return "Task[" + id + "]";
    }
}


