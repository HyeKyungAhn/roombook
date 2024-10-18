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
  <title></title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jsCalendar/jsCalendar.css">
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jsCalendar/jsCalendar.micro.css">
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/jsCalendar/jsCalendar.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/jsCalendar/jsCalendar.datepicker.js"></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/jsCalendar/jsCalendar.lang.ko.js"></script>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/space.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
</head>
<body>
  <div class="spaceListRoot">
    <div class="headerWrapper">
      <h1>공간 목록</h1>
    </div>
    <div class="datePickerWrapper">
      <label for="myCalendar" class="hidden">날짜 선택</label>
      <input type="text" name="myCalendar" value="" id="myCalendar" class="myCalendar datePicker" readonly>
    </div>
    <div id="spaceList" class="spaceList"></div>
  </div>
<nav id="paginationNav" class="paginationContainer" aria-label="pagination">
</nav>
<script>
  const spaceListWrapper = document.getElementById('spaceList');

  const timestampAdding90Days = 90 * 24 * 3600 * 1000;
  const bookingDate = '${date}' ? convertPlainDateToDate('${date}') : new Date();
  const bookingDatePickerFormat = reverseSlashDate(convertDateToSlashDate(bookingDate));
  const todayDatePickerFormat = reverseSlashDate(convertDateToSlashDate(new Date));
  const threeMonthsLater = reverseSlashDate(convertDateToSlashDate(new Date(Date.now() + timestampAdding90Days)));
  const calendarEl = document.getElementById('myCalendar');
  const myDatePicker = new jsCalendar.datepicker({
    target: calendarEl,
    navigatorPosition : 'right',
    monthFormat : 'month YYYY',
    language : 'ko',
    date: bookingDatePickerFormat,
    min : todayDatePickerFormat,
    max : threeMonthsLater,
  });

  let links;

  myDatePicker.jsCalendar.onDateClick(function(event, date){
    if(event.target.classList.contains('jsCalendar-unselectable')) return;

    const plainDate = convertDateToPlainDate(date);
    const page = pagination.pageHandler ? pagination.pageHandler.currentPage : 1;

    requestSpaceList(page, plainDate);
  });

  document.addEventListener('DOMContentLoaded', function(){
    const date = convertDateToPlainDate(bookingDate);
    requestSpaceList('${page}', date);
  });

  //// Space List ////

  function requestSpaceList(page, date, callback) {
    fetch(`<c:url value="${spaceListRequestUrl}"/>?page=\${page}&date=\${date}`, {
      method: 'GET',
    }).then(response => {
      if (response.status === 200) {
        return response.text();
      } else if(response.status === 204) {
        informListEmpty();
        return null;
      } else if(response.status === 304) {
        location.href = response.headers.get('location');
      } else {
        throw new Error("bad request!");
      }
    }).then(response => {
      if(!response) return;
      const jsonData = JSON.parse(response);
      const slashDate = convertPlainDateToSlahDate(date);
      const reverseDate = reverseSlashDate(slashDate);
      myDatePicker.set(reverseDate);

      initSpaceList();
      addSpaceList(jsonData);

      pagination.pageHandler = jsonData.pageHandler;
      pagination.showPaginationNav();
      pagination.initialize();

      if(typeof callback === "function"){
        callback();
      }
    }).catch(error => {
      console.error('ERROR: ', error);
    });
  }

  function scrollToBottom() {
    window.scrollTo(0, 0);
  }

  function informListEmpty() {
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
          <div class="rescItem" data-no="\${resc.rescNo}"><span>#</span><span>\${resc.value}</span></div>
        `);
      }

      const spaceWrapper = document.createElement('div');
      spaceWrapper.insertAdjacentHTML('beforeend', `
        <div class="spaceName">
            <a href="\${jsonData.links.find(link => link.rel === 'spaceDetail').href.replace('{spaceNo}', space.spaceNo)}">\${space.spaceNm}</a>
        </div>
        <div>
            <div class="spaceBriefInfoRow">
              <span class="spaceCapacity">\${space.maxCapacity}명</span>
              <div class="spaceUsgTime">
                <span class="spaceStartTime">\${space.startTm[0].toString().padStart(2,'0')}:\${space.startTm[1].toString().padStart(2,'0')}</span>
                <span>-</span>
                <span class="spaceFinishTime">\${space.finishTm[0].toString().padStart(2,'0')}:\${space.finishTm[1].toString().padStart(2,'0')}</span>
              </div>
              <span class="spaceMaxBookingTime">\${space.maxRsvsTms} 시간</span>
              <span class="spaceWeekend \${space.weekend == 'Y'?'bookable':'unbookable'}">주말 이용 \${space.weekend == 'Y'? '가능' : '불가'}</span>
            </div>
            <div class="spaceBriefInfoRow">
              <p class="spaceLocation">\${space.spaceLoc}</p>
            </div>
        </div>
        <div class="spaceDescription">\${space.spaceDesc}</div>
      `);

      const infoWrapper = document.createElement('div');
      infoWrapper.classList.add('spaceInfo');
      infoWrapper.appendChild(spaceWrapper);
      infoWrapper.appendChild(rescWrapper);

      const thumbnailWrapper = document.createElement('div');
      thumbnailWrapper.insertAdjacentHTML('afterbegin',`<img class="thumbnailImg" alt="공간 대표사진" src="\${file.rename? jsonData.thumbnailPath+'/'+file.rename:jsonData.noImgPath}"/>`);

      const bookingBtnWrapper = document.createElement('div');
      const bookingUrl = jsonData.links.find((link) => link.rel==='booking').href.replace('{space-no}', space.spaceNo).replace('{?date}',`?date=\${getPlainDate()}`)
      bookingBtnWrapper.insertAdjacentHTML('afterbegin', `<a class="btnS bg_yellow color_lightBlack bookingBtn" href="\${bookingUrl}">예약하기</a>`)

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

  const pagination ={
    CLASSES: {
      CHEVRON_BTN: 'chevronBtn',
    },
    pageHandler: null,
    pageInputEl: document.getElementById('pageInput'),
    prevBtnEl: document.getElementById('prevBtn'),
    nextBtnEl: document.getElementById('nextBtn'),
    totalPageEl: document.getElementById('totalPage'),
    paginationNavEl: document.getElementById('paginationNav'),
    chevronBtns: document.getElementsByClassName('chevronBtn'),

    initialize: () => {
      pagination.pageInputEl = document.getElementById('pageInput');
      pagination.prevBtnEl = document.getElementById('prevBtn');
      pagination.nextBtnEl = document.getElementById('nextBtn');
      pagination.totalPageEl = document.getElementById('totalPage');
      pagination.paginationNavEl = document.getElementById('paginationNav');
      pagination.chevronBtns = document.getElementsByClassName('chevronBtn');

      if(!pagination.pageHandler) return;
      pagination.prevBtnEl.disabled = !pagination.pageHandler.showPrev;
      pagination.nextBtnEl.disabled = !pagination.pageHandler.showNext;

      pagination.prevBtnEl.ariaDisabled = !pagination.pageHandler.showPrev;
      pagination.nextBtnEl.ariaDisabled = !pagination.pageHandler.showNext;
      pagination.pageInputEl.value = pagination.pageHandler.currentPage;
      pagination.totalPageEl.innerText = pagination.pageHandler.totalPage;

      pagination.addEvents();
    },
    doRedirect: e => {
      if (e.target.classList.contains("iconChevronStart") && pagination.pageHandler.currentPage !== 1) {
        requestSpaceList(pagination.pageHandler.currentPage-1, convertDateToPlainDate(convertSlashDateToDate(calendarEl.value)), () => {
          scrollToBottom();
        });
      }
      if(e.target.classList.contains("iconChevronEnd") && pagination.pageHandler.currentPage !== pagination.pageHandler.totalPage){
        requestSpaceList(pagination.pageHandler.currentPage+1, convertDateToPlainDate(convertSlashDateToDate(calendarEl.value)), () => {
          scrollToBottom();
        });
      }
    },
    removePaginationNav: () => {
      this.paginationNavEl.removeChild(paginationWrapperEl);
    },
    showPaginationNav: () => {
      const paginationWrapper = pagination.paginationNavEl.querySelector('.paginationWrapper');
      if(!paginationWrapper) pagination.addPaginationNav();
    },
    addPaginationNav: () => {
      pagination.paginationNavEl.insertAdjacentHTML('afterbegin', `
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
    },
    addEvents: () => {
      pagination.removeEvents();
      Array.from(pagination.chevronBtns).forEach(btn => btn.addEventListener('click', pagination.eventHandlers.onChevronBtnClick));
      pagination.pageInputEl.addEventListener('keydown', pagination.eventHandlers.onKeyDown);
    },
    removeEvents: () => {
      Array.from(pagination.chevronBtns).forEach(btn => btn.removeEventListener('click', pagination.eventHandlers.onChevronBtnClick));
      pagination.pageInputEl.removeEventListener('keydown', pagination.eventHandlers.onKeyDown);
    },
    eventHandlers: {
      onChevronBtnClick: (e) => {
        pagination.doRedirect(e);
      },
      onKeyDown: (e) => {
        if (e.key === 'Enter') {
          if(pagination.pageInputEl.value===''||pagination.pageInputEl.value === pagination.pageHandler.currentPage){
            return;
          }
          if (0 <= pagination.pageInputEl.value && pagination.pageInputEl.value <= pagination.pageHandler.totalPage) {
            requestSpaceList(pagination.pageInputEl.value, convertDateToPlainDate(convertSlashDateToDate(calendarEl.value)));
          }
        }
      },
    }
  }

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
