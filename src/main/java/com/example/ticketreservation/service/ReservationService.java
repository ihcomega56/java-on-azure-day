package com.example.ticketreservation.service;

import com.example.ticketreservation.dao.ReservationDAO;
import com.example.ticketreservation.exception.SoldOutException;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ReservationService {

    @Autowired
    private ReservationDAO reservationDAO;
    
    @Autowired
    private EventService eventService;
    
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
        }
    }
    
    private String generateConfirmationCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
