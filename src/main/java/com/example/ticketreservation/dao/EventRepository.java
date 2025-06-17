package com.example.ticketreservation.dao;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.ticketreservation.model.Event;

/**
 * イベントエンティティ用のSpring Data JPAリポジトリ
 * Spring Boot 3.2 + JPA対応
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * 利用可能なイベントを取得（日付が未来で席が空いているもの）
     * 
     * @param now 現在日時
     * @return 利用可能なイベントリスト
     */
    @Query("FROM Event WHERE eventDate > :now AND availableSeats > 0 ORDER BY eventDate")
    List<Event> findAvailableEvents(@Param("now") LocalDateTime now);

    /**
     * カテゴリ別でイベントを検索
     * 
     * @param category カテゴリ名
     * @return カテゴリに一致するイベントリスト
     */
    @Query("FROM Event WHERE category = :category AND eventDate > :now ORDER BY eventDate")
    List<Event> findEventsByCategory(@Param("category") String category, @Param("now") LocalDateTime now);

    /**
     * 日付範囲でイベントを検索
     * 
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @return 指定範囲内のイベントリスト
     */
    @Query("FROM Event WHERE eventDate BETWEEN :startDate AND :endDate ORDER BY eventDate")
    List<Event> findEventsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);

    /**
     * 会場名でイベントを検索
     * 
     * @param venue 会場名
     * @return 指定会場のイベントリスト
     */
    List<Event> findByVenueContainingIgnoreCaseOrderByEventDate(String venue);
}
