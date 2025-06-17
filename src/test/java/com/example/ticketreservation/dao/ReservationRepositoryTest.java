package com.example.ticketreservation.dao;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Reservation;

/**
 * ReservationRepositoryのテストクラス
 * 予約データアクセスのテストを実施
 */
@DataJpaTest
@ActiveProfiles("test")
public class ReservationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReservationRepository reservationRepository;

    private Event testEvent;
    private Reservation testReservation1;
    private Reservation testReservation2;

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
        testEvent = entityManager.persistAndFlush(testEvent);

        // テスト用予約データの準備
        testReservation1 = new Reservation(testEvent, "test1@example.com", 2, "ABCD1234");
        testReservation1.setStatus(Reservation.ReservationStatus.CONFIRMED);
        testReservation1.setReservationTime(LocalDateTime.now());
        testReservation1.setTotalPrice(10000.0);

        testReservation2 = new Reservation(testEvent, "test2@example.com", 1, "EFGH5678");
        testReservation2.setStatus(Reservation.ReservationStatus.CONFIRMED);
        testReservation2.setReservationTime(LocalDateTime.now());
        testReservation2.setTotalPrice(5000.0);
    }

    @Test
    public void testSave_正常保存() {
        // Given - テストデータの準備は setUp() で完了

        // When - テスト対象メソッドの実行
        Reservation saved = reservationRepository.save(testReservation1);

        // Then - 結果の検証
        assertThat("保存されたエンティティが返されること", saved, notNullValue());
        assertThat("IDが自動生成されること", saved.getId(), notNullValue());
        assertThat("イベントが正しく設定されること", saved.getEvent(), equalTo(testEvent));
        assertThat("メールアドレスが正しく保存されること", saved.getEmail(), equalTo("test1@example.com"));
        assertThat("数量が正しく保存されること", saved.getQuantity(), equalTo(2));
        assertThat("確認コードが正しく保存されること", saved.getConfirmationCode(), equalTo("ABCD1234"));
        assertThat("総価格が正しく保存されること", saved.getTotalPrice(), equalTo(10000.0));
        assertThat("ステータスが正しく保存されること", 
                  saved.getStatus(), equalTo(Reservation.ReservationStatus.CONFIRMED));
    }

    @Test
    public void testFindById_正常取得() {
        // Given - テストデータを保存
        Reservation saved = entityManager.persistAndFlush(testReservation1);

        // When - テスト対象メソッドの実行
        Optional<Reservation> result = reservationRepository.findById(saved.getId());

        // Then - 結果の検証
        assertThat("予約が取得できること", result.isPresent(), is(true));
        Reservation found = result.get();
        assertThat("メールアドレスが正しいこと", found.getEmail(), equalTo("test1@example.com"));
        assertThat("数量が正しいこと", found.getQuantity(), equalTo(2));
        assertThat("確認コードが正しいこと", found.getConfirmationCode(), equalTo("ABCD1234"));
    }

    @Test
    public void testFindById_存在しないID() {
        // Given - 存在しないID
        Long nonExistentId = 999L;

        // When - テスト対象メソッドの実行
        Optional<Reservation> result = reservationRepository.findById(nonExistentId);

        // Then - 結果の検証（Java 11+ Optional improved assertion）
        assertThat("存在しないIDの場合空のOptionalが返されること", result.isEmpty(), is(true));
    }

    @Test
    public void testFindByConfirmationCode_確認コードで検索() {
        // Given - テストデータを保存
        entityManager.persistAndFlush(testReservation1);

        // When - テスト対象メソッドの実行
        Reservation result = reservationRepository.findByConfirmationCode("ABCD1234");

        // Then - 結果の検証
        assertThat("確認コードで予約が取得できること", result, notNullValue());
        assertThat("メールアドレスが正しいこと", result.getEmail(), equalTo("test1@example.com"));
        assertThat("数量が正しいこと", result.getQuantity(), equalTo(2));
        assertThat("確認コードが正しいこと", result.getConfirmationCode(), equalTo("ABCD1234"));
    }

    @Test
    public void testFindByConfirmationCode_存在しない確認コード() {
        // Given - 存在しない確認コード
        String nonExistentCode = "INVALID";

        // When - テスト対象メソッドの実行
        Reservation result = reservationRepository.findByConfirmationCode(nonExistentCode);

        // Then - 結果の検証
        assertThat("存在しない確認コードの場合nullが返されること", result, nullValue());
    }

    @Test
    public void testFindByEmailOrderByReservationTimeDesc_メールアドレスで検索() {
        // Given - テストデータを保存
        entityManager.persistAndFlush(testReservation1);

        // When - テスト対象メソッドの実行
        List<Reservation> result = reservationRepository.findByEmailOrderByReservationTimeDesc("test1@example.com");

        // Then - 結果の検証
        assertThat("メールアドレスで予約が取得できること", result.size(), equalTo(1));
        Reservation found = result.get(0);
        assertThat("メールアドレスが正しいこと", found.getEmail(), equalTo("test1@example.com"));
        assertThat("数量が正しいこと", found.getQuantity(), equalTo(2));
        assertThat("確認コードが正しいこと", found.getConfirmationCode(), equalTo("ABCD1234"));
    }

    @Test
    public void testFindByEmailOrderByReservationTimeDesc_複数予約() {
        // Given - 同じメールアドレスで複数予約を保存
        Reservation additionalReservation = new Reservation(testEvent, "test1@example.com", 3, "IJKL9012");
        additionalReservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        
        entityManager.persistAndFlush(testReservation1);
        entityManager.persistAndFlush(additionalReservation);

        // When - テスト対象メソッドの実行
        List<Reservation> result = reservationRepository.findByEmailOrderByReservationTimeDesc("test1@example.com");

        // Then - 結果の検証
        assertThat("複数の予約が取得できること", result.size(), equalTo(2));
        
        // 結果に両方の予約が含まれることを確認
        boolean hasFirstReservation = result.stream().anyMatch(r -> "ABCD1234".equals(r.getConfirmationCode()));
        boolean hasSecondReservation = result.stream().anyMatch(r -> "IJKL9012".equals(r.getConfirmationCode()));
        
        assertThat("最初の予約が含まれること", hasFirstReservation, is(true));
        assertThat("追加の予約が含まれること", hasSecondReservation, is(true));
    }

    @Test
    public void testFindByEmailOrderByReservationTimeDesc_存在しないメールアドレス() {
        // Given - 存在しないメールアドレス
        String nonExistentEmail = "nonexistent@example.com";

        // When - テスト対象メソッドの実行
        List<Reservation> result = reservationRepository.findByEmailOrderByReservationTimeDesc(nonExistentEmail);

        // Then - 結果の検証
        assertThat("存在しないメールアドレスの場合空のリストが返されること", result.size(), equalTo(0));
    }

    @Test
    public void testFindByEventId_イベントIDで検索() {
        // Given - テストデータを保存
        entityManager.persistAndFlush(testReservation1);
        entityManager.persistAndFlush(testReservation2);

        // When - テスト対象メソッドの実行
        List<Reservation> result = reservationRepository.findByEventId(testEvent.getId());

        // Then - 結果の検証
        assertThat("該当イベントの予約が全て取得できること", result.size(), equalTo(2));
        
        // 全ての予約が同じイベントに関連していることを確認
        for (Reservation reservation : result) {
            assertThat("イベントIDが正しいこと", reservation.getEvent().getId(), equalTo(testEvent.getId()));
        }
    }

    @Test
    public void testFindAll_全件取得() {
        // Given - テストデータを保存
        entityManager.persistAndFlush(testReservation1);
        entityManager.persistAndFlush(testReservation2);

        // When - テスト対象メソッドの実行
        List<Reservation> result = reservationRepository.findAll();

        // Then - 結果の検証
        assertThat("全ての予約が取得できること", result.size(), equalTo(2));
        
        // 両方の予約が含まれることを確認
        boolean hasReservation1 = result.stream().anyMatch(r -> "test1@example.com".equals(r.getEmail()));
        boolean hasReservation2 = result.stream().anyMatch(r -> "test2@example.com".equals(r.getEmail()));
        
        assertThat("1つ目の予約が含まれること", hasReservation1, is(true));
        assertThat("2つ目の予約が含まれること", hasReservation2, is(true));
    }

    @Test
    public void testUpdate_ステータス更新() {
        // Given - テストデータを保存
        Reservation saved = entityManager.persistAndFlush(testReservation1);
        Long savedId = saved.getId();

        // When - ステータスを更新
        saved.setStatus(Reservation.ReservationStatus.CANCELLED);
        Reservation updated = reservationRepository.save(saved);
        entityManager.flush();

        // Then - 結果の検証
        assertThat("更新されたエンティティが返されること", updated, notNullValue());
        assertThat("IDが変わらないこと", updated.getId(), equalTo(savedId));
        assertThat("ステータスが更新されること", 
                  updated.getStatus(), equalTo(Reservation.ReservationStatus.CANCELLED));
        
        // データベースから再取得して確認
        Optional<Reservation> reloaded = reservationRepository.findById(savedId);
        assertThat("データベースの値も更新されること", reloaded.isPresent(), is(true));
        assertThat("ステータスがDBでも更新されること", 
                  reloaded.get().getStatus(), equalTo(Reservation.ReservationStatus.CANCELLED));
    }

    @Test
    public void testDelete_削除() {
        // Given - テストデータを保存
        Reservation saved = entityManager.persistAndFlush(testReservation1);
        Long savedId = saved.getId();

        // When - テスト対象メソッドの実行
        reservationRepository.deleteById(savedId);
        entityManager.flush();

        // Then - 結果の検証
        Optional<Reservation> deletedReservation = reservationRepository.findById(savedId);
        assertThat("削除された予約が取得できないこと", deletedReservation.isPresent(), is(false));
    }
}
