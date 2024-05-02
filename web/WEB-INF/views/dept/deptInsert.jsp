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
    <div> <!-- TODO 필수 값 입력 체크 -->
        <form method="post" action="<c:url value='/dept/save'/>" accept-charset="UTF-8" name="deptInfo" id="deptForm">
            <div>
                <label>부서명
                <input type="text" name="deptNm" value="${deptNm}" ${param != null ? 'autofocus': null}>
                </label>
            </div>
            <div>
                <label>영문 부서명
                <input type="text" name="engDeptNm" value="${engDeptNm}">
                </label>
            </div>
            <div>
                <label>상위 부서
                <select name="parent">
                    <option value="#">최상위 부서</option>
                    <c:forEach var="dept" items="${CdAndNm}">
                        <option value="${dept.DEPT_CD}" ${dept.DEPT_NM==param.parent ? 'selected' : null}>${dept.DEPT_NM}</option>
                    </c:forEach>
                </select>
                </label>
            </div>
            <div>
                <label>부서장
                <input type="text" name="mngr" value="${mngr}">
                </label>
            </div>
        </form>

        <button type="submit" form="deptForm">제출하기</button>
    </div>
<script>
    window.onbeforeunload = function(e) {
        console.log(e);
        e.preventDefault();
        return false;
    };

    document.getElementById("deptForm").onsubmit = function() {
        window.onbeforeunload = null;
        return true;
    };
</script>
</body>
</html>