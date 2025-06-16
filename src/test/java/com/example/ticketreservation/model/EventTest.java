package com.example.ticketreservation.model;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Eventモデルクラスのテストクラス
 * エンティティクラスの基本動作をテストする
 */
public class EventTest {

    private Event event;
    private LocalDateTime testEventDate;

    @Before
    public void setUp() {
        testEventDate = LocalDateTime.now().plusDays(7);
    }

    @Test
    public void testDefaultConstructor_デフォルトコンストラクタ() {
        // When - デフォルトコンストラクタでの作成
        Event event = new Event();

        // Then - 結果の検証
        assertThat("作成日時が自動設定されること", event.getCreatedAt(), notNullValue());
        assertThat("更新日時が自動設定されること", event.getUpdatedAt(), notNullValue());
        
        // 設定された時間が現在時刻に近いことを確認（1分以内）
        LocalDateTime now = LocalDateTime.now();
        assertThat("作成日時が現在時刻に近いこと", 
                  event.getCreatedAt().isBefore(now.plusMinutes(1)), is(true));
        assertThat("更新日時が現在時刻に近いこと", 
                  event.getUpdatedAt().isBefore(now.plusMinutes(1)), is(true));
    }

    @Test
    public void testParameterizedConstructor_パラメータ付きコンストラクタ() {
        // Given - テストデータの準備
        String eventName = "テストコンサート";
        String description = "テスト用イベントです";
        String venue = "東京ドーム";
        String category = "音楽";
        Integer totalSeats = 100;
        Double price = 5000.0;

        // When - パラメータ付きコンストラクタでの作成
        Event event = new Event(eventName, description, testEventDate, venue, category, totalSeats, price);

        // Then - 結果の検証
        assertThat("イベント名が正しく設定されること", event.getEventName(), equalTo(eventName));
        assertThat("説明が正しく設定されること", event.getDescription(), equalTo(description));
        assertThat("イベント日時が正しく設定されること", event.getEventDate(), equalTo(testEventDate));
        assertThat("会場が正しく設定されること", event.getVenue(), equalTo(venue));
        assertThat("カテゴリが正しく設定されること", event.getCategory(), equalTo(category));
        assertThat("総席数が正しく設定されること", event.getTotalSeats(), equalTo(totalSeats));
        assertThat("利用可能席数が総席数と同じに設定されること", event.getAvailableSeats(), equalTo(totalSeats));
        assertThat("価格が正しく設定されること", event.getPrice(), equalTo(price));
        assertThat("作成日時が自動設定されること", event.getCreatedAt(), notNullValue());
        assertThat("更新日時が自動設定されること", event.getUpdatedAt(), notNullValue());
    }

    @Test
    public void testGettersAndSetters_ゲッターセッター() {
        // Given - テストデータの準備
        event = new Event();
        Long id = 1L;
        String eventName = "テストイベント";
        String description = "テスト説明";
        String venue = "テスト会場";
        String category = "テストカテゴリ";
        Integer totalSeats = 200;
        Integer availableSeats = 150;
        Double price = 3000.0;
        LocalDateTime eventDate = LocalDateTime.now().plusDays(10);

        // When - セッターでの値設定
        event.setId(id);
        event.setEventName(eventName);
        event.setDescription(description);
        event.setEventDate(eventDate);
        event.setVenue(venue);
        event.setCategory(category);
        event.setTotalSeats(totalSeats);
        event.setAvailableSeats(availableSeats);
        event.setPrice(price);

        // Then - ゲッターでの値確認
        assertThat("IDが正しく設定・取得されること", event.getId(), equalTo(id));
        assertThat("イベント名が正しく設定・取得されること", event.getEventName(), equalTo(eventName));
        assertThat("説明が正しく設定・取得されること", event.getDescription(), equalTo(description));
        assertThat("イベント日時が正しく設定・取得されること", event.getEventDate(), equalTo(eventDate));
        assertThat("会場が正しく設定・取得されること", event.getVenue(), equalTo(venue));
        assertThat("カテゴリが正しく設定・取得されること", event.getCategory(), equalTo(category));
        assertThat("総席数が正しく設定・取得されること", event.getTotalSeats(), equalTo(totalSeats));
        assertThat("利用可能席数が正しく設定・取得されること", event.getAvailableSeats(), equalTo(availableSeats));
        assertThat("価格が正しく設定・取得されること", event.getPrice(), equalTo(price));
    }

    @Test
    public void testPreUpdate_更新前処理() {
        // Given - テストデータの準備
        event = new Event();
        LocalDateTime initialUpdatedAt = event.getUpdatedAt();
        
        // 少し時間を経過させる
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // テスト用なので無視
        }

        // When - preUpdateメソッドの実行
        event.preUpdate();

        // Then - 結果の検証
        assertThat("更新日時がpreUpdate実行後に更新されること", 
                  event.getUpdatedAt().isAfter(initialUpdatedAt), is(true));
    }

    @Test
    public void testReservationsRelationship_予約リレーション() {
        // Given - テストデータの準備
        event = new Event("テストイベント", "説明", testEventDate, "会場", "カテゴリ", 100, 5000.0);
        event.setId(1L);
        
        List<Reservation> reservations = new ArrayList<>();
        Reservation reservation1 = new Reservation(event, "test1@example.com", 2, "CODE001");
        Reservation reservation2 = new Reservation(event, "test2@example.com", 3, "CODE002");
        reservations.add(reservation1);
        reservations.add(reservation2);

        // When - 予約リストの設定
        event.setReservations(reservations);

        // Then - 結果の検証
        assertThat("予約リストが正しく設定されること", event.getReservations(), notNullValue());
        assertThat("予約リストのサイズが正しいこと", event.getReservations().size(), equalTo(2));
        assertThat("予約リストの内容が正しいこと", event.getReservations(), equalTo(reservations));
    }

    @Test
    public void testToString_文字列表現() {
        // Given - テストデータの準備
        event = new Event("テストコンサート", "説明", testEventDate, "東京ドーム", "音楽", 100, 5000.0);
        event.setId(1L);

        // When - toString()メソッドの実行
        String result = event.toString();

        // Then - 結果の検証
        assertThat("toString()が正常に実行されること", result, notNullValue());
        assertThat("IDが文字列に含まれること", result.contains("id=1"), is(true));
        assertThat("イベント名が文字列に含まれること", result.contains("テストコンサート"), is(true));
        assertThat("会場が文字列に含まれること", result.contains("東京ドーム"), is(true));
        assertThat("カテゴリが文字列に含まれること", result.contains("音楽"), is(true));
        assertThat("価格が文字列に含まれること", result.contains("5000.0"), is(true));
    }

    @Test
    public void testAvailableSeatsInitialization_利用可能席数初期化() {
        // Given & When - パラメータ付きコンストラクタでイベント作成
        Integer totalSeats = 250;
        Event event = new Event("イベント", "説明", testEventDate, "会場", "カテゴリ", totalSeats, 8000.0);

        // Then - 利用可能席数が総席数と同じに初期化されることを確認
        assertThat("利用可能席数が総席数と同じに初期化されること", 
                  event.getAvailableSeats(), equalTo(totalSeats));
    }

    @Test
    public void testCreatedAtAndUpdatedAt_作成日時と更新日時() {
        // When - 新しいイベントの作成
        event = new Event();
        LocalDateTime createdAt = event.getCreatedAt();
        LocalDateTime updatedAt = event.getUpdatedAt();

        // Then - 作成時の確認
        assertThat("作成日時が設定されること", createdAt, notNullValue());
        assertThat("更新日時が設定されること", updatedAt, notNullValue());
        assertThat("作成日時と更新日時が同じであること", createdAt, equalTo(updatedAt));
        
        // 時間を進めてから更新
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // テスト用なので無視
        }
        
        LocalDateTime newUpdatedAt = LocalDateTime.now();
        event.setUpdatedAt(newUpdatedAt);
        
        assertThat("更新日時が変更できること", 
                  event.getUpdatedAt(), equalTo(newUpdatedAt));
        assertThat("作成日時は変更されないこと", 
                  event.getCreatedAt(), equalTo(createdAt));
    }

    @Test
    public void testNullValues_null値の処理() {
        // Given - 一部nullを含むイベント
        event = new Event();
        
        // When - null値の設定
        event.setEventName(null);
        event.setDescription(null);
        event.setVenue(null);
        event.setCategory(null);

        // Then - null値が正しく処理されることを確認
        assertThat("イベント名がnullに設定されること", event.getEventName(), nullValue());
        assertThat("説明がnullに設定されること", event.getDescription(), nullValue());
        assertThat("会場がnullに設定されること", event.getVenue(), nullValue());
        assertThat("カテゴリがnullに設定されること", event.getCategory(), nullValue());
    }

    @Test
    public void testNumericValues_数値の処理() {
        // Given - 数値データのテスト
        event = new Event();
        
        // When - 様々な数値の設定
        event.setTotalSeats(0);
        event.setAvailableSeats(-1); // 負の値
        event.setPrice(0.0);

        // Then - 数値が正しく処理されることを確認
        assertThat("総席数が0に設定されること", event.getTotalSeats(), equalTo(0));
        assertThat("利用可能席数が負の値に設定されること", event.getAvailableSeats(), equalTo(-1));
        assertThat("価格が0.0に設定されること", event.getPrice(), equalTo(0.0));
    }
}
