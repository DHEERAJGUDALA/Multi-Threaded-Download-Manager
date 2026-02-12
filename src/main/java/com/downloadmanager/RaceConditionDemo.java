package com.downloadmanager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class RaceConditionDemo {
    public static void main(String[] args){
        System.out.println("=== Race Condition Demo ===");
        System.out.println();
        long[] unsafeCounter={0};

        AtomicLong safeCounter=new AtomicLong(0);
         ExecutorService pool = Executors.newFixedThreadPool(10);

         for(int i=0;i<10;i++) {
             pool.submit(() -> {
                 for (int j = 0; j < 100_000; j++) {
                     unsafeCounter[0]++;
                     safeCounter.incrementAndGet();
                 }
             });
         }
             pool.shutdown();
             try {
                 if(!pool.awaitTermination(30, TimeUnit.SECONDS)){
                     System.out.println("Tasks didn't finish in time! Forcing Shutdown");
                     pool.shutdownNow();
                     if(!pool.awaitTermination(10,TimeUnit.SECONDS)){
                         System.err.println("Pool didn't Terminate");
                     }
                 }
             }catch(InterruptedException e){
               pool.shutdownNow();
               Thread.currentThread().interrupt();
             }
             System.out.println("Expected: 1,000,000");
             System.out.println("Unsafe Counter"+unsafeCounter[0]);
             System.out.println("Safe Counter:"+safeCounter.get());
             System.out.println();

             if(unsafeCounter[0]!=1_000_000){
                 System.out.println("Unsafe counter Lost"+(1_000_000-unsafeCounter[0])+"increments!");
                 System.out.println("This is a Race Conditon");
             }
             System.out.print("AtomicLong is correct!");
         }
    }
