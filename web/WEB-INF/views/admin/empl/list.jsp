<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-08-24
  Time: 오전 12:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE>
<html lang="kr">
<head>
    <title>roombook admin |권한 관리</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/empl.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
</head>
<body>
    <div class="horizontalCenter1000 paddingTop40">
        <div class="headerWrapper">
            <h1>권한 관리 페이지</h1>
        </div>
        <div class="">
            <div class="flexRow">
                <select id="searchSelect" class="roundInputWidth140 marginRight10">
                    <option value="">전체</option>
                    <option value="name">이름</option>
                    <option value="email">이메일</option>
                    <option value="id">아이디</option>
                    <option value="emplno">사원번호</option>
                    <option value="authName">권한명</option>
                </select>
                <input id="searchInput" type="text" id="search" class="roundInputWidth200">
                <button id="searchBtn" class="btnS2" type="button">검색</button>
            </div>
            <div>
                <div id="listRoot" class="emplListContainer">
                    <div id="emplListHeader" class="emplListHeader">
                        <div class="emplName emplColumn">이름</div>
                        <div class="emplEmail emplColumn">이메일</div>
                        <div class="emplId emplColumn">아이디</div>
                        <div class="emplno emplColumn">사원번호</div>
                        <div class="emplAuthName emplColumn">권한명</div>
                    </div>
                    <div id="emplListContent" class="emplListContent">

                    </div>
                </div>
            </div>
        </div>
        <nav id="paginationNav" class="paginationContainer" aria-label="pagination">
            <div class="paginationArrow">
                <button type="button" id="prevBtn" class="chevronBtn iconChevronStart" aria-label="previous" aria-disabled="">
                </button>
            </div>
            <div class="paginationPageNumber">
                <input id="pageInput" class="currentPageInput" type="number" value=""><span class="pageSlash">/</span><span id="totalPage" class="totalPageNumber"></span>
            </div>
            <div class="paginationArrow">
                <button type="button" id="nextBtn" class="chevronBtn iconChevronEnd" aria-label="next" aria-disabled="">
                </button>
            </div>
        </nav>
    </div>
<script>
    const searchSelectEl = document.getElementById('searchSelect');
    const searchInputEl = document.getElementById('searchInput');
    const searchBtnEl = document.getElementById('searchBtn');
    const listRootEl = document.getElementById('listRoot');
    const listHeaderEl = document.getElementById('emplListHeader');
    const listContentEl = document.getElementById('emplListContent');

    let authSelectEl = document.getElementById('authSelect');
    let authModBtnEl = document.getElementById('authModBtn');
    let emplListRowEls = document.querySelectorAll('.emplRow');

    //pagination
    const pageInputEl = document.querySelector('#pageInput');
    const chevronBtns = document.getElementsByClassName('chevronBtn');
    const prevBtnEl = document.getElementById('prevBtn');
    const nextBtnEl = document.getElementById('nextBtn');
    const totalPageEl = document.getElementById('totalPage');

    let pageHandler;
    let links;
    let currentPage = 1;

    function initPagination(){
        if(!pageHandler) return;

        prevBtnEl.disabled = !pageHandler.showPrev;
        nextBtnEl.disabled = !pageHandler.showNext; //true

        prevBtnEl.ariaDisabled = !pageHandler.showPrev;
        nextBtnEl.ariaDisabled = !pageHandler.showNext;
        pageInputEl.value = pageHandler.currentPage;
        totalPageEl.innerText = pageHandler.totalPage;
    }

    Array.from(chevronBtns).forEach(btn => btn.addEventListener('click', (e)=>{ pagination.doRedirect(e); }));

    const pagination ={
        doRedirect : e => {
            if (e.target.classList.contains("iconChevronStart") && pageHandler.currentPage !== 1) {
                requestEmplList(pageHandler.currentPage-1, searchSelectEl.value, searchInputEl.value);
            }
            if(e.target.classList.contains("iconChevronEnd") && pageHandler.currentPage !== pageHandler.totalPage){
                requestEmplList(pageHandler.currentPage+1, searchSelectEl.value, searchInputEl.value);
            }
        }
    }

    pageInputEl.addEventListener("keydown", (e) => {
        if (e.key === 'Enter') {
            if(pageInputEl.value===''||pageInputEl.value===`${'${pageHandler.currentPage}'}`){
                return;
            }
            if(0<=pageInputEl.value&& pageInputEl.value<=`${'${pageHandler.totalPage}'}`){
                requestEmplList(pageInputEl.value, searchSelectEl.value, searchInputEl.value);
            }
        }
    });


    document.addEventListener('DOMContentLoaded', function(){
        const params = new Proxy(new URLSearchParams(window.location.search), {
            get: (searchParams, prop) => searchParams.get(prop)
        });

        let pageParam = convertInvalidPageInput(params.page);
        let optionParam = params.option;
        let optionValueParam = params.optionValue;

        requestEmplList(pageParam, optionParam, optionValueParam);
    });

    function convertInvalidPageInput(page){
        return typeof page !== 'number' || !page ? 1 : page;
    }

    searchBtnEl.addEventListener('click', function () {
        const isValid = validateSearchInput();
        if(!isValid) return;

        requestEmplList(pageHandler.currentPage, searchSelectEl.value, searchInputEl.value);
    });

    function validateSearchInput() {
        if (searchSelectEl.value === "authName" && !verifyAuthName(searchSelectEl.value, searchInputEl.value)) {
            alert("검색어를 다시 입력해주세요.");
            return false;
        }
        return true;
    }

    function requestEmplList(page, option, optionValue) {
        fetch(`/api/admin/empls?page=${'${page}'}&option=${'${option}'}&optionValue=${'${optionValue}'}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        }).then(response => {
            return response.text();
        }).then(data => {
            const appState = JSON.parse(data);

            if(appState.msg==='SUCCESS'){
                pageHandler = appState.ph;
                initPagination();
                links = appState.links;

                addEmplList(appState.emplList);
            } else if(appState.msg==='INVALID_REQUEST'){
                location.href = appState.redirectUrl;
            }
            return true;
        }).catch(error => console.error('Error:', error));
    }

    function addEmplList(empls) {
        listContentEl.innerHTML = empls.map((empl, index) => {
            return `<div class='emplRow' data-id='${'${empl.emplId}'}'>
                        <div class='emplRowSub'>
                            <div class='emplName emplColumn'>${'${empl.rnm}'}</div>
                            <div class='emplEmail emplColumn'>${'${empl.email}'}</div>
                            <div class='emplId emplColumn'>${'${empl.emplId}'}</div>
                            <div class='emplno emplColumn'>${'${empl.empno}'}</div>
                            <div class='emplAuthName emplColumn'>${'${empl.emplAuthNm}'}</div>
                        </div>
                    </div>`
        }).join('');
        initEmplListRow();
    }

    function initEmplListRow(){
        emplListRowEls = document.querySelectorAll('.emplRow');

        emplListRowEls.forEach((target) => target.addEventListener('click', function (e) {
            toggleBoxHandler(e);
        }))
    }

    function toggleBoxHandler(e) {
        if(e.target.closest('.toggleBox')) return;

        const lastNode = e.target.closest('.emplRow').lastChild;

        const classes = lastNode.classList;
        if(classes && classes.contains('toggleBox')){
            deleteAuthToggle();
            return;
        }

        deleteAuthToggle();

        let row = e.target.closest('.emplRow');
        let emplId = row.dataset.id;
        row.insertAdjacentHTML('beforeend', `
                <div class="toggleBox">
                    <div>
                        <p class="emplToggleBoxHeader marginBottom5">권한 변경</p>
                        <p class="marginBottom5">권한을 선택해주세요. 변경하기 전 <strong>권한 변경 대상</strong>을 다시 한 번 확인해주세요.</p>
                    </div>
                    <div class="flexRow">
                        <select id="authSelect" class="roundInputWidth200 marginRight10">
                            <option value="사원">사원</option>
                            <option value="공간예약관리자">공간예약관리자</option>
                            <option value="사원관리자">사원관리자</option>
                            <option value="슈퍼관리자">슈퍼관리자</option>
                        </select>
                        <button type="button" id="authModBtn" class="btnS2" data-id="${'${emplId}'}">권한 변경</button>
                    </div>
                <div>
            `);

        initAuthModeRelatedEl();
    }

    function deleteAuthToggle() {
        const toggleboxEl = document.querySelector('.toggleBox');

        if (toggleboxEl) {
            toggleboxEl.parentNode.removeChild(toggleboxEl);
        }
    }

    function initAuthModeRelatedEl(){
        authSelectEl = document.getElementById('authSelect');
        authModBtnEl = document.getElementById('authModBtn');

        authModBtnEl.addEventListener('click', function (e) {
            requestAuthModification(e)
        });
    }

    function requestAuthModification(e) {
        const emplId = e.target.dataset.id;
        const url = links.find((element) => element.rel === 'updateAuth').href.replace('{id}', emplId);
        const content = {
            'authNm': authSelectEl.value
        }

        fetch(url, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(content),
        }).then(response => {
            return response.text();
        }).then(data => {
            const result = JSON.parse(data);
            console.log(result);
            if(result.result==="SUCCESS"){
                alert("권한 변경에 성공했습니다.");
                location.reload();
            }
            return true;
        }).catch(error => console.error('Error:', error));
    }

    function clearEmplList() {
        listContentEl.innerHTML = '';
    }

    function verifyAuthName(option, optionValue) {
        const validAuthNames = ['슈퍼관리자', '사원관리자', '공간예약관리자', '관리자', '사원'];
        return validAuthNames.includes(optionValue);
    }
</script>
</body>
</html>
