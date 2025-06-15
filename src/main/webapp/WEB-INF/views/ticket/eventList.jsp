<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="custom" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>イベント一覧 - チケット予約システム</title>
    <style>
        body {
            font-family: 'Hiragino Sans', 'ヒラギノ角ゴシック', 'Yu Gothic', '游ゴシック', sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 30px;
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
            margin-bottom: 10px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #333;
        }
        input, select {
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
        .btn-secondary {
            background-color: #6c757d;
        }
        .btn-secondary:hover {
            background-color: #545b62;
        }
        .event-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        .event-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            background-color: white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            transition: transform 0.2s;
        }
        .event-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.15);
        }
        .event-title {
            font-size: 18px;
            font-weight: bold;
            color: #333;
            margin-bottom: 10px;
        }
        .event-info {
            margin-bottom: 8px;
            color: #666;
        }
        .event-category {
            display: inline-block;
            background-color: #e9ecef;
            color: #495057;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            margin-bottom: 10px;
        }
        .event-price {
            font-size: 20px;
            font-weight: bold;
            color: #28a745;
            margin: 10px 0;
        }
        .event-seats {
            color: #dc3545;
            font-weight: bold;
        }
        .no-events {
            text-align: center;
            padding: 50px;
            color: #666;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🎫 イベント一覧</h1>
        
        <!-- 検索フォーム -->
        <div class="search-form">
            <form method="get" action="${pageContext.request.contextPath}/events">
                <div class="form-group">
                    <label for="category">カテゴリ:</label>
                    <select name="category" id="category">
                        <option value="">すべて</option>
                        <option value="コンサート" ${param.category == 'コンサート' ? 'selected' : ''}>コンサート</option>
                        <option value="演劇" ${param.category == '演劇' ? 'selected' : ''}>演劇</option>
                        <option value="スポーツ" ${param.category == 'スポーツ' ? 'selected' : ''}>スポーツ</option>
                        <option value="展示" ${param.category == '展示' ? 'selected' : ''}>展示</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="date">開催日以降:</label>
                    <input type="date" name="date" id="date" value="${param.date}">
                </div>
                
                <div class="form-group">
                    <input type="submit" value="検索" class="btn">
                    <a href="${pageContext.request.contextPath}/events" class="btn btn-secondary">クリア</a>
                </div>
            </form>
        </div>
        
        <!-- イベント一覧 -->
        <c:choose>
            <c:when test="${not empty events}">
                <div class="event-grid">
                    <c:forEach var="event" items="${events}">
                        <div class="event-card">
                            <div class="event-category">${event.category}</div>
                            <div class="event-title">${event.eventName}</div>
                            <div class="event-info">📅 <custom:formatLocalDateTime localDateTime="${event.eventDate}" pattern="yyyy年MM月dd日 HH:mm" /></div>
                            <div class="event-info">📍 ${event.venue}</div>
                            <div class="event-price">¥<fmt:formatNumber value="${event.price}" pattern="#,###" /></div>
                            <div class="event-info">
                                <span class="event-seats">残り座席: ${event.availableSeats}席</span>
                                / 全${event.totalSeats}席
                            </div>
                            <div style="margin-top: 15px;">
                                <a href="${pageContext.request.contextPath}/events/${event.id}" class="btn">詳細を見る</a>
                                <c:if test="${event.availableSeats > 0}">
                                    <a href="${pageContext.request.contextPath}/events/${event.id}/reserve" class="btn" style="background-color: #28a745;">予約する</a>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="no-events">
                    <h3>該当するイベントが見つかりません</h3>
                    <p>検索条件を変更してお試しください。</p>
                </div>
            </c:otherwise>
        </c:choose>
        
        <!-- ナビゲーションリンク -->
        <div style="margin-top: 30px; text-align: center;">
            <a href="${pageContext.request.contextPath}/myreservations?email=" class="btn btn-secondary">予約確認</a>
        </div>
    </div>
</body>
</html>
