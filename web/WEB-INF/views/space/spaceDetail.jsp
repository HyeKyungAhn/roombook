<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-06-05
  Time: 오후 9:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <title></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/spaceDetail.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/space.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jsCalendar/jsCalendar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jsCalendar/jsCalendar.micro.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jsCalendar/jsCalendar.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jsCalendar/jsCalendar.datepicker.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jsCalendar/jsCalendar.lang.ko.js"></script>
</head>
<body>
<div class="spaceDetailRootWrapper">
    <div class="breadScrumbContainer">
        <a href="${pageContext.request.contextPath}/spaces"  class="breadScrumb">
            <span>공간 목록</span>
        </a>
        <span class="breadScrumbCurrentPage">공간 상세 정보</span>
    </div>
    <div class="headerWrapper">
        <h1 id="name" class="spaceName"></h1>
    </div>
    <div>
        <div class="product-imgs">
            <div class = "img-display">
                <div id="imgShowcase" class = "img-showcase"></div>
            </div>
            <div id="imgSelect" class="img-select"></div>
        </div>
        <div class="bookingBtnWrapper">
            <button type="button" class="btnM bg_yellow" id="bookBtn">예약하기</button>
        </div>
        <div class="spaceInfo">
            <div class="spaceInfoRow">
                <span class="spaceInfoName">위치</span>
                <span id="location" class="spaceInfoContent"></span>
            </div>
            <div class="spaceInfoRow">
                <span class="spaceInfoName">예약 가능 시간</span>
                <span id="maxTime" class="spaceInfoContent"></span>
                <div class="tooltip">
                    <span class="toolTipMsg tooltipRight">최대 <strong>연속</strong>으로 예약 가능한 시간입니다.</span>
                </div>
            </div>
            <div class="spaceInfoRow">
                <span class="spaceInfoName">주말 이용</span>
                <span id="weekend" class="spaceInfoContent"></span>
            </div>
            <div class="spaceInfoRow">
                <span class="spaceInfoName">이용시간</span>
                <div class="spaceInfoContent usgTime">
                    <span id="startTime"></span>
                    <span>-</span>
                    <span id="finishTime"></span>
                </div>
            </div>
            <div class="spaceInfoRow">
                <span class="spaceInfoName">최대 수용인원</span>
                <span id="capacity" class="spaceInfoContent"></span>
            </div>
            <div class="spaceInfoRow">
                <span class="spaceInfoName">공간 설명</span>
            </div>
            <div class="spaceInfoRow">
                <p id="description" class="spaceInfoContent spaceDescription"></p>
            </div>
            <div class="spaceInfoRow">
                <span class="spaceInfoName">옵션(facility)</span>
            </div>
            <div class="spaceInfoRow">
                <div id="resources" class="spaceInfoContent"></div>
            </div>
            <div class="spaceInfoRow">
                <span class="spaceInfoName">공간 예약 스케줄</span>
            </div>
            <div class="timeTableWrapper">
                <span class="spaceInfoName"></span>
                <div>
                    <label for="myCalendar" class="hidden">예약 날짜</label>
                    <input type="text" name="myCalendar" value="" id="myCalendar" class="myCalendar">
                </div>
                <div id="timeTable" class="timeTable"></div>
            </div>
            <div class="btnWrapper">
                <button type="button" id="listBtn" class="btnM2">목록</button>
            </div>
        </div>
    </div>
</div>
<script>
    const jsonData = JSON.parse('${jsonSpace}');
    const imgPath = '${imgPath}';
    const noImgPath = '${noImgPath}';

    const nameEl = document.getElementById('name');
    const locationEl = document.getElementById('location');
    const descriptionEl = document.getElementById('description');
    const maxTimeEl = document.getElementById('maxTime');
    const weekendEl = document.getElementById('weekend');
    const startTimeEl = document.getElementById('startTime');
    const finishTimeEl = document.getElementById('finishTime');
    const capacityEl = document.getElementById('capacity');
    const timeTableEl = document.getElementById('timeTable');

    const listBtn = document.getElementById('listBtn');
    const bookBtnEl = document.getElementById('bookBtn');

    const timestampAdding90Days = 90 * 24 * 3600 * 1000;
    const todayDate = new Date();
    const todayDatePickerFormat = reverseSlashDate(convertDateToSlashDate(todayDate));
    const after90DaysDatePickerFormat = reverseSlashDate(convertDateToSlashDate(new Date(Date.now() + timestampAdding90Days)));
    const calendarEl = document.getElementById('myCalendar');
    const myDatePicker = new jsCalendar.datepicker({
        target: calendarEl,
        navigatorPosition : 'right',
        monthFormat : 'month YYYY',
        language : 'ko',
        date: todayDatePickerFormat,
        min : todayDatePickerFormat,
        max : after90DaysDatePickerFormat,
    });

    listBtn.addEventListener('click', () =>{
        location.href = '<c:url value="${spaceListUri}"/>';
    });

    bookBtnEl.addEventListener('click', function() {
       location.href = `<c:url value="${bookingUri}"/>?date=\${getPlainDate()}`;
    });

    document.addEventListener('DOMContentLoaded', function(){
        renderSpaceInfo();
        renderResources();
        requestTimeslots();
        calendarEl.readOnly = true;
    });

    myDatePicker.jsCalendar.onDateClick(function(event, date){
        requestTimeslots();
    });

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

    function renderSpaceInfo(){
        nameEl.innerText = jsonData.spaceNm;
        locationEl.innerText = jsonData.spaceLoc;
        descriptionEl.innerText = jsonData.spaceDesc;
        maxTimeEl.innerText = jsonData.maxRsvsTms + '시간';
        weekendEl.innerText = jsonData.weekend === 'Y' ? '가능' : '불가';
        startTimeEl.innerText = `\${jsonData.startTm[0].toString().padStart(2,'0')}:\${jsonData.startTm[1].toString().padStart(2,'0')}`;
        finishTimeEl.innerText = `\${jsonData.finishTm[0].toString().padStart(2,'0')}:\${jsonData.finishTm[1].toString().padStart(2,'0')}`;
        capacityEl.innerText = jsonData.maxCapacity + '명';
    }

    function renderResources() {
        const jsonResources = jsonData.resources;
        const resourcesEl = document.getElementById('resources');

        resourcesEl.innerHTML = jsonResources.map((resc, index) => {
            return `
            <div class="rescItem">
                <span>#</span>
                <span>${'${resc.value}'}</span>
            </div>
            `
        }).join('');
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
<script src="${pageContext.request.contextPath}/resources/js/imgSlider.js"></script>
</body>
</html>
