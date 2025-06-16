package com.example.ticketreservation.service;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.example.ticketreservation.dao.EventDAO;
import com.example.ticketreservation.model.Event;

/**
 * EventServiceのテストクラス
 * イベント管理に関するビジネスロジックのテストを実施
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-applicationContext.xml"})
public class EventServiceTest {

    @Mock
    private EventDAO eventDAO;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
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
    }

    @Test
    public void testGetEventById_正常取得() {
        // Given - テストデータの準備
        Long eventId = 1L;
        
        when(eventDAO.getEventById(eventId)).thenReturn(testEvent);

        // When - テスト対象メソッドの実行
        Event result = eventService.getEventById(eventId);

        // Then - 結果の検証
        assertThat("イベントが正常に取得されること", result, notNullValue());
        assertThat("取得したイベントのIDが正しいこと", result.getId(), equalTo(eventId));
        assertThat("取得したイベント名が正しいこと", result.getEventName(), equalTo("テストコンサート"));
        verify(eventDAO).getEventById(eventId);
    }

    @Test
    public void testGetEventById_存在しないID() {
        // Given - テストデータの準備（存在しないID）
        Long eventId = 999L;
        
        when(eventDAO.getEventById(eventId)).thenReturn(null);

        // When - テスト対象メソッドの実行
        Event result = eventService.getEventById(eventId);

        // Then - 結果の検証
        assertThat("存在しないIDの場合はnullが返されること", result, nullValue());
        verify(eventDAO).getEventById(eventId);
    }

    @Test
    public void testGetAllEvents_正常取得() {
        // Given - テストデータの準備
        List<Event> expectedEvents = Arrays.asList(testEvent);
        
        when(eventDAO.getAllEvents()).thenReturn(expectedEvents);

        // When - テスト対象メソッドの実行
        List<Event> result = eventService.getAllEvents();

        // Then - 結果の検証
        assertThat("全イベントリストが正常に取得されること", result, notNullValue());
        assertThat("イベントリストのサイズが正しいこと", result.size(), equalTo(1));
        verify(eventDAO).getAllEvents();
    }

    @Test
    public void testGetAvailableEvents_正常取得() {
        // Given - テストデータの準備
        List<Event> expectedEvents = Arrays.asList(testEvent);
        
        when(eventDAO.getAvailableEvents()).thenReturn(expectedEvents);

        // When - テスト対象メソッドの実行
        List<Event> result = eventService.getAvailableEvents();

        // Then - 結果の検証
        assertThat("利用可能イベントリストが正常に取得されること", result, notNullValue());
        assertThat("イベントリストのサイズが正しいこと", result.size(), equalTo(1));
        verify(eventDAO).getAvailableEvents();
    }

    @Test
    public void testGetEventsByCategory_正常取得() {
        // Given - テストデータの準備
        String category = "音楽";
        List<Event> expectedEvents = Arrays.asList(testEvent);
        
        when(eventDAO.getEventsByCategory(category)).thenReturn(expectedEvents);

        // When - テスト対象メソッドの実行
        List<Event> result = eventService.getEventsByCategory(category);

        // Then - 結果の検証
        assertThat("カテゴリ別イベントリストが正常に取得されること", result, notNullValue());
        assertThat("イベントリストのサイズが正しいこと", result.size(), equalTo(1));
        assertThat("取得したイベントのカテゴリが正しいこと", result.getFirst().getCategory(), equalTo(category));
        verify(eventDAO).getEventsByCategory(category);
    }

    @Test
    public void testGetEventsFromDate_正常取得() {
        // Given - テストデータの準備
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDateTime expectedFromDateTime = fromDate.atStartOfDay();
        List<Event> expectedEvents = Arrays.asList(testEvent);
        
        when(eventDAO.getEventsFromDate(expectedFromDateTime)).thenReturn(expectedEvents);

        // When - テスト対象メソッドの実行
        List<Event> result = eventService.getEventsFromDate(fromDate);

        // Then - 結果の検証
        assertThat("日付指定イベントリストが正常に取得されること", result, notNullValue());
        assertThat("イベントリストのサイズが正しいこと", result.size(), equalTo(1));
        verify(eventDAO).getEventsFromDate(expectedFromDateTime);
    }

    @Test
    public void testGetEventsByCategoryFromDate_正常取得() {
        // Given - テストデータの準備
        String category = "音楽";
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDateTime expectedFromDateTime = fromDate.atStartOfDay();
        List<Event> expectedEvents = Arrays.asList(testEvent);
        
        when(eventDAO.getEventsByCategoryFromDate(category, expectedFromDateTime)).thenReturn(expectedEvents);

        // When - テスト対象メソッドの実行
        List<Event> result = eventService.getEventsByCategoryFromDate(category, fromDate);

        // Then - 結果の検証
        assertThat("カテゴリ・日付指定イベントリストが正常に取得されること", result, notNullValue());
        assertThat("イベントリストのサイズが正しいこと", result.size(), equalTo(1));
        verify(eventDAO).getEventsByCategoryFromDate(category, expectedFromDateTime);
    }

    @Test
    public void testReduceAvailableSeats_正常減少() {
        // Given - テストデータの準備
        Long eventId = 1L;
        int quantity = 10;
        int initialSeats = 100;
        
        testEvent.setAvailableSeats(initialSeats);
        when(eventDAO.getEventById(eventId)).thenReturn(testEvent);

        // When - テスト対象メソッドの実行
        eventService.reduceAvailableSeats(eventId, quantity);

        // Then - 結果の検証
        assertThat("利用可能席数が正しく減少していること", 
                  testEvent.getAvailableSeats(), equalTo(initialSeats - quantity));
        verify(eventDAO).getEventById(eventId);
        verify(eventDAO).updateEvent(testEvent);
    }

    @Test
    public void testReduceAvailableSeats_境界値_全席数減少() {
        // Given - テストデータの準備（全席数分減少）
        Long eventId = 1L;
        int quantity = 100;
        int initialSeats = 100;
        
        testEvent.setAvailableSeats(initialSeats);
        when(eventDAO.getEventById(eventId)).thenReturn(testEvent);

        // When - テスト対象メソッドの実行
        eventService.reduceAvailableSeats(eventId, quantity);

        // Then - 結果の検証
        assertThat("全席数減少時に0になること", testEvent.getAvailableSeats(), equalTo(0));
        verify(eventDAO).updateEvent(testEvent);
    }

    @Test
    public void testReduceAvailableSeats_存在しないイベント() {
        // Given - テストデータの準備（存在しないイベント）
        Long eventId = 999L;
        int quantity = 10;
        
        when(eventDAO.getEventById(eventId)).thenReturn(null);

        // When - テスト対象メソッドの実行
        eventService.reduceAvailableSeats(eventId, quantity);

        // Then - 結果の検証（何も処理されないことを確認）
        verify(eventDAO).getEventById(eventId);
        verify(eventDAO, never()).updateEvent(org.mockito.Mockito.any(Event.class));
    }

    @Test
    public void testIncreaseAvailableSeats_正常増加() {
        // Given - テストデータの準備
        Long eventId = 1L;
        int quantity = 10;
        int initialSeats = 50;
        
        testEvent.setAvailableSeats(initialSeats);
        when(eventDAO.getEventById(eventId)).thenReturn(testEvent);

        // When - テスト対象メソッドの実行
        eventService.increaseAvailableSeats(eventId, quantity);

        // Then - 結果の検証
        assertThat("利用可能席数が正しく増加していること", 
                  testEvent.getAvailableSeats(), equalTo(initialSeats + quantity));
        verify(eventDAO).getEventById(eventId);
        verify(eventDAO).updateEvent(testEvent);
    }

    @Test
    public void testIncreaseAvailableSeats_存在しないイベント() {
        // Given - テストデータの準備（存在しないイベント）
        Long eventId = 999L;
        int quantity = 10;
        
        when(eventDAO.getEventById(eventId)).thenReturn(null);

        // When - テスト対象メソッドの実行
        eventService.increaseAvailableSeats(eventId, quantity);

        // Then - 結果の検証（何も処理されないことを確認）
        verify(eventDAO).getEventById(eventId);
        verify(eventDAO, never()).updateEvent(org.mockito.Mockito.any(Event.class));
    }

    @Test
    public void testSaveEvent_正常保存() {
        // Given - テストデータの準備
        Long expectedId = 2L;
        
        when(eventDAO.saveEvent(testEvent)).thenReturn(expectedId);

        // When - テスト対象メソッドの実行
        Long result = eventService.saveEvent(testEvent);

        // Then - 結果の検証
        assertThat("保存されたイベントのIDが正しく返されること", result, equalTo(expectedId));
        verify(eventDAO).saveEvent(testEvent);
    }

    @Test
    public void testUpdateEvent_正常更新() {
        // Given - テストデータの準備

        // When - テスト対象メソッドの実行
        eventService.updateEvent(testEvent);

        // Then - 結果の検証
        verify(eventDAO).updateEvent(testEvent);
    }

    @Test
    public void testDeleteEvent_正常削除() {
        // Given - テストデータの準備

        // When - テスト対象メソッドの実行
        eventService.deleteEvent(testEvent);

        // Then - 結果の検証
        verify(eventDAO).deleteEvent(testEvent);
    }
}
