package com.example.ticketreservation.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.ticketreservation.exception.SoldOutException;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Reservation;
import com.example.ticketreservation.service.EventService;
import com.example.ticketreservation.service.ReservationService;

@Controller
public class TicketController {

    @Autowired
    private EventService eventService;
    
    @Autowired
    private ReservationService reservationService;
    
    // ルートページ
    @GetMapping("/")
    public String root() {
        return "redirect:/events";
    }
    
    // ホームページ
    @GetMapping("/home")
    public String home() {
        return "redirect:/events";
    }
    
    // イベント一覧ページ
    @GetMapping("/events")
    public String listEvents(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String date,
            Model model) {
        
        List<Event> events;
        
        if (category != null && !category.isEmpty() && date != null && !date.isEmpty()) {
            // カテゴリと日付の両方が指定されている場合
            LocalDate fromDate = LocalDate.parse(date);
            events = eventService.getEventsByCategoryFromDate(category, fromDate);
        } else if (category != null && !category.isEmpty()) {
            // カテゴリのみ指定されている場合
            events = eventService.getEventsByCategory(category);
        } else if (date != null && !date.isEmpty()) {
            // 日付のみ指定されている場合
            LocalDate fromDate = LocalDate.parse(date);
            events = eventService.getEventsFromDate(fromDate);
        } else {
            // どちらも指定されていない場合
            events = eventService.getAvailableEvents();
        }
        
        model.addAttribute("events", events);
        return "ticket/eventList";
    }
    
    // イベント詳細ページ
    @GetMapping("/events/{eventId}")
    public String eventDetails(@PathVariable Long eventId, Model model) {
        Event event = eventService.getEventById(eventId);
        
        if (event == null) {
            return "redirect:/events";
        }
        
        model.addAttribute("event", event);
        return "ticket/eventDetails";
    }
    
    // 予約フォームページ
    @GetMapping("/events/{eventId}/reserve")
    public String showReservationForm(@PathVariable Long eventId, Model model) {
        Event event = eventService.getEventById(eventId);
        
        if (event == null || event.getAvailableSeats() <= 0) {
            return "redirect:/events";
        }
        
        model.addAttribute("event", event);
        return "ticket/reservationForm";
    }
    
    // 予約処理
    @PostMapping("/reserve")
    public String reserveTicket(
            @RequestParam("eventId") Long eventId,
            @RequestParam("email") String email,
            @RequestParam("quantity") Integer quantity,
            RedirectAttributes redirectAttributes) {
        
        try {
            Reservation reservation = reservationService.reserveTicket(eventId, email, quantity);
            redirectAttributes.addFlashAttribute("message", "予約が完了しました。確認コード: " + reservation.getConfirmationCode());
            return "redirect:/confirmation/" + reservation.getConfirmationCode();
        } catch (SoldOutException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/events/" + eventId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "予約処理中にエラーが発生しました: " + e.getMessage());
            return "redirect:/events/" + eventId;
        }
    }
    
    // 予約確認ページ
    @GetMapping("/confirmation/{confirmationCode}")
    public String confirmationPage(@PathVariable String confirmationCode, Model model) {
        Reservation reservation = reservationService.getReservationByConfirmationCode(confirmationCode);
        
        if (reservation == null) {
            return "redirect:/events";
        }
        
        model.addAttribute("reservation", reservation);
        return "ticket/confirmation";
    }
    
    // 自分の予約一覧ページ
    @GetMapping("/myreservations")
    public String myReservations(@RequestParam("email") String email, Model model) {
        List<Reservation> reservations = reservationService.getReservationsByEmail(email);
        model.addAttribute("reservations", reservations);
        return "ticket/myReservations";
    }
    
    // 予約キャンセルフォーム
    @GetMapping("/cancel/{reservationId}")
    public String showCancelForm(@PathVariable Long reservationId, Model model) {
        Reservation reservation = reservationService.getReservationById(reservationId);
        
        if (reservation == null) {
            return "redirect:/events";
        }
        
        model.addAttribute("reservation", reservation);
        return "ticket/cancelForm";
    }
    
    // 予約キャンセル処理
    @PostMapping("/cancel")
    public String cancelReservation(
            @RequestParam("reservationId") Long reservationId,
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {
        
        Reservation reservation = reservationService.getReservationById(reservationId);
        
        if (reservation == null) {
            redirectAttributes.addFlashAttribute("error", "予約が見つかりません");
            return "redirect:/events";
        }
        
        if (!reservation.getEmail().equals(email)) {
            redirectAttributes.addFlashAttribute("error", "予約者のメールアドレスが一致しません");
            return "redirect:/cancel/" + reservationId;
        }
        
        reservationService.cancelReservation(reservation.getConfirmationCode());
        redirectAttributes.addFlashAttribute("message", "予約をキャンセルしました");
        
        return "redirect:/events";
    }
}
