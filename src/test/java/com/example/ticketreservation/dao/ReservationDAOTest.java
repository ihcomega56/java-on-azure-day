package com.example.ticketreservation.dao;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Reservation;

/**
 * ReservationDAOのテストクラス
 * 予約データアクセスのテストを実施
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-applicationContext.xml"})
@Transactional
public class ReservationDAOTest {

    @Autowired
    private ReservationDAO reservationDAO;

    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private Event testEvent;
    private Reservation testReservation1;
    private Reservation testReservation2;

    @Before
    public void setUp() {
        // テスト用イベントを作成・保存
        testEvent = new Event(
            "テストコンサート", 
            "テスト用イベントです", 
            LocalDateTime.now().plusDays(7), 
            "東京ドーム", 
            "音楽", 
            100, 
            5000.0
        );
        Long eventId = eventDAO.saveEvent(testEvent);
        testEvent.setId(eventId);
        
        // テスト用予約を作成
        testReservation1 = new Reservation(testEvent, "test1@example.com", 2, "ABCD1234");
        testReservation1.setStatus(Reservation.ReservationStatus.CONFIRMED);
        testReservation1.setReservationTime(LocalDateTime.now());
        testReservation1.setTotalPrice(10000.0);
        
        testReservation2 = new Reservation(testEvent, "test2@example.com", 1, "EFGH5678");
        testReservation2.setStatus(Reservation.ReservationStatus.CONFIRMED);
        testReservation2.setReservationTime(LocalDateTime.now());
        testReservation2.setTotalPrice(5000.0);
        
        // セッションをフラッシュして確実にデータベースに反映
        sessionFactory.getCurrentSession().flush();
    }

    @Test
    public void testSaveReservation_正常保存() {
        // Given - テストデータの準備は setUp() で完了

        // When - テスト対象メソッドの実行
        Long savedId = reservationDAO.saveReservation(testReservation1);

        // Then - 結果の検証
        assertThat("保存されたIDが正しく返されること", savedId, notNullValue());
        assertThat("保存されたIDが正の値であること", savedId > 0, is(true));
        
        // 保存されたデータを取得して確認
        Reservation saved = reservationDAO.getReservationById(savedId);
        assertThat("保存されたデータが正しく取得できること", saved, notNullValue());
        assertThat("メールアドレスが正しく保存されること", saved.getEmail(), equalTo("test1@example.com"));
        assertThat("数量が正しく保存されること", saved.getQuantity(), equalTo(2));
        assertThat("確認コードが正しく保存されること", saved.getConfirmationCode(), equalTo("ABCD1234"));
    }

    @Test
    public void testGetReservationById_正常取得() {
        // Given - テストデータの準備
        Long savedId = reservationDAO.saveReservation(testReservation1);
        sessionFactory.getCurrentSession().flush();

        // When - テスト対象メソッドの実行
        Reservation result = reservationDAO.getReservationById(savedId);

        // Then - 結果の検証
        assertThat("予約が正常に取得されること", result, notNullValue());
        assertThat("取得した予約のIDが正しいこと", result.getId(), equalTo(savedId));
        assertThat("取得した予約のメールアドレスが正しいこと", result.getEmail(), equalTo("test1@example.com"));
        assertThat("イベント情報が正しく取得されること", result.getEvent(), notNullValue());
        assertThat("イベント名が正しく取得されること", result.getEvent().getEventName(), equalTo("テストコンサート"));
    }

    @Test
    public void testGetReservationById_存在しないID() {
        // Given - テストデータの準備（存在しないID）
        Long nonExistentId = 999L;

        // When - テスト対象メソッドの実行
        Reservation result = reservationDAO.getReservationById(nonExistentId);

        // Then - 結果の検証
        assertThat("存在しないIDの場合はnullが返されること", result, nullValue());
    }

    @Test
    public void testGetReservationByConfirmationCode_正常取得() {
        // Given - テストデータの準備
        reservationDAO.saveReservation(testReservation1);
        sessionFactory.getCurrentSession().flush();

        // When - テスト対象メソッドの実行
        Reservation result = reservationDAO.getReservationByConfirmationCode("ABCD1234");

        // Then - 結果の検証
        assertThat("予約が正常に取得されること", result, notNullValue());
        assertThat("取得した予約の確認コードが正しいこと", result.getConfirmationCode(), equalTo("ABCD1234"));
        assertThat("取得した予約のメールアドレスが正しいこと", result.getEmail(), equalTo("test1@example.com"));
        assertThat("イベント情報が正しく取得されること", result.getEvent(), notNullValue());
    }

    @Test
    public void testGetReservationByConfirmationCode_存在しない確認コード() {
        // Given - テストデータの準備（存在しない確認コード）
        String nonExistentCode = "ZZZZ9999";

        // When - テスト対象メソッドの実行
        Reservation result = reservationDAO.getReservationByConfirmationCode(nonExistentCode);

        // Then - 結果の検証
        assertThat("存在しない確認コードの場合はnullが返されること", result, nullValue());
    }

    @Test
    public void testGetReservationsByEmail_正常取得() {
        // Given - テストデータの準備
        reservationDAO.saveReservation(testReservation1);
        sessionFactory.getCurrentSession().flush();

        // When - テスト対象メソッドの実行
        List<Reservation> result = reservationDAO.getReservationsByEmail("test1@example.com");

        // Then - 結果の検証
        assertThat("予約リストが正常に取得されること", result, notNullValue());
        assertThat("予約リストのサイズが正しいこと", result.size(), equalTo(1));
        assertThat("取得した予約のメールアドレスが正しいこと", result.get(0).getEmail(), equalTo("test1@example.com"));
        assertThat("イベント情報が正しく取得されること", result.get(0).getEvent(), notNullValue());
    }

    @Test
    public void testGetReservationsByEmail_複数予約() {
        // Given - テストデータの準備（同じメールアドレスで複数予約）
        Reservation additionalReservation = new Reservation(testEvent, "test1@example.com", 3, "IJKL9012");
        additionalReservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        additionalReservation.setReservationTime(LocalDateTime.now().plusMinutes(10));
        additionalReservation.setTotalPrice(15000.0);
        
        reservationDAO.saveReservation(testReservation1);
        reservationDAO.saveReservation(additionalReservation);
        sessionFactory.getCurrentSession().flush();

        // When - テスト対象メソッドの実行
        List<Reservation> result = reservationDAO.getReservationsByEmail("test1@example.com");

        // Then - 結果の検証
        assertThat("予約リストが正常に取得されること", result, notNullValue());
        assertThat("予約リストのサイズが正しいこと", result.size(), equalTo(2));
        // 予約時間の降順でソートされていることを確認
        assertThat("最新の予約が最初に表示されること", 
                  result.get(0).getReservationTime().isAfter(result.get(1).getReservationTime()), is(true));
    }

    @Test
    public void testGetReservationsByEmail_存在しないメールアドレス() {
        // Given - テストデータの準備（存在しないメールアドレス）
        String nonExistentEmail = "nonexistent@example.com";

        // When - テスト対象メソッドの実行
        List<Reservation> result = reservationDAO.getReservationsByEmail(nonExistentEmail);

        // Then - 結果の検証
        assertThat("予約リストが正常に取得されること", result, notNullValue());
        assertThat("存在しないメールアドレスの場合は空のリストが返されること", result.size(), equalTo(0));
    }

    @Test
    public void testGetReservationsByEventId_正常取得() {
        // Given - テストデータの準備
        reservationDAO.saveReservation(testReservation1);
        reservationDAO.saveReservation(testReservation2);
        sessionFactory.getCurrentSession().flush();

        // When - テスト対象メソッドの実行
        List<Reservation> result = reservationDAO.getReservationsByEventId(testEvent.getId());

        // Then - 結果の検証
        assertThat("予約リストが正常に取得されること", result, notNullValue());
        assertThat("予約リストのサイズが正しいこと", result.size(), equalTo(2));
        // 両方の予約が同じイベントIDを持つことを確認
        for (Reservation reservation : result) {
            assertThat("イベントIDが正しいこと", reservation.getEvent().getId(), equalTo(testEvent.getId()));
        }
    }

    @Test
    public void testGetAllReservations_正常取得() {
        // Given - テストデータの準備
        reservationDAO.saveReservation(testReservation1);
        reservationDAO.saveReservation(testReservation2);
        sessionFactory.getCurrentSession().flush();

        // When - テスト対象メソッドの実行
        List<Reservation> result = reservationDAO.getAllReservations();

        // Then - 結果の検証
        assertThat("全予約リストが正常に取得されること", result, notNullValue());
        assertThat("予約リストのサイズが正しいこと", result.size() >= 2, is(true));
        // イベント情報が正しく取得されることを確認
        for (Reservation reservation : result) {
            assertThat("イベント情報が正しく取得されること", reservation.getEvent(), notNullValue());
        }
    }

    @Test
    public void testUpdateReservation_正常更新() {
        // Given - テストデータの準備
        Long savedId = reservationDAO.saveReservation(testReservation1);
        sessionFactory.getCurrentSession().flush();
        
        // 保存されたデータを取得
        Reservation savedReservation = reservationDAO.getReservationById(savedId);
        
        // ステータスを変更
        savedReservation.setStatus(Reservation.ReservationStatus.CANCELLED);

        // When - テスト対象メソッドの実行
        reservationDAO.updateReservation(savedReservation);
        sessionFactory.getCurrentSession().flush();

        // Then - 結果の検証
        Reservation updatedReservation = reservationDAO.getReservationById(savedId);
        assertThat("予約ステータスが正しく更新されること", 
                  updatedReservation.getStatus(), equalTo(Reservation.ReservationStatus.CANCELLED));
    }

    @Test
    public void testDeleteReservation_正常削除() {
        // Given - テストデータの準備
        Long savedId = reservationDAO.saveReservation(testReservation1);
        sessionFactory.getCurrentSession().flush();
        
        // 保存されたデータを取得
        Reservation savedReservation = reservationDAO.getReservationById(savedId);

        // When - テスト対象メソッドの実行
        reservationDAO.deleteReservation(savedReservation);
        sessionFactory.getCurrentSession().flush();

        // Then - 結果の検証
        Reservation deletedReservation = reservationDAO.getReservationById(savedId);
        assertThat("削除された予約はnullで取得されること", deletedReservation, nullValue());
    }
}
