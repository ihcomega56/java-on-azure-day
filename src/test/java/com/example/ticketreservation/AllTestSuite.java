package com.example.ticketreservation;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import com.example.ticketreservation.controller.TicketControllerTest;
import com.example.ticketreservation.dao.EventRepositoryTest;
import com.example.ticketreservation.dao.ReservationRepositoryTest;
import com.example.ticketreservation.exception.SoldOutExceptionTest;
import com.example.ticketreservation.model.EventTest;
import com.example.ticketreservation.model.ReservationTest;
import com.example.ticketreservation.service.EventServiceTest;
import com.example.ticketreservation.service.ReservationServiceTest;

/**
 * 全テストクラスを実行するテストスイート
 * JUnit 5対応版
 * 
 * 実行順序:
 * 1. サービス層テスト（ビジネスロジック）
 * 2. データアクセス層テスト（リポジトリ）
 * 3. モデル層テスト（エンティティクラス）
 * 4. コントローラー層テスト（Webレイヤー）
 * 5. 例外クラステスト
 */
@Suite
@SelectClasses({
    // サービス層（優先度: 最高）
    ReservationServiceTest.class,
    EventServiceTest.class,
    
    // データアクセス層（優先度: 高）
    ReservationRepositoryTest.class,
    EventRepositoryTest.class,
    
    // モデル層（優先度: 中）
    ReservationTest.class,
    EventTest.class,
    
    // コントローラー層（優先度: 中）
    TicketControllerTest.class,
    
    // 例外クラス（優先度: 低）
    SoldOutExceptionTest.class
})
public class AllTestSuite {
    // テストスイートの実行により、
    // すべてのテストクラスが順次実行されます
    // JUnit 5の@Suiteアノテーションを使用
}
