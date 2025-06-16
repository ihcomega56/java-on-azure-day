package com.example.ticketreservation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * メール送信サービス
 * 非同期でのメール送信処理を提供
 */
@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    /**
     * 確認メールを非同期で送信
     * 
     * @param email 送信先メールアドレス
     * @param confirmationCode 確認コード
     * @return 送信完了のCompletableFuture
     */
    @Async("taskExecutor")
    public CompletableFuture<Void> sendConfirmationEmailAsync(String email, String confirmationCode) {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("メール送信開始: {} 宛て確認コード: {}", email, confirmationCode);
                
                // メール送信の擬似処理（実際のメール送信ロジックをここに実装）
                Thread.sleep(1000); // 1秒のメール送信処理時間をシミュレート
                
                logger.info("メール送信完了: {} 宛て", email);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("メール送信中にエラーが発生しました: {}", e.getMessage());
                throw new RuntimeException("メール送信に失敗しました", e);
            }
        });
    }
    
    /**
     * キャンセル通知メールを非同期で送信
     * 
     * @param email 送信先メールアドレス
     * @param confirmationCode 確認コード
     * @return 送信完了のCompletableFuture
     */
    @Async("taskExecutor")
    public CompletableFuture<Void> sendCancellationEmailAsync(String email, String confirmationCode) {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("キャンセル通知メール送信開始: {} 宛て確認コード: {}", email, confirmationCode);
                
                // メール送信の擬似処理
                Thread.sleep(800); // 800msのメール送信処理時間をシミュレート
                
                logger.info("キャンセル通知メール送信完了: {} 宛て", email);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("キャンセル通知メール送信中にエラーが発生しました: {}", e.getMessage());
                throw new RuntimeException("キャンセル通知メール送信に失敗しました", e);
            }
        });
    }
}