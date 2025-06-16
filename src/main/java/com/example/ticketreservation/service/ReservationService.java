package com.example.ticketreservation.service;

import com.example.ticketreservation.dao.ReservationDAO;
import com.example.ticketreservation.exception.SoldOutException;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class ReservationService {

    @Autowired
    private ReservationDAO reservationDAO;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private EmailService emailService;
    
    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {
        return reservationDAO.getAllReservations();
    }
    
    @Transactional(readOnly = true)
    public Reservation getReservationById(Long id) {
        return reservationDAO.getReservationById(id);
    }
    
    @Transactional(readOnly = true)
    public Reservation getReservationByConfirmationCode(String confirmationCode) {
        return reservationDAO.getReservationByConfirmationCode(confirmationCode);
    }
    
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByEmail(String email) {
        return reservationDAO.getReservationsByEmail(email);
    }
    
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByEventId(Long eventId) {
        return reservationDAO.getReservationsByEventId(eventId);
    }
    
    public Reservation reserveTicket(Long eventId, String email, Integer quantity) throws SoldOutException {
        Event event = eventService.getEventById(eventId);
        
        if (event == null) {
            throw new SoldOutException("指定されたイベントが見つかりません");
        }
        
        if (event.getAvailableSeats() < quantity) {
            throw new SoldOutException("チケットが売り切れています。利用可能席数: " + event.getAvailableSeats());
        }
        
        // 確認コード生成
        String confirmationCode = generateConfirmationCode();
        
        // 予約作成
        Reservation reservation = new Reservation(event, email, quantity, confirmationCode);
        
        // 予約保存
        Long reservationId = reservationDAO.saveReservation(reservation);
        
        // 利用可能席数を減少
        eventService.reduceAvailableSeats(eventId, quantity);
        
        return reservationDAO.getReservationById(reservationId);
    }
    
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationDAO.getReservationById(reservationId);
        
        if (reservation != null) {
            // 利用可能席数を増加
            eventService.increaseAvailableSeats(reservation.getEvent().getId(), reservation.getQuantity());
            
            // 予約ステータスをキャンセルに変更
            reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
            reservationDAO.updateReservation(reservation);
            
            // キャンセル通知メールを非同期で送信
            emailService.sendCancellationEmailAsync(reservation.getEmail(), reservation.getConfirmationCode());
        }
    }
    
    private String generateConfirmationCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * 非同期でチケット予約を処理
     * 将来的なVirtual Threads対応を想定した実装
     * 
     * @param eventId イベントID
     * @param email メールアドレス
     * @param quantity 予約数量
     * @return 予約結果のCompletableFuture
     */
    @Async("taskExecutor")
    public CompletableFuture<Reservation> reserveTicketAsync(Long eventId, String email, Integer quantity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return reserveTicket(eventId, email, quantity);
            } catch (SoldOutException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * 予約処理とメール送信を非同期で統合実行
     * メール送信は予約完了後にバックグラウンドで実行
     * 
     * @param eventId イベントID
     * @param email メールアドレス
     * @param quantity 予約数量
     * @return 予約結果とメール送信完了のCompletableFuture
     */
    public CompletableFuture<Reservation> reserveTicketWithEmailAsync(Long eventId, String email, Integer quantity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 予約処理を実行
                Reservation reservation = reserveTicket(eventId, email, quantity);
                
                // メール送信を非同期で実行（結果を待たない）
                emailService.sendConfirmationEmailAsync(email, reservation.getConfirmationCode());
                
                return reservation;
            } catch (SoldOutException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
