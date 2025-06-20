package com.example.ticketreservation.service;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.ticketreservation.dao.EventRepository;
import com.example.ticketreservation.dao.ReservationRepository;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Reservation;

/**
 * Optionalクラス導入によるNull安全性向上の機能をテストするクラス
 * 新しく追加されたOptional型の戻り値に特化したテストケース
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class OptionalRefactoringTest {

    @Mock
    private EventRepository eventRepository;
    
    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private EventService eventService;
    
    @InjectMocks
    private ReservationService reservationService;

    private Event testEvent;
    private Reservation testReservation;

    @BeforeEach
    public void setUp() {
        // テストデータの準備
        testEvent = new Event(
            "Optionalテストイベント", 
            "Optional機能検証用", 
            LocalDateTime.now().plusDays(7), 
            "テスト会場", 
            "テスト", 
            50, 
            3000.0
        );
        testEvent.setId(1L);
        
        testReservation = new Reservation(testEvent, "optional@test.com", 2, "OPT12345");
        testReservation.setId(1L);
        testReservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
    }

    @Test
    public void testGetEventById_Optional利用による安全性確認() {
        // Given - イベントが存在する場合
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // When - Optional型でイベントを取得
        Optional<Event> result = eventService.getEventById(1L);

        // Then - Optional型が正しく返されることを確認
        assertThat("Optional型で結果が返されること", result, notNullValue());
        assertThat("Optionalにイベントが含まれること", result.isPresent(), equalTo(true));
        assertThat("取得したイベントが正しいこと", result.get().getId(), equalTo(1L));
        assertThat("取得したイベント名が正しいこと", result.get().getEventName(), equalTo("Optionalテストイベント"));
        
        verify(eventRepository).findById(1L);
    }

    @Test
    public void testGetEventById_Optional空の場合の安全性確認() {
        // Given - イベントが存在しない場合
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // When - Optional型でイベントを取得
        Optional<Event> result = eventService.getEventById(999L);

        // Then - 空のOptionalが返されることを確認
        assertThat("Optional型で結果が返されること", result, notNullValue());
        assertThat("Optionalが空であること", result.isEmpty(), equalTo(true));
        
        verify(eventRepository).findById(999L);
    }

    @Test
    public void testGetReservationById_Optional利用による安全性確認() {
        // Given - 予約が存在する場合
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        // When - Optional型で予約を取得
        Optional<Reservation> result = reservationService.getReservationById(1L);

        // Then - Optional型が正しく返されることを確認
        assertThat("Optional型で結果が返されること", result, notNullValue());
        assertThat("Optionalに予約が含まれること", result.isPresent(), equalTo(true));
        assertThat("取得した予約が正しいこと", result.get().getId(), equalTo(1L));
        assertThat("取得した予約のメールアドレスが正しいこと", result.get().getEmail(), equalTo("optional@test.com"));
        
        verify(reservationRepository).findById(1L);
    }

    @Test
    public void testGetReservationById_Optional空の場合の安全性確認() {
        // Given - 予約が存在しない場合
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // When - Optional型で予約を取得
        Optional<Reservation> result = reservationService.getReservationById(999L);

        // Then - 空のOptionalが返されることを確認
        assertThat("Optional型で結果が返されること", result, notNullValue());
        assertThat("Optionalが空であること", result.isEmpty(), equalTo(true));
        
        verify(reservationRepository).findById(999L);
    }

    @Test
    public void testGetReservationByConfirmationCode_Optional利用による安全性確認() {
        // Given - 確認コードに対応する予約が存在する場合
        when(reservationRepository.findByConfirmationCode("OPT12345")).thenReturn(testReservation);

        // When - Optional型で予約を取得
        Optional<Reservation> result = reservationService.getReservationByConfirmationCode("OPT12345");

        // Then - Optional型が正しく返されることを確認
        assertThat("Optional型で結果が返されること", result, notNullValue());
        assertThat("Optionalに予約が含まれること", result.isPresent(), equalTo(true));
        assertThat("取得した予約の確認コードが正しいこと", result.get().getConfirmationCode(), equalTo("OPT12345"));
        
        verify(reservationRepository).findByConfirmationCode("OPT12345");
    }

    @Test
    public void testGetReservationByConfirmationCode_Optional空の場合の安全性確認() {
        // Given - 確認コードに対応する予約が存在しない場合
        when(reservationRepository.findByConfirmationCode("INVALID")).thenReturn(null);

        // When - Optional型で予約を取得
        Optional<Reservation> result = reservationService.getReservationByConfirmationCode("INVALID");

        // Then - 空のOptionalが返されることを確認
        assertThat("Optional型で結果が返されること", result, notNullValue());
        assertThat("Optionalが空であること", result.isEmpty(), equalTo(true));
        
        verify(reservationRepository).findByConfirmationCode("INVALID");
    }

    @Test
    public void testOptional機能の連鎖的利用_map操作() {
        // Given - イベントが存在する場合
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // When - Optionalのmap操作を使用してイベント名を取得
        Optional<String> eventName = eventService.getEventById(1L)
            .map(Event::getEventName);

        // Then - map操作が正しく動作することを確認
        assertThat("map操作の結果がOptionalで返されること", eventName, notNullValue());
        assertThat("map操作でイベント名が取得されること", eventName.isPresent(), equalTo(true));
        assertThat("取得したイベント名が正しいこと", eventName.get(), equalTo("Optionalテストイベント"));
    }

    @Test
    public void testOptional機能の連鎖的利用_filter操作() {
        // Given - イベントが存在する場合
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // When - Optionalのfilter操作を使用して利用可能席数をチェック
        Optional<Event> availableEvent = eventService.getEventById(1L)
            .filter(event -> event.getAvailableSeats() > 0);

        // Then - filter操作が正しく動作することを確認
        assertThat("filter操作の結果がOptionalで返されること", availableEvent, notNullValue());
        assertThat("filter条件を満たすイベントが取得されること", availableEvent.isPresent(), equalTo(true));
        assertThat("フィルタされたイベントが正しいこと", availableEvent.get().getId(), equalTo(1L));
    }

    @Test
    public void testOptional機能の連鎖的利用_filter操作_条件不一致() {
        // Given - 利用可能席数が0のイベント
        testEvent.setAvailableSeats(0);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // When - Optionalのfilter操作を使用して利用可能席数をチェック
        Optional<Event> availableEvent = eventService.getEventById(1L)
            .filter(event -> event.getAvailableSeats() > 0);

        // Then - filter条件を満たさない場合は空のOptionalが返されることを確認
        assertThat("filter操作の結果がOptionalで返されること", availableEvent, notNullValue());
        assertThat("filter条件を満たさない場合は空のOptionalが返されること", availableEvent.isEmpty(), equalTo(true));
    }
}