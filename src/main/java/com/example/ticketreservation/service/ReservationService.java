package com.example.ticketreservation.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.ticketreservation.dao.ReservationRepository;
import com.example.ticketreservation.exception.SoldOutException;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Reservation;

/**
 * 予約管理サービス
 * Spring Boot 3.2 + JPA対応版
 */
@Service
@Transactional
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private EventService eventService;
    
    /**
     * 全ての予約を取得
     */
    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    
    /**
     * IDで予約を取得
     */
    @Transactional(readOnly = true)
    public Reservation getReservationById(Long id) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        return reservation.orElse(null);
    }
    
    /**
     * 確認コードで予約を取得
     */
    @Transactional(readOnly = true)
    public Reservation getReservationByConfirmationCode(String confirmationCode) {
        return reservationRepository.findByConfirmationCode(confirmationCode);
    }
    
    /**
     * メールアドレスで予約を取得
     */
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByEmail(String email) {
        return reservationRepository.findByEmailOrderByReservationTimeDesc(email);
    }
    
    /**
     * チケットを予約する
     * 
     * @param eventId イベントID
     * @param email メールアドレス
     * @param quantity 予約数
     * @return 予約情報
     * @throws SoldOutException 売り切れの場合
     */
    public Reservation reserveTicket(Long eventId, String email, Integer quantity) throws SoldOutException {
        // イベントの存在確認と空席確認
        Event event = eventService.getEventById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("指定されたイベントが存在しません");
        }
        
        if (event.getAvailableSeats() < quantity) {
            throw new SoldOutException("申し訳ございません。ご希望の席数が確保できません。");
        }
        
        // 予約作成
        Reservation reservation = new Reservation();
        reservation.setEvent(event);
        reservation.setEmail(email);
        reservation.setQuantity(quantity);
        reservation.setTotalPrice(event.getPrice() * quantity);
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setConfirmationCode(generateConfirmationCode());
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        
        // 予約保存
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // 利用可能座席数を減らす
        eventService.reduceAvailableSeats(eventId, quantity);
        
        return savedReservation;
    }

    /**
     * チケット予約の非同期処理（Virtual Threads活用）
     * 高負荷時でも軽量なVirtual Threadsでスケーラビリティを向上
     * 
     * @param eventId イベントID
     * @param email メールアドレス
     * @param quantity 予約数
     * @return 予約情報のCompletableFuture
     */
    @Async("taskExecutor")
    public CompletableFuture<Reservation> reserveTicketAsync(Long eventId, String email, Integer quantity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Event event = eventService.getEventById(eventId);
                if (event == null) {
                    throw new IllegalArgumentException("指定されたイベントが存在しません");
                }
                
                if (event.getAvailableSeats() < quantity) {
                    throw new SoldOutException("申し訳ございません。ご希望の席数が確保できません。");
                }
                
                // 予約作成
                Reservation reservation = new Reservation();
                reservation.setEvent(event);
                reservation.setEmail(email);
                reservation.setQuantity(quantity);
                reservation.setTotalPrice(event.getPrice() * quantity);
                reservation.setReservationTime(LocalDateTime.now());
                reservation.setConfirmationCode(generateConfirmationCode());
                reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
                
                // 予約保存
                Reservation savedReservation = reservationRepository.save(reservation);
                
                // 利用可能座席数を減らす
                eventService.reduceAvailableSeats(eventId, quantity);
                
                return savedReservation;
            } catch (Exception e) {
                throw new RuntimeException("予約処理中にエラーが発生しました: " + e.getMessage(), e);
            }
        }, Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * 確認メール送信の非同期処理（Virtual Threads活用）
     * I/O待機時間の長いメール送信処理を非同期化してレスポンス性能向上
     * 
     * @param email 送信先メールアドレス
     * @param confirmationCode 確認コード
     * @return 送信完了のCompletableFuture
     */
    @Async("taskExecutor")
    public CompletableFuture<Void> sendConfirmationEmailAsync(String email, String confirmationCode) {
        return CompletableFuture.runAsync(() -> {
            try {
                // メール送信処理（実際の実装では外部SMTPサーバーとの通信）
                System.out.println("📧 メール送信開始: " + email + " (確認コード: " + confirmationCode + ")");
                
                // メール送信の擬似処理（実際にはSMTP処理が入る）
                Thread.sleep(1000); // ネットワークI/O待機をシミュレート
                
                System.out.println("✅ メール送信完了: " + email);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("メール送信処理が中断されました", e);
            } catch (Exception e) {
                System.err.println("❌ メール送信失敗: " + email + " - " + e.getMessage());
                // 実際の実装では、メール送信失敗をログに記録し、必要に応じて再送処理を行う
            }
        }, Executors.newVirtualThreadPerTaskExecutor());
    }
    
    /**
     * 予約をキャンセルする
     * 
     * @param confirmationCode 確認コード
     * @return キャンセルされた予約情報
     */
    public Reservation cancelReservation(String confirmationCode) {
        Reservation reservation = reservationRepository.findByConfirmationCode(confirmationCode);
        if (reservation == null) {
            throw new IllegalArgumentException("指定された確認コードの予約が見つかりません");
        }
        
        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new IllegalStateException("この予約は既にキャンセルされています");
        }
        
        // ステータスをキャンセルに変更
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        Reservation cancelledReservation = reservationRepository.save(reservation);
        
        // 利用可能座席数を戻す
        eventService.increaseAvailableSeats(reservation.getEvent().getId(), reservation.getQuantity());
        
        return cancelledReservation;
    }
    
    /**
     * 確認コードを生成する（8桁のランダム文字列）
     */
    private String generateConfirmationCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
