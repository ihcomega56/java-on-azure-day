<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="custom" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>チケット予約 - ${event.eventName}</title>
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
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            color: #333;
        }
        input, select {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 16px;
            box-sizing: border-box;
        }
        input:focus, select:focus {
            outline: none;
            border-color: #007bff;
            box-shadow: 0 0 0 2px rgba(0,123,255,0.25);
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
        .event-summary {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
            border-left: 4px solid #007bff;
        }
        .event-title {
            font-size: 20px;
            font-weight: bold;
            color: #333;
            margin-bottom: 10px;
        }
        .event-info {
            margin-bottom: 8px;
            color: #666;
        }
        .price-calculation {
            background-color: #e9f7ef;
            padding: 15px;
            border-radius: 8px;
            margin: 20px 0;
            border: 1px solid #c3e6cb;
        }
        .total-price {
            font-size: 18px;
            font-weight: bold;
            color: #155724;
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
        <h1>🎫 チケット予約</h1>
        
        <!-- イベント情報サマリー -->
        <div class="event-summary">
            <div class="event-title">${event.eventName}</div>
            <div class="event-info">📅 <custom:formatLocalDateTime localDateTime="${event.eventDate}" pattern="yyyy年MM月dd日(E) HH:mm" /></div>
            <div class="event-info">📍 ${event.venue}</div>
            <div class="event-info">💰 ¥<fmt:formatNumber value="${event.price}" pattern="#,###" /> / 1枚</div>
            <div class="event-info">🪑 残り座席: ${event.availableSeats}席</div>
        </div>
        
        <!-- 予約フォーム -->
        <form method="post" action="${pageContext.request.contextPath}/reserve" onsubmit="return validateForm()">
            <input type="hidden" name="eventId" value="${event.id}">
            
            <div class="form-group">
                <label for="email">メールアドレス <span class="required">*</span></label>
                <input type="email" id="email" name="email" required>
                <div class="help-text">予約確認メールと予約管理で使用します</div>
            </div>
            
            <div class="form-group">
                <label for="quantity">チケット枚数 <span class="required">*</span></label>
                <select id="quantity" name="quantity" required onchange="calculateTotal()">
                    <option value="">選択してください</option>
                    <c:forEach var="i" begin="1" end="${event.availableSeats > 10 ? 10 : event.availableSeats}">
                        <option value="${i}">${i}枚</option>
                    </c:forEach>
                </select>
                <div class="help-text">最大10枚まで予約可能です</div>
            </div>
            
            <!-- 料金計算 -->
            <div class="price-calculation" id="priceCalculation" style="display: none;">
                <div>チケット料金: ¥<fmt:formatNumber value="${event.price}" pattern="#,###" /> × <span id="selectedQuantity">0</span>枚</div>
                <div class="total-price">合計金額: ¥<span id="totalPrice">0</span></div>
            </div>
            
            <!-- 送信ボタン -->
            <div style="text-align: center; margin-top: 30px;">
                <button type="submit" class="btn btn-success">
                    🎫 予約を確定する
                </button>
                <a href="${pageContext.request.contextPath}/events/${event.id}" class="btn btn-secondary">
                    キャンセル
                </a>
            </div>
        </form>
    </div>
    
    <script>
        function calculateTotal() {
            const quantity = document.getElementById('quantity').value;
            const pricePerTicket = ${event.price};
            
            if (quantity && quantity > 0) {
                const total = quantity * pricePerTicket;
                document.getElementById('selectedQuantity').textContent = quantity;
                document.getElementById('totalPrice').textContent = total.toLocaleString();
                document.getElementById('priceCalculation').style.display = 'block';
            } else {
                document.getElementById('priceCalculation').style.display = 'none';
            }
        }
        
        function validateForm() {
            const email = document.getElementById('email').value;
            const quantity = document.getElementById('quantity').value;
            
            if (!email || !quantity) {
                alert('すべての必須項目を入力してください。');
                return false;
            }
            
            if (!email.includes('@')) {
                alert('正しいメールアドレスを入力してください。');
                return false;
            }
            
            const confirmed = confirm(
                'チケット枚数: ' + quantity + '枚\n' +
                '合計金額: ¥' + (quantity * ${event.price}).toLocaleString() + '\n\n' +
                '予約を確定しますか？'
            );
            
            return confirmed;
        }
    </script>
</body>
</html>
