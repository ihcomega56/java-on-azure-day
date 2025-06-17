package com.example.ticketreservation.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 非同期処理設定クラス
 * Java 21のVirtual Threadsを活用した高性能な並行処理を実現
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Virtual Thread Executorを定義
     * 従来のプラットフォームスレッドと比較して軽量で高性能
     * - メモリ使用量: プラットフォームスレッド約2MB → Virtual Thread数KB
     * - スケーラビリティ: 数千〜数万の同時接続にも対応可能
     * - I/O待機時のリソース効率: 大幅に改善
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * レガシー互換用のVirtual Thread Executor
     * 既存コードからの移行時に使用
     */
    @Bean(name = "virtualThreadExecutor")
    public Executor virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}