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
    <title>회원가입</title>
    <style>
        .hidden {
            display: none;
        }
        .error {
            color: red;
        }
        .success {
            color: green;
        }
    </style>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/zxcvbn/zxcvbn.js"></script>
</head>
<body>
<h2>회원가입</h2>
<form id="signupForm" action="/signup" method="post">
    <div>
        <div><label for="nameInput">이름</label></div>
        <div><input type="text" id="nameInput" class="emplNameEl" name="name" autocomplete="name" required></div>
        <p id="nameValidationMsg"></p>
    </div>

    <div>
        <div><label for="idInput">아이디</label></div>
        <div>
            <input type="text" id="idInput" class="" name="id" autocomplete="username" required>
            <button type="button" id="idDubCheckBtn" class="">중복 확인</button>
        </div>
        <p id="IdValidationMsg" class=""></p>
    </div>

    <div>
        <div><span>비밀번호 안전도:</span><span id="pwdScore"></span></div>
        <div><label for="pwdInput">비밀번호</label></div>
        <div><input type="password" id="pwdInput" class="" name="pwd" autocomplete="new-password" required></div>
        <p id="pwdValidationMsg"></p>
    </div>

    <div>
        <div>
            <div><label for="emailInput">이메일</label></div>
            <div>
                <input type="text" id="emailInput" class="" name="email" autocomplete="email" required>
                <button type="button" id="emailVerificationBtn">본인 인증</button>
            </div>
            <p id="emailValidationMsg"></p>
        </div>
        <div id="verificationCodeBox" class="hidden">
            <div>
                <input type="text" id="verificationCodeInput" class="" name="verificationCode" autocomplete="one-time-code" placeholder="인증번호">
            </div>
            <p id="verificationCodeValidationMsg"></p>
        </div>
    </div>
    <div>
        <div><label for="emplnoInput">사원번호</label></div>
        <div><input type="text" id="emplnoInput" class="" name="emplno" autocomplete="on" required></div>
        <p id="emplnoValidationMsg"></p>
    </div>

    <div>
        <input type="button" id="signupBtn" class="" value="회원가입하기" disabled>
    </div>
</form>
<script src="${pageContext.request.contextPath}/js/signupValidation.js"></script>
</body>
</html>