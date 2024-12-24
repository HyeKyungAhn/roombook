const nameInputEl = document.getElementById('nameInput');
const idInputEl = document.getElementById('idInput');
const pwdInputEl = document.getElementById('pwdInput');
const pwdScoreBoxEl = document.getElementById('pwdScoreBox');
const pwdScoreInputEl = document.getElementById('pwdScore');
const emailInputEl = document.getElementById('emailInput');
const emailInputBoxEl = document.getElementById('emailInputBox');
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

const CLASSES_COLOR = {
    error: 'fontColor_red',
    warning: 'fontColor_yellow',
    green: 'fontColor_green'
}

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

function showVerificationCodeInputBox() {
    verificationCodeBox.classList.remove('hidden');
}

function hideVerificationCodeInputBox() {
    verificationCodeBox.classList.add('hidden');
}

function setColoredMsg(msgElement, msg, color) {
    msgElement.innerText = msg;
    msgElement.classList.add(color);
}

//////////// Validation ////////////

const myZxcvbn = {
    onInputPwd : function(){
        pwdMsgEl.innerText = '';
        const pwd = pwdInputEl.value;

        pwdScoreInputEl.classList.remove(CLASSES_COLOR.error, CLASSES_COLOR.warning, CLASSES_COLOR.green);
        pwdMsgEl.classList.remove(CLASSES_COLOR.error, CLASSES_COLOR.green);

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
                setColoredMsg(pwdScoreInputEl, '안전도 나쁨', CLASSES_COLOR.error);
                setColoredMsg(pwdMsgEl, appState.validationMessages.pwdInsafe, CLASSES_COLOR.error);
                appState.pwdSafe = false;
                break;
            }
            case 1: {
                setColoredMsg(pwdScoreInputEl, '안전도 낮음', CLASSES_COLOR.error);
                setColoredMsg(pwdMsgEl, appState.validationMessages.pwdInsafe, CLASSES_COLOR.error);
                appState.pwdSafe = false;
                break;
            }
            case 2: {
                setColoredMsg(pwdScoreInputEl, '안전도 보통', CLASSES_COLOR.warning);
                appState.pwdSafe = true;
                break;
            }
            case 3: {
                setColoredMsg(pwdScoreInputEl, '안전도 좋음', CLASSES_COLOR.green);
                appState.pwdSafe = true;
                break;
            }
            case 4: {
                setColoredMsg(pwdScoreInputEl, '안전도 아주좋음', CLASSES_COLOR.green);
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
    const name = nameInputEl.value;
    nameMsgEl.innerText = '';
    nameMsgEl.classList.remove(CLASSES_COLOR.error);

    if(!name) {
        setColoredMsg(nameMsgEl, appState.validationMessages.nameEmpty, CLASSES_COLOR.error);
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
        setColoredMsg(nameMsgEl, appState.validationMessages.nameInvalid, CLASSES_COLOR.error);
    }

    checkSignUpEligibility();
}


function validateId(){
    const id = idInputEl.value;
    idMsgEl.innerText = '';
    appState.idUnique = false;
    idMsgEl.classList.remove(CLASSES_COLOR.error);

    if(!id) {
        setColoredMsg(idMsgEl, appState.validationMessages.idEmpty, CLASSES_COLOR.error);
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
        setColoredMsg(idMsgEl, appState.validationMessages.idInvalid, CLASSES_COLOR.error);
        idDupCheckBtn.disabled = true;
    }

    checkSignUpEligibility();
}

let timeout = null

function validateEmail(){
    const email = emailInputEl.value;
    emailMsgEl.innerText = '';
    emailMsgEl.classList.remove(CLASSES_COLOR.error);

    if(!email) {
        setColoredMsg(emailMsgEl, appState.validationMessages.emailEmpty, CLASSES_COLOR.error);
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
        setColoredMsg(emailMsgEl, appState.validationMessages.emailInvalid, CLASSES_COLOR.error);
    }

    checkSignUpEligibility();
}

function validateEmplno(){
    const emplno = emplnoInputEl.value;
    emplnoMsgEl.innerText = '';
    emplnoMsgEl.classList.remove(CLASSES_COLOR.error);

    if(!emplno) {
        setColoredMsg(emplnoMsgEl, appState.validationMessages.emplnoEmpty, CLASSES_COLOR.error);
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
        setColoredMsg(emplnoMsgEl, appState.validationMessages.emplnoInvalid, CLASSES_COLOR.error);
    }

    checkSignUpEligibility();
}

function validateVerificationCode() {
    const verificationCode = verificationCodeInputEl.value;
    verificationCodeMsgEl.innerText = '';
    verificationCodeMsgEl.classList.remove(CLASSES_COLOR.error);

    if(!verificationCode) {
        setColoredMsg(verificationCodeMsgEl, appState.validationMessages.verificationCodeEmpty, CLASSES_COLOR.error);
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
                    setColoredMsg(idMsgEl, appState.validationMessages.duplicatedId, CLASSES_COLOR.error);
                    appState.idUnique = false;
                    idInputEl.focus();
                } else{
                    setColoredMsg(idMsgEl, appState.validationMessages.usableId, CLASSES_COLOR.green);
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

function toggleVcodeThrobber() {
    const throbberBoxEl = document.getElementById('throbberBox');

    if(throbberBoxEl) {
        throbberBoxEl.remove();
    } else {
        emailInputBoxEl.insertAdjacentHTML('afterend', `
            <div id="throbberBox" class="flexRow marginTop5 marginBottom5">
                <div>
                    <span class="throbberText">발급중</span>
                </div>
                <img alt="인증 메일 전송 중" class="throbberImage" src="/img/throbber.gif">
            </div>
            `);
    }
}

emailVerificationBtn.addEventListener('click', function(){
    verificationCodeInputEl.innerText = '';
    hideVerificationCodeInputBox();

    const emailVerificationLink = appState.links.find((element) => element.rel === 'emailVerification');
    const email = {
        'email': emailInputEl.value,
    }

    fetchVerificationCode(emailVerificationLink.href, email);
});

function fetchVerificationCode(url, email) {
    const defaultOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(email),
    }

    const fetchPromise = fetch(url, defaultOptions);

    toggleVcodeThrobber();

    fetchPromise
        .then(response => response.text())
        .then(response => {
            toggleVcodeThrobber();
            const jsonResponse = JSON.parse(response);

            if(jsonResponse.result === "SUCCESS"){
                appState.verificationCodeSent = true;
                showVerificationCodeInputBox();
                checkSignUpEligibility();
            } else if(jsonResponse.result === "SIGNUP_UNABLE" || jsonResponse.result === "FAIL") {
                alert(jsonResponse.errorMessage);
            } else if(jsonResponse.result === "EMAIL_EXIST"){
                emailMsgEl.innerText = jsonResponse.errorMessage;
            } else {
                validateEmail();
            }
        })
        .catch(error => console.error('Error:', error));
}

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