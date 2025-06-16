package com.example.ticketreservation.model;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Reservationモデルクラスのテストクラス
 * エンティティクラスの基本動作をテストする
 */
public class ReservationTest {

    private Event testEvent;
    private Reservation reservation;

    @BeforeEach
    public void setUp() {
        // テスト用イベントの準備
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
    }

    @Test
    public void testDefaultConstructor_デフォルトコンストラクタ() {
        // When - デフォルトコンストラクタでの作成
        Reservation reservation = new Reservation();

        // Then - 結果の検証
        assertThat("予約時間が自動設定されること", reservation.getReservationTime(), notNullValue());
        assertThat("作成日時が自動設定されること", reservation.getCreatedAt(), notNullValue());
        assertThat("更新日時が自動設定されること", reservation.getUpdatedAt(), notNullValue());
        
        // 設定された時間が現在時刻に近いことを確認（1分以内）
        LocalDateTime now = LocalDateTime.now();
        assertThat("予約時間が現在時刻に近いこと", 
                  reservation.getReservationTime().isBefore(now.plusMinutes(1)), is(true));
        assertThat("作成日時が現在時刻に近いこと", 
                  reservation.getCreatedAt().isBefore(now.plusMinutes(1)), is(true));
    }

    @Test
    public void testParameterizedConstructor_パラメータ付きコンストラクタ() {
        // Given - テストデータの準備
        String email = "test@example.com";
        Integer quantity = 2;
        String confirmationCode = "ABCD1234";

        // When - パラメータ付きコンストラクタでの作成
        Reservation reservation = new Reservation(testEvent, email, quantity, confirmationCode);

        // Then - 結果の検証
        assertThat("イベントが正しく設定されること", reservation.getEvent(), equalTo(testEvent));
        assertThat("メールアドレスが正しく設定されること", reservation.getEmail(), equalTo(email));
        assertThat("数量が正しく設定されること", reservation.getQuantity(), equalTo(quantity));
        assertThat("確認コードが正しく設定されること", reservation.getConfirmationCode(), equalTo(confirmationCode));
        assertThat("総価格が自動計算されること", reservation.getTotalPrice(), equalTo(10000.0)); // 5000 * 2
        assertThat("ステータスがCONFIRMEDに設定されること", 
                  reservation.getStatus(), equalTo(Reservation.ReservationStatus.CONFIRMED));
    }

    @Test
    public void testGettersAndSetters_ゲッターセッター() {
        // Given - テストデータの準備
        reservation = new Reservation();
        Long id = 1L;
        String email = "test@example.com";
        Integer quantity = 3;
        Double totalPrice = 15000.0;
        String confirmationCode = "EFGH5678";
        LocalDateTime reservationTime = LocalDateTime.now();
        Reservation.ReservationStatus status = Reservation.ReservationStatus.CANCELLED;

        // When - セッターでの値設定
        reservation.setId(id);
        reservation.setEvent(testEvent);
        reservation.setEmail(email);
        reservation.setQuantity(quantity);
        reservation.setTotalPrice(totalPrice);
        reservation.setConfirmationCode(confirmationCode);
        reservation.setReservationTime(reservationTime);
        reservation.setStatus(status);

        // Then - ゲッターでの値確認
        assertThat("IDが正しく設定・取得されること", reservation.getId(), equalTo(id));
        assertThat("イベントが正しく設定・取得されること", reservation.getEvent(), equalTo(testEvent));
        assertThat("メールアドレスが正しく設定・取得されること", reservation.getEmail(), equalTo(email));
        assertThat("数量が正しく設定・取得されること", reservation.getQuantity(), equalTo(quantity));
        assertThat("総価格が正しく設定・取得されること", reservation.getTotalPrice(), equalTo(totalPrice));
        assertThat("確認コードが正しく設定・取得されること", reservation.getConfirmationCode(), equalTo(confirmationCode));
        assertThat("予約時間が正しく設定・取得されること", reservation.getReservationTime(), equalTo(reservationTime));
        assertThat("ステータスが正しく設定・取得されること", reservation.getStatus(), equalTo(status));
    }

    @Test
    public void testPreUpdate_更新前処理() {
        // Given - テストデータの準備
        reservation = new Reservation();
        LocalDateTime initialUpdatedAt = reservation.getUpdatedAt();
        
        // 少し時間を経過させる
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // テスト用なので無視
        }

        // When - preUpdateメソッドの実行
        reservation.preUpdate();

        // Then - 結果の検証
        assertThat("更新日時がpreUpdate実行後に更新されること", 
                  reservation.getUpdatedAt().isAfter(initialUpdatedAt), is(true));
    }

    @Test
    public void testReservationStatus_ステータス列挙型() {
        // When & Then - 列挙型の値確認
        Reservation.ReservationStatus[] statuses = Reservation.ReservationStatus.values();
        
        assertThat("ステータスの種類数が正しいこと", statuses.length, equalTo(2));
        assertThat("CONFIRMEDステータスが存在すること", 
                  Reservation.ReservationStatus.CONFIRMED, notNullValue());
        assertThat("CANCELLEDステータスが存在すること", 
                  Reservation.ReservationStatus.CANCELLED, notNullValue());
        
        // valueOf()メソッドのテスト
        assertThat("文字列からCONFIRMEDステータスが取得できること", 
                  Reservation.ReservationStatus.valueOf("CONFIRMED"), 
                  equalTo(Reservation.ReservationStatus.CONFIRMED));
        assertThat("文字列からCANCELLEDステータスが取得できること", 
                  Reservation.ReservationStatus.valueOf("CANCELLED"), 
                  equalTo(Reservation.ReservationStatus.CANCELLED));
    }

    @Test
    public void testToString_文字列表現() {
        // Given - テストデータの準備
        reservation = new Reservation(testEvent, "test@example.com", 2, "ABCD1234");
        reservation.setId(1L);

        // When - toString()メソッドの実行
        String result = reservation.toString();

        // Then - 結果の検証
        assertThat("toString()が正常に実行されること", result, notNullValue());
        assertThat("IDが文字列に含まれること", result.contains("id=1"), is(true));
        assertThat("メールアドレスが文字列に含まれること", result.contains("test@example.com"), is(true));
        assertThat("数量が文字列に含まれること", result.contains("quantity=2"), is(true));
        assertThat("確認コードが文字列に含まれること", result.contains("ABCD1234"), is(true));
    }

    @Test
    public void testCalculateTotalPrice_総価格計算() {
        // Given - 異なる価格のイベント
        Event expensiveEvent = new Event(
            "高額イベント", 
            "高額テストイベント", 
            LocalDateTime.now().plusDays(10), 
            "高級会場", 
            "プレミアム", 
            50, 
            25000.0
        );

        // When - 複数枚予約の作成
        Reservation expensiveReservation = new Reservation(expensiveEvent, "test@example.com", 3, "IJKL9012");

        // Then - 総価格の確認
        assertThat("総価格が正しく計算されること", 
                  expensiveReservation.getTotalPrice(), equalTo(75000.0)); // 25000 * 3
    }

    @Test
    public void testCreatedAtAndUpdatedAt_作成日時と更新日時() {
        // When - 新しい予約の作成
        reservation = new Reservation();
        LocalDateTime createdAt = reservation.getCreatedAt();
        LocalDateTime updatedAt = reservation.getUpdatedAt();

        // Then - 作成時の確認
        // Then - 作成時の確認
        assertThat("作成日時が設定されること", createdAt, notNullValue());
        assertThat("更新日時が設定されること", updatedAt, notNullValue());
        // マイクロ秒の精度で微小な差が生じる可能性があるため、秒の部分が同じであることを確認
        assertThat("作成日時と更新日時の秒数が同じであること", 
                  createdAt.withNano(0), equalTo(updatedAt.withNano(0)));
        
        // 時間を進めてから更新
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // テスト用なので無視
        }
        
        LocalDateTime newUpdatedAt = LocalDateTime.now();
        reservation.setUpdatedAt(newUpdatedAt);
        
        assertThat("更新日時が変更できること", 
                  reservation.getUpdatedAt(), equalTo(newUpdatedAt));
        assertThat("作成日時は変更されないこと", 
                  reservation.getCreatedAt(), equalTo(createdAt));
    }
}
