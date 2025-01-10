<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-04-05
  Time: 오후 5:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <title></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/deptStyle.css">
</head>
<script>
    const msg = '${msg}';
    if(msg==='SAME_NM'){
        alert('중복된 부서이름이 존재합니다. 이름을 변경해주세요');
    } else if(msg ==='NO_DEPT'){
        alert('저장할 부서의 정보가 없습니다. 다시 시도해주세요.');
    } else if(msg ==='NO_INPUT'){
        alert('부서 정보를 모두 입력해주세요');
    } else if(msg ==='WRONG_INPUT'){
        alert('잘못된 입력입니다. 다시 입력해주세요.');
    }
</script>
<body>
    <div class="horizontalCenter800 deptInsertRoot">
        <div class="headerWrapper">
            <h1>새 부서 추가</h1>
        </div>
        <div class="infoSection">
            <form method="post" action="<c:url value='/dept/save'/>" accept-charset="UTF-8" name="deptInfo" id="deptForm">
                <p>* 필수정보를 모두 입력해주세요</p>
                <div class="infoRow">
                    <label for="deptNm" class="infoName">부서명<span>*</span></label>
                    <input type="text" name="deptNm" id="deptNm" class="roundInputWidth200" value="${deptNm}" ${param != null ? 'autofocus': null}>
                </div>
                <div class="infoRow">
                    <label class="infoName">영문 부서명</label>
                    <input type="text" name="engDeptNm" id="engDeptNm" class="roundInputWidth200" value="${engDeptNm}">
                </div>
                <div class="infoRow">
                    <label class="parent infoName">상위 부서<span>*</span></label>
                    <select name="parent" id="parent" class="roundInputWidth200">
                        <option value="#">최상위 부서</option>
                        <c:forEach var="dept" items="${CdAndNm}">
                            <option value="${dept.deptCd}" ${dept.deptNm==param.parent ? 'selected' : null}>${dept.deptNm}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="infoRow">
                    <label for="searchInput" class="infoName">관리자 직원</label>
                </div>
                <div class="infoRow">
                    <input type="hidden" name="mngrId" id="mngrId" value="${mngr.emplId}">
                    <div id="searchMngr" class="hidden searchInputWrapper">
                        <input id="searchInput" class="searchInput" type="text" placeholder="이름 또는 이메일을 입력하세요"/>
                        <div id="searchResult" class="searchResult searchList hidden"></div>
                    </div>
                    <div id="mngrProfile" class="mngrProfile selectProfile hidden">
                        <div class="selectProfileImgWrapper">
                            <img src="${mngr.prfPhotoPath?profileImgPath+'/'+mngr.prfPhotoPath:noImgPath}" class="profilePhoto" alt="프로필 사진"/>
                        </div>
                        <div class="selectProfileInfoWrapper">
                            <p class='profileNm selectedProfileName'><span class='nm'>${mngr.rnm}</span><span class='engNm'>${mngr.engNm?mngr.engNm:''}</span></p>
                            <p class='profileEmail selectedProfileEmail'>${mngr.email}</p>
                        </div>
                        <div>
                            <span id="closeBtn" class="closeBtn">&times;</span>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="btnWrapper">
            <button type="submit" form="deptForm" class="btnM2">다음으로</button>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/resources/js/searchScript.js"></script>
    <script>
    window.onbeforeunload = function(e) {
        console.log(e);
        e.preventDefault();
        return false;
    };
    const submitForm = document.getElementById('deptForm');

    submitForm.addEventListener('submit', function(e) {
        e.preventDefault();
        window.onbeforeunload = null;
        let deptNm = document.getElementById('deptNm').value;
        let parent = document.getElementById('parent').value;
        let engDeptNm = document.getElementById('engDeptNm').value;

        if(deptNm===''||parent===''||engDeptNm===''){
            alert('필수 정보를 모두 입력해주세요');
            return false;
        }
        window.onbeforeunload = null;
        submitForm.submit();
    });
</script>
</body>
</html>