package com.example.ticketreservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
}
