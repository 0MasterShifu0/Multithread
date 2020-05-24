package com.mon.project.multithread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ExecutorFactory {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorFactory.class);
   public static ExecutorService executor = Executors.newFixedThreadPool(4, new ThreadFactory() {
        int count = 4;

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "custom-executor-" + count++);
        }
    });

    private static ExecutorService executors ;

    public static synchronized ExecutorService executor(){
        if (null != executors && !executors.isShutdown() ) {
            return executors ;
        }
        logger.warn("new executors...");
        executors  = Executors.newFixedThreadPool(4, new ThreadFactory() {
            int count = 4;

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "custom-executor-" + count++);
            }
        });
        return executors ;
    }
}
