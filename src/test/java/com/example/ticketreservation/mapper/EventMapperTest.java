package com.example.ticketreservation.mapper;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.ticketreservation.dto.EventRecord;
import com.example.ticketreservation.model.Event;

/**
 * EventMapperクラスのテストクラス
 * エンティティとDTOの相互変換機能をテストする
 */
public class EventMapperTest {

    private Event testEvent;
    private EventRecord testEventRecord;
    private LocalDateTime testEventDate;
    private LocalDateTime testCreatedAt;
    private LocalDateTime testUpdatedAt;

    @BeforeEach
    public void setUp() {
        testEventDate = LocalDateTime.of(2025, 7, 15, 19, 0);
        testCreatedAt = LocalDateTime.of(2025, 6, 1, 10, 0);
        testUpdatedAt = LocalDateTime.of(2025, 6, 10, 15, 30);

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
    }

    @Test
    public void testToDto_正常変換() {
        // When - EntityをDTOに変換
        EventRecord result = EventMapper.toDto(testEvent);

        // Then - 結果の検証
        assertThat("変換結果がnullでないこと", result, notNullValue());
        assertThat("IDが正しく変換されること", result.id(), equalTo(testEvent.getId()));
        assertThat("イベント名が正しく変換されること", result.eventName(), equalTo(testEvent.getEventName()));
        assertThat("説明が正しく変換されること", result.description(), equalTo(testEvent.getDescription()));
        assertThat("イベント日時が正しく変換されること", result.eventDate(), equalTo(testEvent.getEventDate()));
        assertThat("会場が正しく変換されること", result.venue(), equalTo(testEvent.getVenue()));
        assertThat("カテゴリが正しく変換されること", result.category(), equalTo(testEvent.getCategory()));
        assertThat("総席数が正しく変換されること", result.totalSeats(), equalTo(testEvent.getTotalSeats()));
        assertThat("利用可能席数が正しく変換されること", result.availableSeats(), equalTo(testEvent.getAvailableSeats()));
        assertThat("価格が正しく変換されること", result.price(), equalTo(testEvent.getPrice()));
        assertThat("作成日時が正しく変換されること", result.createdAt(), equalTo(testEvent.getCreatedAt()));
        assertThat("更新日時が正しく変換されること", result.updatedAt(), equalTo(testEvent.getUpdatedAt()));
    }

    @Test
    public void testToDto_null値処理() {
        // When - nullのEntityをDTOに変換
        EventRecord result = EventMapper.toDto(null);

        // Then - nullが返されることを確認
        assertThat("null入力時はnullが返されること", result, nullValue());
    }

    @Test
    public void testToEntity_正常変換() {
        // When - DTOをEntityに変換
        Event result = EventMapper.toEntity(testEventRecord);

        // Then - 結果の検証
        assertThat("変換結果がnullでないこと", result, notNullValue());
        assertThat("IDが正しく変換されること", result.getId(), equalTo(testEventRecord.id()));
        assertThat("イベント名が正しく変換されること", result.getEventName(), equalTo(testEventRecord.eventName()));
        assertThat("説明が正しく変換されること", result.getDescription(), equalTo(testEventRecord.description()));
        assertThat("イベント日時が正しく変換されること", result.getEventDate(), equalTo(testEventRecord.eventDate()));
        assertThat("会場が正しく変換されること", result.getVenue(), equalTo(testEventRecord.venue()));
        assertThat("カテゴリが正しく変換されること", result.getCategory(), equalTo(testEventRecord.category()));
        assertThat("総席数が正しく変換されること", result.getTotalSeats(), equalTo(testEventRecord.totalSeats()));
        assertThat("利用可能席数が正しく変換されること", result.getAvailableSeats(), equalTo(testEventRecord.availableSeats()));
        assertThat("価格が正しく変換されること", result.getPrice(), equalTo(testEventRecord.price()));
        assertThat("作成日時が正しく変換されること", result.getCreatedAt(), equalTo(testEventRecord.createdAt()));
        assertThat("更新日時が正しく変換されること", result.getUpdatedAt(), equalTo(testEventRecord.updatedAt()));
    }

    @Test
    public void testToEntity_null値処理() {
        // When - nullのDTOをEntityに変換
        Event result = EventMapper.toEntity(null);

        // Then - nullが返されることを確認
        assertThat("null入力時はnullが返されること", result, nullValue());
    }

    @Test
    public void testRoundTrip_相互変換() {
        // When - Entity→DTO→Entityの相互変換
        EventRecord dto = EventMapper.toDto(testEvent);
        Event reconvertedEntity = EventMapper.toEntity(dto);

        // Then - 元のEntityと変換後のEntityが同じ値を持つことを確認
        assertThat("IDが保持されること", reconvertedEntity.getId(), equalTo(testEvent.getId()));
        assertThat("イベント名が保持されること", reconvertedEntity.getEventName(), equalTo(testEvent.getEventName()));
        assertThat("説明が保持されること", reconvertedEntity.getDescription(), equalTo(testEvent.getDescription()));
        assertThat("イベント日時が保持されること", reconvertedEntity.getEventDate(), equalTo(testEvent.getEventDate()));
        assertThat("会場が保持されること", reconvertedEntity.getVenue(), equalTo(testEvent.getVenue()));
        assertThat("カテゴリが保持されること", reconvertedEntity.getCategory(), equalTo(testEvent.getCategory()));
        assertThat("総席数が保持されること", reconvertedEntity.getTotalSeats(), equalTo(testEvent.getTotalSeats()));
        assertThat("利用可能席数が保持されること", reconvertedEntity.getAvailableSeats(), equalTo(testEvent.getAvailableSeats()));
        assertThat("価格が保持されること", reconvertedEntity.getPrice(), equalTo(testEvent.getPrice()));
        assertThat("作成日時が保持されること", reconvertedEntity.getCreatedAt(), equalTo(testEvent.getCreatedAt()));
        assertThat("更新日時が保持されること", reconvertedEntity.getUpdatedAt(), equalTo(testEvent.getUpdatedAt()));
    }

    @Test
    public void testReversedRoundTrip_逆方向相互変換() {
        // When - DTO→Entity→DTOの相互変換
        Event entity = EventMapper.toEntity(testEventRecord);
        EventRecord reconvertedDto = EventMapper.toDto(entity);

        // Then - 元のDTOと変換後のDTOが同じ値を持つことを確認
        assertThat("IDが保持されること", reconvertedDto.id(), equalTo(testEventRecord.id()));
        assertThat("イベント名が保持されること", reconvertedDto.eventName(), equalTo(testEventRecord.eventName()));
        assertThat("説明が保持されること", reconvertedDto.description(), equalTo(testEventRecord.description()));
        assertThat("イベント日時が保持されること", reconvertedDto.eventDate(), equalTo(testEventRecord.eventDate()));
        assertThat("会場が保持されること", reconvertedDto.venue(), equalTo(testEventRecord.venue()));
        assertThat("カテゴリが保持されること", reconvertedDto.category(), equalTo(testEventRecord.category()));
        assertThat("総席数が保持されること", reconvertedDto.totalSeats(), equalTo(testEventRecord.totalSeats()));
        assertThat("利用可能席数が保持されること", reconvertedDto.availableSeats(), equalTo(testEventRecord.availableSeats()));
        assertThat("価格が保持されること", reconvertedDto.price(), equalTo(testEventRecord.price()));
        assertThat("作成日時が保持されること", reconvertedDto.createdAt(), equalTo(testEventRecord.createdAt()));
        assertThat("更新日時が保持されること", reconvertedDto.updatedAt(), equalTo(testEventRecord.updatedAt()));
    }

    @Test
    public void testPartialData_部分的データ() {
        // Given - 一部がnullのEntity
        Event partialEvent = new Event();
        partialEvent.setId(2L);
        partialEvent.setEventName("部分的イベント");
        // 他のフィールドはnullまたはデフォルト値

        // When - 部分的データをDTOに変換
        EventRecord result = EventMapper.toDto(partialEvent);

        // Then - null値も正しく変換されることを確認
        assertThat("IDが正しく変換されること", result.id(), equalTo(2L));
        assertThat("イベント名が正しく変換されること", result.eventName(), equalTo("部分的イベント"));
        assertThat("説明がnullであること", result.description(), nullValue());
        assertThat("会場がnullであること", result.venue(), nullValue());
        assertThat("カテゴリがnullであること", result.category(), nullValue());
    }
}