package com.example.ticketreservation.dao;

import com.example.ticketreservation.model.Reservation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReservationDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    // 全ての予約を取得
    @SuppressWarnings("unchecked")
    public List<Reservation> getAllReservations() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM Reservation ORDER BY reservationTime DESC");
        return query.list();
    }
    
    // IDで予約を検索
    public Reservation getReservationById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return (Reservation) session.get(Reservation.class, id);
    }
    
    // 確認コードで予約を検索
    @SuppressWarnings("unchecked")
    public Reservation getReservationByConfirmationCode(String confirmationCode) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "FROM Reservation WHERE confirmationCode = :code");
        query.setParameter("code", confirmationCode);
        return (Reservation) query.uniqueResult();
    }
    
    // メールアドレスで予約を検索
    @SuppressWarnings("unchecked")
    public List<Reservation> getReservationsByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "FROM Reservation WHERE email = :email ORDER BY reservationTime DESC");
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
