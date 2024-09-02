<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-08-12
  Time: 오후 4:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <title>roombook | 회원가입</title>
</head>
<body>
<div>
    <p>회원가입 완료</p>
    <p>RoomBook의 일원이 되신 것을 환영합니다!</p>
</div>
<div>
    <button type="button" id="signInBtn">로그인</button>
    <button type="button" id="homeBtn">홈으로</button>
</div>
<script>
    const signInBtnEl = document.getElementById('signInBtn');
    const homeBtnEl = document.getElementById('homeBtn');
    const links = JSON.parse('${links}');

    signInBtnEl.addEventListener('click', function () {
        location.href = links.signin;
    });

    homeBtnEl.addEventListener('click', function () {
        location.href = links.home;
    });
</script>
</body>
</html>
