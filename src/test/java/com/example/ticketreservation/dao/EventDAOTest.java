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

/**
 * EventDAOのテストクラス
 * イベントデータアクセスのテストを実施
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-applicationContext.xml"})
@Transactional
public class EventDAOTest {

    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private Event futureEvent1;
    private Event futureEvent2;
    private Event pastEvent;
    private Event soldOutEvent;

    @Before
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
    public void testSaveEvent_正常保存() {
        // Given - テストデータの準備は setUp() で完了

        // When - テスト対象メソッドの実行
        Long savedId = eventDAO.saveEvent(futureEvent1);

        // Then - 結果の検証
        assertThat("保存されたIDが正しく返されること", savedId, notNullValue());
        assertThat("保存されたIDが正の値であること", savedId > 0, is(true));
        
        // 保存されたデータを取得して確認
        Event saved = eventDAO.getEventById(savedId);
        assertThat("保存されたデータが正しく取得できること", saved, notNullValue());
        assertThat("イベント名が正しく保存されること", saved.getEventName(), equalTo("テストコンサート1"));
        assertThat("会場が正しく保存されること", saved.getVenue(), equalTo("東京ドーム"));
        assertThat("カテゴリが正しく保存されること", saved.getCategory(), equalTo("音楽"));
        assertThat("料金が正しく保存されること", saved.getPrice(), equalTo(5000.0));
    }

    @Test
    public void testGetEventById_正常取得() {
        // Given - テストデータの準備
        Long savedId = eventDAO.saveEvent(futureEvent1);
        sessionFactory.getCurrentSession().flush();

        // When - テスト対象メソッドの実行
        Event result = eventDAO.getEventById(savedId);

        // Then - 結果の検証
        assertThat("イベントが正常に取得されること", result, notNullValue());
        assertThat("取得したイベントのIDが正しいこと", result.getId(), equalTo(savedId));
        assertThat("取得したイベント名が正しいこと", result.getEventName(), equalTo("テストコンサート1"));
        assertThat("取得したイベントの会場が正しいこと", result.getVenue(), equalTo("東京ドーム"));
    }

    @Test
    public void testGetEventById_存在しないID() {
        // Given - テストデータの準備（存在しないID）
        Long nonExistentId = 999L;

        // When - テスト対象メソッドの実行
        Event result = eventDAO.getEventById(nonExistentId);

        // Then - 結果の検証
        assertThat("存在しないIDの場合はnullが返されること", result, nullValue());
    }

    @Test
    public void testGetAllEvents_正常取得() {
        // Given - テストデータの準備
        eventDAO.saveEvent(futureEvent1);
        eventDAO.saveEvent(futureEvent2);
        eventDAO.saveEvent(pastEvent);
        sessionFactory.getCurrentSession().flush();

        // When - テスト対象メソッドの実行
        List<Event> result = eventDAO.getAllEvents();

        // Then - 結果の検証
        assertThat("全イベントリストが正常に取得されること", result, notNullValue());
        assertThat("イベントリストのサイズが正しいこと", result.size() >= 3, is(true));
        
        // イベント日付でソートされていることを確認（最初の要素と最後の要素の比較）
        if (result.size() >= 2) {
            LocalDateTime firstEventDate = result.get(0).getEventDate();
            LocalDateTime lastEventDate = result.get(result.size() - 1).getEventDate();
            assertThat("イベントが日付順でソートされていること", 
                      firstEventDate.isBefore(lastEventDate) || firstEventDate.isEqual(lastEventDate), is(true));
        }
    }

    @Test
    public void testGetAvailableEvents_正常取得() {
        // Given - テストデータの準備
        eventDAO.saveEvent(futureEvent1);
        eventDAO.saveEvent(futureEvent2);
        eventDAO.saveEvent(pastEvent); // 過去のイベントは除外される
        eventDAO.saveEvent(soldOutEvent); // 売り切れイベントは除外される
        sessionFactory.getCurrentSession().flush();

        // When - テスト対象メソッドの実行
        List<Event> result = eventDAO.getAvailableEvents();

        // Then - 結果の検証
        assertThat("利用可能イベントリストが正常に取得されること", result, notNullValue());
        
        // 利用可能なイベントのみが取得されることを確認
        for (Event event : result) {
            assertThat("未来の日付のイベントのみ取得されること", 
                      event.getEventDate().isAfter(LocalDateTime.now()), is(true));
            assertThat("利用可能席数が0より大きいイベントのみ取得されること", 
                      event.getAvailableSeats() > 0, is(true));
        }
    }

    @Test
    public void testGetEventsByCategory_正常取得() {
        // Given - テストデータの準備
        eventDAO.saveEvent(futureEvent1); // 音楽カテゴリ
        eventDAO.saveEvent(futureEvent2); // スポーツカテゴリ
        eventDAO.saveEvent(pastEvent); // 音楽カテゴリだが過去のイベント
        sessionFactory.getCurrentSession().flush();

        // When - テスト対象メソッドの実行
        List<Event> result = eventDAO.getEventsByCategory("音楽");

        // Then - 結果の検証
        assertThat("カテゴリ別イベントリストが正常に取得されること", result, notNullValue());
        
        // 指定したカテゴリのイベントのみが取得されることを確認
        for (Event event : result) {
            assertThat("指定したカテゴリのイベントのみ取得されること", 
                      event.getCategory(), equalTo("音楽"));
            assertThat("未来の日付のイベントのみ取得されること", 
                      event.getEventDate().isAfter(LocalDateTime.now()), is(true));
            assertThat("利用可能席数が0より大きいイベントのみ取得されること", 
                      event.getAvailableSeats() > 0, is(true));
        }
    }

    @Test
    public void testGetEventsByCategory_存在しないカテゴリ() {
        // Given - テストデータの準備
        eventDAO.saveEvent(futureEvent1);
        sessionFactory.getCurrentSession().flush();

        // When - テスト対象メソッドの実行
        List<Event> result = eventDAO.getEventsByCategory("演劇");

        // Then - 結果の検証
        assertThat("イベントリストが正常に取得されること", result, notNullValue());
        assertThat("存在しないカテゴリの場合は空のリストが返されること", result.size(), equalTo(0));
    }

    @Test
    public void testGetEventsFromDate_正常取得() {
        // Given - テストデータの準備
        LocalDateTime fromDate = LocalDateTime.now().plusDays(5);
        
        eventDAO.saveEvent(futureEvent1); // 7日後
        eventDAO.saveEvent(futureEvent2); // 14日後
        eventDAO.saveEvent(pastEvent); // 過去
        sessionFactory.getCurrentSession().flush();

        // When - テスト対象メソッドの実行
        List<Event> result = eventDAO.getEventsFromDate(fromDate);

        // Then - 結果の検証
        assertThat("日付指定イベントリストが正常に取得されること", result, notNullValue());
        
        // 指定した日付以降のイベントのみが取得されることを確認
        for (Event event : result) {
            assertThat("指定した日付以降のイベントのみ取得されること", 
                      event.getEventDate().isAfter(fromDate) || event.getEventDate().isEqual(fromDate), is(true));
            assertThat("利用可能席数が0より大きいイベントのみ取得されること", 
                      event.getAvailableSeats() > 0, is(true));
        }
    }

    @Test
    public void testGetEventsByCategoryFromDate_正常取得() {
        // Given - テストデータの準備
        LocalDateTime fromDate = LocalDateTime.now().plusDays(5);
        
        eventDAO.saveEvent(futureEvent1); // 音楽、7日後
        eventDAO.saveEvent(futureEvent2); // スポーツ、14日後
        
        // 音楽カテゴリで20日後のイベントを追加
        Event futureMusic = new Event(
            "未来の音楽イベント", 
            "20日後の音楽イベント", 
            LocalDateTime.now().plusDays(20), 
            "横浜アリーナ", 
            "音楽", 
            300, 
            7000.0
        );
        eventDAO.saveEvent(futureMusic);
        sessionFactory.getCurrentSession().flush();

        // When - テスト対象メソッドの実行
        List<Event> result = eventDAO.getEventsByCategoryFromDate("音楽", fromDate);

        // Then - 結果の検証
        assertThat("カテゴリ・日付指定イベントリストが正常に取得されること", result, notNullValue());
        
        // 指定した条件のイベントのみが取得されることを確認
        for (Event event : result) {
            assertThat("指定したカテゴリのイベントのみ取得されること", 
                      event.getCategory(), equalTo("音楽"));
            assertThat("指定した日付以降のイベントのみ取得されること", 
                      event.getEventDate().isAfter(fromDate) || event.getEventDate().isEqual(fromDate), is(true));
            assertThat("利用可能席数が0より大きいイベントのみ取得されること", 
                      event.getAvailableSeats() > 0, is(true));
        }
    }

    @Test
    public void testUpdateEvent_正常更新() {
        // Given - テストデータの準備
        Long savedId = eventDAO.saveEvent(futureEvent1);
        sessionFactory.getCurrentSession().flush();
        
        // 保存されたデータを取得
        Event savedEvent = eventDAO.getEventById(savedId);
        
        // 価格と利用可能席数を変更
        savedEvent.setPrice(6000.0);
        savedEvent.setAvailableSeats(80);

        // When - テスト対象メソッドの実行
        eventDAO.updateEvent(savedEvent);
        sessionFactory.getCurrentSession().flush();

        // Then - 結果の検証
        Event updatedEvent = eventDAO.getEventById(savedId);
        assertThat("価格が正しく更新されること", updatedEvent.getPrice(), equalTo(6000.0));
        assertThat("利用可能席数が正しく更新されること", updatedEvent.getAvailableSeats(), equalTo(80));
    }

    @Test
    public void testDeleteEvent_正常削除() {
        // Given - テストデータの準備
        Long savedId = eventDAO.saveEvent(futureEvent1);
        sessionFactory.getCurrentSession().flush();
        
        // 保存されたデータを取得
        Event savedEvent = eventDAO.getEventById(savedId);

        // When - テスト対象メソッドの実行
        eventDAO.deleteEvent(savedEvent);
        sessionFactory.getCurrentSession().flush();

        // Then - 結果の検証
        Event deletedEvent = eventDAO.getEventById(savedId);
        assertThat("削除されたイベントはnullで取得されること", deletedEvent, nullValue());
    }

    @Test
    public void testGetEventsFromDate_境界値テスト() {
        // Given - テストデータの準備（境界値：現在時刻ちょうど）
        LocalDateTime exactNow = LocalDateTime.now();
        
        Event exactTimeEvent = new Event(
            "現在時刻のイベント", 
            "現在時刻ちょうどのイベント", 
            exactNow, 
            "テスト会場", 
            "テスト", 
            50, 
            1000.0
        );
        eventDAO.saveEvent(exactTimeEvent);
        sessionFactory.getCurrentSession().flush();

        // When - テスト対象メソッドの実行
        List<Event> result = eventDAO.getEventsFromDate(exactNow);

        // Then - 結果の検証
        assertThat("境界値でのイベントリストが正常に取得されること", result, notNullValue());
        
        // 指定した時刻以降（同じ時刻を含む）のイベントが取得されることを確認
        boolean foundExactTimeEvent = false;
        for (Event event : result) {
            if (event.getEventName().equals("現在時刻のイベント")) {
                foundExactTimeEvent = true;
                break;
            }
        }
        assertThat("境界値のイベントが取得されること", foundExactTimeEvent, is(true));
    }
}
