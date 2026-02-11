package com.downloadmanager;

public class ThreadDemo {
    public static void main(String []args){
        String name = Thread.currentThread().getName();
        System.out.println("Main thread is: " + name);

        // Thread 1
        Thread t1 = new Thread(() -> {
            System.out.println("Thread 1 is running on: " + Thread.currentThread().getName());
        });

        // Thread 2
        Thread t2 = new Thread(() -> {
            System.out.println("Thread 2 is running on: " + Thread.currentThread().getName());
        });

        // Thread 3
        Thread t3 = new Thread(() -> {
            System.out.println("Thread 3 is running on: " + Thread.currentThread().getName());
        });

        // You MUST start all of them!
        t1.start();
        t2.start();
        t3.start();
    }
}