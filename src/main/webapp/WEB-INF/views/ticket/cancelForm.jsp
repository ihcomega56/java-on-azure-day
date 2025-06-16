<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="custom" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>予約キャンセル - チケット予約システム</title>
    <style>
        body {
            font-family: 'Hiragino Sans', 'ヒラギノ角ゴシック', 'Yu Gothic', '游ゴシック', sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .warning-message {
            background-color: #fff3cd;
            border: 1px solid #ffeaa7;
            color: #856404;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
        }
        .warning-icon {
            font-size: 24px;
            margin-right: 10px;
        }
        .reservation-details {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin: 20px 0;
            border-left: 4px solid #dc3545;
        }
        .detail-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 8px 0;
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
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            color: #333;
        }
        input {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 16px;
            box-sizing: border-box;
        }
        input:focus {
            outline: none;
            border-color: #dc3545;
            box-shadow: 0 0 0 2px rgba(220,53,69,0.25);
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
        .required {
            color: #dc3545;
        }
        .help-text {
            font-size: 14px;
            color: #666;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>❌ 予約キャンセル</h1>
        
        <div class="warning-message">
            <span class="warning-icon">⚠️</span>
            <strong>予約をキャンセルしようとしています</strong><br>
            キャンセル後は元に戻すことができません。よくご確認の上、実行してください。
        </div>
        
        <div class="reservation-details">
            <h3>キャンセル対象の予約</h3>
            
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
                <span class="detail-label">🎟️ チケット枚数</span>
                <span class="detail-value">${reservation.quantity}枚</span>
            </div>
            
            <div class="detail-row">
                <span class="detail-label">💰 合計金額</span>
                <span class="detail-value">¥<fmt:formatNumber value="${reservation.totalPrice}" pattern="#,###" /></span>
            </div>
            
            <div class="detail-row">
                <span class="detail-label">🔑 確認コード</span>
                <span class="detail-value">${reservation.confirmationCode}</span>
            </div>
        </div>
        
        <!-- キャンセルフォーム -->
        <form method="post" action="${pageContext.request.contextPath}/cancel" onsubmit="return confirmCancel()">
            <input type="hidden" name="reservationId" value="${reservation.id}">
            
            <div class="form-group">
                <label for="email">予約者メールアドレス <span class="required">*</span></label>
                <input type="email" id="email" name="email" required>
                <div class="help-text">予約時に使用したメールアドレスを入力してください</div>
            </div>
            
            <div style="text-align: center; margin-top: 30px;">
                <button type="submit" class="btn btn-danger">
                    ❌ 予約をキャンセルする
                </button>
                <a href="${pageContext.request.contextPath}/myreservations?email=" class="btn btn-secondary">
                    戻る
                </a>
            </div>
        </form>
    </div>
    
    <script>
        function confirmCancel() {
            const email = document.getElementById('email').value;
            
            if (!email) {
                alert('メールアドレスを入力してください。');
                return false;
            }
            
            if (!email.includes('@')) {
                alert('正しいメールアドレスを入力してください。');
                return false;
            }
            
            const confirmed = confirm(
                '以下の予約をキャンセルします。\n\n' +
                'イベント: ${reservation.event.eventName}\n' +
                'チケット枚数: ${reservation.quantity}枚\n' +
                '金額: ¥' + ${reservation.totalPrice}.toLocaleString() + '\n\n' +
                '本当にキャンセルしますか？'
            );
            
            return confirmed;
        }
    </script>
</body>
</html>
