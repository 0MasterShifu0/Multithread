package com.mon.project.multithread.cmpletableFutureThread;

import com.mon.project.multithread.completableFutureThread.CompletableFutureThread;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

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
}
