<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-06-04
  Time: 오후 11:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <title>roombook | 공간 상세</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/spaceDetail.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/customTag.css"/>
</head>
<body>
<h1>공간 상세 정보</h1>
<div>
    <div class="product-imgs">
        <div class = "img-display">
            <div id="imgShowcase" class = "img-showcase">
            </div>
        </div>
        <div id="imgSelect" class="img-select">
        </div>
    </div>
    <div>
        <div>
            <span>공간명</span>
            <span>${space.SPACE_NM}</span>
        </div>
        <div>
            <span>위치(20자 이내)</span>
            <span>${space.SPACE_LOC_DESC}</span>
        </div>
        <div>
            <span>공간 설명(100자)</span>
            <span>${space.SPACE_ADTN_DESC}</span>
        </div>
        <div>
            <span>최대 연속 예약 가능 시간(시간 단위)</span>
            <span>${space.SPACE_MAX_RSVD_TMS}</span>
        </div>
        <div>
            <span>공간 주말 이용 가능 여부</span>
            <span>${space.SPACE_WKEND_USG_POSBL_YN.toString()}</span>
        </div>
        <div>
            <span>이용시간</span>
            <span>${space.SPACE_USG_POSBL_BGN_TM}</span>
            <span>${space.SPACE_USG_POSBL_END_TM}</span>
        </div>
        <div>
            <span>최대 수용인원</span>
            <span>${space.SPACE_MAX_PSON_CNT}</span>
        </div>
        <div>
            <span>옵션(facility)</span>
            <div id="facilities"></div>
        </div>
        <div>
            <span>목록 숨김 여부</span>
            <span>${space.SPACE_HIDE_YN.toString()}</span>
        </div>
        <div>
            <button type="button" id="editBtn">수정</button>
            <button type="button" id="listBtn">목록</button>
        </div>
    </div>
</div>
<script>
    const jsonFiles = '${files}';
    const imgPath = '${imgPath}';

    const editBtn = document.getElementById('editBtn');
    const paths = window.location.pathname.split('/');

    editBtn.addEventListener('click', () => {
        location.href = '<c:url value="/admin-spaces"/>/'+paths[paths.length-1]+'/edit';
    })

    const listBtn = document.getElementById('listBtn');
    listBtn.addEventListener('click', () =>{
       location.href = '<c:url value="/admin-spaces"/>';
    });

    const jsonRescs = '${resources}';
    const rescsObject = JSON.parse(jsonRescs);
    const resources = document.getElementById('facilities');

    resources.innerHTML = rescsObject.map((resc, index) => {
        return `<span class='rescTag'>${'${resc.value}'}</span>`
    }).join('');
</script>
<script src="${pageContext.request.contextPath}/js/imgSlider.js"></script>
</body>
</html>