package com.example.ticketreservation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import com.example.ticketreservation.dao.EventDAOTest;
import com.example.ticketreservation.dao.ReservationDAOTest;
import com.example.ticketreservation.exception.SoldOutExceptionTest;
import com.example.ticketreservation.model.EventTest;
import com.example.ticketreservation.model.ReservationTest;
import com.example.ticketreservation.service.EventServiceTest;
import com.example.ticketreservation.service.ReservationServiceTest;

/**
 * 全テストクラスを実行するテストスイート
 * 
 * 実行順序:
 * 1. サービス層テスト（ビジネスロジック）
 * 2. データアクセス層テスト（データベース処理）
 * 3. モデル層テスト（エンティティクラス）
 * 4. 例外クラステスト
 */
@RunWith(Suite.class)
@SuiteClasses({
    // サービス層（優先度: 最高）
    ReservationServiceTest.class,
    EventServiceTest.class,
    
    // データアクセス層（優先度: 高）
    ReservationDAOTest.class,
    EventDAOTest.class,
    
    // モデル層（優先度: 中）
    ReservationTest.class,
    EventTest.class,
    
    // 例外クラス（優先度: 低）
    SoldOutExceptionTest.class
})
public class AllTestSuite {
    // テストスイートの実行により、
    // すべてのテストクラスが順次実行されます
}
