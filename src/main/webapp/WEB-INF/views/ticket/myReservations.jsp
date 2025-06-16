<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="custom" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>予約一覧 - チケット予約システム</title>
    <style>
        body {
            font-family: 'Hiragino Sans', 'ヒラギノ角ゴシック', 'Yu Gothic', '游ゴシック', sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1000px;
            margin: 0 auto;
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .search-form {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
        }
        .form-group {
            display: inline-block;
            margin-right: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #333;
        }
        input {
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        .btn {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .btn-danger {
            background-color: #dc3545;
        }
        .btn-danger:hover {
            background-color: #c82333;
        }
        .btn-secondary {
            background-color: #6c757d;
        }
        .btn-secondary:hover {
            background-color: #545b62;
        }
        .reservation-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            background-color: white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .reservation-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            padding-bottom: 15px;
            border-bottom: 1px solid #e9ecef;
        }
        .event-title {
            font-size: 18px;
            font-weight: bold;
            color: #333;
        }
        .confirmation-code {
            font-family: monospace;
            background-color: #f8f9fa;
            padding: 4px 8px;
            border-radius: 4px;
            border: 1px solid #e9ecef;
        }
        .reservation-info {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 15px;
        }
        .info-item {
            display: flex;
            flex-direction: column;
        }
        .info-label {
            font-size: 12px;
            color: #666;
            margin-bottom: 4px;
        }
        .info-value {
            font-weight: bold;
            color: #333;
        }
        .status-confirmed {
            color: #28a745;
            font-weight: bold;
        }
        .status-cancelled {
            color: #dc3545;
            font-weight: bold;
        }
        .no-reservations {
            text-align: center;
            padding: 50px;
            color: #666;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>📄 予約一覧</h1>
        
        <!-- 検索フォーム -->
        <div class="search-form">
            <form method="get" action="${pageContext.request.contextPath}/myreservations">
                <div class="form-group">
                    <label for="email">メールアドレス:</label>
                    <input type="email" name="email" id="email" value="${param.email}" placeholder="予約時のメールアドレス" required>
                </div>
                <div class="form-group">
                    <input type="submit" value="検索" class="btn">
                </div>
            </form>
        </div>
        
        <!-- 予約一覧 -->
        <c:choose>
            <c:when test="${not empty reservations}">
                <c:forEach var="reservation" items="${reservations}">
                    <div class="reservation-card">
                        <div class="reservation-header">
                            <div class="event-title">${reservation.event.eventName}</div>
                            <div class="confirmation-code">確認コード: ${reservation.confirmationCode}</div>
                        </div>
                        
                        <div class="reservation-info">
                            <div class="info-item">
                                <div class="info-label">📅 開催日時</div>
                                <div class="info-value">
                                    <custom:formatLocalDateTime localDateTime="${reservation.event.eventDate}" pattern="yyyy年MM月dd日 HH:mm" />
                                </div>
                            </div>
                            
                            <div class="info-item">
                                <div class="info-label">📍 会場</div>
                                <div class="info-value">${reservation.event.venue}</div>
                            </div>
                            
                            <div class="info-item">
                                <div class="info-label">🎟️ チケット枚数</div>
                                <div class="info-value">${reservation.quantity}枚</div>
                            </div>
                            
                            <div class="info-item">
                                <div class="info-label">💰 合計金額</div>
                                <div class="info-value">¥<fmt:formatNumber value="${reservation.totalPrice}" pattern="#,###" /></div>
                            </div>
                            
                            <div class="info-item">
                                <div class="info-label">⏰ 予約日時</div>
                                <div class="info-value">
                                    <custom:formatLocalDateTime localDateTime="${reservation.reservationTime}" pattern="yyyy年MM月dd日 HH:mm" />
                                </div>
                            </div>
                            
                            <div class="info-item">
                                <div class="info-label">📊 ステータス</div>
                                <div class="info-value">
                                    <c:choose>
                                        <c:when test="${reservation.status == 'CONFIRMED'}">
                                            <span class="status-confirmed">✅ 予約確定</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-cancelled">❌ キャンセル済み</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                        
                        <!-- アクションボタン -->
                        <c:if test="${reservation.status == 'CONFIRMED'}">
                            <div style="text-align: right;">
                                <a href="${pageContext.request.contextPath}/cancel/${reservation.id}" class="btn btn-danger">
                                    ❌ 予約をキャンセル
                                </a>
                            </div>
                        </c:if>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="no-reservations">
                    <c:choose>
                        <c:when test="${empty param.email}">
                            <h3>予約を検索してください</h3>
                            <p>上記のフォームにメールアドレスを入力して検索してください。</p>
                        </c:when>
                        <c:otherwise>
                            <h3>予約が見つかりません</h3>
                            <p>指定されたメールアドレスでの予約は見つかりませんでした。</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:otherwise>
        </c:choose>
        
        <!-- ナビゲーションリンク -->
        <div style="margin-top: 30px; text-align: center;">
            <a href="${pageContext.request.contextPath}/events" class="btn btn-secondary">
                📋 イベント一覧に戻る
            </a>
        </div>
    </div>
</body>
</html>
