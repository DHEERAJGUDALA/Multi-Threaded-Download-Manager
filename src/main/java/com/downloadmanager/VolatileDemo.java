package com.downloadmanager;

public class VolatileDemo {
    private static boolean taskDone = false;
    private static boolean taskDoneVolatile=false;

    public static void main(String[] args){
        System.out.println("Volatile Demo");
        System.out.print("Main thread will wait for worker to set flag");

        System.out.println("Test 1: WITHOUT volatile");
        System.out.println("Main thread will wait for worker to set flag...");

        Thread worker1=new Thread(()->{
            try{
                Thread.sleep(10);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
            taskDone=true;
            System.out.println("[Worker] Set taskDone = true");
        });
        worker1.start();

        int waitCount1=0;
        while(!taskDone){
            waitCount1++;
        }
        System.out.println("  [Main] Detected taskDone! Took " + waitCount1 + " checks");
        System.out.println();

        Thread worker2 = new Thread(()->{
            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
            taskDoneVolatile=true;
            System.out.println("  [Worker] Set taskDoneVolatile = true");
        });
        worker2.start();
        int waitCount2=0;

        while(!taskDoneVolatile){
            waitCount2++;
        }
        System.out.println("  [Main] Detected taskDoneVolatile! Took " + waitCount2 + "checks");
        System.out.println();

        System.out.println("=== Summary ===");
        System.out.println("Both worked THIS TIME, but without volatile:");
        System.out.println("  - JIT compiler might cache the value");
        System.out.println("  - Main thread could loop FOREVER");
        System.out.println("  - Bug appears randomly (worst kind!)");
        System.out.println("  - More likely in production (JIT optimizes more)");
        System.out.println();
        System.out.println("With volatile:");
        System.out.println("  - GUARANTEED to see the latest value");
        System.out.println("  - Never loops forever");
        System.out.println("  - Works in all environments");
    }
}
