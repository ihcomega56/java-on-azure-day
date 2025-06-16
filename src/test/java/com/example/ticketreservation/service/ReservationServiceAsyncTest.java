package com.example.ticketreservation.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.example.ticketreservation.dao.ReservationDAO;
import com.example.ticketreservation.exception.SoldOutException;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Reservation;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ReservationServiceの非同期処理テスト
 * Virtual Threads導入によるスレッド処理改善のテスト
 */
@RunWith(MockitoJUnitRunner.class)
public class ReservationServiceAsyncTest {

    @Mock
    private ReservationDAO reservationDAO;

    @Mock
    private EventService eventService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    public void testReserveTicketAsync_正常予約() throws InterruptedException, ExecutionException, SoldOutException {
        // Given - テストデータの準備
        Long eventId = 1L;
        String email = "test@example.com";
        Integer quantity = 2;
        
        Event event = createTestEvent(eventId, "Test Event", 100);
        Reservation expectedReservation = createTestReservation(1L, event, email, quantity, "TESTCODE");

        when(eventService.getEventById(eventId)).thenReturn(event);
        when(reservationDAO.saveReservation(any(Reservation.class))).thenReturn(1L);
        when(reservationDAO.getReservationById(1L)).thenReturn(expectedReservation);

        // When - 非同期予約処理の実行
        CompletableFuture<Reservation> future = reservationService.reserveTicketAsync(eventId, email, quantity);
        Reservation result = future.get();

        // Then - 結果の検証
        assertThat(result, notNullValue());
        assertThat(result.getEmail(), equalTo(email));
        assertThat(result.getQuantity(), equalTo(quantity));
        assertThat(result.getConfirmationCode(), equalTo("TESTCODE"));

        // 各サービスメソッドが正しく呼び出されることを確認
        verify(eventService).getEventById(eventId);
        verify(eventService).reduceAvailableSeats(eventId, quantity);
        verify(reservationDAO).saveReservation(any(Reservation.class));
        verify(reservationDAO).getReservationById(1L);
    }

    @Test
    public void testReserveTicketWithEmailAsync_正常予約とメール送信() throws InterruptedException, ExecutionException, SoldOutException {
        // Given - テストデータの準備
        Long eventId = 1L;
        String email = "test@example.com";
        Integer quantity = 1;
        
        Event event = createTestEvent(eventId, "Test Event", 50);
        Reservation expectedReservation = createTestReservation(1L, event, email, quantity, "MAILTEST");

        when(eventService.getEventById(eventId)).thenReturn(event);
        when(reservationDAO.saveReservation(any(Reservation.class))).thenReturn(1L);
        when(reservationDAO.getReservationById(1L)).thenReturn(expectedReservation);
        when(emailService.sendConfirmationEmailAsync(email, "MAILTEST"))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When - 予約とメール送信の非同期処理を実行
        CompletableFuture<Reservation> future = reservationService.reserveTicketWithEmailAsync(eventId, email, quantity);
        Reservation result = future.get();

        // Then - 結果の検証
        assertThat(result, notNullValue());
        assertThat(result.getEmail(), equalTo(email));
        assertThat(result.getQuantity(), equalTo(quantity));

        // メール送信が非同期で呼び出されることを確認
        verify(emailService).sendConfirmationEmailAsync(email, "MAILTEST");
    }

    @Test
    public void testReserveTicketAsync_売り切れ時例外発生() throws InterruptedException, ExecutionException {
        // Given - 売り切れ状態のイベント
        Long eventId = 1L;
        String email = "test@example.com";
        Integer quantity = 5;
        
        Event soldOutEvent = createTestEvent(eventId, "Sold Out Event", 2); // 残席2席

        when(eventService.getEventById(eventId)).thenReturn(soldOutEvent);

        // When & Then - 非同期予約処理で例外が発生することを確認
        try {
            CompletableFuture<Reservation> future = reservationService.reserveTicketAsync(eventId, email, quantity);
            future.get(); // ExecutionExceptionがスローされる
            fail("例外が発生すべきです");
        } catch (ExecutionException e) {
            // ExecutionExceptionの原因がRuntimeExceptionであることを確認
            assertThat(e.getCause(), instanceOf(RuntimeException.class));
            RuntimeException runtimeException = (RuntimeException) e.getCause();
            assertThat(runtimeException.getCause(), instanceOf(SoldOutException.class));
        }
    }

    @Test
    public void testReserveTicketAsync_イベント不存在時例外発生() throws InterruptedException, ExecutionException {
        // Given - 存在しないイベント
        Long eventId = 999L;
        String email = "test@example.com";
        Integer quantity = 1;

        when(eventService.getEventById(eventId)).thenReturn(null);

        // When & Then - 非同期予約処理で例外が発生することを確認
        try {
            CompletableFuture<Reservation> future = reservationService.reserveTicketAsync(eventId, email, quantity);
            future.get(); // ExecutionExceptionがスローされる
            fail("例外が発生すべきです");
        } catch (ExecutionException e) {
            // ExecutionExceptionの原因がRuntimeExceptionであることを確認
            assertThat(e.getCause(), instanceOf(RuntimeException.class));
            RuntimeException runtimeException = (RuntimeException) e.getCause();
            assertThat(runtimeException.getCause(), instanceOf(SoldOutException.class));
        }
    }

    /**
     * テスト用のEventオブジェクトを作成
     */
    private Event createTestEvent(Long id, String name, int availableSeats) {
        Event event = new Event();
        event.setId(id);
        event.setEventName(name);
        event.setAvailableSeats(availableSeats);
        event.setTotalSeats(100);
        event.setPrice(5000.0); // 価格を設定
        event.setCategory("TEST");
        event.setVenue("Test Venue");
        return event;
    }

    /**
     * テスト用のReservationオブジェクトを作成
     */
    private Reservation createTestReservation(Long id, Event event, String email, Integer quantity, String confirmationCode) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setEvent(event);
        reservation.setEmail(email);
        reservation.setQuantity(quantity);
        reservation.setConfirmationCode(confirmationCode);
        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        return reservation;
    }
}