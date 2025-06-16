package com.example.ticketreservation.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.ticketreservation.dao.ReservationRepository;
import com.example.ticketreservation.exception.SoldOutException;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Reservation;

/**
 * ReservationServiceのテストクラス
 * 予約に関するビジネスロジックのテストを実施
 * Spring Boot 3.2 + JUnit 5対応版
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EventService eventService;

    @InjectMocks
    private ReservationService reservationService;

    private Event testEvent;
    private Reservation testReservation;

    @BeforeEach
    public void setUp() {
        // テストデータの準備
        testEvent = new Event(
            "テストコンサート", 
            "テスト用イベントです", 
            LocalDateTime.now().plusDays(7), 
            "東京ドーム", 
            "音楽", 
            100, 
            5000.0
        );
        testEvent.setId(1L);
        
        testReservation = new Reservation(testEvent, "test@example.com", 2, "ABCD1234");
        testReservation.setId(1L);
        testReservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
    }

    @Test
    public void testReserveTicket_正常予約() throws SoldOutException {
        // Given - テストデータの準備
        Long eventId = 1L;
        String email = "test@example.com";
        Integer quantity = 2;
        
        when(eventService.getEventById(eventId)).thenReturn(testEvent);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // When - テスト対象メソッドの実行
        Reservation result = reservationService.reserveTicket(eventId, email, quantity);

        // Then - 結果の検証
        assertThat("予約が正常に作成されること", result, notNullValue());
        assertThat("予約者のメールアドレスが正しく設定されること", result.getEmail(), equalTo(email));
        assertThat("予約数量が正しく設定されること", result.getQuantity(), equalTo(quantity));
        assertThat("確認コードが設定されること", result.getConfirmationCode(), notNullValue());
        assertThat("確認コードは8文字であること", result.getConfirmationCode().length(), equalTo(8));
        
        // 依存関係の呼び出し確認
        verify(eventService).getEventById(eventId);
        verify(eventService).reduceAvailableSeats(eventId, quantity);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    public void testReserveTicket_売り切れ時例外発生() {
        // Given - テストデータの準備（売り切れ状態）
        Long eventId = 1L;
        String email = "test@example.com";
        Integer quantity = 10; // 利用可能席数（100）より大きい値
        
        testEvent.setAvailableSeats(5); // 席数不足の状態
        when(eventService.getEventById(eventId)).thenReturn(testEvent);

        // When & Then - SoldOutExceptionが発生することを期待
        assertThrows(SoldOutException.class, () -> {
            reservationService.reserveTicket(eventId, email, quantity);
        });
    }

    @Test
    public void testReserveTicket_存在しないイベント時例外発生() {
        // Given - テストデータの準備（存在しないイベント）
        Long eventId = 999L;
        String email = "test@example.com";
        Integer quantity = 2;
        
        when(eventService.getEventById(eventId)).thenReturn(null);

        // When & Then - IllegalArgumentExceptionが発生することを期待
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.reserveTicket(eventId, email, quantity);
        });
    }

    @Test
    public void testReserveTicket_境界値_利用可能席数ちょうど() throws SoldOutException {
        // Given - テストデータの準備（利用可能席数ちょうどの予約）
        Long eventId = 1L;
        String email = "test@example.com";
        Integer quantity = 100; // 利用可能席数と同じ
        
        when(eventService.getEventById(eventId)).thenReturn(testEvent);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // When - テスト対象メソッドの実行
        Reservation result = reservationService.reserveTicket(eventId, email, quantity);

        // Then - 結果の検証
        assertThat("境界値での予約が正常に作成されること", result, notNullValue());
        verify(eventService).reduceAvailableSeats(eventId, quantity);
    }

    @Test
    public void testCancelReservation_正常キャンセル() {
        // Given - テストデータの準備
        String confirmationCode = "ABCD1234";
        
        when(reservationRepository.findByConfirmationCode(confirmationCode)).thenReturn(testReservation);

        // When - テスト対象メソッドの実行
        Reservation result = reservationService.cancelReservation(confirmationCode);

        // Then - 結果の検証
        verify(reservationRepository).findByConfirmationCode(confirmationCode);
        verify(eventService).increaseAvailableSeats(testEvent.getId(), testReservation.getQuantity());
        verify(reservationRepository).save(testReservation);
        
        // 予約ステータスがキャンセルに変更されていることを確認
        assertThat("予約ステータスがキャンセルに変更されること", 
                  testReservation.getStatus(), equalTo(Reservation.ReservationStatus.CANCELLED));
    }

    @Test
    public void testCancelReservation_存在しない予約() {
        // Given - テストデータの準備（存在しない予約）
        String confirmationCode = "INVALID999";
        
        when(reservationRepository.findByConfirmationCode(confirmationCode)).thenReturn(null);

        // When & Then - 例外が発生することを確認
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.cancelReservation(confirmationCode);
        });
        
        verify(reservationRepository).findByConfirmationCode(confirmationCode);
        verify(eventService, never()).increaseAvailableSeats(anyLong(), anyInt());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    public void testGetReservationById_正常取得() {
        // Given - テストデータの準備
        Long reservationId = 1L;
        
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(testReservation));

        // When - テスト対象メソッドの実行
        Reservation result = reservationService.getReservationById(reservationId);

        // Then - 結果の検証
        assertThat("予約が正常に取得されること", result, notNullValue());
        assertThat("取得した予約のIDが正しいこと", result.getId(), equalTo(reservationId));
        verify(reservationRepository).findById(reservationId);
    }

    @Test
    public void testGetReservationByConfirmationCode_正常取得() {
        // Given - テストデータの準備
        String confirmationCode = "ABCD1234";
        
        when(reservationRepository.findByConfirmationCode(confirmationCode)).thenReturn(testReservation);

        // When - テスト対象メソッドの実行
        Reservation result = reservationService.getReservationByConfirmationCode(confirmationCode);

        // Then - 結果の検証
        assertThat("予約が正常に取得されること", result, notNullValue());
        assertThat("取得した予約の確認コードが正しいこと", result.getConfirmationCode(), equalTo(confirmationCode));
        verify(reservationRepository).findByConfirmationCode(confirmationCode);
    }

    @Test
    public void testGetReservationsByEmail_正常取得() {
        // Given - テストデータの準備
        String email = "test@example.com";
        List<Reservation> expectedReservations = Arrays.asList(testReservation);
        
        when(reservationRepository.findByEmailOrderByReservationTimeDesc(email)).thenReturn(expectedReservations);

        // When - テスト対象メソッドの実行
        List<Reservation> result = reservationService.getReservationsByEmail(email);

        // Then - 結果の検証
        assertThat("予約リストが正常に取得されること", result, notNullValue());
        assertThat("予約リストのサイズが正しいこと", result.size(), equalTo(1));
        assertThat("取得した予約のメールアドレスが正しいこと", result.get(0).getEmail(), equalTo(email));
        verify(reservationRepository).findByEmailOrderByReservationTimeDesc(email);
    }

    @Test
    public void testGetAllReservations_正常取得() {
        // Given - テストデータの準備
        List<Reservation> expectedReservations = Arrays.asList(testReservation);
        
        when(reservationRepository.findAll()).thenReturn(expectedReservations);

        // When - テスト対象メソッドの実行
        List<Reservation> result = reservationService.getAllReservations();

        // Then - 結果の検証
        assertThat("全予約リストが正常に取得されること", result, notNullValue());
        assertThat("予約リストのサイズが正しいこと", result.size(), equalTo(1));
        verify(reservationRepository).findAll();
    }
}
