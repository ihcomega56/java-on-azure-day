package com.example.ticketreservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.ticketreservation.dao.EventRepository;
import com.example.ticketreservation.model.Event;

/**
 * イベント管理サービス
 * Spring Boot 3.2 + JPA対応版
 */
@Service
@Transactional
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    
    /**
     * 全てのイベントを取得
     */
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    
    /**
     * 利用可能なイベントを取得（未来の日付で席が空いているもの）
     */
    @Transactional(readOnly = true)
    public List<Event> getAvailableEvents() {
        return eventRepository.findAvailableEvents(LocalDateTime.now());
    }
    
    /**
     * カテゴリ別でイベントを取得
     */
    @Transactional(readOnly = true)
    public List<Event> getEventsByCategory(String category) {
        return eventRepository.findEventsByCategory(category, LocalDateTime.now());
    }
    
    /**
     * 指定日付以降のイベントを取得
     */
    @Transactional(readOnly = true)
    public List<Event> getEventsFromDate(LocalDate fromDate) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        return eventRepository.findAvailableEvents(fromDateTime);
    }
    
    /**
     * カテゴリと日付でイベントを取得
     */
    @Transactional(readOnly = true)
    public List<Event> getEventsByCategoryFromDate(String category, LocalDate fromDate) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        return eventRepository.findEventsByCategory(category, fromDateTime);
    }
    
    /**
     * IDでイベントを取得
     */
    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        Optional<Event> event = eventRepository.findById(id);
        return event.orElse(null);
    }
    
    /**
     * イベントを保存
     */
    public Long saveEvent(Event event) {
        Event savedEvent = eventRepository.save(event);
        return savedEvent.getId();
    }
    
    /**
     * イベントを更新
     */
    public void updateEvent(Event event) {
        eventRepository.save(event);
    }
    
    /**
     * イベントを削除
     */
    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }
    
    /**
     * 利用可能座席数を減らす（予約時）
     */
    public void reduceAvailableSeats(Long eventId, int quantity) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            event.setAvailableSeats(event.getAvailableSeats() - quantity);
            eventRepository.save(event);
        }
    }
    
    /**
     * 利用可能座席数を増やす（キャンセル時）
     */
    public void increaseAvailableSeats(Long eventId, int quantity) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            event.setAvailableSeats(event.getAvailableSeats() + quantity);
            eventRepository.save(event);
        }
    }

    /**
     * イベント検索の非同期処理（Virtual Threads活用）
     * 複雑な検索条件でもVirtual Threadsにより高速に並列処理
     * 
     * @param category カテゴリ（null可）
     * @param fromDate 開始日（null可）
     * @return イベントリストのCompletableFuture
     */
    @Async("taskExecutor")
    public CompletableFuture<List<Event>> searchEventsAsync(String category, LocalDate fromDate) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (category != null && !category.isEmpty() && fromDate != null) {
                    // カテゴリと日付の両方が指定された場合
                    LocalDateTime fromDateTime = fromDate.atStartOfDay();
                    return eventRepository.findEventsByCategory(category, fromDateTime);
                } else if (category != null && !category.isEmpty()) {
                    // カテゴリのみ指定された場合
                    return eventRepository.findEventsByCategory(category, LocalDateTime.now());
                } else if (fromDate != null) {
                    // 日付のみ指定された場合
                    LocalDateTime fromDateTime = fromDate.atStartOfDay();
                    return eventRepository.findAvailableEvents(fromDateTime);
                } else {
                    // 両方とも指定されていない場合
                    return eventRepository.findAvailableEvents(LocalDateTime.now());
                }
            } catch (Exception e) {
                throw new RuntimeException("イベント検索中にエラーが発生しました: " + e.getMessage(), e);
            }
        }, Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * 複数条件でのイベント並列検索（Virtual Threads活用）
     * 複数の検索条件を並列実行して結果をマージ
     * 
     * @param categories 検索対象カテゴリリスト
     * @param fromDate 開始日
     * @return 検索結果をマージしたイベントリストのCompletableFuture
     */
    @Async("taskExecutor")
    public CompletableFuture<List<Event>> searchEventsByMultipleCategoriesAsync(List<String> categories, LocalDate fromDate) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 各カテゴリでの検索を並列実行
                List<CompletableFuture<List<Event>>> futures = categories.stream()
                    .map(category -> searchEventsAsync(category, fromDate))
                    .toList();
                
                // 全ての検索結果を待機してマージ
                List<Event> allEvents = futures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .distinct()  // 重複除去
                    .toList();
                
                return allEvents;
            } catch (Exception e) {
                throw new RuntimeException("複数条件イベント検索中にエラーが発生しました: " + e.getMessage(), e);
            }
        }, Executors.newVirtualThreadPerTaskExecutor());
    }
}
