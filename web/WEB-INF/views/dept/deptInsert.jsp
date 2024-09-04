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
    <title>roombook | 부서 추가</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <link rel='stylesheet' href='https://unpkg.com/ionicons@4.5.10-0/dist/css/ionicons.min.css'>
    <link rel='stylesheet' href='https://fonts.googleapis.com/css?family=Montserrat:500,700&amp;display=swap'>
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
    <div>
        <form method="post" action="<c:url value='/dept/save'/>" accept-charset="UTF-8" name="deptInfo" id="deptForm">
            <p>* 필수정보를 모두 입력해주세요</p>
            <div>
                <label>부서명*
                <input type="text" name="deptNm" id="deptNm" value="${deptNm}" ${param != null ? 'autofocus': null}>
                </label>
            </div>
            <div>
                <label>영문 부서명
                <input type="text" name="engDeptNm" id="engDeptNm" value="${engDeptNm}">
                </label>
            </div>
            <div>
                <label>상위 부서*
                <select name="parent" id="parent">
                    <option value="#">최상위 부서</option>
                    <c:forEach var="dept" items="${CdAndNm}">
                        <option value="${dept.deptCd}" ${dept.deptNm==param.parent ? 'selected' : null}>${dept.deptNm}</option>
                    </c:forEach>
                </select>
                </label>
            </div>
            <div>
                <div>관리자 직원</div>
                <input type="hidden" name="mngrId" id="mngrId" value="${mngr.emplId}">
                <div id="searchMngr" class="hide">
                    <label>
                        <input id="searchInput" type="text" placeholder="이름 또는 이메일을 입력하세요"/>
                    </label>
                    <div id="searchResult" class="searchResult hide"></div>
                </div>
                <div id="mngrProfile" class="mngrProfile hide">
                    <span id="closeBtn" class="closeBtn">&times;</span>
                    <div>
                        <img src="${mngr.prfPhotoPath}" class="profilePhoto" alt="프로필 사진"/>
                    </div>
                    <div>
                        <p class='profileNm'><span class='nm'>${mngr.rnm}</span><span class='engNm'>${mngr.engNm?mngr.engNm:''}</span></p>
                        <p class='profileEmail'>${mngr.email}</p>
                    </div>
                </div>
            </div>
        </form>

        <button type="submit" form="deptForm">제출하기</button>
    </div>
    <script src="${pageContext.request.contextPath}/js/searchScript.js"></script>
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