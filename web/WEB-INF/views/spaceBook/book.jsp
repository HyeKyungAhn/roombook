<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-09-06
  Time: 오후 3:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <title>roombook | 공간 예약</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jsCalendar/jsCalendar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jsCalendar/jsCalendar.micro.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/spaceBook.css"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jsCalendar/jsCalendar.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jsCalendar/jsCalendar.datepicker.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jsCalendar/jsCalendar.lang.ko.js"></script>
</head>
<body>
<div>
    <div class="rootWrapper">
        <div>
            <h1>회의실 이름</h1>
        </div>
        <div>
            <div class="spaceInfo">
                <span id="maxCapacity" class="maxCapacity"></span>
                <span id="maxBookingTime" class="maxBookingTime"></span>
                <div id="bookingAvailableTime" class="bookingAvailableTime">
                    <span id="startTime"></span>
                    <span>~</span>
                    <span id="endTime"></span>
                </div>
                <span id="weekendYn" class="weekendYn"></span>
            </div>
        </div>
    </div>
    <input type="text" name="myCalendar" value="" id="myCalendar" class="myCalendar">
    <select id="bookingBeginTime" name="bookingBeginTime"></select>
    <select id="bookingEndTime" name="bookingEndTime"></select>

    <div>
        <textarea id="bookingContent" name="bookCn" rows="2" cols="50"></textarea>
    </div>
    <button id="bookingBtn" type="button">예약하기</button>
</div>
<script>
    const spaceData = JSON.parse('${spaceData}');
    let timeslots;

    const maxCapacityEl = document.getElementById('maxCapacity');
    const maxBookingTimeEl = document.getElementById('maxBookingTime');
    const bookingAvailableTimeEl = document.getElementById('bookingAvailableTime');
    const weekendYnEl = document.getElementById('weekendYn');
    const startTimeEl = document.getElementById('startTime');
    const endTimeEl = document.getElementById('endTime');
    const bookingBeginTimeEl = document.getElementById('bookingBeginTime');
    const bookingEndTimeEl = document.getElementById('bookingEndTime');
    const bookingContentEl = document.getElementById('bookingContent');
    const bookingBtnEl = document.getElementById('bookingBtn');

    const timestampAdding90Days = 90 * 24 * 3600 * 1000;
    const bookingDate = '${date}' ? convertPlainDateToDate('${date}') : new Date();
    const bookingDatePickerFormat = reverseSlashDate(convertDateToSlashDate(bookingDate));
    const after90DaysDatePickerFormat = reverseSlashDate(convertDateToSlashDate(new Date(bookingDate.getTime() + timestampAdding90Days)));

    const element = document.getElementById('myCalendar');
    const myDatePicker = new jsCalendar.datepicker({
        target: element,
        navigatorPosition : 'right',
        monthFormat : 'month YYYY',
        language : 'ko',
        date: bookingDatePickerFormat,
        min : bookingDatePickerFormat,
        max : after90DaysDatePickerFormat,
    });


    ////EventListener////
    window.onpageshow = (e) => {
        if (e.persisted || (window.performance && window.performance.navigation.type === 2)) {
            initInputs();
        }
    }

    document.addEventListener('DOMContentLoaded', function(){
        init();

        requestTimeslots('${date}'?'${date}':convertDateToPlainDate(new Date()));
    });

    bookingBeginTimeEl.addEventListener('change', function() {
        const bookingFinishHour = spaceData.finishTm[0];
        printOptions(bookingEndTimeEl, timeslots, parseInt(bookingBeginTimeEl.value) + 1, parseInt(bookingFinishHour) + 1);
    });

    bookingEndTimeEl.addEventListener('change', function(){
        if (isContinuousBooking(timeslots, bookingBeginTimeEl.value, bookingEndTimeEl.value)) {
            if(isExceedMaxBookingTime(spaceData.maxRsvsTms, bookingBeginTimeEl.value, bookingEndTimeEl.value)){
                alert('예약 가능 시간을 초과했습니다.');
                bookingEndTimeEl.value = bookingEndTimeEl.children[0].value;
            }
        } else {
            alert('연속된 예약만 할 수 있습니다.');
            bookingEndTimeEl.value = bookingEndTimeEl.children[0].value;
        }
    });

    myDatePicker.jsCalendar.onDateClick(function(event, date){
        const formedDate = convertDateToPlainDate(date);
        requestTimeslots(formedDate);
    });


    ////API Request////
    bookingBtnEl.addEventListener('click', function() {
        const bookingData = {
            'spaceNo': spaceData.spaceNo,
            'spaceBookCn': bookingContentEl.value,
            'date': element.value,
            'beginTime': convertTimeArrToString([bookingBeginTimeEl.value,0]),
            'endTime': convertTimeArrToString([bookingEndTimeEl.value,0]),
        }

        if(!validateBookingData(bookingData)) return false;

       fetch('<c:url value="${bookingUrl}"/>', {
           method: 'POST',
           headers: {
               'Content-Type': 'application/json',
           },
           body: JSON.stringify(bookingData),
       }).then(response => {
           if(response.ok){
               if(confirm('예약에 성공하였습니다.\n내 예약 페이지로 이동하시겠습니까?')){
                   location.href = response.headers.get('location');
               } else {
                   history.back();
               }
           } else {
               return response.text();
           }
       }).then(text => {
           const jsonData = JSON.parse(text);
           alert(jsonData.errorMessage);
       }).catch(error => {
           console.error('Error:', error);
       });
    });

    function requestTimeslots(date) {
        fetch(`<c:url value="${bookedTimeslotsUrl}"/>?date=\${date}`, {
            method: 'GET'
        }).then(response => {
            return response.text();
        }).then(text => {
            timeslots = JSON.parse(text);

            const bookingDate = convertPlainDateToDate(date);
            const bookingStartHour = spaceData.startTm[0];
            const bookingFinishHour = spaceData.finishTm[0];

            initTimeslotsSelect(bookingDate, bookingStartHour, bookingFinishHour);
            return true;
        }).catch(error => {
            console.error('Error:', error);
        });
    }

    ////Validation////
    function validateBookingData(bookingData) {
        let result = true;
        if(!bookingData.spaceNo){
            alert('예약정보가 잘못되었습니다.\n새로고침 후 다시 예약해주세요.');
            result = false;
        } else if(!Number.isInteger(bookingData.spaceNo)){
            alert('잘못된 예약정보가 입력되었습니다.\n새로고침 후 다시 예약해주세요.');
            result = false;
        } else if(!bookingData.spaceBookCn){
            alert('예약 내용을 입력해주세요.');
            result = false;
        } else if(!bookingData.date) {
            alert('날짜를 입력해주세요.');
            result = false;
        } else if(!bookingData.date.match(/^20[0-9]{2}\/(0[1-9]|1[0-2])\/(0[1-9]|1\d|2\d|3[0-1])$/g)) {
            alert('날짜를 형식에 맞게 입력해주세요.');
            result = false;
        } else if(!bookingData.beginTime.match(/^(0[1-9]|1[0-9]|2[0-4]):(0[0-9]|[1-5]\d)$/g)
            ||!bookingData.endTime.match(/^(0[1-9]|1[0-9]|2[0-4]):(0[0-9]|[1-5]\d)$/g)
            ||!bookingData.beginTime || !bookingData.endTime){
            alert('시간을 선택해주세요.');
            result = false;
        }

        return result;
    }

    function isToday(bookingDate){
        return bookingDate.setHours(0,0,0,0) === new Date().setHours(0,0,0,0);
    }

    function isExceedMaxBookingTime(maxBookingTm, beginTime, endTime) {
        return (endTime - beginTime) > maxBookingTm;
    }

    function isContinuousBooking(timeslots, bookingBeginTime, bookingEndTime) {
        for (const timeslot of timeslots) {
            if (bookingBeginTime <= timeslot.beginTime[3] && timeslot.beginTime[3] < bookingEndTime) {
                return false;
            }
        }
        return true;
    }

    function isBookingAfterOwn(){
        for (const element of timeslots) {
            if(element.selfBook && parseInt(bookingBeginTimeEl.value) === (element.endTime[3] + 1)){
                alert('본인 예약 바로 뒤에 새로운 예약을 할 수 없습니다.\n기존 예약을 삭제/수정 후 다시 시도해주세요.');
                return true;
            }
        }
        return false;
    }

    function isBookingUnavailable(bookingStartTime){
        return parseInt(bookingStartTime) === -1;
    }

    function canBookToday(today, bookingFinishHour){
        return today.getHours() < parseInt(bookingFinishHour);
    }

    ////Initialization////
    function init() {
        maxCapacityEl.innerText = spaceData.maxCapacity + '명';
        maxBookingTimeEl.innerText = spaceData.maxRsvsTms + '시간';
        startTimeEl.innerText = convertTimeArrToString(spaceData.startTm);
        endTimeEl.innerText = convertTimeArrToString(spaceData.finishTm);
        weekendYnEl.innerText = spaceData.weekend === 'Y' ? '주말 예약 가능' : '주말 예약 불가';
    }

    function initInputs(){
        myDatePicker.set('now');
        requestTimeslots(convertDateToPlainDate(new Date()));
        bookingContentEl.value = '';
    }

    function initTimeslotsSelect(bookingDate, bookingStartHour, bookingFinishHour) {
        const today = new Date();

        if(isToday(bookingDate)){
            if (!canBookToday(today, bookingFinishHour)) {
                printBlockOptions(bookingBeginTimeEl);
                printBlockOptions(bookingEndTimeEl);
                return;
            }

            bookingStartHour = adjustBookingStartTime(today, bookingStartHour);
        }

        printOptions(bookingBeginTimeEl, timeslots, bookingStartHour, bookingFinishHour);
        printOptions(bookingEndTimeEl, timeslots, parseInt(bookingBeginTimeEl.value) + 1, parseInt(bookingFinishHour) + 1);
    }

    /** 현재 시간이 예약 시작 시간 이후일 때
     * 현재 이후에 예약 가능하도록 예약 시작 시간 조정 **/
    function adjustBookingStartTime(today, bookingStartTime){
        if(today.getHours() <= bookingStartTime) {
            return bookingStartTime;
        } else {
            return today.getHours();
        }
    }

    function printOptions(element, timeslots, bookingStartHour, bookingFinishHour) {
        element.innerHTML = "";

        for(let time = bookingStartHour; time < bookingFinishHour; time++){
            let stringBeginTime = time.toString().padStart(2, '0');
            let isDisabled = false;
            let isSelfBooking = false;

            for (const element of timeslots) {
                if (element.beginTime[0] <= time && time < element.endTime[0]) {
                    isDisabled = true;
                    isSelfBooking = element.selfBook;
                    break;
                }
            }

            element.insertAdjacentHTML('beforeend',
                `<option name='time' value="\${time}" \${isDisabled?'disabled':''}>\${stringBeginTime}:00 \${isSelfBooking?'(본인예약)':''}</option>`);
        }
    }

    function printBlockOptions(element) {
        element.innerHTML = "";

        element.insertAdjacentHTML('beforeend',
            `<option selected value="예약불가" disabled>예약 불가</option>`);
    }

    ////Date Conversion////
    function convertTimeArrToString(timeArr) {
        return `\${timeArr[0].toString().padStart(2, '0')}:\${timeArr[1].toString().padStart(2, '0')}`;
    }

    function convertDateToPlainDate(date) {
        const year = date.getFullYear();
        const month = (date.getMonth()+1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');

        return `\${year}\${month.toString().padStart(2, '0')}\${day.toString().padStart(2, '0')}`;
    }

    function convertPlainDateToDate(plainDate) {
        const year = plainDate.substring(0,4)
        const monthIndex = parseInt(plainDate.substring(4, 6)) - 1;
        const day = plainDate.substring(6);

        return new Date(year, monthIndex, day);
    }

    function convertDateToSlashDate(date) {
        const year = date.getFullYear();
        const month = (date.getMonth()+1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        return `\${year}/\${month}/\${day}`;
    }

    function reverseSlashDate(slashDate) {
        const dateArr = slashDate.split('/');
        return `\${dateArr[2]}/\${dateArr[1]}/\${dateArr[0]}`;
    }
</script>
</body>
</html>