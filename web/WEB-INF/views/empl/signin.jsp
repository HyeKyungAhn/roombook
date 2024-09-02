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
</head>
<body>
<div>
    <div>
        <h1>로그인</h1>
    </div>
    <div>
        <div>
            <label for="idInput">아이디</label>
            <input id="idInput" placeholder="아이디를 입력하세요">
            <div>
                <p id="idMsg"></p>
            </div>
        </div>
        <div>
            <label for="passwordInput">비밀번호</label>
            <input type="password" id="passwordInput" placeholder="비밀번호를 입력하세요">
            <div>
                <p id="passwordMsg"></p>
            </div>
        </div>
        <div>
            <button id="signInBtn" type="button">로그인</button>
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
