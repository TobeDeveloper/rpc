package org.myan.io;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by myan on 2017/10/30.
 * Intellij IDEA
 */
public class PromiseTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                System.out.println("task["+finalI+"] started!");
                try {
                    // time cost
                    Thread.sleep(1000*(3-finalI));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("task["+finalI+"] finished");
                return "result["+finalI+"]";
            }, executor);
            future.thenAccept(System.out::println);
        }

        System.out.println("Main thread finished.");
        executor.shutdown();
    }

}
