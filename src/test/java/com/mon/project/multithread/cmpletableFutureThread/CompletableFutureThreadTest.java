package com.mon.project.multithread.cmpletableFutureThread;

import com.mon.project.multithread.blockingQueueThread.CompletableFutureConsumer;
import com.mon.project.multithread.completableFutureThread.CompletableFutureThread;
import com.mon.project.stream.vo.SpartVO;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CompletableFutureThreadTest {
    private static final Logger logger = LoggerFactory.getLogger(CompletableFutureThreadTest.class);
    //异步后进行拼接变换
    @Test
    public void thenApply() {
        String result = CompletableFuture.supplyAsync(() -> "hello").thenApply(s -> s + " world").join();
        System.out.println(result);  //hello world
    }

    //Async结尾的方法都是可以异步执行的
    //测试异步调用捕获异常
    @Test
    public void thenAsyncException() {
        logger.info("thenAsync start");
        CompletableFuture cf = new CompletableFutureThread().thenCombineException();
        logger.info("thenAsync end");
        logger.info(cf.join().toString());
        if("Error".equals(cf.join().toString())){
            logger.error("error : ",cf);
        }
    }

    @Test
    public void thenCombineAcceptEitherTest(){
        logger.info("thenAsync start");
        CompletableFuture cf = new CompletableFutureThread().thenCombineAcceptEither();
        logger.info("thenAsync end");
        logger.info(cf.join().toString());
        if("Error".equals(cf.join().toString())){
            logger.error("error : ",cf);
        }
    }

    @Test
    public void thenCombineAcceptEitherSelfTest(){
        logger.info("thenAsync start");
        CompletableFuture cf = new CompletableFutureThread().thenCombineAcceptEitherSelf();
        logger.info("thenAsync end");
        logger.info(cf.join().toString());
        if("Error".equals(cf.join().toString())){
            logger.error("error : ",cf);
        }
    }

    @Test
    public void consumerTest(){
        logger.info("thenAsync start");
        // 声明一个容量为10的缓存队列
        BlockingQueue<SpartVO> queue = new LinkedBlockingQueue<>(10);
        CompletableFuture cf = new CompletableFutureConsumer(queue).consumer();
        logger.info("thenAsync end");
        logger.info(cf.join().toString());
        if("Error".equals(cf.join().toString())){
            logger.error("error : ",cf);
        }
    }

    @Test
    public void allOfAsyncExampleTest() throws InterruptedException {
        logger.info("thenAsync start");
        // 声明一个容量为10的缓存队列
        BlockingQueue<SpartVO> queue = new LinkedBlockingQueue<>(10);
        CompletableFutureConsumer consumer = new CompletableFutureConsumer(queue);
        CompletableFuture cf = consumer.allOfAsyncExample();
        Thread.sleep(500);
        consumer.stop();
        logger.info("thenAsync end");
        logger.info("allOfAsyncExampleTest : "+cf.join().toString());
        if("Error".equals(cf.join().toString())){
            logger.error("error : ",cf);
        }
    }

    @Test
    public void thenComposeConsumerTest() throws InterruptedException {
        logger.info("thenAsync start");
        // 声明一个容量为10的缓存队列
        BlockingQueue<SpartVO> queue = new LinkedBlockingQueue<>(20);
        CompletableFutureConsumer consumer = new CompletableFutureConsumer(queue);
        CompletableFuture cf = consumer.thenComposeConsumer();
        for( int i = 0 ; i < 20 ; i++ ){
            //logger.info("product input : "+i);
            //Thread.sleep(50);
            SpartVO spart = new SpartVO();
            spart.setTgtBoqId(String.valueOf(i));
            if (cf.isDone()) {
                System.out.println("cf.isDone");
                break;
            }
            System.out.println("cf.is not Done");
            if (!queue.offer(spart, 2, TimeUnit.SECONDS)) {
                System.out.println("放入数据失败：" + spart);
            }
            Thread.sleep(20);
            if( 15 == i ){
                consumer.stop();
            }
        }
        Thread.sleep(100);



        logger.info("thenAsync end");
        logger.info("allOfAsyncExampleTest : "+cf.join().toString());
        if("Error".equals(cf.join().toString())){
            logger.error("error : ",cf);
        }
    }

}
