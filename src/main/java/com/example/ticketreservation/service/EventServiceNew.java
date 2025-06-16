package com.example.ticketreservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
}
