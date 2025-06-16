# GitHub Copilot カスタムインストラクション

## プロジェクト概要
このプロジェクトは Java Spring Framework を使用したチケット予約システムです。

## コーディング規約

### 一般的なルール
- **言語**: Java 8+ (将来的に Java 21 へアップグレード予定)
- **フレームワーク**: Spring Framework 4.3+ (将来的に Spring Boot 3.2 へアップグレード予定)
- **命名規則**: キャメルケースを使用
- **インデント**: スペース4つ
- **文字エンコーディング**: UTF-8
- **改行コード**: LF

### パッケージ構造
```
com.example.ticketreservation
├── config/      # 設定クラス
├── controller/  # コントローラークラス
├── dao/         # データアクセスオブジェクト
├── exception/   # カスタム例外クラス
├── model/       # エンティティクラス
├── service/     # ビジネスロジッククラス
└── util/        # ユーティリティクラス
```

### コメント規約
- **日本語コメント**: ビジネスロジックの説明は日本語で記述
- **Javadoc**: パブリックメソッドには必須
- **インラインコメント**: 複雑なロジックには日本語で説明を追加

## テストケース規約

### テストクラス命名
- テスト対象クラス名 + `Test`
- 例: `EventService` → `EventServiceTest`

### テストメソッド命名
- **日本語で記述**: テスト内容が分かりやすいよう日本語メソッド名を使用
- **命名パターン**: `test[対象メソッド]_[テストシナリオ]`
- **例**: 
  - `testReserveTicket_正常予約()` 
  - `testReserveTicket_売り切れ時例外発生()`
  - `testGetEventById_存在しないID()`

### テスト構造
Given-When-Then パターンを明確に記述:

```java
@Test
public void testReserveTicket_正常予約() throws SoldOutException {
    // Given - テストデータの準備
    Long eventId = 1L;
    String email = "test@example.com";
    Integer quantity = 2;
    
    // When - テスト対象メソッドの実行
    Reservation result = reservationService.reserveTicket(eventId, email, quantity);
    
    // Then - 結果の検証
    assertThat(result, notNullValue());
    assertThat(result.getEmail(), equalTo(email));
    assertThat(result.getQuantity(), equalTo(quantity));
}
```

### テストカテゴリ
- **正常系**: `_正常[処理内容]`
- **異常系**: `_[例外条件]時例外発生`
- **境界値**: `_境界値[条件]`
- **エラーハンドリング**: `_[エラー条件]時[期待する動作]`

### アサーション
- **Hamcrest Matchers** を優先的に使用
- **日本語コメント**: アサーションには日本語コメントを追加
```java
// Then - 予約が正常に作成されることを確認
assertThat(reservation.getConfirmationCode(), notNullValue());
assertThat(reservation.getConfirmationCode().length(), equalTo(8));
```

### モックの使用
- **Mockito** を使用
- **Given**: `when().thenReturn()` でモックの動作を定義
- **Verify**: `verify()` で期待されるメソッド呼び出しを確認

## データベース設計

### エンティティ設計
- **JPA アノテーション**: `@Entity`, `@Table`, `@Column` を適切に使用
- **リレーション**: `@OneToMany`, `@ManyToOne` で関連を定義
- **日本語フィールド名**: 必要に応じてコメントで日本語説明を追加

### DAO 設計
- **インターフェース + 実装**: DAOはインターフェースで定義し、実装クラスを作成
- **JdbcTemplate**: データベースアクセスには JdbcTemplate を使用
- **トランザクション**: `@Transactional` アノテーションを適切に使用

## エラーハンドリング

### カスタム例外
- **ビジネス例外**: `SoldOutException` など、ビジネスロジック固有の例外を定義
- **日本語メッセージ**: エラーメッセージは日本語で記述
- **例外階層**: 適切な例外継承階層を構築

### ログ出力
- **SLF4J + Logback**: ログフレームワークとして使用
- **日本語ログ**: 必要に応じて日本語でログメッセージを記述
- **ログレベル**: DEBUG, INFO, WARN, ERROR を適切に使い分け

## Spring 設定

### 設定ファイル
- **XML設定**: `applicationContext.xml`, `spring-servlet.xml` を使用
- **プロパティファイル**: `application.properties` で設定値を管理
- **コンポーネントスキャン**: 適切なパッケージスキャンを設定

### 依存性注入
- **@Autowired**: フィールド注入よりもコンストラクタ注入を推奨
- **@Service**, **@Repository**, **@Controller**: 適切なステレオタイプアノテーションを使用

## コード品質

### 静的解析
- **可読性**: メソッドは短く、単一責任の原則に従う
- **重複排除**: DRY原則に従い、コードの重複を避ける
- **命名**: 意味のある名前を使用

### パフォーマンス
- **N+1問題**: JOINやFETCHを使用して回避
- **キャッシュ**: 適切な場所でキャッシュを活用
- **リソース管理**: try-with-resources文を使用

## 特記事項
- このプロジェクトは将来的に Java 21 + Spring Boot 3.2 への移行を予定
- 移行時の互換性を考慮したコード記述を心がける
- テストケースは移行前後での動作確認に重要な役割を果たす

## ドキュメント規約

### 言語
- **基本言語**: 全てのドキュメントは日本語で記述
- **技術用語**: 英語の技術用語は適切に使用し、必要に応じて日本語で説明を併記
- **コード例**: コメントとドキュメントは日本語、コード自体は英語

### ドキュメントタイプ別規約
- **README.md**: プロジェクト概要、セットアップ手順、使用方法を日本語で記述
- **API仕様書**: エンドポイント説明、パラメータ説明、レスポンス例を日本語で記述
- **設計書**: アーキテクチャ図、ER図の説明を日本語で記述
- **CHANGELOG.md**: 変更履歴を日本語で記述
- **FAQ.md**: よくある質問と回答を日本語で記述

### マークダウン記法
- **見出し**: 日本語で記述
- **リスト**: 項目説明は日本語
- **表**: ヘッダーと内容は日本語
- **コードブロック**: コメントは日本語、コードは英語

### 例外ケース
- **国際的な設定ファイル**: `application.properties`のキー名など、システム要件で英語が必要な場合
- **Git コミットメッセージ**: プロジェクトの方針に従う
- **外部API連携**: 外部仕様に合わせた英語ドキュメント
