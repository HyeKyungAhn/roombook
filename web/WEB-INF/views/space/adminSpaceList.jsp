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
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jsCalendar/jsCalendar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jsCalendar/jsCalendar.micro.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jsCalendar/jsCalendar.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jsCalendar/jsCalendar.datepicker.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jsCalendar/jsCalendar.lang.ko.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/space.css">
</head>
<body>
<div>
    <div>
        <h1>공간 목록(관리자 페이지)</h1>
    </div>
    <div>
        <div>
            <input type="text" name="myCalendar" value="" id="myCalendar" class="myCalendar">
        </div>
        <div id="spaceList" class="spaceList">
        </div>
    </div>
</div>
<nav id="paginationNav" class="paginationContainer" aria-label="pagination"></nav>
<script>
    const spaceListWrapper = document.getElementById('spaceList');

    const timestampAdding90Days = 90 * 24 * 3600 * 1000;
    const bookingDate = '${date}' ? convertPlainDateToDate('${date}') : new Date();
    const bookingDatePickerFormat = reverseSlashDate(convertDateToSlashDate(bookingDate));

    const after90DaysDatePickerFormat = reverseSlashDate(convertDateToSlashDate(new Date(Date.now() + timestampAdding90Days)));
    const calendarEl = document.getElementById('myCalendar');
    const myDatePicker = new jsCalendar.datepicker({
        target: calendarEl,
        navigatorPosition : 'right',
        monthFormat : 'month YYYY',
        language : 'ko',
        date: bookingDatePickerFormat,
        min : bookingDatePickerFormat,
        max : after90DaysDatePickerFormat,
    });

    const pageInputEl = document.querySelector('#pageInput');
    const chevronBtns = document.getElementsByClassName('chevronBtn');
    const prevBtnEl = document.getElementById('prevBtn');
    const nextBtnEl = document.getElementById('nextBtn');
    const totalPageEl = document.getElementById('totalPage');
    const paginationNavEl = document.getElementById('paginationNav');

    let pageHandler;
    let links;
    let currentPage = 1;

    myDatePicker.jsCalendar.onDateClick(function(event, date){
        if(event.target.classList.contains('jsCalendar-unselectable')) return;

        const plainDate = convertDateToPlainDate(date);
        const page = pageHandler ? pageHandler.currentPage : 1;
        requestSpaceList(page, plainDate);
    });

    document.addEventListener('DOMContentLoaded', function(){
        requestSpaceList(1, convertDateToPlainDate(new Date()));
    });

    //// Space List ////

    function requestSpaceList(page, date) {
        fetch(`<c:url value="${spaceListRequestUrl}"/>?page=\${page}&date=\${date}`, {
            method: 'GET',
        }).then(response => {
            if (response.status === 200) {
                return response.text();
            } else if(response.status === 204) {
                markListEmpty();
                return null;
            } else {
                location.href = response.headers.get('location');
            }
        }).then(response => {
            if(!response) return;
            const jsonData = JSON.parse(response);
            const slashDate = convertPlainDateToSlahDate(date);
            const reverseDate = reverseSlashDate(slashDate);
            myDatePicker.set(reverseDate);
            pageHandler = jsonData.pageHandler;

            initSpaceList();
            addSpaceList(jsonData);
            showPaginationNav();
            initPagination();
        }).catch(error => {
            console.error('ERROR: ', error);
        });
    }

    function markListEmpty() {
        spaceListWrapper.insertAdjacentHTML('afterbegin', `
            <div>
                <span>예약 가능한 공간이 없습니다.</span>
            </div>
        `);
    }

    function addSpaceList(jsonData) {
        for (let space of jsonData.spaces) {
            const rescs = Object.values(jsonData.resources).filter((resc) => resc.spaceNo === space.spaceNo);
            const file = Object.values(jsonData.files).find((file) => file.loc_no === space.spaceNo);
            const bookings = Object.values(jsonData.bookings).filter((booking) => booking.spaceNo === space.spaceNo);

            const rescWrapper = document.createElement('div');
            rescWrapper.classList.add('rescInfo');
            for (let resc of rescs) {
                rescWrapper.insertAdjacentHTML('beforeend',`
          <span data-no="\${resc.rescNo}">\${resc.value}</span>
        `);
            }

            const spaceWrapper = document.createElement('div');
            spaceWrapper.classList.add('spaceInfo');
            spaceWrapper.insertAdjacentHTML('beforeend', `
        <div>
            <a href="\${jsonData.links.find(link => link.rel === 'spaceDetail').href.replace('{spaceNo}', space.spaceNo)}">\${space.spaceNm}</a>
        </div>
        <div>
            <span>\${space.maxCapacity}명</span>
            <span>\${space.spaceLoc}</span>
            <span>\${space.spaceDesc}</span>
        </div>
        <div>
            <span>최대 연속 예약 시간:\${space.maxRsvsTms}</span>
            <div>
              <span>\${space.startTm[0].toString().padStart(2,'0')}:\${space.startTm[1].toString().padStart(2,'0')}</span>
              <span>-</span>
              <span>\${space.finishTm[0].toString().padStart(2,'0')}:\${space.finishTm[1].toString().padStart(2,'0')}</span>
            </div>
            <span>주말 이용 \${space.weekend == 'Y'? '가능' : '불가'}</span>
        </div>
      `);

            const infoWrapper = document.createElement('div');
            infoWrapper.classList.add('info');
            infoWrapper.appendChild(spaceWrapper);
            infoWrapper.appendChild(rescWrapper);

            const thumbnailWrapper = document.createElement('div');
            thumbnailWrapper.insertAdjacentHTML('afterbegin',`
                <a href="\${jsonData.links.find(link => link.rel === 'spaceDetail').href.replace('{spaceNo}', space.spaceNo)}">
                  <img class="thumbnailImg" alt="공간 대표사진" src="\${file.rename? jsonData.thumbnailPath+'/'+file.rename:jsonData.noImgPath}"/>
                </a>`);
            const bookingBtnWrapper = document.createElement('div');
            const bookingUrl = jsonData.links.find((link) => link.rel==='booking').href.replace('{space-no}', space.spaceNo).replace('{?date}',`?date=\${getPlainDate()}`)
            bookingBtnWrapper.insertAdjacentHTML('afterbegin', `<a class="bookingBtn" href="\${bookingUrl}">예약하기</a>`)

            const spaceContent = document.createElement('div');
            spaceContent.classList.add('spaceContent');
            spaceContent.appendChild(thumbnailWrapper);
            spaceContent.appendChild(infoWrapper);
            spaceContent.appendChild(bookingBtnWrapper);

            const timeslotWrapper = document.createElement('div');
            timeslotWrapper.classList.add('timeslotWrapper');

            for (let time = space.startTm[0]; time < space.finishTm[0]; time++) {
                const isBooked = bookings.some((booking) => booking.beginTime[0] <= time && time < booking.endTime[0]);
                timeslotWrapper.insertAdjacentHTML('beforeend', `
          <div class="timeslot">
            <div><span class="time">\${time}</span></div>
            <div><span class="slot \${isBooked?'booked':''}"></span></div>
          </div>
        `);
            }

            const spaceListItem = document.createElement('div');
            spaceListItem.classList.add('item');
            spaceListItem.appendChild(spaceContent);
            spaceListItem.appendChild(timeslotWrapper);

            spaceListWrapper.appendChild(spaceListItem);
        }
    }

    function initSpaceList(){
        spaceListWrapper.innerHTML = '';
    }

    //// Pagination ////
    function addPaginationNav() {
        paginationNavEl.insertAdjacentHTML('beforebegin', `
            <div class="paginationWrapper">
                <div class="paginationArrow">
                  <button type="button" id="prevBtn" class="chevronBtn iconChevronStart" aria-label="previous" aria-disabled=""></button>
                </div>
                <div class="paginationPageNumber">
                  <input id="pageInput" class="currentPageInput" type="number" value=""><span class="pageSlash">/</span><span id="totalPage" class="totalPageNumber"></span>
                </div>
                <div class="paginationArrow">
                  <button type="button" id="nextBtn" class="chevronBtn iconChevronEnd" aria-label="next" aria-disabled="">
                  </button>
                </div>
            </div>
        `);
    }

    function removePaginationNav() {
        paginationNavEl.removeChild(paginationWrapperEl);
    }

    function showPaginationNav() {
        const paginationWrapper = paginationNavEl.querySelector('.paginationWrapper');
        if(!paginationWrapper) addPaginationNav();
    }

    function initPagination(){
        if(!pageHandler) return;

        prevBtnEl.disabled = !pageHandler.showPrev;
        nextBtnEl.disabled = !pageHandler.showNext; //true

        prevBtnEl.ariaDisabled = !pageHandler.showPrev;
        nextBtnEl.ariaDisabled = !pageHandler.showNext;
        pageInputEl.value = pageHandler.currentPage;
        totalPageEl.innerText = pageHandler.totalPage;
    }

    Array.from(chevronBtns).forEach(btn => btn.addEventListener('click', (e)=>{ pagination.doRedirect(e); }));

    const pagination ={
        doRedirect : e => {
            if (e.target.classList.contains("iconChevronStart") && pageHandler.currentPage !== 1) {
                requestSpaceList(pageHandler.currentPage-1, convertDateToPlainDate(convertSlashDateToDate(calendarEl.value)));
            }
            if(e.target.classList.contains("iconChevronEnd") && pageHandler.currentPage !== pageHandler.totalPage){
                requestSpaceList(pageHandler.currentPage+1, convertDateToPlainDate(convertSlashDateToDate(calendarEl.value)));
            }
        }
    }

    pageInputEl.addEventListener("keydown", (e) => {
        if (e.key === 'Enter') {
            if(pageInputEl.value===''||pageInputEl.value===`${'${pageHandler.currentPage}'}`){
                return;
            }
            if(0<=pageInputEl.value&& pageInputEl.value<=`${'${pageHandler.totalPage}'}`){
                requestSpaceList(pageInputEl.value, convertDateToPlainDate(convertSlashDateToDate(calendarEl.value)));
            }
        }
    });

    //// Convert ////

    function getPlainDate(){
        return `\${myDatePicker.jsCalendar._now.getFullYear()}\${String(myDatePicker.jsCalendar._now.getMonth()+1).padStart(2,'0')}\${String(myDatePicker.jsCalendar._now.getDate()).padStart(2,'0')}`
    }

    function reverseSlashDate(slashDate) {
        const dateArr = slashDate.split('/');
        return `\${dateArr[2]}/\${dateArr[1]}/\${dateArr[0]}`;
    }

    function convertDateToSlashDate(date) {
        const year = date.getFullYear();
        const month = (date.getMonth()+1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        return `\${year}/\${month}/\${day}`;
    }

    function convertDateToPlainDate(date) {
        const year = date.getFullYear();
        const month = (date.getMonth()+1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        return `\${year}\${month.toString().padStart(2, '0')}\${day.toString().padStart(2, '0')}`;
    }

    function convertPlainDateToSlahDate(plainDate) {
        const date = convertPlainDateToDate(plainDate);
        return convertDateToSlashDate(date);
    }

    function convertSlashDateToDate(date) {
        const dateArr = date.split('/');
        return new Date(parseInt(dateArr[0]), parseInt(dateArr[1])-1, parseInt(dateArr[2]));
    }

    function convertPlainDateToDate(plainDate) {
        const year = plainDate.substring(0,4)
        const monthIndex = parseInt(plainDate.substring(4, 6)) - 1;
        const day = plainDate.substring(6);

        return new Date(year, monthIndex, day);
    }
</script>
</body>
</html>
