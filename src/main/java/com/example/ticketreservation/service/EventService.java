package com.example.ticketreservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticketreservation.dao.EventDAO;
import com.example.ticketreservation.model.Event;

@Service
@Transactional
public class EventService {

    @Autowired
    private EventDAO eventDAO;
    
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventDAO.getAllEvents();
    }
    
    @Transactional(readOnly = true)
    public List<Event> getAvailableEvents() {
        return eventDAO.getAvailableEvents();
    }
    
    @Transactional(readOnly = true)
    public List<Event> getEventsByCategory(String category) {
        return eventDAO.getEventsByCategory(category);
    }
    
    @Transactional(readOnly = true)
    public List<Event> getEventsFromDate(LocalDate fromDate) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        return eventDAO.getEventsFromDate(fromDateTime);
    }
    
    @Transactional(readOnly = true)
    public List<Event> getEventsByCategoryFromDate(String category, LocalDate fromDate) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        return eventDAO.getEventsByCategoryFromDate(category, fromDateTime);
    }
    
    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        return eventDAO.getEventById(id);
    }
    
    public Long saveEvent(Event event) {
        return eventDAO.saveEvent(event);
    }
    
    public void updateEvent(Event event) {
        eventDAO.updateEvent(event);
    }
    
    public void deleteEvent(Event event) {
        eventDAO.deleteEvent(event);
    }
    
    public void reduceAvailableSeats(Long eventId, int quantity) {
        Event event = eventDAO.getEventById(eventId);
        if (event != null) {
            event.setAvailableSeats(event.getAvailableSeats() - quantity);
            eventDAO.updateEvent(event);
        }
    }
    
    public void increaseAvailableSeats(Long eventId, int quantity) {
        Event event = eventDAO.getEventById(eventId);
        if (event != null) {
            event.setAvailableSeats(event.getAvailableSeats() + quantity);
            eventDAO.updateEvent(event);
        }
    }
    
    /**
     * イベント検索を非同期で実行
     * 複雑な検索条件でも並行処理により高速化
     * 
     * @param category カテゴリ（null可）
     * @param fromDate 開始日（null可）
     * @return 検索結果のCompletableFuture
     */
    @Async("taskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<List<Event>> searchEventsAsync(String category, LocalDate fromDate) {
        return CompletableFuture.supplyAsync(() -> {
            if (category != null && !category.isEmpty() && fromDate != null) {
                return eventDAO.getEventsByCategoryFromDate(category, fromDate.atStartOfDay());
            } else if (category != null && !category.isEmpty()) {
                return eventDAO.getEventsByCategory(category);
            } else if (fromDate != null) {
                return eventDAO.getEventsFromDate(fromDate.atStartOfDay());
            } else {
                return eventDAO.getAvailableEvents();
            }
        });
    }
    
    /**
     * 利用可能なイベントを非同期で取得
     * 
     * @return 利用可能イベントのCompletableFuture
     */
    @Async("taskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<List<Event>> getAvailableEventsAsync() {
        return CompletableFuture.supplyAsync(() -> eventDAO.getAvailableEvents());
    }
}
