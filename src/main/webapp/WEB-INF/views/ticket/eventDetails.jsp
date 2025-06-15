<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="custom" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${event.eventName} - チケット予約システム</title>
    <style>
        body {
            font-family: 'Hiragino Sans', 'ヒラギノ角ゴシック', 'Yu Gothic', '游ゴシック', sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
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
            margin: 5px;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .btn-success {
            background-color: #28a745;
        }
        .btn-success:hover {
            background-color: #218838;
        }
        .btn-secondary {
            background-color: #6c757d;
        }
        .btn-secondary:hover {
            background-color: #545b62;
        }
        .event-category {
            display: inline-block;
            background-color: #e9ecef;
            color: #495057;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 14px;
            margin-bottom: 20px;
        }
        .event-title {
            font-size: 28px;
            font-weight: bold;
            color: #333;
            margin-bottom: 20px;
        }
        .event-info {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }
        .info-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #e9ecef;
        }
        .info-row:last-child {
            border-bottom: none;
        }
        .info-label {
            font-weight: bold;
            color: #495057;
        }
        .info-value {
            color: #333;
        }
        .event-description {
            line-height: 1.6;
            color: #666;
            margin: 20px 0;
        }
        .price-highlight {
            font-size: 24px;
            font-weight: bold;
            color: #28a745;
        }
        .seats-warning {
            color: #dc3545;
            font-weight: bold;
        }
        .alert {
            padding: 12px 20px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
        .alert-success {
            background-color: #d4edda;
            border-color: #c3e6cb;
            color: #155724;
        }
        .alert-danger {
            background-color: #f8d7da;
            border-color: #f5c6cb;
            color: #721c24;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- メッセージ表示 -->
        <c:if test="${not empty message}">
            <div class="alert alert-success">
                ${message}
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                ${error}
            </div>
        </c:if>
        
        <div class="event-category">${event.category}</div>
        <h1 class="event-title">${event.eventName}</h1>
        
        <div class="event-info">
            <div class="info-row">
                <span class="info-label">📅 開催日時</span>
                <span class="info-value">
                    <custom:formatLocalDateTime localDateTime="${event.eventDate}" pattern="yyyy年MM月dd日(E) HH:mm" />
                </span>
            </div>
            <div class="info-row">
                <span class="info-label">📍 会場</span>
                <span class="info-value">${event.venue}</span>
            </div>
            <div class="info-row">
                <span class="info-label">💰 料金</span>
                <span class="info-value price-highlight">¥<fmt:formatNumber value="${event.price}" pattern="#,###" /></span>
            </div>
            <div class="info-row">
                <span class="info-label">🪑 座席情報</span>
                <span class="info-value">
                    <c:choose>
                        <c:when test="${event.availableSeats <= 10}">
                            <span class="seats-warning">残り${event.availableSeats}席</span>
                        </c:when>
                        <c:otherwise>
                            残り${event.availableSeats}席
                        </c:otherwise>
                    </c:choose>
                    / 全${event.totalSeats}席
                </span>
            </div>
        </div>
        
        <c:if test="${not empty event.description}">
            <h3>イベント詳細</h3>
            <div class="event-description">
                ${event.description}
            </div>
        </c:if>
        
        <!-- アクションボタン -->
        <div style="margin-top: 30px; text-align: center;">
            <c:choose>
                <c:when test="${event.availableSeats > 0}">
                    <a href="${pageContext.request.contextPath}/events/${event.id}/reserve" class="btn btn-success">
                        🎫 このイベントを予約する
                    </a>
                </c:when>
                <c:otherwise>
                    <button class="btn" disabled style="background-color: #dc3545;">
                        売り切れ
                    </button>
                </c:otherwise>
            </c:choose>
            <a href="${pageContext.request.contextPath}/events" class="btn btn-secondary">
                📋 イベント一覧に戻る
            </a>
        </div>
    </div>
</body>
</html>
