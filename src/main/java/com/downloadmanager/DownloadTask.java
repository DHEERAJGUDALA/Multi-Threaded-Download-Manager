package com.downloadmanager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

public class DownloadTask implements Runnable {
    private final String fileUrl;
    private final Path downloadPath;
    private final int taskId;

    private final AtomicLong bytesDownloaded = new AtomicLong(0);
    private long totalBytes = -1;
    private volatile DownloadStatus status = DownloadStatus.WAITING;
    private volatile String errorMessage = null;

    private static final int BUFFER_SIZE=8192;

    public DownloadTask(int taskId,String fileUrl,Path downloadPath){
        this.taskId=taskId;
        this.fileUrl=fileUrl;
        this.downloadPath=downloadPath;
    }

    @Override
    public void run(){
        String threadName = Thread.currentThread().getName();
        System.out.printf("[Task %d] Starting on thread : %s%n", taskId,threadName);
        System.out.printf("[Task %d] URL : %s%n", taskId,fileUrl);
        status = DownloadStatus.DOWNLOADING;
    try{
        URL url=new URL(fileUrl);
        HttpURLConnection connection=(HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(30000);
        connection.setRequestProperty("User-Agent","JavaDownloadManager/1.0");

        int responseCode = connection.getResponseCode();
        if(responseCode!= HttpURLConnection.HTTP_OK){
            throw new IOException("Server returned HTTP "+ responseCode);
        }
        totalBytes = connection.getContentLengthLong();
        System.out.printf("[Task %d] File size: %s%n", taskId,formatFileSize(totalBytes));

        try(InputStream inputStream=connection.getInputStream();
         FileOutputStream outputStream=new FileOutputStream(downloadPath.toFile())){
             byte[] buffer = new byte[BUFFER_SIZE];
             int bytesRead;

             while((bytesRead=inputStream.read(buffer))!=-1 && !Thread.currentThread().isInterrupted()){
                 outputStream.write(buffer, 0 , bytesRead);
                 bytesDownloaded.addAndGet(bytesRead);
             }
                 if(Thread.currentThread().isInterrupted()){
                     status= DownloadStatus.CANCELLED;
                     System.out.printf("[Task %d] Download cancelled!%n" ,  taskId);
                 }else{
                     status = DownloadStatus.COMPLETED;
                     System.out.printf("[Task %d] Completed! Saved: %s%n",taskId, downloadPath.getFileName());
                 }
            }
         }catch(IOException e){
             status = DownloadStatus.FAILED;
             errorMessage=e.getMessage();
            System.out.printf("[Task %d] FAILED: %s%n", taskId,errorMessage);
        }
    }

    public int getTaskId(){return taskId;}
    public String getFileUrl(){return fileUrl;}
    public DownloadStatus getStatus(){return status;}
    public String getErrorMessage(){return errorMessage;}
    public long getTotalBytes(){return totalBytes;}
    public long getBytesDownloaded(){return bytesDownloaded.get();}
    public double getProgress(){
        if(totalBytes<=0)return 0;
        return (double) bytesDownloaded.get()/totalBytes*100;
        }

    private String formatFileSize(long bytes){
        if(bytes<0)return "Unknown";
        if(bytes<1024)return bytes+ " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes /1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes/ (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
        }
}
