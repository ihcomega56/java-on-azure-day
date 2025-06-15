package com.example.ticketreservation.dao;

import com.example.ticketreservation.model.Event;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class EventDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    // すべてのイベントを取得
    @SuppressWarnings("unchecked")
    public List<Event> getAllEvents() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Event ORDER BY eventDate").list();
    }
    
    // 利用可能なイベントを取得（日付が未来のもの）
    @SuppressWarnings("unchecked")
    public List<Event> getAvailableEvents() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                "FROM Event WHERE eventDate > :now AND availableSeats > 0 ORDER BY eventDate")
                .setParameter("now", LocalDateTime.now())
                .list();
    }
    
    // カテゴリでイベントを検索
    @SuppressWarnings("unchecked")
    public List<Event> getEventsByCategory(String category) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "FROM Event WHERE category = :category AND eventDate > :now AND availableSeats > 0 ORDER BY eventDate");
        query.setParameter("category", category);
        query.setParameter("now", LocalDateTime.now());
        return query.list();
    }
    
    // 日付からイベントを検索
    @SuppressWarnings("unchecked")
    public List<Event> getEventsFromDate(LocalDateTime fromDate) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "FROM Event WHERE eventDate >= :fromDate AND availableSeats > 0 ORDER BY eventDate");
        query.setParameter("fromDate", fromDate);
        return query.list();
    }
    
    // カテゴリと日付からイベントを検索
    @SuppressWarnings("unchecked")
    public List<Event> getEventsByCategoryFromDate(String category, LocalDateTime fromDate) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "FROM Event WHERE category = :category AND eventDate >= :fromDate AND availableSeats > 0 ORDER BY eventDate");
        query.setParameter("category", category);
        query.setParameter("fromDate", fromDate);
        return query.list();
    }
    
    // IDでイベントを検索
    public Event getEventById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return (Event) session.get(Event.class, id);
    }
    
    // イベントを保存
    public Long saveEvent(Event event) {
        Session session = sessionFactory.getCurrentSession();
        return (Long) session.save(event);
    }
    
    // イベントを更新
    public void updateEvent(Event event) {
        Session session = sessionFactory.getCurrentSession();
        session.update(event);
    }
    
    // イベントを削除
    public void deleteEvent(Event event) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(event);
    }
}
