const nameInputEl = document.getElementById('nameInput');
const idInputEl = document.getElementById('idInput');
const pwdInputEl = document.getElementById('pwdInput');
const pwdScoreInputEl = document.getElementById('pwdScore');
const emailInputEl = document.getElementById('emailInput');
const verificationCodeInputEl = document.getElementById('verificationCodeInput');
const emplnoInputEl = document.getElementById('emplnoInput');

const verificationCodeBox = document.getElementById('verificationCodeBox');

const idDupCheckBtn = document.getElementById('idDubCheckBtn');
const emailVerificationBtn = document.getElementById('emailVerificationBtn');
const signupBtn = document.getElementById('signupBtn');

const nameMsgEl = document.getElementById('nameValidationMsg');
const idMsgEl = document.getElementById('IdValidationMsg');
const pwdMsgEl = document.getElementById('pwdValidationMsg');
const emailMsgEl = document.getElementById('emailValidationMsg');
const verificationCodeMsgEl = document.getElementById('verificationCodeValidationMsg');
const emplnoMsgEl = document.getElementById('emplnoValidationMsg');

let appState;

function init() {
    idDupCheckBtn.disabled = appState.idDubCheckBtnDisabled;
    emailVerificationBtn.disabled = appState.verificationBtnDisabled;
    signupBtn.disabled = appState.signupBtnDisabled;

    nameInputEl.value = '';
    idInputEl.value = '';
    pwdInputEl.value = '';
    emailInputEl.value = '';
    verificationCodeInputEl.value = '';
    emplnoInputEl.value = '';
}

function toggleVerificationCodeInputBox(){
    verificationCodeBox.classList.remove('hidden');
}

//////////// Validation ////////////

const myZxcvbn = {
    onInputPwd : function(){
        pwdMsgEl.innerText = '';
        const pwd = pwdInputEl.value;

        if(!pwd) {
            pwdScoreInputEl.innerText = '';
            pwdMsgEl.innerText = appState.validationMessages.pwdEmpty;
            appState.pwdSafe = false;
            checkSignUpEligibility();
            return;
        }

        const result = zxcvbn(pwd);

        switch (result.score){
            case 0: {
                pwdScoreInputEl.innerText = '나쁨';
                pwdMsgEl.innerText = appState.validationMessages.pwdInsafe;
                appState.pwdSafe = false;
                break;
            }
            case 1: {
                pwdScoreInputEl.innerText = '낮음';
                pwdMsgEl.innerText = appState.validationMessages.pwdInsafe;
                appState.pwdSafe = false;
                break;
            }
            case 2: {
                pwdScoreInputEl.innerText = '보통';
                appState.pwdSafe = true;
                break;
            }
            case 3: {
                pwdScoreInputEl.innerText = '좋음';
                appState.pwdSafe = true;
                break;
            }
            case 4: {
                pwdScoreInputEl.innerText = '아주좋음';
                appState.pwdSafe = true;
                break;
            }
        }
        checkSignUpEligibility();
    }
}

function checkSignUpEligibility() {
    signupBtn.disabled = !(appState.nameValid && appState.idValid && appState.idUnique && appState.pwdSafe
        && appState.emailValid && appState.verificationCodeSent && !appState.verificationCodeEmpty && appState.emplnoValid);
}

function showErrorMsg(res){
    if(!res.nameValid) validateName();
    if(!res.idValid || !res.idUnique) validateId();
    if(!res.pwdSafe) myZxcvbn.onInputPwd();
    if(!res.emailValid) validateEmail();
    if(!res.verificationCodeEmpty) validateVerificationCode();
    if(!res.emplnoValid) validateEmplno();
}

//////////// Validation(Event Handlers) ////////////

function validateName(){
    nameMsgEl.innerText = '';
    const name = nameInputEl.value;

    if(!name) {
        nameMsgEl.innerText = appState.validationMessages.nameEmpty;
        appState.nameValid = false;
        checkSignUpEligibility();
        return;
    }

    const regex = /^(?:[가-힣]{2,18}|[a-zA-Z]{2,18})$/g;

    const isNameValid = name.match(regex);

    if(isNameValid){
        appState.nameValid = true;
    } else {
        appState.nameValid = false;
        nameMsgEl.innerText = appState.validationMessages.nameInvalid;
    }

    checkSignUpEligibility();
}


function validateId(){
    idMsgEl.innerText = '';
    const id = idInputEl.value;

    if(!id) {
        idMsgEl.innerText = appState.validationMessages.idEmpty;
        appState.idValid = false;
        checkSignUpEligibility();
        return;
    }

    const regex = /^[a-zA-Z0-9_$]{5,20}$/g;

    const isInputValid = idInputEl.value.match(regex);

    if(isInputValid){
        appState.idValid = true;
        idDupCheckBtn.disabled = false;
    } else {
        appState.idValid = false;
        idMsgEl.innerText = appState.validationMessages.idInvalid;
        idDupCheckBtn.disabled = true;
    }

    checkSignUpEligibility();
}

let timeout = null

function validateEmail(){
    emailMsgEl.innerText = '';
    const email = emailInputEl.value;

    if(!email) {
        emailMsgEl.innerText = appState.validationMessages.emailEmpty;
        appState.emailValid = false;
        emailVerificationBtn.disabled = true;
        checkSignUpEligibility();
        return;
    }

    const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/g;

    const isInputValid = email.match(regex);

    if(isInputValid){
        appState.emailValid = true;
        emailVerificationBtn.disabled = false;
    } else {
        appState.emailValid = false;
        emailVerificationBtn.disabled = true;
        emailMsgEl.innerText = appState.validationMessages.emailInvalid;
    }

    checkSignUpEligibility();
}

function validateEmplno(){
    emplnoMsgEl.innerText = '';
    const emplno = emplnoInputEl.value;

    if(!emplno) {
        emplnoMsgEl.innerText = appState.validationMessages.emplnoEmpty;
        appState.emplnoValid = false;
        checkSignUpEligibility();
        return;
    }

    const regex = /^\d{6,12}$/g;

    const isInputValid = emplno.match(regex);

    if(isInputValid){
        appState.emplnoValid = true;
    } else {
        appState.emplnoValid = false;
        emplnoMsgEl.innerText = appState.validationMessages.emplnoInvalid;
    }

    checkSignUpEligibility();
}

function validateVerificationCode() {
    verificationCodeMsgEl.innerText = '';
    const verificationCode = verificationCodeInputEl.value;

    if(!verificationCode) {
        verificationCodeMsgEl.innerText = appState.validationMessages.verificationCodeEmpty;
        appState.verificationCodeEmpty = true;
    } else {
        appState.verificationCodeEmpty = false;
    }

    checkSignUpEligibility();
}

//////////// Event Listeners ////////////

window.addEventListener('pageshow', function (e) {
    if(e.persisted){
        getAppStates();
        init();
    }
});

document.addEventListener('DOMContentLoaded', function(){
    getAppStates();
});

function getAppStates () {
    fetch('/api/signup/appstate', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    })
        .then(response => {
            return response.text();
        })
        .then(data => {
            appState = JSON.parse(data);
            init();
            return true;
        })
        .catch(error => console.error('Error:', error));
}

nameInputEl.addEventListener('input', validateName);
idInputEl.addEventListener('input', validateId);
pwdInputEl.addEventListener('input', myZxcvbn.onInputPwd);
emplnoInputEl.addEventListener('input', validateEmplno);
verificationCodeInputEl.addEventListener('input', validateVerificationCode);

idDupCheckBtn.addEventListener('click', function(){
    idMsgEl.innerText = '';
    const id = idInputEl.value;

    const idDupCheckUrl = appState.links.find((element) => element.rel === 'idDuplicationCheck').href.replace('{id}', id);

    if (id) {
        fetch(idDupCheckUrl, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        })
            .then(response => response.text())
            .then((response) => {
                const jsonResponse = JSON.parse(response);
                if(jsonResponse.hasId){
                    idMsgEl.innerText = appState.validationMessages.duplicatedId;
                    appState.idUnique = false;
                    idInputEl.focus();
                } else{
                    idMsgEl.innerText = appState.validationMessages.usableId;
                    appState.idUnique = true;
                }

                checkSignUpEligibility();

                return true;
            })
            .catch(error => console.error('Error:', error));
    }

});

emailInputEl.addEventListener('keydown', function() {
    clearTimeout(timeout);

    timeout = setTimeout(function() {
        validateEmail();
    }, 200); // 100ms 동안 대기
});

emailVerificationBtn.addEventListener('click', function(){
    const emailVerificationLink = appState.links.find((element) => element.rel === 'emailVerification');
    const email = {
        'email': emailInputEl.value,
    }

    fetch(emailVerificationLink.href, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(email),
    })
        .then(response => response.text())
        .then(response => {
            const jsonResponse = JSON.parse(response);

            if(jsonResponse.result === "SUCCESS"){
                appState.verificationCodeSent = true;
                toggleVerificationCodeInputBox();
                checkSignUpEligibility();
            } else if(jsonResponse.result === "SIGNUP_UNABLE") {
                alert(jsonResponse.errorMessage);
            } else if(jsonResponse.result === "EMAIL_EXIST"){
                emailMsgEl.innerText = jsonResponse.errorMessage;
            } else {
                validateEmail();
            }
        })
        .catch(error => console.error('Error:', error));
});

signupBtn.addEventListener('click', function(){
    const signupLink = appState.links.find((element) => element.rel === 'signup');

    const inputs = {
        'name': nameInputEl.value,
        'id': idInputEl.value,
        'pwd': pwdInputEl.value,
        'email': emailInputEl.value,
        'verificationCode': verificationCodeInputEl.value,
        'emplno': emplnoInputEl.value
    }

    fetch(signupLink.href, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(inputs),
    })
        .then(response => response.text())
        .then(response => {
            const jsonResponse = JSON.parse(response);

            if(jsonResponse.serverState.result === "INVALID_INPUTS"){
                showErrorMsg(result);
            } else if(jsonResponse.serverState.result === "FAIL"){
                alert(jsonResponse.serverState.errorMessage);
            }else if(jsonResponse.serverState.result === "SUCCESS") {
                location.href = appState.links.find((element) => element.rel === 'signupSuccess').href;
            }
        })
        .catch(error => console.error('Error:', error));
});