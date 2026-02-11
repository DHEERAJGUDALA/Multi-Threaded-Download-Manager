package com.downloadmanager;

public class ThreadDemo {
    public static void main(String []args){
        System.out.println("Main thread:"+Thread.currentThread().getName());
        System.out.println();
        SimpleTask task1= new SimpleTask("Download-A");
        SimpleTask task2= new SimpleTask("Download-B");
        SimpleTask task3= new SimpleTask("Download-C");

        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);
        Thread thread3 = new Thread(task3);

        System.out.println("Starting all downloads");
        long startTime = System.currentTimeMillis();

        thread1.start();
        thread2.start();
        thread3.start();

        try{
            thread1.join();
            thread2.join();
            thread3.join();
        }catch(InterruptedException e){
            System.out.println("Main thread was interrupted!");
        }
        long endTime=System.currentTimeMillis();
        System.out.println();
        System.out.println("All downloads finished");
        System.out.println("Total time: " + (endTime-startTime)+"ms");
    }
}