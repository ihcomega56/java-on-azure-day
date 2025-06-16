package com.example.ticketreservation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 非同期処理設定クラス
 * Java 17環境での並行処理性能向上を目的とした設定
 * 
 * 将来的にJava 21環境でVirtual Threadsに対応する際の基盤として設計
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    
    /**
     * 非同期タスク実行用のExecutorを設定
     * 
     * Java 17環境では通常のThreadPoolを使用し、
     * 将来的にJava 21環境では以下のように変更予定：
     * return Executors.newVirtualThreadPerTaskExecutor();
     * 
     * @return 非同期処理用のExecutor
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        // Java 17環境でのスレッドプール設定
        // 高い並行性を実現するために多めのスレッド数を設定
        return Executors.newFixedThreadPool(50);
    }
}