<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-05-07
  Time: 오후 5:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <title>roombook | 구성원 수정</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/deptStyle.css">
    <link rel="stylesheet" href="https://unpkg.com/ionicons@4.5.10-0/dist/css/ionicons.min.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Montserrat:500,700&amp;display=swap">
</head>
<body>
    <div class="horizontalCenter800">
        <div class="headerWrapper">
            <h1>부서 구성원 수정</h1>
        </div>
        <div class="infoSection">
            <div class="infoRow">
                <span class="infoName">부서명</span>
                <span class="infoContent">${deptNm}(${engDeptNm})</span>
            </div>
            <div class="infoRow">
                <span class="infoName">멤버 검색</span>
            </div>
            <div class="infoRow">
                <div id="searchContainer" class="searchInputWrapper">
                    <input type="text" id="searchInput" class="searchInput" placeholder="이름 또는 이메일을 입력하세요">
                    <div id="searchResult" class="searchResult searchList hidden"></div>
                </div>
                <div class="profileListScroll">
                    <div id="memProfileContainer" class="memProfileContainer">
                        <c:if test="${deptMemAndDeptNm ne null}">
                        <c:forEach var="mem" items="${deptMemAndDeptNm}" varStatus="status">
                            <div class="memProfile selectProfile marginBottom10" data-id="${mem.emplId}">
                                <div class="selectProfileImgWrapper">
                                    <img src="${mngr.prfPhotoPath?profileImgPath+'/'+mngr.prfPhotoPath:noImgPath}" class="profilePhoto" alt="프로필 사진"/>
                                </div>
                                <div class="selectProfileInfoWrapper">
                                    <p class='profileNm selectedProfileName'><span class='nm'>${mem.rnm}</span><span class='engNm'>${mem.engNm?mngr.engNm:''}</span></p>
                                    <p class='profileEmail selectedProfileEmail'>${mem.email}</p>
                                </div>
                                <div>
                                    <span id="closeBtn" class="closeBtn">&times;</span>
                                </div>
                            </div>
                        </c:forEach>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
        <div id="btnContainer" class="btnWrapper">
            <button type="button" id="cancelBtn" class="btnM2">취소</button>
            <button type="button" id="submitBtn" class="btnM2">저장</button>
        </div>
    </div>
    <script>
        const resultControl = {
            cancelBtn: document.getElementById("cancelBtn"),
            submitBtn: document.getElementById("submitBtn"),
            profileContainer: document.getElementById("memProfileContainer"),
            initialize(){
                this.addEvents()
            },
            addEvents(){
                this.removeEvents();
                this.cancelBtn.addEventListener('click', resultControl.eventHandlers.onCancel);
                resultControl.submitBtn.addEventListener('click', resultControl.eventHandlers.onSubmit);
            },
            removeEvents(){
                resultControl.cancelBtn.removeEventListener('click', resultControl.eventHandlers.onCancel);
                resultControl.submitBtn.removeEventListener('click', resultControl.eventHandlers.onSubmit);
            },
            eventHandlers: {
                onCancel(){
                    if(confirm('수정 중인 정보는 저장되지 않습니다.\n취소하시겠습니까?')){
                        window.history.back();
                    }
                },
                onSubmit(){
                    const deptMemData = {
                        memIds: [],
                        deptCd: '${deptCd}'
                    }

                    for(let child of resultControl.profileContainer.children){
                        deptMemData.memIds.push(child.getAttribute("data-id"));
                    }


                    fetch('<c:url value="/dept/mem"/>', {
                        method : 'POST',
                        headers: {'Content-Type': 'application/json;charset=utf-8'},
                        body: JSON.stringify(deptMemData)
                    }).then(response => {
                        return response.text();
                    }).then(result => {
                        if(result==='SUCCESS'){
                            alert('정상적으로 수정되었습니다.');
                            window.location.replace('<c:url value="/dept/dept?deptCd=${param.deptCd}"/>')
                        } else if(result ==='INVALID_INPUT'){
                            alert('유효하지 않은 입력입니다.');
                        } else {
                            alert('부서 정보가 올바르지 않습니다.\n새로고침 후 다시 시도해주세요.')
                        }
                    }).catch(error => {
                        console.error('Error sending data:', error);
                    });
                }

            }
        }
        const search = {
            CLASSES: {
                HIDE: 'hidden'
            },
            isModalVisible: false,
            modalEl: document.getElementById("searchResult"),
            inputEl: document.getElementById("searchInput"),
            timeout: null,
            initialize(){
                search.addEvents();
            },
            showModal(){
                search.modalEl.classList.remove(search.CLASSES.HIDE);
                search.isModalVisible = true;
            },
            hideModal(){
                search.modalEl.classList.add(search.CLASSES.HIDE);
                search.isModalVisible = false;
            },
            addEvents(){
                search.removeEvents();
                search.inputEl.addEventListener('keydown', search.eventHandlers.onKeyDown);
                search.inputEl.addEventListener('click', search.eventHandlers.onInputClick);
                search.modalEl.addEventListener('click', search.eventHandlers.onModalClick);
                window.addEventListener('click', search.eventHandlers.onWindowClick);
            },
            removeEvents(){
                search.inputEl.removeEventListener('keydown', search.eventHandlers.onKeyDown);
                search.inputEl.addEventListener('click', search.eventHandlers.onInputClick);
                search.modalEl.removeEventListener('click', search.eventHandlers.onModalClick);
                window.removeEventListener('click', search.eventHandlers.onWindowClick);
            },
            eventHandlers: {
                onKeyDown() {
                    clearTimeout(search.timeout);

                    search.timeout = setTimeout(function() {
                        console.log(search.inputEl.value);
                        search.searchRealTime(search.inputEl.value);
                    }, 100); // 100ms 동안 대기
                },
                onModalClick(event) {
                    event.stopImmediatePropagation();
                    if (event.target.id === 'searchResult') return; //empl 정보 사이 간격 클릭 시

                    const clickedEmpl = event.target.closest('.searchedEmpl');
                    const emplId = clickedEmpl.getAttribute('data-id');

                    if(memProfile.checkDuplication(emplId)) return;

                    const emplImg = clickedEmpl.querySelector('img').getAttribute('src');
                    const emplNm = clickedEmpl.querySelector('.emplNm').innerText;
                    const emplEngNm = clickedEmpl.querySelector('.emplEngNm').innerText;
                    const emplEmail = clickedEmpl.querySelector('.emplEmail').innerText;

                    memProfile.insertProfile(emplId, emplImg, emplNm, emplEngNm, emplEmail);
                    search.clearModal();
                    search.hideModal();
                },
                onWindowClick(event) {
                    if(event.target !== search.inputEl){
                        search.hideModal();
                    }
                },
                onInputClick() {
                    if(search.inputEl.value.trim()===''){
                        search.modalEl.innerHTML = '';
                        search.hideModal();
                    } else if(!search.isModalVisible){
                        search.searchRealTime(search.inputEl.value);
                    }
                }
            },
            searchRealTime(keyword) {
                if(keyword.trim()===''){
                    search.clearModal();
                    search.hideModal();
                    return;
                }

                let url = '<c:url value="/dept/memSrch"/>?keyword='+keyword;

                fetch(url, {
                    method : 'GET',
                    headers: {'Content-Type': 'application/json;charset=utf-8'},
                }).then(response => {
                    return response.json();
                }).then(objs => {
                    search.addSearchResults(objs);
                }).catch(error => {
                    console.error('Error sending data:', error);
                });
            },
            addSearchResults(objs) {
                if(objs.length===0){
                    search.clearModal();
                    return;
                }
                search.modalEl.innerHTML = objs.map((r, index) => {
                    const isDuplicated = memProfile.checkDuplication(r.emplId);

                    return `<div id='searchedEmpl\${index}' class='searchedEmpl emplProfile searchedProfile \${isDuplicated?'unclickable':""}' data-id='\${r.emplId}'>
                                <div class="searchedProfileImageContent">
                                    <div class='imgContainer searchedProfileImageWrapper'>
                                        <img src='\${r.prfPhotoPath?profileImgPath+'/'+r.prfPhotoPath:"/img/noImg.png"}' class='emplImg profileImg searchedProfileImage' alt="프로필 사진"/>
                                    </div>
                                </div>
                                <div class='searchedProfileInfoContent'>
                                    <p class='profileNm searchedProfileName'><span class='emplNm'>\${r.rnm}</span><span class='emplEngNm'>\${r.engNm?'('+r.engNm+')':''}</span>
                                        \${isDuplicated?'<span class="duplicationInfo">(이미 선택된 구성원입니다.)</span>':''}
                                    </p>
                                    <p class='emplEmail profileEmail searchedProfileEmail'>\${r.email}</p>
                                </div>
                            </div>`
                }).join('');

                search.showModal();
            },
            clearModal(){
                search.modalEl.innerHTML = '';
            }
        }

        const memProfile = {
            CLASSES: {
              memProfiles: "memProfile",
            },
            memContainerEl: document.getElementById("memProfileContainer"),
            initialize() {
                memProfile.addEvents();
            },
            addEvents() {
                memProfile.removeEvents();
                memProfile.memContainerEl.addEventListener('click', memProfile.eventHandlers.onProfileClick);
            },
            removeEvents() {
                memProfile.memContainerEl.removeEventListener('click', memProfile.eventHandlers.onProfileClick);

            },
            eventHandlers: {
                onProfileClick(event){
                    const closestBtn = event.target.closest('.closeBtn');
                    if(event.target===closestBtn){
                        event.target.closest('.memProfile').remove();
                    }
                }
            },
            insertProfile(id, img, nm, engNm, email){
                if(memProfile.checkDuplication(id)){
                    return false;
                }
                memProfile.memContainerEl.insertAdjacentHTML("afterbegin",
                    `<div class="memProfile selectProfile marginBottom10" data-id="\${id}">
                        <div class="selectProfileImgWrapper">
                            <img src="\${img}" class="profilePhoto" alt="프로필 사진"/>
                        </div>
                        <div class="selectProfileInfoWrapper">
                            <p class="profileNm selectedProfileName"><span class="nm">\${nm}</span><span class="engNm">\${engNm?'('+engNm+')':''}</span></p>
                            <p class="profileEmail selectedProfileEmail">\${email}</p>
                        </div>
                        <div>
                            <span class="closeBtn">&times;</span>
                        </div>
                    </div>`);
                return true;
            },
            deleteProfile(emplId){

            },
            checkDuplication(id){
                const profiles = document.getElementsByClassName(memProfile.CLASSES.memProfiles);
                for(let profile of profiles){
                    let emplId = profile.getAttribute('data-id');
                    if(emplId===id){
                        return true;
                    }
                }
                return false;
            }
        }

        search.initialize();
        memProfile.initialize();
        resultControl.initialize();
    </script>
</body>
</html>
