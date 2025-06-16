package com.example.ticketreservation.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.example.ticketreservation.exception.SoldOutException;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Reservation;
import com.example.ticketreservation.service.EventService;
import com.example.ticketreservation.service.ReservationService;

/**
 * TicketController のテストクラス
 * コントローラーレイヤーの動作を検証
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-applicationContext.xml"})
public class TicketControllerTest {

    @Mock
    private EventService eventService;
    
    @Mock
    private ReservationService reservationService;
    
    @InjectMocks
    private TicketController ticketController;
    
    private MockMvc mockMvc;
    
    private Event testEvent;
    private Reservation testReservation;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
        
        // テストデータの準備
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setEventName("テストコンサート");
        testEvent.setDescription("テスト用のコンサートイベント");
        testEvent.setEventDate(LocalDateTime.now().plusDays(7));
        testEvent.setVenue("テスト会場");
        testEvent.setPrice(5000.0);
        testEvent.setTotalSeats(100);
        testEvent.setAvailableSeats(50);
        testEvent.setCategory("CONCERT");
        
        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setEvent(testEvent);
        testReservation.setEmail("test@example.com");
        testReservation.setQuantity(2);
        testReservation.setConfirmationCode("ABC12345");
    }
    
    @Test
    public void testRoot_リダイレクト確認() throws Exception {
        // When & Then - ルートアクセス時にイベント一覧にリダイレクトされること
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));
    }
    
    @Test
    public void testHome_リダイレクト確認() throws Exception {
        // When & Then - ホームアクセス時にイベント一覧にリダイレクトされること
        mockMvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));
    }
    
    @Test
    public void testListEvents_フィルタなし() throws Exception {
        // Given - 利用可能なイベントリストを準備
        List<Event> events = Arrays.asList(testEvent);
        when(eventService.getAvailableEvents()).thenReturn(events);
        
        // When & Then - フィルタなしでイベント一覧を取得
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/eventList"))
                .andExpect(model().attribute("events", events));
        
        verify(eventService).getAvailableEvents();
    }
    
    @Test
    public void testListEvents_カテゴリフィルタ() throws Exception {
        // Given - カテゴリでフィルタされたイベントリストを準備
        List<Event> events = Arrays.asList(testEvent);
        when(eventService.getEventsByCategory("CONCERT")).thenReturn(events);
        
        // When & Then - カテゴリでフィルタされたイベント一覧を取得
        mockMvc.perform(get("/events").param("category", "CONCERT"))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/eventList"))
                .andExpect(model().attribute("events", events));
        
        verify(eventService).getEventsByCategory("CONCERT");
    }
    
    @Test
    public void testListEvents_日付フィルタ() throws Exception {
        // Given - 日付でフィルタされたイベントリストを準備
        List<Event> events = Arrays.asList(testEvent);
        String dateString = "2024-01-15";
        LocalDate date = LocalDate.parse(dateString);
        when(eventService.getEventsFromDate(date)).thenReturn(events);
        
        // When & Then - 日付でフィルタされたイベント一覧を取得
        mockMvc.perform(get("/events").param("date", dateString))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/eventList"))
                .andExpect(model().attribute("events", events));
        
        verify(eventService).getEventsFromDate(date);
    }
    
    @Test
    public void testListEvents_カテゴリと日付フィルタ() throws Exception {
        // Given - カテゴリと日付でフィルタされたイベントリストを準備
        List<Event> events = Arrays.asList(testEvent);
        String dateString = "2024-01-15";
        LocalDate date = LocalDate.parse(dateString);
        when(eventService.getEventsByCategoryFromDate("CONCERT", date)).thenReturn(events);
        
        // When & Then - カテゴリと日付でフィルタされたイベント一覧を取得
        mockMvc.perform(get("/events")
                .param("category", "CONCERT")
                .param("date", dateString))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/eventList"))
                .andExpect(model().attribute("events", events));
        
        verify(eventService).getEventsByCategoryFromDate("CONCERT", date);
    }
    
    @Test
    public void testEventDetails_正常表示() throws Exception {
        // Given - イベントが存在する場合
        when(eventService.getEventById(1L)).thenReturn(testEvent);
        
        // When & Then - イベント詳細が正常に表示されること
        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/eventDetails"))
                .andExpect(model().attribute("event", testEvent));
        
        verify(eventService).getEventById(1L);
    }
    
    @Test
    public void testEventDetails_存在しないイベント() throws Exception {
        // Given - イベントが存在しない場合
        when(eventService.getEventById(999L)).thenReturn(null);
        
        // When & Then - イベント一覧にリダイレクトされること
        mockMvc.perform(get("/events/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));
        
        verify(eventService).getEventById(999L);
    }
    
    @Test
    public void testShowReservationForm_正常表示() throws Exception {
        // Given - 予約可能なイベントが存在する場合
        when(eventService.getEventById(1L)).thenReturn(testEvent);
        
        // When & Then - 予約フォームが正常に表示されること
        mockMvc.perform(get("/events/1/reserve"))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/reservationForm"))
                .andExpect(model().attribute("event", testEvent));
        
        verify(eventService).getEventById(1L);
    }
    
    @Test
    public void testShowReservationForm_売り切れイベント() throws Exception {
        // Given - 売り切れのイベント
        testEvent.setAvailableSeats(0);
        when(eventService.getEventById(1L)).thenReturn(testEvent);
        
        // When & Then - イベント一覧にリダイレクトされること
        mockMvc.perform(get("/events/1/reserve"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));
        
        verify(eventService).getEventById(1L);
    }
    
    @Test
    public void testReserveTicket_正常予約() throws Exception {
        // Given - 正常な予約処理
        when(reservationService.reserveTicket(1L, "test@example.com", 2))
                .thenReturn(testReservation);
        
        // When & Then - 予約が正常に完了すること
        mockMvc.perform(post("/reserve")
                .param("eventId", "1")
                .param("email", "test@example.com")
                .param("quantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/confirmation/ABC12345"))
                .andExpect(flash().attribute("message", containsString("予約が完了しました")));
        
        verify(reservationService).reserveTicket(1L, "test@example.com", 2);
    }
    
    @Test
    public void testReserveTicket_売り切れ例外() throws Exception {
        // Given - 売り切れ例外が発生する場合
        when(reservationService.reserveTicket(1L, "test@example.com", 2))
                .thenThrow(new SoldOutException("チケットが売り切れです"));
        
        // When & Then - エラーメッセージと共にイベント詳細にリダイレクトされること
        mockMvc.perform(post("/reserve")
                .param("eventId", "1")
                .param("email", "test@example.com")
                .param("quantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events/1"))
                .andExpect(flash().attribute("error", "チケットが売り切れです"));
        
        verify(reservationService).reserveTicket(1L, "test@example.com", 2);
    }
    
    @Test
    public void testConfirmationPage_正常表示() throws Exception {
        // Given - 確認コードで予約が検索できる場合
        when(reservationService.getReservationByConfirmationCode("ABC12345"))
                .thenReturn(testReservation);
        
        // When & Then - 確認ページが正常に表示されること
        mockMvc.perform(get("/confirmation/ABC12345"))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/confirmation"))
                .andExpect(model().attribute("reservation", testReservation));
        
        verify(reservationService).getReservationByConfirmationCode("ABC12345");
    }
    
    @Test
    public void testConfirmationPage_無効な確認コード() throws Exception {
        // Given - 無効な確認コードの場合
        when(reservationService.getReservationByConfirmationCode("INVALID"))
                .thenReturn(null);
        
        // When & Then - イベント一覧にリダイレクトされること
        mockMvc.perform(get("/confirmation/INVALID"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));
        
        verify(reservationService).getReservationByConfirmationCode("INVALID");
    }
    
    @Test
    public void testMyReservations_正常表示() throws Exception {
        // Given - メールアドレスに紐づく予約リスト
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationService.getReservationsByEmail("test@example.com"))
                .thenReturn(reservations);
        
        // When & Then - 予約一覧が正常に表示されること
        mockMvc.perform(get("/myreservations").param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/myReservations"))
                .andExpect(model().attribute("reservations", reservations));
        
        verify(reservationService).getReservationsByEmail("test@example.com");
    }
    
    @Test
    public void testShowCancelForm_正常表示() throws Exception {
        // Given - 予約が存在する場合
        when(reservationService.getReservationById(1L)).thenReturn(testReservation);
        
        // When & Then - キャンセルフォームが正常に表示されること
        mockMvc.perform(get("/cancel/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("ticket/cancelForm"))
                .andExpect(model().attribute("reservation", testReservation));
        
        verify(reservationService).getReservationById(1L);
    }
    
    @Test
    public void testCancelReservation_正常キャンセル() throws Exception {
        // Given - 正常なキャンセル処理
        when(reservationService.getReservationById(1L)).thenReturn(testReservation);
        
        // When & Then - 予約が正常にキャンセルされること
        mockMvc.perform(post("/cancel")
                .param("reservationId", "1")
                .param("email", "test@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"))
                .andExpect(flash().attribute("message", "予約をキャンセルしました"));
        
        verify(reservationService).getReservationById(1L);
        verify(reservationService).cancelReservation(1L);
    }
    
    @Test
    public void testCancelReservation_メールアドレス不一致() throws Exception {
        // Given - メールアドレスが一致しない場合
        when(reservationService.getReservationById(1L)).thenReturn(testReservation);
        
        // When & Then - エラーメッセージと共にキャンセルフォームにリダイレクトされること
        mockMvc.perform(post("/cancel")
                .param("reservationId", "1")
                .param("email", "wrong@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cancel/1"))
                .andExpect(flash().attribute("error", "予約者のメールアドレスが一致しません"));
        
        verify(reservationService).getReservationById(1L);
        verify(reservationService, never()).cancelReservation(1L);
    }
}
