package com.mon.project.multithread.completableFutureThread;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mon.project.multithread.ExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class CompletableFutureThread {
    private static final Logger logger = LoggerFactory.getLogger(CompletableFutureThread.class);

    //Async结尾的方法都是可以异步执行的
    //结合两个CompletionStage的结果，进行转化后返回
    public CompletableFuture thenCombineException() {
        logger.info("thenCombineException start");
        CompletableFuture cf = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            logger.info("hello");
            return "hello";
        },ExecutorFactory.executor).thenCombine(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(10000);
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


    //自定义超时异常
    public CompletableFuture thenCombineAcceptEitherSelf() {
        logger.info("thenCombineException start");
        CompletableFuture cf = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("hello");
            return "hello";
        },ExecutorFactory.executor).applyToEither(failAfter(Duration.ofSeconds(2)), Function.identity())
        .exceptionally(e -> {
            logger.info("thenAsync exception : ",e);
            return "Error";
        });
        cf.cancel(true);
        logger.info("thenCombineException end");
        return cf;
    }


    //加入超时异常处理
    public CompletableFuture thenCombineAcceptEither() {
        logger.info("thenCombineException start");
        CompletableFuture cf = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            logger.info("hello");
            return "hello";
        },ExecutorFactory.executor);
        within( cf, Duration.ofSeconds(2))
         .exceptionally(e -> {
            logger.info("thenAsync exception : ",e);
            return "Error";
        });
        logger.info("thenCombineException end");
        return cf;
    }

    public static <T> CompletableFuture<T> within(CompletableFuture<T> future, Duration duration) {
        final CompletableFuture<T> timeout = failAfter(duration);
        return future.applyToEither(timeout, Function.identity());
    }

    public static <T> CompletableFuture<T> failAfter(Duration duration) {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        scheduler.schedule(() -> {
            final TimeoutException ex = new TimeoutException("Timeout after " + duration);
            return promise.completeExceptionally(ex);
        }, duration.toMillis(), MILLISECONDS);
        return promise;
    }

    public static <T> CompletableFuture<T> failAfter(Duration duration, CompletableFuture<Integer> cf) {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        scheduler.schedule(() -> {
            final TimeoutException ex = new TimeoutException("Timeout after " + duration);
            cf.cancel(true);
            //cf.get(0,TimeUnit.SECONDS);
            cf.complete(2);
            logger.warn("cf complete");
            return promise.completeExceptionally(ex);
        }, duration.toMillis(), MILLISECONDS);
        return promise;
    }

    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(
                    1,
                    new ThreadFactoryBuilder()
                            .setDaemon(true)
                            .setNameFormat("failAfter-%d")
                            .build());
}
