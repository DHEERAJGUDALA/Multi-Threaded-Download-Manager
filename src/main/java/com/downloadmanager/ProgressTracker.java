package com.downloadmanager;

import java.util.List;

public class ProgressTracker implements Runnable {
    private final List<DownloadTask>tasks;
    private final long updateIntervalMs;
    public ProgressTracker(List<DownloadTask>tasks,long updateIntervalMs){
        this.tasks=tasks;
        this.updateIntervalMs=updateIntervalMs;
    }
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            printProgress();
            if (allTasksFinished()) {
                printProgress();
                break;
            }
            try {
                Thread.sleep(updateIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("\n[ProgressTracker] Monitoring stopped.");
    }
        private void printProgress(){
            System.out.println("\n--- Download Progress ---");
            for(DownloadTask task:tasks){
                String bar = buildProgressBar(task.getProgress(),30);
                System.out.printf("[Task %d] %s %5.1f%% | %s / %s | %s%n",
                        task.getTaskId(),
                        bar,
                        task.getProgress(),
                        formatSize(task.getBytesDownloaded()),
                        formatSize(task.getTotalBytes()),
                        task.getStatus().getDisplayName());
            }
        }

        private String buildProgressBar(double percent,int width){
            int filled = (int) (percent/100*width);
            int empty=width-filled;
            StringBuilder sb=new StringBuilder("[");
            for(int i=0;i<filled;i++)sb.append('#');
            for(int i=0;i<empty;i++)sb.append('-');
            sb.append(']');
            return sb.toString();
        }

        private boolean allTasksFinished(){
            for(DownloadTask task : tasks){
                DownloadStatus status=task.getStatus();
                if(status==DownloadStatus.WAITING||status==DownloadStatus.DOWNLOADING){
                    return false;
                }
            }
            return true;
        }
        private String formatSize(long bytes){
            if(bytes<0)return "???";
            if(bytes <1024) return bytes + " B";
            if(bytes<1024*1024)return String.format("%.1f KB",bytes/1024.0);
            if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
            return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
        }
}
