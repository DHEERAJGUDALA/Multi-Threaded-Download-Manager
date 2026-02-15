package com.downloadmanager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DownloadManager {

    private final ExecutorService executor;
    private final List<DownloadTask> tasks = new ArrayList<>();
    private final List<Future<?>> futures = new ArrayList<>();
    private final Path downloadDirectory;
    private int taskCounter = 0;

    public DownloadManager(int maxConcurrentDownloads, Path downloadDirectory) {
        this.executor = Executors.newFixedThreadPool(maxConcurrentDownloads);
        this.downloadDirectory = downloadDirectory;
    }

    public DownloadTask addDownload(String url) {
        taskCounter++;
        String fileName = extractFileName(url);
        Path filePath = downloadDirectory.resolve(fileName);

        DownloadTask task = new DownloadTask(taskCounter, url, filePath);
        tasks.add(task);
        return task;
    }

    public void startAll() {
        for (DownloadTask task : tasks) {
            Future<?> future = executor.submit(task);
            futures.add(future);
        }
    }

    public void startProgressMonitor() {
        ProgressTracker tracker = new ProgressTracker(tasks, 500);
        Thread trackerThread = new Thread(tracker, "Progress-Monitor");
        trackerThread.setDaemon(true);
        trackerThread.start();
    }

    public void waitForAll() {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                // Task failure already handled inside DownloadTask
            }
        }
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.out.println("Force shutting down...");
                executor.shutdownNow();
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.out.println("Executor did not terminate.");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void cancelAll() {
        for (Future<?> future : futures) {
            future.cancel(true);
        }
    }

    public List<DownloadTask> getTasks() {
        return tasks;
    }

    public void printSummary() {
        System.out.println("\n========== Download Summary ==========");
        int completed = 0, failed = 0, cancelled = 0;

        for (DownloadTask task : tasks) {
            System.out.printf("[Task %d] %s - %s%n",
                    task.getTaskId(),
                    task.getStatus().getDisplayName(),
                    task.getFileUrl());

            if (task.getErrorMessage() != null) {
                System.out.printf("         Error: %s%n", task.getErrorMessage());
            }

            switch (task.getStatus()) {
                case COMPLETED: completed++; break;
                case FAILED:    failed++;    break;
                case CANCELLED: cancelled++; break;
                default: break;
            }
        }

        System.out.println("======================================");
        System.out.printf("Total: %d | Completed: %d | Failed: %d | Cancelled: %d%n",
                tasks.size(), completed, failed, cancelled);
    }

    private String extractFileName(String url) {
        String path = url.substring(url.lastIndexOf('/') + 1);
        if (path.isEmpty()) {
            return "download_" + taskCounter;
        }
        int queryIndex = path.indexOf('?');
        if (queryIndex > 0) {
            path = path.substring(0, queryIndex);
        }
        return path;
    }
}