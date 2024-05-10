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
    <title>roombook | 부서</title>
</head>
<body>
<div>
    <h1>부서 상세페이지</h1>
    <a class="" href="<c:url value='/dept/mod?deptCd=${deptAndMngrData.DEPT_CD}'/>">
        <span>부서 수정</span>
    </a>
    <button id="deptDelBtn">
        <span>부서 삭제</span>
    </button>
    <div>
        <h2>부서 정보</h2>
        <div>
            <span>부서명</span>
            <span>${deptAndMngrData.DEPT_NM}</span>
        </div>

        <div>
            <span>부서 영문명</span>
            <span>${deptAndMngrData.ENG_DEPT_NM}</span>
        </div>

        <div>
            <p>관리자 직원</p>
            <c:choose>
            <c:when test="${deptAndMngrData.EMPL_ID ne null}">
            <div>
                <span>직원 아이디</span>
                <span>${deptAndMngrData.EMPL_ID}</span>
            </div>
            <div>
                <span>사진</span>
                <span>${deptAndMngrData.PRF_PHOTO_PATH}</span>
            </div>
            <div>
                <span>이름</span>
                <span>${deptAndMngrData.RNM}</span>
            </div>
            <div>
                <span>영문명</span>
                <span>${deptAndMngrData.ENG_NM}</span>
            </div>
            <div>
                <span>사원번호</span>
                <span>${deptAndMngrData.EMPNO}</span>
            </div>
            <div>
                <span>이메일</span>
                <span>${deptAndMngrData.EMAIL}</span>
            </div>
            </c:when>
            <c:otherwise>
            <div>없음</div>
            </c:otherwise>
            </c:choose>
        </div>

        <div>
            <h2>구성원</h2>
            <a href="<c:url value='/dept/mem?deptCd=${deptAndMngrData.DEPT_CD}'/>">
                <span>추가/수정</span>
            </a>
            <div>
                <c:forEach var="mem" items="${memberInfo}">
                <div>
                    <div>
                        <span>직원 아이디</span>
                        <span>${mem.EMPL_ID}</span>
                    </div>
                    <div>
                        <span>사진</span>
                        <span>${mem.PRF_PHOTO_PATH}</span>
                    </div>
                    <div>
                        <span>이름</span>
                        <span>${mem.RNM}</span>
                    </div>
                    <div>
                        <span>영문명</span>
                        <span>${mem.ENG_NM}</span>
                    </div>
                    <div>
                        <span>사원번호</span>
                        <span>${mem.EMPNO}</span>
                    </div>
                    <div>
                        <span>이메일</span>
                        <span>${mem.EMAIL}</span>
                    </div>
                </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>
<script>
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
        const hasChildren = ${deptAndMngrData.CDR_DEPT_CNT eq 0 ? false : true};
        if(hasChildren){
            alert("하위 부서가 있는 부서는 삭제할 수 없습니다\n하위 부서를 이동한 후 다시 시도하세요.");
            return false;
        }

        fetch('/dept/del', {
            method : 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(${deptAndMngrData.DEPT_CD})
        }).then(response => {
            return response.text();
        }).then(msg => {
            switch (msg){
                case 'DEL_OK':
                    alert('부서 삭제가 완료되었습니다.');
                    window.location.replace('<c:url value="/dept/list"/>'); break;
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
</script>
</body>
</html>
