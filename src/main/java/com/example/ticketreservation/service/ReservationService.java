package com.example.ticketreservation.service;

import com.example.ticketreservation.dao.ReservationRepository;
import com.example.ticketreservation.exception.SoldOutException;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        return reservationRepository.findByEmailOrderByReservationDateDesc(email);
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
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setConfirmationCode(generateConfirmationCode());
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        
        // 予約保存
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // 利用可能座席数を減らす
        eventService.reduceAvailableSeats(eventId, quantity);
        
        return savedReservation;
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
