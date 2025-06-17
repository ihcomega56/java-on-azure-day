package com.example.ticketreservation.demo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Virtual Threads性能デモクラス
 * Java 21のVirtual Threadsの効果を実証
 */
public class VirtualThreadsDemo {
    
    public static void main(String[] args) {
        System.out.println("🚀 Virtual Threads性能デモを開始します...");
        
        // Virtual Thread Executorの作成
        ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
        
        // 従来のThread Pool Executor（比較用）
        ExecutorService platformExecutor = Executors.newFixedThreadPool(100);
        
        int taskCount = 1000; // タスク数
        
        System.out.println("\n📊 " + taskCount + "個のタスクを実行します");
        
        // Virtual Threadsでの実行時間測定
        long virtualStart = System.currentTimeMillis();
        CompletableFuture<Void>[] virtualTasks = IntStream.range(0, taskCount)
            .mapToObj(i -> CompletableFuture.runAsync(() -> {
                try {
                    // I/O待機をシミュレート（メール送信やDB処理など）
                    Thread.sleep(10);
                    
                    // 現在のスレッドがVirtual Threadかどうかを確認
                    Thread currentThread = Thread.currentThread();
                    if (currentThread.isVirtual()) {
                        System.out.printf("✅ タスク%d: Virtual Thread [%s]\n", 
                            i, currentThread.toString());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, virtualExecutor))
            .toArray(CompletableFuture[]::new);
        
        CompletableFuture.allOf(virtualTasks).join();
        long virtualEnd = System.currentTimeMillis();
        
        // Platform Threadsでの実行時間測定
        long platformStart = System.currentTimeMillis();
        CompletableFuture<Void>[] platformTasks = IntStream.range(0, taskCount)
            .mapToObj(i -> CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, platformExecutor))
            .toArray(CompletableFuture[]::new);
        
        CompletableFuture.allOf(platformTasks).join();
        long platformEnd = System.currentTimeMillis();
        
        // 結果の表示
        long virtualTime = virtualEnd - virtualStart;
        long platformTime = platformEnd - platformStart;
        
        System.out.println("\n📈 パフォーマンス比較結果:");
        System.out.println("⚡ Virtual Threads実行時間: " + virtualTime + "ms");
        System.out.println("🐌 Platform Threads実行時間: " + platformTime + "ms");
        System.out.println("📊 性能向上率: " + 
            String.format("%.1f%%", ((double)(platformTime - virtualTime) / platformTime) * 100));
        
        virtualExecutor.shutdown();
        platformExecutor.shutdown();
        
        System.out.println("\n✨ Virtual Threadsデモ完了！");
        System.out.println("💡 Virtual Threadsは軽量で高性能な並行処理を実現します");
    }
}