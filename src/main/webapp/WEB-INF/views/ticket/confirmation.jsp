<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="custom" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>予約確認 - チケット予約システム</title>
    <style>
        body {
            font-family: 'Hiragino Sans', 'ヒラギノ角ゴシック', 'Yu Gothic', '游ゴシック', sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 700px;
            margin: 0 auto;
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .success-message {
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
            text-align: center;
        }
        .success-icon {
            font-size: 48px;
            margin-bottom: 15px;
        }
        .confirmation-code {
            font-size: 24px;
            font-weight: bold;
            color: #28a745;
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 8px;
            text-align: center;
            margin: 20px 0;
            border: 2px dashed #28a745;
        }
        .reservation-details {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin: 20px 0;
        }
        .detail-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #e9ecef;
        }
        .detail-row:last-child {
            border-bottom: none;
        }
        .detail-label {
            font-weight: bold;
            color: #495057;
        }
        .detail-value {
            color: #333;
        }
        .total-price {
            font-size: 20px;
            font-weight: bold;
            color: #28a745;
        }
        .btn {
            background-color: #007bff;
            color: white;
            padding: 12px 24px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 16px;
            margin: 5px;
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
        .important-note {
            background-color: #fff3cd;
            border: 1px solid #ffeaa7;
            color: #856404;
            padding: 15px;
            border-radius: 8px;
            margin: 20px 0;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="success-message">
            <div class="success-icon">🎉</div>
            <h2>予約が完了しました！</h2>
            <p>チケットの予約が正常に完了いたしました。</p>
        </div>
        
        <div class="confirmation-code">
            確認コード: ${reservation.confirmationCode}
        </div>
        
        <div class="reservation-details">
            <h3>📋 予約詳細</h3>
            
            <div class="detail-row">
                <span class="detail-label">🎫 イベント名</span>
                <span class="detail-value">${reservation.event.eventName}</span>
            </div>
            
            <div class="detail-row">
                <span class="detail-label">📅 開催日時</span>
                <span class="detail-value">
                    <custom:formatLocalDateTime localDateTime="${reservation.event.eventDate}" pattern="yyyy年MM月dd日(E) HH:mm" />
                </span>
            </div>
            
            <div class="detail-row">
                <span class="detail-label">📍 会場</span>
                <span class="detail-value">${reservation.event.venue}</span>
            </div>
            
            <div class="detail-row">
                <span class="detail-label">📧 予約者メール</span>
                <span class="detail-value">${reservation.email}</span>
            </div>
            
            <div class="detail-row">
                <span class="detail-label">🎟️ チケット枚数</span>
                <span class="detail-value">${reservation.quantity}枚</span>
            </div>
            
            <div class="detail-row">
                <span class="detail-label">⏰ 予約日時</span>
                <span class="detail-value">
                    <custom:formatLocalDateTime localDateTime="${reservation.reservationTime}" pattern="yyyy年MM月dd日 HH:mm" />
                </span>
            </div>
            
            <div class="detail-row">
                <span class="detail-label">💰 合計金額</span>
                <span class="detail-value total-price">¥<fmt:formatNumber value="${reservation.totalPrice}" pattern="#,###" /></span>
            </div>
        </div>
        
        <div class="important-note">
            <h4>⚠️ 重要なご案内</h4>
            <ul>
                <li>確認コードは予約の管理・キャンセル時に必要です。大切に保管してください。</li>
                <li>イベント当日は、確認コードまたはメールアドレスをご提示ください。</li>
                <li>予約のキャンセルは、イベント開催日の前日まで可能です。</li>
            </ul>
        </div>
        
        <div style="text-align: center; margin-top: 30px;">
            <a href="${pageContext.request.contextPath}/events" class="btn">
                📋 他のイベントを見る
            </a>
            <a href="${pageContext.request.contextPath}/myreservations?email=${reservation.email}" class="btn btn-secondary">
                📄 予約一覧を確認
            </a>
        </div>
    </div>
</body>
</html>
