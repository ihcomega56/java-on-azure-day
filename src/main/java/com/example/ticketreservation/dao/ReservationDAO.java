package com.example.ticketreservation.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.ticketreservation.model.Reservation;

@Repository
public class ReservationDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    // 全ての予約を取得
    @SuppressWarnings("unchecked")
    public List<Reservation> getAllReservations() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM Reservation r JOIN FETCH r.event ORDER BY r.reservationTime DESC");
        return query.list();
    }
    
    // IDで予約を検索
    public Reservation getReservationById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "FROM Reservation r JOIN FETCH r.event WHERE r.id = :id");
        query.setParameter("id", id);
        return (Reservation) query.uniqueResult();
    }
    
    // 確認コードで予約を検索
    public Reservation getReservationByConfirmationCode(String confirmationCode) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "FROM Reservation r JOIN FETCH r.event WHERE r.confirmationCode = :code");
        query.setParameter("code", confirmationCode);
        return (Reservation) query.uniqueResult();
    }
    
    // メールアドレスで予約を検索
    @SuppressWarnings("unchecked")
    public List<Reservation> getReservationsByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "FROM Reservation r JOIN FETCH r.event WHERE r.email = :email ORDER BY r.reservationTime DESC");
        query.setParameter("email", email);
        return query.list();
    }
    
    // イベントIDで予約を検索
    @SuppressWarnings("unchecked")
    public List<Reservation> getReservationsByEventId(Long eventId) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "FROM Reservation r WHERE r.event.id = :eventId ORDER BY reservationTime DESC");
        query.setParameter("eventId", eventId);
        return query.list();
    }
    
    // 予約を保存
    public Long saveReservation(Reservation reservation) {
        Session session = sessionFactory.getCurrentSession();
        return (Long) session.save(reservation);
    }
    
    // 予約を更新
    public void updateReservation(Reservation reservation) {
        Session session = sessionFactory.getCurrentSession();
        session.update(reservation);
    }
    
    // 予約を削除
    public void deleteReservation(Reservation reservation) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(reservation);
    }
}
