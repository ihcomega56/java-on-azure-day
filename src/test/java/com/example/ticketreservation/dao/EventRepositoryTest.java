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

/**
 * EventRepositoryのテストクラス
 * イベントデータアクセスのテストを実施
 */
@DataJpaTest
@ActiveProfiles("test")
public class EventRepositoryTest {

    /**
     * テスト用イベントデータレコード
     */
    record TestEventInfo(String name, String description, String venue, String category, Integer seats, Double price) {
        static final TestEventInfo CONCERT = new TestEventInfo(
            "テストコンサート1", "テスト用音楽イベント", "東京ドーム", "音楽", 100, 5000.0
        );
        static final TestEventInfo BASEBALL = new TestEventInfo(
            "テスト野球試合", "テスト用スポーツイベント", "東京ドーム", "スポーツ", 200, 3000.0
        );
        static final TestEventInfo PAST_EVENT = new TestEventInfo(
            "過去のイベント", "過去のテストイベント", "過去会場", "過去", 50, 2000.0
        );
        static final TestEventInfo SOLD_OUT = new TestEventInfo(
            "売り切れイベント", "売り切れテストイベント", "満席会場", "満席", 0, 8000.0
        );
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;

    private Event futureEvent1;
    private Event futureEvent2;
    private Event pastEvent;
    private Event soldOutEvent;

    @BeforeEach
    public void setUp() {
        // 未来のイベント1（音楽カテゴリ）
        futureEvent1 = new Event(
            "テストコンサート1", 
            "テスト用音楽イベント", 
            LocalDateTime.now().plusDays(7), 
            "東京ドーム", 
            "音楽", 
            100, 
            5000.0
        );
        
        // 未来のイベント2（スポーツカテゴリ）
        futureEvent2 = new Event(
            "テスト野球試合", 
            "テスト用スポーツイベント", 
            LocalDateTime.now().plusDays(14), 
            "東京ドーム", 
            "スポーツ", 
            200, 
            3000.0
        );
        
        // 過去のイベント
        pastEvent = new Event(
            "過去のコンサート", 
            "過去のテストイベント", 
            LocalDateTime.now().minusDays(7), 
            "横浜アリーナ", 
            "音楽", 
            150, 
            4000.0
        );
        
        // 売り切れイベント
        soldOutEvent = new Event(
            "売り切れコンサート", 
            "売り切れテストイベント", 
            LocalDateTime.now().plusDays(21), 
            "さいたまスーパーアリーナ", 
            "音楽", 
            50, 
            6000.0
        );
        soldOutEvent.setAvailableSeats(0); // 売り切れ状態
    }

    @Test
    public void testSave_正常保存() {
        // Given - テストデータの準備は setUp() で完了

        // When - テスト対象メソッドの実行
        Event saved = eventRepository.save(futureEvent1);

        // Then - 結果の検証
        assertThat("保存されたエンティティが返されること", saved, notNullValue());
        assertThat("IDが自動生成されること", saved.getId(), notNullValue());
        assertThat("イベント名が正しく保存されること", saved.getEventName(), equalTo("テストコンサート1"));
        assertThat("会場が正しく保存されること", saved.getVenue(), equalTo("東京ドーム"));
        assertThat("カテゴリが正しく保存されること", saved.getCategory(), equalTo("音楽"));
        assertThat("総席数が正しく保存されること", saved.getTotalSeats(), equalTo(100));
        assertThat("利用可能席数が正しく保存されること", saved.getAvailableSeats(), equalTo(100));
        assertThat("価格が正しく保存されること", saved.getPrice(), equalTo(5000.0));
    }

    @Test
    public void testFindById_正常取得() {
        // Given - テストデータを保存
        Event saved = entityManager.persistAndFlush(futureEvent1);

        // When - テスト対象メソッドの実行
        Optional<Event> result = eventRepository.findById(saved.getId());

        // Then - 結果の検証
        assertThat("イベントが取得できること", result.isPresent(), is(true));
        Event found = result.get();
        assertThat("イベント名が正しいこと", found.getEventName(), equalTo("テストコンサート1"));
        assertThat("会場が正しいこと", found.getVenue(), equalTo("東京ドーム"));
        assertThat("カテゴリが正しいこと", found.getCategory(), equalTo("音楽"));
    }

    @Test
    public void testFindById_存在しないID() {
        // Given - 存在しないID
        Long nonExistentId = 999L;

        // When - テスト対象メソッドの実行
        Optional<Event> result = eventRepository.findById(nonExistentId);

        // Then - 結果の検証
        assertThat("存在しないIDの場合空のOptionalが返されること", result.isPresent(), is(false));
    }

    @Test
    public void testFindAll_全件取得() {
        // Given - テストデータを保存
        entityManager.persistAndFlush(futureEvent1);
        entityManager.persistAndFlush(futureEvent2);
        entityManager.persistAndFlush(pastEvent);

        // When - テスト対象メソッドの実行
        List<Event> result = eventRepository.findAll();

        // Then - 結果の検証
        assertThat("全てのイベントが取得できること", result.size(), equalTo(3));
        
        // イベント日時でソートされているかは実装依存なので、とりあえず件数のみ確認
        boolean containsEvent1 = result.stream().anyMatch(e -> "テストコンサート1".equals(e.getEventName()));
        boolean containsEvent2 = result.stream().anyMatch(e -> "テスト野球試合".equals(e.getEventName()));
        boolean containsPastEvent = result.stream().anyMatch(e -> "過去のコンサート".equals(e.getEventName()));
        
        assertThat("未来のイベント1が含まれること", containsEvent1, is(true));
        assertThat("未来のイベント2が含まれること", containsEvent2, is(true));
        assertThat("過去のイベントが含まれること", containsPastEvent, is(true));
    }

    @Test
    public void testFindAvailableEvents_利用可能イベント() {
        // Given - テストデータを保存
        entityManager.persistAndFlush(futureEvent1);
        entityManager.persistAndFlush(futureEvent2);
        entityManager.persistAndFlush(pastEvent); // 過去のイベントは除外される
        entityManager.persistAndFlush(soldOutEvent); // 売り切れイベントは除外される

        // When - テスト対象メソッドの実行
        LocalDateTime now = LocalDateTime.now();
        List<Event> result = eventRepository.findAvailableEvents(now);

        // Then - 結果の検証
        assertThat("利用可能なイベントのみ取得されること", result.size(), equalTo(2));
        
        // 全てのイベントが未来で、利用可能席数が0より大きいことを確認
        for (Event event : result) {
            assertThat("イベント日時が現在より後であること", 
                      event.getEventDate().isAfter(now), is(true));
            assertThat("利用可能席数が0より大きいこと", 
                      event.getAvailableSeats() > 0, is(true));
        }
    }

    @Test
    public void testFindEventsByCategory_カテゴリ別未来イベント() {
        // Given - テストデータを保存
        entityManager.persistAndFlush(futureEvent1); // 音楽カテゴリ
        entityManager.persistAndFlush(futureEvent2); // スポーツカテゴリ
        entityManager.persistAndFlush(pastEvent); // 音楽カテゴリだが過去のイベント

        // When - テスト対象メソッドの実行
        LocalDateTime now = LocalDateTime.now();
        List<Event> result = eventRepository.findEventsByCategory("音楽", now);

        // Then - 結果の検証
        assertThat("音楽カテゴリの未来イベントのみ取得されること", result.size(), equalTo(1));
        Event musicEvent = result.get(0);
        assertThat("カテゴリが音楽であること", musicEvent.getCategory(), equalTo("音楽"));
        assertThat("イベント日時が現在より後であること", 
                  musicEvent.getEventDate().isAfter(now), is(true));
        assertThat("イベント名が正しいこと", musicEvent.getEventName(), equalTo("テストコンサート1"));
    }

    @Test
    public void testFindEventsByCategory_存在しないカテゴリ() {
        // Given - テストデータを保存
        entityManager.persistAndFlush(futureEvent1);

        // When - 存在しないカテゴリでの検索
        LocalDateTime now = LocalDateTime.now();
        List<Event> result = eventRepository.findEventsByCategory("演劇", now);

        // Then - 結果の検証
        assertThat("該当するイベントが0件であること", result.size(), equalTo(0));
    }

    @Test
    public void testFindEventsByDateRange_指定日範囲のイベント() {
        // Given - テストデータを保存
        entityManager.persistAndFlush(futureEvent1); // 7日後
        entityManager.persistAndFlush(futureEvent2); // 14日後
        entityManager.persistAndFlush(pastEvent); // 過去

        // When - 10日後から20日後までの範囲で検索
        LocalDateTime startDate = LocalDateTime.now().plusDays(10);
        LocalDateTime endDate = LocalDateTime.now().plusDays(20);
        List<Event> result = eventRepository.findEventsByDateRange(startDate, endDate);

        // Then - 結果の検証
        assertThat("指定範囲内のイベントのみ取得されること", result.size(), equalTo(1));
        Event event = result.get(0);
        assertThat("取得されたイベントが正しいこと", event.getEventName(), equalTo("テスト野球試合"));
        assertThat("イベント日時が範囲内であること", 
                  event.getEventDate().isAfter(startDate) && event.getEventDate().isBefore(endDate), is(true));
    }

    @Test
    public void testDelete_削除() {
        // Given - テストデータを保存
        Event saved = entityManager.persistAndFlush(futureEvent1);
        Long savedId = saved.getId();

        // When - テスト対象メソッドの実行
        eventRepository.deleteById(savedId);
        entityManager.flush();

        // Then - 結果の検証
        Optional<Event> deletedEvent = eventRepository.findById(savedId);
        assertThat("削除されたイベントが取得できないこと", deletedEvent.isPresent(), is(false));
    }

    @Test
    public void testUpdate_更新() {
        // Given - テストデータを保存
        Event saved = entityManager.persistAndFlush(futureEvent1);
        Long savedId = saved.getId();

        // When - データを更新
        saved.setAvailableSeats(80);
        saved.setPrice(6000.0);
        Event updated = eventRepository.save(saved);
        entityManager.flush();

        // Then - 結果の検証
        assertThat("更新されたエンティティが返されること", updated, notNullValue());
        assertThat("IDが変わらないこと", updated.getId(), equalTo(savedId));
        assertThat("利用可能席数が更新されること", updated.getAvailableSeats(), equalTo(80));
        assertThat("価格が更新されること", updated.getPrice(), equalTo(6000.0));
        
        // データベースから再取得して確認
        Optional<Event> reloaded = eventRepository.findById(savedId);
        assertThat("データベースの値も更新されること", reloaded.isPresent(), is(true));
        assertThat("利用可能席数がDBでも更新されること", reloaded.get().getAvailableSeats(), equalTo(80));
        assertThat("価格がDBでも更新されること", reloaded.get().getPrice(), equalTo(6000.0));
    }
}
