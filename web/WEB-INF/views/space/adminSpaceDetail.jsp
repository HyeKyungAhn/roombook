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
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/space.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jsCalendar/jsCalendar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jsCalendar/jsCalendar.micro.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jsCalendar/jsCalendar.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jsCalendar/jsCalendar.datepicker.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jsCalendar/jsCalendar.lang.ko.js"></script>
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
        <button type="button" id="bookBtn">예약하기</button>
    </div>
    <div>
        <div>
            <span>공간명</span>
            <span id="name"></span>
        </div>
        <div>
            <span>위치(20자 이내)</span>
            <span id="location"></span>
        </div>
        <div>
            <span>공간 설명(100자)</span>
            <span id="description"></span>
        </div>
        <div>
            <span>최대 연속 예약 가능 시간(시간 단위)</span>
            <span id="maxTime"></span>
        </div>
        <div>
            <span>공간 주말 이용 가능 여부</span>
            <span id="weekend"></span>
        </div>
        <div>
            <span>이용시간</span>
            <span id="startTime"></span>
            <span>-</span>
            <span id="finishTime"></span>
        </div>
        <div>
            <span>최대 수용인원</span>
            <span id="capacity"></span>
        </div>
        <div>
            <span>옵션(facility)</span>
            <div id="resources"></div>
        </div>
        <div>
            <span>목록 숨김 여부</span>
            <span id="hideYn"></span>
        </div>
        <div>
            <div>
                <input type="text" name="myCalendar" value="" id="myCalendar" class="myCalendar">
            </div>
            <div id="timeTable"></div>
        </div>
        <div>
            <button type="button" id="editBtn">수정</button>
            <button type="button" id="listBtn">목록</button>
        </div>
    </div>
</div>
<script>
    const jsonData = JSON.parse('${jsonSpace}');
    const imgPath = '${imgPath}';

    const nameEl = document.getElementById('name');
    const locationEl = document.getElementById('location');
    const descriptionEl = document.getElementById('description');
    const maxTimeEl = document.getElementById('maxTime');
    const weekendEl = document.getElementById('weekend');
    const startTimeEl = document.getElementById('startTime');
    const finishTimeEl = document.getElementById('finishTime');
    const capacityEl = document.getElementById('capacity');
    const hideYnEl = document.getElementById('hideYn');
    const timeTableEl = document.getElementById('timeTable');

    const editBtn = document.getElementById('editBtn');
    const paths = window.location.pathname.split('/');
    const bookBtnEl = document.getElementById('bookBtn');
    const listBtn = document.getElementById('listBtn');

    const timestampAdding90Days = 90 * 24 * 3600 * 1000;
    const bookingDate = new Date();
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

    editBtn.addEventListener('click', () => {
        location.href = '<c:url value="${editUri}"/>';
    });

    bookBtnEl.addEventListener('click', function() {
        location.href = `<c:url value="${bookingUri}"/>?date=\${getPlainDate()}`;
    });

    listBtn.addEventListener('click', () =>{
       location.href = '<c:url value="${spaceListUri}"/>';
    });

    document.addEventListener('DOMContentLoaded', function(){
        renderSpaceInfo();
        renderResources();
        requestTimeslots();
    });

    myDatePicker.jsCalendar.onDateClick(function(event, date){
        requestTimeslots();
    });

    function renderSpaceInfo(){
        nameEl.innerText = jsonData.spaceNm;
        locationEl.innerText = jsonData.spaceLoc;
        descriptionEl.innerText = jsonData.spaceDesc;
        maxTimeEl.innerText = jsonData.maxRsvsTms + '시간';
        weekendEl.innerText = jsonData.weekend;
        startTimeEl.innerText = `\${jsonData.startTm[0].toString().padStart(2,'0')}:\${jsonData.startTm[1].toString().padStart(2,'0')}`;
        finishTimeEl.innerText = `\${jsonData.finishTm[0].toString().padStart(2,'0')}:\${jsonData.finishTm[1].toString().padStart(2,'0')}`;
        capacityEl.innerText = jsonData.maxCapacity;
        hideYnEl.innerText = jsonData.hide;
    }

    function renderResources() {
        const jsonResources = jsonData.resources;
        const resourcesEl = document.getElementById('resources');

        resourcesEl.innerHTML = jsonResources.map((resc, index) => {
            return `<span class='rescTag'>${'${resc.value}'}</span>`
        }).join('');
    }

    function requestTimeslots(){
        const date = getPlainDate();
        fetch(`<c:url value="${requestTimeslots}"/>?date=\${date}`)
        .then(response => {
            if (response.ok) {
                return response.text();
            }
        }).then(text => {
            const slashDate = convertPlainDateToSlahDate(date);
            const reverseDate = reverseSlashDate(slashDate);
            myDatePicker.set(reverseDate);

            const timeslots = JSON.parse(text);
            clearTimeTable();
            renderTimeTable(timeslots)
        });
    }

    function clearTimeTable() {
        timeTableEl.innerText = '';
    }
    function renderTimeTable(timeslots) {
        const timeslotWrapper = document.createElement('div');
        timeslotWrapper.classList.add('timeslotWrapper');

        for (let time = jsonData.startTm[0]; time < jsonData.finishTm[0]; time++) {
            const isBooked = timeslots.some((timeslot) => timeslot.beginTime[0] <= time && time < timeslot.endTime[0]);
            timeslotWrapper.insertAdjacentHTML('beforeend', `
                <div class="timeslot">
                <div><span class="time">\${time}</span></div>
                <div><span class="slot \${isBooked?'booked':''}"></span></div>
                </div>
            `);
        }

        timeTableEl.appendChild(timeslotWrapper);
    }

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
<script src="${pageContext.request.contextPath}/js/imgSlider.js"></script>
</body>
</html>