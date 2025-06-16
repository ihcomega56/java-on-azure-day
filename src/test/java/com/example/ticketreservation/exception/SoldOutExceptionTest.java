package com.example.ticketreservation.exception;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 * SoldOutExceptionのテストクラス
 * カスタム例外の動作をテストする
 */
public class SoldOutExceptionTest {

    @Test
    public void testDefaultConstructor_デフォルトメッセージ() {
        // When - デフォルトコンストラクタでの例外作成
        SoldOutException exception = new SoldOutException();

        // Then - 結果の検証
        assertThat("デフォルトメッセージが設定されること", 
                  exception.getMessage(), equalTo("チケットが売り切れています"));
        assertThat("原因がnullであること", exception.getCause(), nullValue());
    }

    @Test
    public void testMessageConstructor_カスタムメッセージ() {
        // Given - テストデータの準備
        String customMessage = "利用可能席数: 0席";

        // When - メッセージ付きコンストラクタでの例外作成
        SoldOutException exception = new SoldOutException(customMessage);

        // Then - 結果の検証
        assertThat("カスタムメッセージが設定されること", 
                  exception.getMessage(), equalTo(customMessage));
        assertThat("原因がnullであること", exception.getCause(), nullValue());
    }

    @Test
    public void testMessageAndCauseConstructor_メッセージと原因() {
        // Given - テストデータの準備
        String customMessage = "予約処理中にエラーが発生しました";
        RuntimeException cause = new RuntimeException("データベース接続エラー");

        // When - メッセージと原因付きコンストラクタでの例外作成
        SoldOutException exception = new SoldOutException(customMessage, cause);

        // Then - 結果の検証
        assertThat("カスタムメッセージが設定されること", 
                  exception.getMessage(), equalTo(customMessage));
        assertThat("原因が正しく設定されること", exception.getCause(), equalTo(cause));
        assertThat("原因のメッセージが正しく取得されること", 
                  exception.getCause().getMessage(), equalTo("データベース接続エラー"));
    }

    @Test
    public void testExceptionThrow_例外発生確認() {
        // Given - テストデータの準備
        String errorMessage = "指定されたイベントが見つかりません";

        try {
            // When - 例外を発生させる
            throw new SoldOutException(errorMessage);
        } catch (SoldOutException e) {
            // Then - 例外が正しくキャッチされることを確認
            assertThat("例外メッセージが正しいこと", e.getMessage(), equalTo(errorMessage));
            assertThat("例外の型が正しいこと", e, instanceOf(SoldOutException.class));
            assertThat("Exceptionのサブクラスであること", e, instanceOf(Exception.class));
        }
    }

    @Test
    public void testExceptionInheritance_継承関係確認() {
        // When - 例外インスタンスの作成
        SoldOutException exception = new SoldOutException();

        // Then - 継承関係の確認
        assertThat("SoldOutExceptionはExceptionのサブクラスであること", 
                  exception instanceof Exception, is(true));
        assertThat("SoldOutExceptionはThrowableのサブクラスであること", 
                  exception instanceof Throwable, is(true));
    }

    @Test
    public void testNullMessage_nullメッセージの処理() {
        // When - nullメッセージでの例外作成
        SoldOutException exception = new SoldOutException(null);

        // Then - 結果の検証
        assertThat("nullメッセージが正しく処理されること", exception.getMessage(), nullValue());
    }

    @Test
    public void testEmptyMessage_空メッセージの処理() {
        // Given - 空文字列メッセージ
        String emptyMessage = "";

        // When - 空メッセージでの例外作成
        SoldOutException exception = new SoldOutException(emptyMessage);

        // Then - 結果の検証
        assertThat("空メッセージが正しく処理されること", exception.getMessage(), equalTo(""));
    }
}
