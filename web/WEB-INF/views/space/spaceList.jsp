<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-06-05
  Time: 오후 9:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE>
<html lang="kr">
<head>
  <title>roombook | 공간 목록</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/space.css">
  <c:if test="${ph.showPrev}">
    <link rel="prev" href="<c:url value="/spaces?page=${ph.currentPage-1}"/>"/>
  </c:if>
  <c:if test="${ph.showNext}">
    <link rel="next" href="<c:url value="/spaces?page=${ph.currentPage+1}"/>"/>
  </c:if>

</head>
<body>
<ul>
  <c:set var="placeNo" value=""/>
  <c:set var="lastFile" value=""/>
  <c:forEach var="place" items="${list}" varStatus="status">
    <c:choose>
      <c:when test="${placeNo ne place.SPACE_NO && status.first}"><!--새로운 공간 && 맨 처음 공간-->
        <c:set var="placeNo" value="${place.SPACE_NO}"/>
        <c:set var="lastFile" value="${place.FILE_NM}"/>
        <il>
        <div>
        <div>
          <a href="<c:url value="/spaces/${placeNo}"/>"><span>${place.SPACE_NM}</span></a><br/>
          <span>${place.SPACE_MAX_PSON_CNT}명</span><br/>
          <span>${place.SPACE_LOC_DESC}</span><br/>
          <span>${place.SPACE_ADTN_DESC}</span><br/>
          <span>${place.SPACE_MAX_RSVD_TMS}</span><br/>
          <span>${place.SPACE_USG_POSBL_BGN_TM}</span><br/>
          <span>${place.SPACE_USG_POSBL_END_TM}</span><br/>
          <span>${place.SPACE_WKEND_USG_POSBL_YN}</span><br/>
        </div>
        <div>
        <span>${place.RESC_NM}</span>
      </c:when>
      <c:when test="${placeNo ne place.SPACE_NO}"> <!--새로운 공간 & 맨 처음 공간이 아님-->
        <c:set var="placeNo" value="${place.SPACE_NO}"/>
        </div>
        </div>
        <div>
          <img class="thumbnailImg" alt="공간 대표사진" src="${thumbnailPath}/${lastFile}">ㅎㅎ
        </div>
        </il>
        <il>
        <c:set var="lastFile" value="${place.FILE_NM}"/>
        <div>
        <div>
          <a href="<c:url value="/spaces/${placeNo}"/>"><span>${place.SPACE_NM}</span></a><br/>
          <span>${place.SPACE_MAX_PSON_CNT}명</span><br/>
          <span>${place.SPACE_LOC_DESC}</span><br/>
          <span>${place.SPACE_ADTN_DESC}</span><br/>
          <span>${place.SPACE_MAX_RSVD_TMS}</span><br/>
          <span>${place.SPACE_USG_POSBL_BGN_TM}</span><br/>
          <span>${place.SPACE_USG_POSBL_END_TM}</span><br/>
          <span>${place.SPACE_WKEND_USG_POSBL_YN}</span><br/>
        </div>
        <div>
        <span>${place.RESC_NM}</span>
      </c:when>
      <c:when test="${placeNo ne place.SPACE_NO && status.last}"> <!-- 새로운 공간 $$ 마지막 공간 -->
        <il>
          <div>
            <div>
              <a href="<c:url value="/spaces/${placeNo}"/>"><span>${place.SPACE_NM}</span></a><br/>
              <span>${place.SPACE_MAX_PSON_CNT}명</span><br/>
              <span>${place.SPACE_LOC_DESC}</span><br/>
              <span>${place.SPACE_ADTN_DESC}</span><br/>
              <span>${place.SPACE_MAX_RSVD_TMS}</span><br/>
              <span>${place.SPACE_USG_POSBL_BGN_TM}</span><br/>
              <span>${place.SPACE_USG_POSBL_END_TM}</span><br/>
              <span>${place.SPACE_WKEND_USG_POSBL_YN}</span><br/>
            </div>
            <div>
              <span>${place.RESC_NM}</span>
            </div>
          </div>
          <div>
            <img class="thumbnailImg" alt="공간 대표사진" src="${thumbnailPath}/${place.FILE_NM}">
          </div>
        </il>
      </c:when>
      <c:when test="${placeNo eq place.SPACE_NO && status.last}"> <!-- 새로운 공간 아님 & 마지막 하나 -->
            <span>${place.RESC_NM}</span>
          </div>
        </div>
        <div>
          <img class="thumbnailImg" alt="공간 대표사진" src="${thumbnailPath}/${place.FILE_NM}">
        </div>
        </il>
      </c:when>
      <c:when test="${placeNo eq place.SPACE_NO}"> <!-- rescs -->
        <span>${place.RESC_NM}</span>
      </c:when>
    </c:choose>
  </c:forEach>
</ul>

<nav id="spaceNav" aria-label="pagination">
  <div class="spacePaginationArrow">
    <button type="button" class="chevronBtn iconChevronStart" aria-label="previous" ${ph.showPrev?'':'disabled'} aria-disabled="${ph.showPrev}">
    </button>
  </div>
  <div>
    <input class="paginationInput" type="number" value="${ph.currentPage}"><span>/</span><div>${ph.totalPage}</div>
  </div>
  <div class="spacePaginationArrow">
    <button type="button" class="chevronBtn iconChevronEnd" aria-label="next" ${ph.showNext?'':'disabled'} aria-disabled="${ph.showNext}">
    </button>
  </div>
</nav>
<script>
  const pnInput = document.querySelector('.paginationInput');

  const chevronBtns = document.getElementsByClassName('chevronBtn');
  Array.from(chevronBtns).forEach(btn => btn.addEventListener('click', (e)=>{ pagination.doRedirect(e); }));

  const pagination ={
    doRedirect : e => {
      if(e.target.classList.contains("iconChevronEnd")){
        location.href = document.querySelector('link[rel="next"]').href;
      }
      if (e.target.classList.contains("iconChevronStart")) {
        location.href = document.querySelector('link[rel="prev"]').href;
      }
    }
  }

  pnInput.addEventListener("keydown", (e) => {
    if (e.key === 'Enter') {
      if(pnInput.value===''||pnInput.value==='${ph.currentPage}'){
        return;
      }
      if(0<=pnInput.value&& pnInput.value<=${ph.totalPage}){
        location.href = "<c:url value="/spaces?page="/>"+pnInput.value;
      }
    }
  });
</script>
</body>
</html>
