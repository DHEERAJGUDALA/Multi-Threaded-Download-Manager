package com.downloadmanager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolDemo {
    public static void main(String[] args){
        System.out.println("Thread Pool Demo");
        System.out.println();

        ExecutorService pool = Executors.newFixedThreadPool(2);

        for(int i=0;i<=5;i++){
            SimpleTask task = new SimpleTask("Task-"+i);
            pool.submit(task);
            System.out.println("Submitted Task-"+i);
        }
        System.out.println();
        System.out.println("All 5 tasks submitted , only 2 run at a time");
        System.out.println("Waiting for all to finish");
        System.out.println();

        pool.shutdown();

        try{
            pool.awaitTermination(30,TimeUnit.SECONDS);
        }catch(InterruptedException e){
            System.out.println("Interrupted while waiting");
        }
        System.out.println();
    }
}
