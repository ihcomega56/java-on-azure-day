package com.example.ticketreservation.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.example.ticketreservation.dao.EventDAO;
import com.example.ticketreservation.model.Event;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * EventServiceの非同期処理テスト
 * Virtual Threads導入によるスレッド処理改善のテスト
 */
@RunWith(MockitoJUnitRunner.class)
public class EventServiceAsyncTest {

    @Mock
    private EventDAO eventDAO;

    @InjectMocks
    private EventService eventService;

    @Test
    public void testSearchEventsAsync_カテゴリと日付指定() throws InterruptedException, ExecutionException {
        // Given - テストデータの準備
        String category = "CONCERT";
        LocalDate fromDate = LocalDate.now().plusDays(1);
        Event event1 = createTestEvent(1L, "Test Concert 1", category);
        Event event2 = createTestEvent(2L, "Test Concert 2", category);
        List<Event> expectedEvents = Arrays.asList(event1, event2);

        when(eventDAO.getEventsByCategoryFromDate(eq(category), any()))
            .thenReturn(expectedEvents);

        // When - 非同期検索の実行
        CompletableFuture<List<Event>> future = eventService.searchEventsAsync(category, fromDate);
        List<Event> result = future.get();

        // Then - 結果の検証
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(2));
        assertThat(result.get(0).getEventName(), equalTo("Test Concert 1"));
        assertThat(result.get(1).getEventName(), equalTo("Test Concert 2"));
        
        // DAOメソッドが正しく呼び出されることを確認
        verify(eventDAO).getEventsByCategoryFromDate(eq(category), any());
    }

    @Test
    public void testSearchEventsAsync_カテゴリのみ指定() throws InterruptedException, ExecutionException {
        // Given - テストデータの準備
        String category = "SPORTS";
        Event event = createTestEvent(1L, "Test Sports Event", category);
        List<Event> expectedEvents = Arrays.asList(event);

        when(eventDAO.getEventsByCategory(category)).thenReturn(expectedEvents);

        // When - 非同期検索の実行
        CompletableFuture<List<Event>> future = eventService.searchEventsAsync(category, null);
        List<Event> result = future.get();

        // Then - 結果の検証
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getEventName(), equalTo("Test Sports Event"));
        
        verify(eventDAO).getEventsByCategory(category);
    }

    @Test
    public void testGetAvailableEventsAsync_正常取得() throws InterruptedException, ExecutionException {
        // Given - テストデータの準備
        Event event1 = createTestEvent(1L, "Available Event 1", "MUSIC");
        Event event2 = createTestEvent(2L, "Available Event 2", "THEATER");
        List<Event> expectedEvents = Arrays.asList(event1, event2);

        when(eventDAO.getAvailableEvents()).thenReturn(expectedEvents);

        // When - 非同期取得の実行
        CompletableFuture<List<Event>> future = eventService.getAvailableEventsAsync();
        List<Event> result = future.get();

        // Then - 結果の検証
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(2));
        
        verify(eventDAO).getAvailableEvents();
    }

    @Test
    public void testSearchEventsAsync_検索条件なし() throws InterruptedException, ExecutionException {
        // Given - テストデータの準備
        Event event = createTestEvent(1L, "Default Event", "OTHER");
        List<Event> expectedEvents = Arrays.asList(event);

        when(eventDAO.getAvailableEvents()).thenReturn(expectedEvents);

        // When - 検索条件なしで非同期検索の実行
        CompletableFuture<List<Event>> future = eventService.searchEventsAsync(null, null);
        List<Event> result = future.get();

        // Then - デフォルトで利用可能イベントが取得されることを確認
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        
        verify(eventDAO).getAvailableEvents();
    }

    /**
     * テスト用のEventオブジェクトを作成
     */
    private Event createTestEvent(Long id, String name, String category) {
        Event event = new Event();
        event.setId(id);
        event.setEventName(name);
        event.setCategory(category);
        event.setAvailableSeats(100);
        event.setTotalSeats(100);
        event.setPrice(3000.0); // 価格を設定
        event.setVenue("Test Venue");
        return event;
    }
}