<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-08-22
  Time: 오후 3:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <title>rookbook</title>
</head>
<body>
<div>
    <p>404 찾을 수 없는 페이지입니다.</p>
    <div>
        <button id="homeBtn">홈으로</button>
    </div>
</div>
<script>
    let links;

    const homeBtnEl = document.getElementById('homeBtn');

    document.addEventListener('DOMContentLoaded', function(){
        links = JSON.parse('${links}');
    });

    homeBtnEl.addEventListener('click', function () {
        location.href = links.home;
    });
</script>
</body>
</html>
