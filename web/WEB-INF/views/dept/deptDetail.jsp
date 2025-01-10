<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-05-01
  Time: 오후 9:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <title></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/deptStyle.css"/>
</head>
<body>
    <div class="horizontalCenter800 deptDetailRoot paddingTop40">
        <div class="breadScrumbContainer">
            <a href="${pageContext.request.contextPath}/dept/list"  class="breadScrumb">
                <span>부서 목록</span>
            </a>
            <span class="breadScrumbCurrentPage">부서 상세 정보</span>
        </div>
        <div class="headerWrapper">
            <h1>부서 상세페이지</h1>
        </div>
        <div>
            <div class="flexRightAlignment flexRow flexJustifyContentEnd">
                <a class="btnM marginRight10 bg_gray" href="<c:url value='/dept/mod?deptCd=${deptAndMngrData.deptCd}'/>">부서 수정</a>
                <button id="deptDelBtn" class="btnM bg_gray">부서 삭제</button>
            </div>
            <div class="infoSection">
                <div class="subHeaderWrapper">
                    <h2>부서 정보</h2>
                </div>
                <div class="infoRow">
                    <span class="infoName">부서명</span>
                    <span class="infoContent">${deptAndMngrData.deptNm}</span>
                </div>
                <div class="infoRow">
                    <span class="infoName">부서 영문명</span>
                    <span class="infoContent">${deptAndMngrData.engDeptNm}</span>
                </div>
                <div class="infoRow">
                    <p class="infoName">관리자 직원</p>
                    <div class="profileWrapper">
                    <c:choose>
                    <c:when test="${deptAndMngrData.emplId ne null}">
                    <div class="profileContent">
                        <div class="profileImageWrapper">
                            <img alt="사원 프로필 사진" src="${deptAndMngrData.prfPhotoPath?profileImgPath+'/'+deptAndMngrData.prfPhotoPath:noImgPath}"/>
                        </div>
                        <div class="profileInfo">
                            <div class="profileInfoRow profileName">
                                <span>${deptAndMngrData.rnm}</span>${deptAndMngrData.engNm?'<span>('+deptAndMngrData.engNm+')</span>':''}
                            </div>
                            <div class="profileInfoRow profileEmail">
                                <span>${deptAndMngrData.email}</span>
                            </div>
                        </div>
                    </div>
                    </c:when>
                    <c:otherwise>
                    <div class="noProfile">없음</div>
                    </c:otherwise>
                    </c:choose>
                    </div>
                </div>
            </div>
            <div class="infoSection">
                <div class="flexRow alignItemCenter flexSpaceBetween">
                    <div class="subHeaderWrapper">
                        <h2>구성원</h2>
                    </div>
                    <div>
                        <a class="btnM bg_gray" href="<c:url value='/dept/mem?deptCd=${deptAndMngrData.deptCd}'/>">추가 및 수정</a>
                    </div>
                </div>
                <div class="profileListScroll">
                    <div class="profileListScrollContent">
                        <c:choose>
                        <c:when test="${!empty memberInfo}">
                        <c:forEach var="mem" items="${memberInfo}">
                        <div class="profileWrapper">
                            <div class="profileContent">
                                <div class="profileImageWrapper">
                                    <img alt="사원 프로필 사진" src="${mem.prfPhotoPath?profileImgPath+'/'+mem.prfPhotoPath:noImgPath}"/>
                                </div>
                                <div class="profileInfo">
                                    <div class="profileInfoRow profileName">
                                        <span>${mem.rnm}</span>${mem.engNm?'<span>('+mem.engNm+')</span>':''}
                                    </div>
                                    <div class="profileInfoRow profileEmail">
                                        <span>${mem.email}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        </c:forEach>
                        </c:when>
                        <c:otherwise>
                        <div>없음</div>
                        </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
        <div class="btnWrapper">
            <button type="button" id="listBtn" class="btnM2">부서 목록</button>
        </div>
    </div>
<script>
    const listBtnEl = document.getElementById('listBtn');

    let msg = '${msg}';

    switch (msg){
        case "MOD_SUCCESS": alert("수정이 완료되었습니다."); break;
    }

    window.onpageshow = (e) => {
        if(e.persisted){
            const delForm = document.getElementById('delForm');
            if(delForm){
                document.body.removeChild(delForm);
            }
        }
    }

    document.getElementById("deptDelBtn").onclick = function(){
        const hasChildren = ${deptAndMngrData.cdrDeptCnt eq 0 || empty deptAndMngrData.cdrDeptCnt ? false : true};

        if(hasChildren){
            alert("하위 부서가 있는 부서는 삭제할 수 없습니다\n하위 부서를 이동한 후 다시 시도하세요.");
            return false;
        }

        fetch('/dept/del', {
            method : 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(${deptAndMngrData.deptCd})
        }).then(response => {
            return response.text();
        }).then(msg => {
            switch (msg){
                case 'DEL_OK':
                    alert('부서 삭제가 완료되었습니다.');
                    window.location.replace('<c:url value="${deptListUri}"/>'); break;
                case 'MEM':
                    alert('구성원이 있는 부서는 삭제할 수 없습니다.\n구성원을 이동하거나 삭제 후 다시 시도하세요.'); break;
                case 'SUB_DEPT':
                    alert('하위 부서가 있는 부서는 삭제할 수 없습니다.\n부서를 이동 또는 삭제 후 다시 시도하세요.'); break;
                case 'DEL_FAIL':
                    alert('부서 삭제가 정상적으로 처리되지 않았습니다.\n새로고침 후 다시 시도하세요.'); break;
                default:
            }
        }).catch(error => {
            console.error('Error sending data:', error);
        });
    }

    listBtnEl.addEventListener('click', () =>{
        location.href = '<c:url value="${deptListUri}"/>';
    });
</script>
</body>
</html>
