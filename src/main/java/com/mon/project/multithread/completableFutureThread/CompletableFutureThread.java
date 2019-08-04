package com.mon.project.multithread.completableFutureThread;


import com.mon.project.multithread.ExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureThread {
    private static final Logger logger = LoggerFactory.getLogger(CompletableFutureThread.class);

    //Async结尾的方法都是可以异步执行的
    //结合两个CompletionStage的结果，进行转化后返回
    public CompletableFuture thenCombineException() {
        logger.info("thenCombineException start");
        CompletableFuture cf = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            logger.info("hello");
            return "hello";
        },ExecutorFactory.executor).thenCombine(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //int i = 7/0;
            logger.info("world");
            return "world";
        },ExecutorFactory.executor), (s1, s2) -> {
            s1 = s1 + " " + s2;
            printWords(s1);
            return "Sucecess";
        }).exceptionally(e -> {
            logger.info("thenAsync exception : ",e);
            return "Error";
        });
        logger.info("thenCombineException end");
        return cf;
    }

    private void printWords(String words){
        logger.info("words : "+words);
    }


}
