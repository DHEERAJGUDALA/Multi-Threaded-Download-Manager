package com.downloadmanager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   Multi-threaded Download Manager v1.0");
        System.out.println("==========================================");
        System.out.println();

        Path downloadDir = Paths.get("downloads");
        try {
            Files.createDirectories(downloadDir);
        } catch (Exception e) {
            System.out.println("Failed to create download directory: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter number of concurrent downloads (1-5): ");
        int maxDownloads = Integer.parseInt(scanner.nextLine().trim());
        maxDownloads = Math.max(1, Math.min(5, maxDownloads));

        DownloadManager manager = new DownloadManager(maxDownloads, downloadDir);

        System.out.println("Enter download URLs (type 'start' to begin downloading):");
        System.out.println();

        while (true) {
            System.out.print("URL> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("start")) {
                break;
            }

            if (input.isEmpty()) {
                continue;
            }

            DownloadTask task = manager.addDownload(input);
            System.out.printf("  Added Task %d: %s%n",
                    task.getTaskId(), task.getFileUrl());
        }

        if (manager.getTasks().isEmpty()) {
            System.out.println("No downloads added. Exiting.");
            scanner.close();
            return;
        }

        System.out.println();
        System.out.printf("Starting %d download(s) with %d thread(s)...%n",
                manager.getTasks().size(), maxDownloads);
        System.out.println();

        manager.startAll();
        manager.startProgressMonitor();
        manager.waitForAll();
        manager.shutdown();

        manager.printSummary();

        scanner.close();
    }
}