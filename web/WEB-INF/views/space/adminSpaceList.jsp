<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-05-31
  Time: 오전 2:19
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
    <link rel="prev" href="<c:url value="/admin-spaces?page=${ph.currentPage-1}"/>"/>
    </c:if>
    <c:if test="${ph.showNext}">
    <link rel="next" href="<c:url value="/admin-spaces?page=${ph.currentPage+1}"/>"/>
    </c:if>

</head>
<body>
    <ul>
        <c:set var="placeNo" value=""/>
        <c:set var="lastFile" value=""/>
        <c:forEach var="place" items="${list}" varStatus="status">
        <c:choose>
        <c:when test="${placeNo ne place.spaceNo && status.first}"><!--새로시작&&처음-->
        <c:set var="placeNo" value="${place.spaceNo}"/>
        <c:set var="lastFile" value="${place.fileNm}"/>
        <il>
            <div>
                <div>
                    <a href="<c:url value="/admin-spaces/${placeNo}"/>"><span>${place.spaceNm}</span></a><br/>
                    <span>${place.spaceMaxPsonCnt}명</span><br/>
                    <span>${place.spaceLocDesc}</span><br/>
                    <span>${place.spaceAdtnDesc}</span><br/>
                    <span>${place.spaceMaxRsvdTms}</span><br/>
                    <span>${place.spaceUsgPosblBgnTm}</span><br/>
                    <span>${place.spaceUsgPosblEndTm}</span><br/>
                    <span>${place.spaceWkendUsgPosblYn}</span><br/>
                    <span>${place.spaceHideYn}</span><br/>
                </div>
                <div>
                    <span>${place.rescNm}</span>
        </c:when>
        <c:when test="${placeNo ne place.spaceNo}"> <!--새로 시작 & 처음이 아님-->
        <c:set var="placeNo" value="${place.spaceNo}"/>
                </div>
            </div>
            <div>
                <img class="thumbnailImg" src="${empty lastFile ? '/img/noImg.png' : thumbnailPath.concat('/').concat(lastFile)}"  alt="공간 대표사진">
            </div>
        </il>
        <il>
        <c:set var="lastFile" value="${place.fileNm}"/>
            <div>
                <div>
                    <a href="<c:url value="/admin-spaces/${placeNo}"/>"><span>${place.spaceNm}</span></a><br/>
                    <span>${place.spaceMaxPsonCnt}명</span><br/>
                    <span>${place.spaceLocDesc}</span><br/>
                    <span>${place.spaceAdtnDesc}</span><br/>
                    <span>${place.spaceMaxRsvdTms}</span><br/>
                    <span>${place.spaceUsgPosblBgnTm}</span><br/>
                    <span>${place.spaceUsgPosblEndTm}</span><br/>
                    <span>${place.spaceWkendUsgPosblYn}</span><br/>
                    <span>${place.spaceHideYn}</span><br/>
                </div>
                <div>
                    <span>${place.rescNm}</span>
        </c:when>
        <c:when test="${placeNo ne place.spaceNo && status.last}"> <!-- 새로 시작 $$ 마지막 하나 -->
        <il>
            <div>
                <div>
                    <a href="<c:url value="/admin-spaces/${placeNo}"/>"><span>${place.spaceNm}</span></a><br/>
                    <span>${place.spaceMaxPsonCnt}명</span><br/>
                    <span>${place.spaceLocDesc}</span><br/>
                    <span>${place.spaceAdtnDesc}</span><br/>
                    <span>${place.spaceMaxRsvdTms}</span><br/>
                    <span>${place.spaceUsgPosblBgnTm}</span><br/>
                    <span>${place.spaceUsgPosblEndTm}</span><br/>
                    <span>${place.spaceWkendUsgPosblYn}</span><br/>
                    <span>${place.spaceHideYn}</span><br/>
                </div>
                <div>
                    <span>${place.rescNm}</span>
                </div>
            </div>
            <div>
                <img class="thumbnailImg" alt="공간 대표사진" src="${empty lastFile?'/img/noImg.png' : thumbnailPath.concat('/').concat(lastFile)}">
            </div>
        </il>
        </c:when>
        <c:when test="${placeNo eq place.spaceNo && status.last}"> <!-- 새로 시작 아님 & 마지막 하나 -->
                    <span>${place.rescNm}</span>
                </div>
            </div>
            <div>
                <img class="thumbnailImg" alt="공간 대표사진" src="${empty lastFile? '/img/noImg.png': thumbnailPath.concat('/').concat(lastFile)}">
            </div>
        </il>
        </c:when>
        <c:when test="${placeNo eq place.spaceNo}"> <!-- rescs -->
            <span>${place.rescNm}</span>
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
                location.href = "<c:url value="/admin-spaces?page="/>"+pnInput.value;
            }
        }
    })
</script>
</body>
</html>
