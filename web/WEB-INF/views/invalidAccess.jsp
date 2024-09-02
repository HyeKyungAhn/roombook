<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-08-20
  Time: 오후 2:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <title>roombook</title>
</head>
<body>
    <div>
        <p>올바르지 않은 접근입니다.</p>
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
