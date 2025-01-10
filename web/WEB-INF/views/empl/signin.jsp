<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-08-12
  Time: 오후 4:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <title>roombook | 로그인</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/login.css">
</head>
<body>
<div class="loginRootWrapper">
    <div class="loginHeaderTextWrapper">
        <h1 class="loginHeaderText">로그인</h1>
    </div>
    <div class="loginWrapper">
        <div class="loginContent">
            <div class="loginInputsWrapper">
                <div class="inputBox">
                    <label for="idInput" class="inputLabel">아이디</label>
                    <input id="idInput" class="textInput" placeholder="아이디를 입력하세요">
                    <div>
                        <p id="idMsg" class="textInputMsg"></p>
                    </div>
                </div>
                <div class="inputBox">
                    <label for="passwordInput" class="inputLabel">비밀번호</label>
                    <input type="password" id="passwordInput" class="textInput" placeholder="비밀번호를 입력하세요">
                    <div>
                        <p id="passwordMsg" class="textInputMsg"></p>
                    </div>
                </div>
            </div>
            <div>
                <button id="signInBtn" class="btnL bg_yellow" type="button">로그인</button>
            </div>
            <div class="loginHelpWrapper">
                <div class="linkBtn">
                    <a href="${pageContext.request.contextPath}/signup">회원가입</a>
                </div>
                <div class="linkBtn">
                    <a href="#">계정 찾기</a>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    const idInputEl = document.getElementById('idInput');
    const passwordInputEl = document.getElementById('passwordInput');
    const signInBtn = document.getElementById('signInBtn');

    const idMsgEl = document.getElementById('idMsg');
    const passwordMsgEl = document.getElementById('passwordMsg');

    let appState;

    document.addEventListener('DOMContentLoaded', function () {
        fetch('/api/signin/appstate', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        }).then(response => {
            return response.text();
        }).then(data => {
            appState = JSON.parse(data);
        }).catch(error => {
            console.error('Error:', error)
        });
    });

    signInBtn.addEventListener('click', function () {
        let signInData = {
            'id': idInputEl.value,
            'password': passwordInputEl.value,
        }

        fetch('/signin', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(signInData),
        }).then(response => {
            return response.text();
        }).then(data => {
            const response = JSON.parse(data);
            if(response.result === 'SUCCESS') {
                location.href = response.redirect;
            } else {
                alert(appState.validationMessage.authFail);
            }
        }).catch(error => {
            console.error('Error:', error)
        });
    });

    function validateInputs(){
        if (!idInputEl.value || idInputEl.value === '') {
            idMsgEl.innerText = appState.validationMessage.emptyId;
        } else if (!passwordInputEl.value || passwordInputEl.value === '') {
            passwordMsgEl.innerText = appState.validationMessage.emptyPassword;
        }
    }
</script>
</body>
</html>
