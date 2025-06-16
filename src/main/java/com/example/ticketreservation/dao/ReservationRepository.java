package com.example.ticketreservation.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.ticketreservation.model.Reservation;

/**
 * 予約エンティティ用のSpring Data JPAリポジトリ
 * Spring Boot 3.2 + JPA対応
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * 確認コードで予約を検索
     * 
     * @param confirmationCode 確認コード
     * @return 予約情報
     */
    Reservation findByConfirmationCode(String confirmationCode);

    /**
     * メールアドレスで予約を検索
     * 
     * @param email メールアドレス
     * @return 該当メールアドレスの予約リスト
     */
    List<Reservation> findByEmailOrderByReservationTimeDesc(String email);

    /**
     * イベントIDで予約を検索
     * 
     * @param eventId イベントID
     * @return 該当イベントの予約リスト
     */
    @Query("FROM Reservation WHERE event.id = :eventId ORDER BY reservationTime")
    List<Reservation> findByEventId(@Param("eventId") Long eventId);

    /**
     * 指定したイベントの予約済み席数を取得
     * 
     * @param eventId イベントID
     * @return 予約済み席数
     */
    @Query("SELECT COALESCE(SUM(r.quantity), 0) FROM Reservation r WHERE r.event.id = :eventId AND r.status = 'CONFIRMED'")
    Integer countReservedSeatsByEventId(@Param("eventId") Long eventId);

    /**
     * アクティブな予約（CONFIRMED, PENDING）を取得
     * 
     * @return アクティブな予約リスト
     */
    @Query("FROM Reservation WHERE status IN ('CONFIRMED', 'PENDING') ORDER BY reservationTime DESC")
    List<Reservation> findActiveReservations();
}
