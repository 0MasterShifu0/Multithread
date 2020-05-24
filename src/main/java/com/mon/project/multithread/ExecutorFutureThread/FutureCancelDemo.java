package com.mon.project.multithread.ExecutorFutureThread;

import java.util.concurrent.*;

public class FutureCancelDemo {
    // 解开Future的神秘面纱之取消任务
    // http://m.mamicode.com/info-detail-2436786.html

    public static void main(String[] args) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        Future<Target> future = exec.submit(new DemoTask());
        TimeUnit.SECONDS.sleep(2); //给足时间让启动起来，但又不足以让其完成
        future.cancel(true);
    }



}

class Target{}//任务目标

class DemoTask implements Callable<Target> { //任务
    // 测试睡眠的中断 http://m.mamicode.com/info-detail-2436786.html
    private static int counter = 0;
    private final int id = counter++;

    @Override
    public Target call() throws Exception {
        System.out.println(this + " start...");
        TimeUnit.SECONDS.sleep(5); //模拟任务运行需要的时间
        System.out.println(this + " completed!");
        return new Target();
    }

    @Override
    public String toString() {
        return "Task[" + id + "]";
    }
    }

    class InterruptedDemoTask implements Callable<Target> { //任务
        // JAVA interrupte中断线程的真正用途
        // https://www.cnblogs.com/jiangzhaowei/p/7201244.html
        private static int counter = 0;
        private final int id = counter++;

        @Override
        public Target call() throws Exception {
            try {
                while (true){
                    Thread.sleep(1000);//阻塞状态，线程被调用了interrupte（）方法，清除中断标志，抛出InterruptedException
                    //dosomething
                    boolean isIn = this.isInterrupted();
                    //运行状态，线程被调用了interrupte（）方法，中断标志被设置为true
                    //非阻塞状态中进行中断线程操作
                    System.out.println(this + " Thread isIn : "+isIn);
                    if(isIn) break;//退出循环，中断进程
                }
            }catch (InterruptedException e){//阻塞状态中进行中断线程操作
                boolean isIn = this.isInterrupted();//退出阻塞状态，且中断标志被清除，重新设置为false，所以此处的isIn为false
                System.out.println(this + " Thread is Interrupted!");
                return new Target();
            }
            return new Target();
        }

        public boolean isInterrupted(){
            return Thread.interrupted();
        }

        @Override
        public String toString() {
            return "Task[" + id + "]";
        }
}
