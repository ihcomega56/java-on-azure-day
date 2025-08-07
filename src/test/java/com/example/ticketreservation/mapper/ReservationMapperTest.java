package com.example.ticketreservation.mapper;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.ticketreservation.dto.EventRecord;
import com.example.ticketreservation.dto.ReservationRecord;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Reservation;
import com.example.ticketreservation.model.Reservation.ReservationStatus;

/**
 * ReservationMapperクラスのテストクラス
 * エンティティとDTOの相互変換機能をテストする
 */
public class ReservationMapperTest {

    private Reservation testReservation;
    private ReservationRecord testReservationRecord;
    private Event testEvent;
    private EventRecord testEventRecord;
    private LocalDateTime testEventDate;
    private LocalDateTime testReservationTime;
    private LocalDateTime testCreatedAt;
    private LocalDateTime testUpdatedAt;

    @BeforeEach
    public void setUp() {
        testEventDate = LocalDateTime.of(2025, 7, 15, 19, 0);
        testReservationTime = LocalDateTime.of(2025, 6, 1, 14, 30);
        testCreatedAt = LocalDateTime.of(2025, 6, 1, 14, 30);
        testUpdatedAt = LocalDateTime.of(2025, 6, 5, 10, 15);

        // テスト用Event作成
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setEventName("テストコンサート");
        testEvent.setDescription("テスト用イベントです");
        testEvent.setEventDate(testEventDate);
        testEvent.setVenue("東京ドーム");
        testEvent.setCategory("音楽");
        testEvent.setTotalSeats(1000);
        testEvent.setAvailableSeats(750);
        testEvent.setPrice(8000.0);
        testEvent.setCreatedAt(testCreatedAt);
        testEvent.setUpdatedAt(testUpdatedAt);

        // テスト用EventRecord作成
        testEventRecord = new EventRecord(
            1L,
            "テストコンサート",
            "テスト用イベントです",
            testEventDate,
            "東京ドーム",
            "音楽",
            1000,
            750,
            8000.0,
            testCreatedAt,
            testUpdatedAt
        );

        // テスト用Reservation作成
        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setEvent(testEvent);
        testReservation.setEmail("test@example.com");
        testReservation.setQuantity(2);
        testReservation.setTotalPrice(16000.0);
        testReservation.setConfirmationCode("CONF001");
        testReservation.setReservationTime(testReservationTime);
        testReservation.setStatus(ReservationStatus.CONFIRMED);
        testReservation.setCreatedAt(testCreatedAt);
        testReservation.setUpdatedAt(testUpdatedAt);

        // テスト用ReservationRecord作成
        testReservationRecord = new ReservationRecord(
            1L,
            testEventRecord,
            "test@example.com",
            2,
            16000.0,
            "CONF001",
            testReservationTime,
            ReservationStatus.CONFIRMED,
            testCreatedAt,
            testUpdatedAt
        );
    }

    @Test
    public void testToDto_正常変換() {
        // When - EntityをDTOに変換
        ReservationRecord result = ReservationMapper.toDto(testReservation);

        // Then - 結果の検証
        assertThat("変換結果がnullでないこと", result, notNullValue());
        assertThat("IDが正しく変換されること", result.id(), equalTo(testReservation.getId()));
        assertThat("イベント情報が正しく変換されること", result.event(), notNullValue());
        assertThat("イベントIDが正しく変換されること", result.event().id(), equalTo(testReservation.getEvent().getId()));
        assertThat("メールアドレスが正しく変換されること", result.email(), equalTo(testReservation.getEmail()));
        assertThat("数量が正しく変換されること", result.quantity(), equalTo(testReservation.getQuantity()));
        assertThat("合計金額が正しく変換されること", result.totalPrice(), equalTo(testReservation.getTotalPrice()));
        assertThat("確認コードが正しく変換されること", result.confirmationCode(), equalTo(testReservation.getConfirmationCode()));
        assertThat("予約時間が正しく変換されること", result.reservationTime(), equalTo(testReservation.getReservationTime()));
        assertThat("ステータスが正しく変換されること", result.status(), equalTo(testReservation.getStatus()));
        assertThat("作成日時が正しく変換されること", result.createdAt(), equalTo(testReservation.getCreatedAt()));
        assertThat("更新日時が正しく変換されること", result.updatedAt(), equalTo(testReservation.getUpdatedAt()));
    }

    @Test
    public void testToDto_null値処理() {
        // When - nullのEntityをDTOに変換
        ReservationRecord result = ReservationMapper.toDto(null);

        // Then - nullが返されることを確認
        assertThat("null入力時はnullが返されること", result, nullValue());
    }

    @Test
    public void testToDto_nullEvent処理() {
        // Given - eventがnullのReservation
        Reservation reservationWithNullEvent = new Reservation();
        reservationWithNullEvent.setId(2L);
        reservationWithNullEvent.setEvent(null);
        reservationWithNullEvent.setEmail("test2@example.com");

        // When - eventがnullのEntityをDTOに変換
        ReservationRecord result = ReservationMapper.toDto(reservationWithNullEvent);

        // Then - eventがnullのDTOが正しく生成されることを確認
        assertThat("変換結果がnullでないこと", result, notNullValue());
        assertThat("IDが正しく設定されること", result.id(), equalTo(2L));
        assertThat("イベント情報がnullであること", result.event(), nullValue());
        assertThat("メールアドレスが正しく設定されること", result.email(), equalTo("test2@example.com"));
    }

    @Test
    public void testToEntity_正常変換() {
        // When - DTOをEntityに変換
        Reservation result = ReservationMapper.toEntity(testReservationRecord);

        // Then - 結果の検証
        assertThat("変換結果がnullでないこと", result, notNullValue());
        assertThat("IDが正しく変換されること", result.getId(), equalTo(testReservationRecord.id()));
        assertThat("イベント情報が正しく変換されること", result.getEvent(), notNullValue());
        assertThat("イベントIDが正しく変換されること", result.getEvent().getId(), equalTo(testReservationRecord.event().id()));
        assertThat("メールアドレスが正しく変換されること", result.getEmail(), equalTo(testReservationRecord.email()));
        assertThat("数量が正しく変換されること", result.getQuantity(), equalTo(testReservationRecord.quantity()));
        assertThat("合計金額が正しく変換されること", result.getTotalPrice(), equalTo(testReservationRecord.totalPrice()));
        assertThat("確認コードが正しく変換されること", result.getConfirmationCode(), equalTo(testReservationRecord.confirmationCode()));
        assertThat("予約時間が正しく変換されること", result.getReservationTime(), equalTo(testReservationRecord.reservationTime()));
        assertThat("ステータスが正しく変換されること", result.getStatus(), equalTo(testReservationRecord.status()));
        assertThat("作成日時が正しく変換されること", result.getCreatedAt(), equalTo(testReservationRecord.createdAt()));
        assertThat("更新日時が正しく変換されること", result.getUpdatedAt(), equalTo(testReservationRecord.updatedAt()));
    }

    @Test
    public void testToEntity_null値処理() {
        // When - nullのDTOをEntityに変換
        Reservation result = ReservationMapper.toEntity(null);

        // Then - nullが返されることを確認
        assertThat("null入力時はnullが返されること", result, nullValue());
    }

    @Test
    public void testToEntity_nullEvent処理() {
        // Given - eventがnullのReservationRecord
        ReservationRecord recordWithNullEvent = new ReservationRecord(
            3L,
            null,
            "test3@example.com",
            1,
            5000.0,
            "CONF003",
            testReservationTime,
            ReservationStatus.CONFIRMED,
            testCreatedAt,
            testUpdatedAt
        );

        // When - eventがnullのDTOをEntityに変換
        Reservation result = ReservationMapper.toEntity(recordWithNullEvent);

        // Then - eventがnullのEntityが正しく生成されることを確認
        assertThat("変換結果がnullでないこと", result, notNullValue());
        assertThat("IDが正しく設定されること", result.getId(), equalTo(3L));
        assertThat("イベント情報がnullであること", result.getEvent(), nullValue());
        assertThat("メールアドレスが正しく設定されること", result.getEmail(), equalTo("test3@example.com"));
    }

    @Test
    public void testRoundTrip_相互変換() {
        // When - Entity→DTO→Entityの相互変換
        ReservationRecord dto = ReservationMapper.toDto(testReservation);
        Reservation reconvertedEntity = ReservationMapper.toEntity(dto);

        // Then - 元のEntityと変換後のEntityが同じ値を持つことを確認
        assertThat("IDが保持されること", reconvertedEntity.getId(), equalTo(testReservation.getId()));
        assertThat("イベントIDが保持されること", reconvertedEntity.getEvent().getId(), equalTo(testReservation.getEvent().getId()));
        assertThat("メールアドレスが保持されること", reconvertedEntity.getEmail(), equalTo(testReservation.getEmail()));
        assertThat("数量が保持されること", reconvertedEntity.getQuantity(), equalTo(testReservation.getQuantity()));
        assertThat("合計金額が保持されること", reconvertedEntity.getTotalPrice(), equalTo(testReservation.getTotalPrice()));
        assertThat("確認コードが保持されること", reconvertedEntity.getConfirmationCode(), equalTo(testReservation.getConfirmationCode()));
        assertThat("予約時間が保持されること", reconvertedEntity.getReservationTime(), equalTo(testReservation.getReservationTime()));
        assertThat("ステータスが保持されること", reconvertedEntity.getStatus(), equalTo(testReservation.getStatus()));
        assertThat("作成日時が保持されること", reconvertedEntity.getCreatedAt(), equalTo(testReservation.getCreatedAt()));
        assertThat("更新日時が保持されること", reconvertedEntity.getUpdatedAt(), equalTo(testReservation.getUpdatedAt()));
    }

    @Test
    public void testReversedRoundTrip_逆方向相互変換() {
        // When - DTO→Entity→DTOの相互変換
        Reservation entity = ReservationMapper.toEntity(testReservationRecord);
        ReservationRecord reconvertedDto = ReservationMapper.toDto(entity);

        // Then - 元のDTOと変換後のDTOが同じ値を持つことを確認
        assertThat("IDが保持されること", reconvertedDto.id(), equalTo(testReservationRecord.id()));
        assertThat("イベントIDが保持されること", reconvertedDto.event().id(), equalTo(testReservationRecord.event().id()));
        assertThat("メールアドレスが保持されること", reconvertedDto.email(), equalTo(testReservationRecord.email()));
        assertThat("数量が保持されること", reconvertedDto.quantity(), equalTo(testReservationRecord.quantity()));
        assertThat("合計金額が保持されること", reconvertedDto.totalPrice(), equalTo(testReservationRecord.totalPrice()));
        assertThat("確認コードが保持されること", reconvertedDto.confirmationCode(), equalTo(testReservationRecord.confirmationCode()));
        assertThat("予約時間が保持されること", reconvertedDto.reservationTime(), equalTo(testReservationRecord.reservationTime()));
        assertThat("ステータスが保持されること", reconvertedDto.status(), equalTo(testReservationRecord.status()));
        assertThat("作成日時が保持されること", reconvertedDto.createdAt(), equalTo(testReservationRecord.createdAt()));
        assertThat("更新日時が保持されること", reconvertedDto.updatedAt(), equalTo(testReservationRecord.updatedAt()));
    }

    @Test
    public void testDifferentReservationStatus_異なる予約ステータス() {
        // Given - CANCELLEDステータスの予約
        testReservation.setStatus(ReservationStatus.CANCELLED);

        // When - Entity→DTO変換
        ReservationRecord result = ReservationMapper.toDto(testReservation);

        // Then - ステータスが正しく変換されることを確認
        assertThat("CANCELLEDステータスが正しく変換されること", 
                  result.status(), equalTo(ReservationStatus.CANCELLED));

        // When - DTO→Entity変換
        ReservationRecord cancelledRecord = new ReservationRecord(
            result.id(), result.event(), result.email(), result.quantity(),
            result.totalPrice(), result.confirmationCode(), result.reservationTime(),
            ReservationStatus.CANCELLED, result.createdAt(), result.updatedAt()
        );
        Reservation reconvertedEntity = ReservationMapper.toEntity(cancelledRecord);

        // Then - ステータスが正しく変換されることを確認
        assertThat("CANCELLEDステータスが正しく変換されること", 
                  reconvertedEntity.getStatus(), equalTo(ReservationStatus.CANCELLED));
    }

    @Test
    public void testEventDataIntegrity_イベントデータ整合性() {
        // When - 予約をDTOに変換
        ReservationRecord result = ReservationMapper.toDto(testReservation);

        // Then - 予約内のイベント情報が完全に変換されることを確認
        EventRecord eventDto = result.event();
        assertThat("イベント情報が含まれること", eventDto, notNullValue());
        assertThat("イベント名が正しいこと", eventDto.eventName(), equalTo("テストコンサート"));
        assertThat("会場が正しいこと", eventDto.venue(), equalTo("東京ドーム"));
        assertThat("価格が正しいこと", eventDto.price(), equalTo(8000.0));
        
        // When - DTOをEntityに変換
        Reservation reconvertedReservation = ReservationMapper.toEntity(result);
        
        // Then - 予約内のイベント情報が完全に復元されることを確認
        Event reconvertedEvent = reconvertedReservation.getEvent();
        assertThat("イベント情報が復元されること", reconvertedEvent, notNullValue());
        assertThat("イベント名が復元されること", reconvertedEvent.getEventName(), equalTo("テストコンサート"));
        assertThat("会場が復元されること", reconvertedEvent.getVenue(), equalTo("東京ドーム"));
        assertThat("価格が復元されること", reconvertedEvent.getPrice(), equalTo(8000.0));
    }
}