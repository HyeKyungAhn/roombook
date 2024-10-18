<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-05-02
  Time: 오후 9:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <title>roombook | 부서 수정</title>
    <link rel='stylesheet' href='https://unpkg.com/ionicons@4.5.10-0/dist/css/ionicons.min.css'>
    <link rel='stylesheet' href='https://fonts.googleapis.com/css?family=Montserrat:500,700&amp;display=swap'>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/deptStyle.css">
</head>
<body>
    <div class="horizontalCenter800">
        <div class="headerWrapper">
            <h1>부서 정보 수정</h1>
        </div>
        <div  class="infoSection">
            <form action="<c:url value="/dept/mod"/>" method="POST" id="deptModForm">
                <p>* 필수정보를 모두 입력해주세요</p>
                <input type="hidden" name="deptCd" class="deptCdInput" value="${deptInfo.deptCd}">
                <div class="infoRow">
                    <label for="deptNm" class="infoName">부서명<span>*</span></label>
                        <input type="text" name="deptNm" id="deptNm" class="deptNmInput roundInputWidth200" value="${deptInfo.deptNm}">
                </div>
                <div class="infoRow">
                    <label class="infoName">영문 부서명</label>
                        <input type="text" name="engDeptNm" id="engDeptNm" class="engDeptNmInput roundInputWidth200" value="${deptInfo.engDeptNm}">
                </div>
                <div class="infoRow">
                    <label for="searchInput" class="infoName">관리자 직원</label>
                </div>
                <div class="infoRow">
                    <input id="mngrId" type="hidden" name="mngrId" value="${mngr.emplId}">
                    <div id="searchMngr" class="hidden">
                        <input id="searchInput" class="searchInput" type="text" placeholder="이름 또는 이메일을 입력하세요"/>
                        <div id="searchResult" class="searchResult searchList hidden"></div>
                    </div>
                    <div id="mngrProfile" class="mngrProfile selectProfile hidden">
                        <div class="selectProfileImgWrapper">
                            <img src="${mngr.prfPhotoPath?profileImgPath+'/'+mngr.prfPhotoPath:noImgPath}" class="profilePhoto" alt="프로필 사진"/>
                        </div>
                        <div class="selectProfileInfoWrapper">
                            <p class='profileNm selectedProfileName'><span class='nm'>${mngr.rnm}</span><span class='engNm'>${mngr.engNm?mngr.engNm:''}</span></p>
                            <p class='profileEmail selectedProfileName'>${mngr.email}</p>
                        </div>
                        <div>
                            <span id="closeBtn" class="closeBtn">&times;</span>
                        </div>
                    </div>
                </div>
                <div class="btnWrapper">
                    <button type="button" id="cancelBtn" class="btnM2">취소</button>
                    <button type="submit" id="submitBtn" class="btnM2">저장</button>
                </div>
            </form>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/searchScript.js"></script>
    <script>
        const msg = '${msg}';
        switch(msg) {
            case "MOD_FAIL" : alert("수정에 실패했습니다. \n새로고침 후 다시 시도해주세요."); break;
            case "DEPTNAME_REQUIRED": alert("부서명을 입력해주세요."); break;
        }

        const searchForm = {
            deptModeFormEl: document.getElementById('deptModForm'),
            cancelBtnEl: document.getElementById('cancelBtn'),
            initialize() {
                searchForm.addEvents();
            },
            addEvents(){
                searchForm.removeEvents();
                searchForm.deptModeFormEl.addEventListener('submit', searchForm.eventHandlers.onSubmit);
                searchForm.cancelBtnEl.addEventListener('click', searchForm.eventHandlers.onCancelBtnClicked);
            },
            removeEvents(){
                searchForm.deptModeFormEl.removeEventListener('submit', searchForm.eventHandlers.onSubmit);
                searchForm.cancelBtnEl.removeEventListener('click', searchForm.eventHandlers.onCancelBtnClicked);
            },
            eventHandlers: {
                onSubmit(event){
                    event.preventDefault();

                    let hasEmptyInput = searchForm.checkEmptyInput();
                    if(hasEmptyInput){
                        alert('부서 필수 정보을 입력하세요.');
                        return false;
                    }
                    if(searchForm.checkNotingChanged()){
                        alert('변경 사항이 없습니다.');
                    } else {
                        searchForm.deptModeFormEl.submit();
                    }
                },
                onCancelBtnClicked(){
                    if(searchForm.checkNotingChanged()){
                        window.history.back();
                    } else {
                        if(confirm('수정 중인 정보는 저장되지 않습니다.\n취소하시겠습니까?')){
                            window.history.back();
                        }
                    }
                }
            },
            checkNotingChanged(){
                const oldDeptCd = '${deptInfo.deptCd}';
                const oldDeptNm = '${deptInfo.deptNm}';
                const oldEngDeptNm = '${deptInfo.engDeptNm}';
                const oldMngrId = '${mngr.emplId}';

                const newDeptCd = document.getElementsByClassName('deptCdInput')[0].value;
                const newDeptNm = document.getElementsByClassName('deptNmInput')[0].value;
                const newEngDeptNm = document.getElementsByClassName('engDeptNmInput')[0].value;
                const newMngrId = document.getElementById('mngrId').value;

                return oldDeptCd === newDeptCd
                    && oldDeptNm === newDeptNm
                    && oldEngDeptNm === newEngDeptNm
                    && oldMngrId === newMngrId;
            },
            checkEmptyInput(){
                const deptNm = document.getElementById('deptNm').value;
                const engDeptNm = document.getElementById('engDeptNm').value;

                return deptNm===''||engDeptNm==='';
            }
        }

        searchForm.initialize();
    </script>
</body>
</html>
