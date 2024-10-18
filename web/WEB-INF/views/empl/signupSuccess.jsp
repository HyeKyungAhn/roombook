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
    <div class="horizontalCenter800 paddingTop40">
        <div>
            <div class="headerWrapper textAlignCenter marginTop10 marginBottom10 fontWeight600 fontSize35">
                <p class="noMargin">회원가입 완료</p>
            </div>
            <p class="textAlignCenter marginTop10 marginBottom10 fontSize20">RoomBook의 일원이 되신 것을 환영합니다!</p>
        </div>
        <div class="btnWrapper">
            <button type="button" id="signInBtn" class="btnM2 bg_yellow">로그인</button>
            <button type="button" id="homeBtn" class="btnM2">홈으로</button>
        </div>
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
