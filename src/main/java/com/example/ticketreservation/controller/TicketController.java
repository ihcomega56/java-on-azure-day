package com.example.ticketreservation.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
    
    // イベント一覧ページ（Virtual Threads活用）
    @GetMapping("/events")
    public String listEvents(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String date,
            Model model) {
        
        try {
            List<Event> events;
            
            // 検索条件に応じてAsync処理を使用
            if ((category != null && !category.isEmpty()) || (date != null && !date.isEmpty())) {
                LocalDate fromDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
                
                // 非同期検索を実行（Virtual Threadsによる高速処理）
                CompletableFuture<List<Event>> eventsFuture = eventService.searchEventsAsync(category, fromDate);
                
                // 結果を待機（タイムアウト付きで安全性確保）
                events = eventsFuture.get(5, TimeUnit.SECONDS);
            } else {
                // 条件指定なしの場合は同期処理（キャッシュ効率を考慮）
                events = eventService.getAvailableEvents();
            }
            
            model.addAttribute("events", events);
            return "ticket/eventList";
            
        } catch (TimeoutException e) {
            // タイムアウト時はフォールバック処理
            List<Event> events = eventService.getAvailableEvents();
            model.addAttribute("events", events);
            model.addAttribute("warning", "検索処理に時間がかかったため、基本的なイベント一覧を表示しています。");
            return "ticket/eventList";
        } catch (InterruptedException | ExecutionException e) {
            // エラー時のフォールバック処理
            List<Event> events = eventService.getAvailableEvents();
            model.addAttribute("events", events);
            model.addAttribute("error", "検索処理中にエラーが発生しました。基本的なイベント一覧を表示しています。");
            return "ticket/eventList";
        }
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
    
    // 予約処理（Virtual Threads活用）
    @PostMapping("/reserve")
    public String reserveTicket(
            @RequestParam("eventId") Long eventId,
            @RequestParam("email") String email,
            @RequestParam("quantity") Integer quantity,
            RedirectAttributes redirectAttributes) {
        
        try {
            // 非同期予約処理を実行（Virtual Threadsによる高速処理）
            CompletableFuture<Reservation> reservationFuture = 
                reservationService.reserveTicketAsync(eventId, email, quantity);
            
            // 予約結果を待機
            Reservation reservation = reservationFuture.get(10, TimeUnit.SECONDS);
            
            // メール送信は非同期で実行（レスポンスを待たない）
            reservationService.sendConfirmationEmailAsync(email, reservation.getConfirmationCode());
            
            redirectAttributes.addFlashAttribute("message", 
                "予約が完了しました。確認コード: " + reservation.getConfirmationCode());
            return "redirect:/confirmation/" + reservation.getConfirmationCode();
            
        } catch (TimeoutException e) {
            // タイムアウト時のフォールバック（同期処理で再試行）
            try {
                Reservation reservation = reservationService.reserveTicket(eventId, email, quantity);
                redirectAttributes.addFlashAttribute("message", 
                    "予約が完了しました。確認コード: " + reservation.getConfirmationCode());
                return "redirect:/confirmation/" + reservation.getConfirmationCode();
            } catch (SoldOutException se) {
                redirectAttributes.addFlashAttribute("error", se.getMessage());
                return "redirect:/events/" + eventId;
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("error", "予約処理中にエラーが発生しました: " + ex.getMessage());
                return "redirect:/events/" + eventId;
            }
        } catch (InterruptedException | ExecutionException e) {
            // 非同期処理エラー時の処理
            Throwable cause = e.getCause();
            if (cause instanceof SoldOutException) {
                redirectAttributes.addFlashAttribute("error", cause.getMessage());
                return "redirect:/events/" + eventId;
            } else {
                redirectAttributes.addFlashAttribute("error", "予約処理中にエラーが発生しました: " + cause.getMessage());
                return "redirect:/events/" + eventId;
            }
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
