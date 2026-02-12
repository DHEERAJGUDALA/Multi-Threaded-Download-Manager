package com.downloadmanager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ThreadDemo {
    public static void main(String []args){
        System.out.println("=== Download Task Test ===");
        System.out.println("Thread: " + Thread.currentThread().getName());
        System.out.println();

        Path downloadDir = Paths.get("downloads");
        try{
            Files.createDirectories(downloadDir);
        }catch(Exception e){
            System.out.println("Failed to create download directory: " + e.getMessage());
            return;
        }

        DownloadTask task=new DownloadTask(
          1,
                "https://raw.githubusercontent.com/dwyl/english-words/master/words_alpha.txt",
          downloadDir.resolve("test-file.txt")
        );

        Thread downloadThread = new Thread(task, "Downloader-1");
        downloadThread.start();

        try{
            downloadThread.join();
        }catch(InterruptedException e){
            System.out.println("Main Thread Interrupted!");
            Thread.currentThread().interrupt();
        }

        System.out.println();
        System.out.println("=== Results ===");
        System.out.printf("Status:     %s%n",
                task.getStatus().getDisplayName());
        System.out.printf("Downloaded: %d bytes%n",
                task.getBytesDownloaded());
        System.out.printf("Total:      %d bytes%n", task.getTotalBytes());
        System.out.printf("Progress:   %.1f%%%n", task.getProgress());

        if (task.getErrorMessage() != null) {
            System.out.println("Error: " + task.getErrorMessage());
        }
    }
}