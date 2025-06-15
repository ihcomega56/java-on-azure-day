<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="localDateTime" required="true" type="java.time.LocalDateTime" %>
<%@ attribute name="pattern" required="false" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    // デフォルトのパターン
    String patternToUse = (pattern != null) ? pattern : "yyyy年MM月dd日 HH:mm";
    
    // LocalDateTimeをjava.util.Dateに変換
    java.util.Date date = null;
    if (localDateTime != null) {
        date = java.util.Date.from(localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
    }
    
    // リクエスト属性としてjava.util.Dateを設定
    request.setAttribute("convertedDate", date);
%>

<c:if test="${not empty convertedDate}">
    <fmt:formatDate value="${convertedDate}" pattern="${patternToUse}" />
</c:if>
<c:if test="${empty convertedDate}">
    -
</c:if>
