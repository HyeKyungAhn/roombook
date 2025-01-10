<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-07-03
  Time: 오후 11:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title></title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/zxcvbn/zxcvbn.js"></script>
</head>
<body>
    <div class="horizontalCenter800">
        <div class="headerWrapper">
            <h1>회원가입</h1>
        </div>
        <form id="signupForm" action="${pageContext.request.contextPath}/signup" method="post">
            <div class="marginBottom10">
                <div class="marginBottom5"><label for="nameInput">이름</label></div>
                <div><input type="text" id="nameInput" class="emplNameEl roundInputWidth200" name="name" autocomplete="name" required></div>
                <p id="nameValidationMsg" class="marginTop5"></p>
            </div>
            <div class="marginBottom10">
                <div class="marginBottom5"><label for="idInput">아이디</label></div>
                <div class="flexRow">
                    <input type="text" id="idInput" class="roundInputWidth200 marginRight10" name="id" autocomplete="username" required>
                    <button type="button" id="idDubCheckBtn" class="btnS2">중복 확인</button>
                </div>
                <p id="IdValidationMsg" class="marginTop5"></p>
            </div>
            <div class="marginBottom10">
                <div class="marginBottom5"><label for="pwdInput">비밀번호</label></div>
                <div><input type="password" id="pwdInput" class="roundInputWidth200" name="pwd" autocomplete="new-password" required></div>
                <div id="pwdScoreBox">
                    <span id="pwdScore"></span>
                </div>
                <p id="pwdValidationMsg" class="marginTop5"></p>
            </div>
            <div class="marginBottom10">
                <div id="emailInputBox">
                    <div class="marginBottom5"><label for="emailInput">이메일</label></div>
                    <div class="flexRow">
                        <input type="text" id="emailInput" class="roundInputWidth200 marginRight10" name="email" autocomplete="email" required>
                        <button type="button" id="emailVerificationBtn" class="btnS2">본인 인증</button>
                    </div>
                    <p id="emailValidationMsg" class="marginTop5"></p>
                </div>
                <div id="verificationCodeBox" class="hidden">
                    <div>
                        <label for="verificationCodeInput" class="hidden">인증번호 입력란</label>
                        <input type="text" id="verificationCodeInput" class="roundInputWidth200" name="verificationCode" autocomplete="one-time-code" placeholder="인증번호">
                    </div>
                    <p id="verificationCodeValidationMsg" class="marginTop5"></p>
                </div>
            </div>
            <div class="marginBottom10">
                <div class="marginBottom5"><label for="emplnoInput">사원번호</label></div>
                <div><input type="text" id="emplnoInput" class="roundInputWidth200" name="emplno" autocomplete="on" required></div>
                <p id="emplnoValidationMsg"></p>
            </div>
            <div class="btnWrapper">
                <input type="button" id="signupBtn" class="btnM bg_yellow" value="회원가입하기" disabled>
            </div>
        </form>
    </div>
<script src="${pageContext.request.contextPath}/resources/js/signupValidation.js"></script>
</body>
</html>