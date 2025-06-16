# チケット予約システム

Java Spring Framework を使用したWebベースのチケット予約システムです。コンサート、演劇、スポーツ観戦などの各種イベントのチケット予約・管理機能を提供します。

## プロジェクト概要

このシステムは、イベント主催者がイベント情報を管理し、一般ユーザーがイベントのチケットを予約できるWebアプリケーションです。Java 8時代の技術スタックで構築されており、将来的にJava 21 + Spring Boot 3.2への移行を予定しています。

### 主な機能

- **イベント管理**
  - イベント一覧表示
  - イベント詳細表示
  - カテゴリ・日付による絞り込み検索

- **チケット予約**
  - チケット予約フォーム
  - リアルタイム座席数管理
  - 予約確認コード生成

- **予約管理**
  - 予約一覧確認
  - 予約キャンセル機能
  - 予約状況の可視化

## 技術スタック

### バックエンド
- **Java**: 8+ (移行予定: Java 21)
- **フレームワーク**: Spring Framework 4.3.30 (移行予定: Spring Boot 3.2)
- **ORM**: Hibernate 5.1.17
- **データベース**: H2 Database (インメモリ)
- **ビルドツール**: Maven 3.x

### フロントエンド
- **テンプレートエンジン**: JSP + JSTL
- **スタイル**: CSS (インライン)
- **JavaScript**: バニラJS

### テスト
- **JUnit**: 4.x
- **Mockito**: モック作成
- **Hamcrest**: アサーション

## プロジェクト構造

```
com.example.ticketreservation
├── config/         # 設定クラス（データ初期化など）
├── controller/     # コントローラークラス（Web API）
├── dao/           # データアクセスオブジェクト
├── exception/     # カスタム例外クラス
├── model/         # エンティティクラス（Event, Reservation）
├── service/       # ビジネスロジッククラス
└── util/          # ユーティリティクラス
```

## セットアップ手順

### 前提条件

- Java 8以上
- Maven 3.x
- Git

### 1. リポジトリのクローン

```bash
git clone <repository-url>
cd java-on-azure-day
```

### 2. 依存関係のインストール

```bash
mvn clean install
```

### 3. アプリケーションの起動

```bash
mvn tomcat7:run
```

### 4. アプリケーションへのアクセス

ブラウザで以下のURLにアクセスしてください：

```
http://localhost:8080/ticket
```

## 使用方法

### イベント一覧の確認

1. トップページ（`/`）または`/events`にアクセス
2. カテゴリや開催日で絞り込み検索が可能
3. 各イベントの詳細を確認

### チケットの予約

1. イベント詳細ページで「予約する」ボタンをクリック
2. メールアドレスと予約枚数を入力
3. 予約内容を確認して予約実行
4. 確認コードが発行されます

### 予約の確認・キャンセル

1. トップページの「予約確認」または`/myreservations`にアクセス
2. 予約時のメールアドレスを入力
3. 予約一覧が表示され、キャンセルも可能

## API エンドポイント

| エンドポイント | メソッド | 説明 |
|---------------|----------|------|
| `/` | GET | ルートページ（イベント一覧にリダイレクト） |
| `/events` | GET | イベント一覧表示 |
| `/events/{eventId}` | GET | イベント詳細表示 |
| `/events/{eventId}/reserve` | GET | 予約フォーム表示 |
| `/reserve` | POST | チケット予約処理 |
| `/confirmation/{confirmationCode}` | GET | 予約確認ページ |
| `/myreservations` | GET | 予約一覧表示 |
| `/cancel/{reservationId}` | GET | キャンセルフォーム表示 |
| `/cancel` | POST | 予約キャンセル処理 |

## データベース設計

### 主要テーブル

#### events
- イベント情報（名前、説明、開催日時、会場、カテゴリ、料金、座席数）

#### reservations  
- 予約情報（確認コード、メール、枚数、予約日時、ステータス）

### リレーション
- Event (1) ←→ (N) Reservation

## テスト実行

### 全テストの実行

```bash
mvn test
```

### 特定のテストクラスの実行

```bash
mvn test -Dtest=EventServiceTest
```

### テストレポートの確認

テスト実行後、以下で詳細なレポートを確認できます：

```
target/surefire-reports/
```

## 開発ガイドライン

### コーディング規約

- **命名規則**: キャメルケース
- **インデント**: スペース4つ
- **文字エンコーディング**: UTF-8
- **コメント**: ビジネスロジックは日本語で記述

### テスト記述規約

- **テストクラス名**: `{対象クラス名}Test`
- **テストメソッド名**: `test{対象メソッド}_{テストシナリオ}` (日本語)
- **テスト構造**: Given-When-Then パターン

### 例

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

## 設定ファイル

### 主要な設定ファイル

- `src/main/resources/application.properties`: アプリケーション設定
- `src/main/webapp/WEB-INF/applicationContext.xml`: Spring設定
- `src/main/webapp/WEB-INF/spring-servlet.xml`: Spring MVC設定
- `src/main/webapp/WEB-INF/web.xml`: サーブレット設定

### データベース設定

デフォルトでH2インメモリデータベースを使用します。起動時に自動的にサンプルデータが投入されます。

## 今後の予定

### 移行計画

- **Java 21**: 最新のLTS版への移行
- **Spring Boot 3.2**: モダンなSpring Boot環境への移行
- **Azure対応**: Azure App ServiceやAzure SQL Databaseへの対応

### 機能拡張

- ユーザー認証機能
- 座席指定機能
- 決済機能統合
- メール通知機能

## トラブルシューティング

### よくある問題

#### ポート8080が使用中の場合

```bash
# 別のポートで起動
mvn tomcat7:run -Dmaven.tomcat.port=8081
```

#### データベース初期化エラー

```bash
# クリーンビルドを実行
mvn clean compile
```

## 貢献方法

1. このリポジトリをフォーク
2. フィーチャーブランチを作成 (`git checkout -b feature/amazing-feature`)
3. 変更をコミット (`git commit -m 'Add some amazing feature'`)
4. ブランチにプッシュ (`git push origin feature/amazing-feature`)
5. プルリクエストを作成

## ライセンス

このプロジェクトはMITライセンスの下で公開されています。詳細は`LICENSE`ファイルをご確認ください。

## 連絡先

プロジェクトに関する質問や提案がございましたら、Issueを作成してください。

---

**注意**: このプロジェクトは学習・デモンストレーション目的で作成されています。本番環境での使用前には、セキュリティ対策や性能調整を適切に実施してください。
