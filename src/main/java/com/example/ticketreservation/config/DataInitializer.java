package com.example.ticketreservation.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.service.EventService;

@Component
public class DataInitializer implements InitializingBean {

    @Autowired
    private EventService eventService;
    
    @Override
    @Transactional
    public void afterPropertiesSet() throws Exception {
        // サンプルデータの作成
        createSampleEvents();
    }
    
    private void createSampleEvents() {
        // コンサートイベント
        Event concert1 = new Event(
                "春のクラシックコンサート",
                "美しい春の音楽をお楽しみください。著名なオーケストラによる演奏です。",
                LocalDateTime.of(2024, 4, 15, 19, 0),
                "東京芸術劇場",
                "コンサート",
                300,
                5000.0
        );
        eventService.saveEvent(concert1);
        
        Event concert2 = new Event(
                "ジャズナイト",
                "素晴らしいジャズミュージシャンによる夜のコンサート",
                LocalDateTime.of(2024, 4, 20, 20, 0),
                "ブルーノート東京",
                "コンサート",
                150,
                8000.0
        );
        eventService.saveEvent(concert2);
        
        // スポーツイベント
        Event sports1 = new Event(
                "野球観戦",
                "プロ野球の試合観戦チケット",
                LocalDateTime.of(2024, 4, 25, 18, 0),
                "東京ドーム",
                "スポーツ",
                1000,
                3000.0
        );
        eventService.saveEvent(sports1);
        
        // 演劇イベント
        Event theater1 = new Event(
                "ミュージカル「夢の扉」",
                "感動のミュージカル作品をお楽しみください",
                LocalDateTime.of(2024, 5, 1, 14, 0),
                "帝国劇場",
                "演劇",
                800,
                12000.0
        );
        eventService.saveEvent(theater1);
        
        Event theater2 = new Event(
                "コメディショー",
                "笑いあふれるコメディパフォーマンス",
                LocalDateTime.of(2024, 5, 10, 19, 30),
                "新宿シアター",
                "演劇",
                200,
                4500.0
        );
        eventService.saveEvent(theater2);
        
        // 展示イベント
        Event exhibition1 = new Event(
                "現代アート展",
                "最新の現代アート作品を展示",
                LocalDateTime.of(2024, 5, 15, 10, 0),
                "国立新美術館",
                "展示",
                500,
                1500.0
        );
        eventService.saveEvent(exhibition1);
    }
}
