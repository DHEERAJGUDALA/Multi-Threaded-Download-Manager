package com.downloadmanager;

public class SimpleTask implements Runnable {
    private final String taskName;

    public SimpleTask(String taskName){
        this.taskName=taskName;
    }
    @Override
    public void run(){
        String threadname=Thread.currentThread().getName();
        System.out.println("["+taskName+"] Started on thread:"+threadname);
        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            System.out.println("["+taskName+"] Was interrupted !");
        }
        System.out.println("["+taskName+"] Finished");
    }
}
