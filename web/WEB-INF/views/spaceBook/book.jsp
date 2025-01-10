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
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jsCalendar/jsCalendar.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jsCalendar/jsCalendar.micro.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/spaceBook.css"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jsCalendar/jsCalendar.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jsCalendar/jsCalendar.datepicker.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jsCalendar/jsCalendar.lang.ko.js"></script>
</head>
<body>
<div>
    <div class="horizontalCenter800 paddingTop40">
        <div class="breadScrumbContainer">
            <span class="breadScrumb">
                <a href="${pageContext.request.contextPath}/spaces">공간 목록</a>
            </span>
            <span class="breadScrumbCurrentPage">공간 예약</span>
        </div>
        <div>
            <div>
                <h1 id="spaceName" class="spaceName"></h1>
            </div>
            <div>
                <div class="spaceInfo spaceInfoRow">
                    <span id="maxCapacity" class="maxCapacity"></span>
                    <div id="bookingAvailableTime" class="bookingAvailableTime">
                        <span id="startTime"></span>
                        <span>~</span>
                        <span id="endTime"></span>
                    </div>
                    <span id="maxBookingTime" class="maxBookingTime"></span>
                    <span id="weekendYn" class="spaceWeekend"></span>
                </div>
                <div class="spaceInfoRow">
                    <label for="myCalendar" class="hidden">예약 날짜 입력</label>
                    <input type="text" name="myCalendar" value="" id="myCalendar" class="myCalendar">
                    <label for="bookingBeginTime" class="hidden">예약 시작 시간</label>
                    <select id="bookingBeginTime" class="bookingBeginTime" name="bookingBeginTime"></select>
                    <label for="bookingEndTime" class="hidden">예약 종료 시간</label>
                    <select id="bookingEndTime" class="bookingEndTime" name="bookingEndTime"></select>
                </div>
                <div class="spaceInfoRow">
                    <label for="bookingContent" class="hidden">예약 내용</label>
                    <textarea id="bookingContent" class="bookingContent" name="bookCn" rows="2" cols="50"></textarea>
                    <p>* 본인의 예약 직후 1시간 이내에는 예약이 불가합니다.</p>
                </div>
                <div class="spaceInfoRow bookingBtnWrapper">
                    <button id="bookingBtn" class="btnM bg_yellow color_lightBlack" type="button">예약하기</button>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/dateTimeConverter.js"></script>
<script>
    const spaceData = JSON.parse('${spaceData}');
    let timeslots;

    const spaceNameEl = document.getElementById('spaceName');
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
    const todayDatePickerFormat = DateTimeConverter.reverseSlashDate(DateTimeConverter.convertDateToSlashDate(new Date()));
    const bookingDate = '${date}' ? DateTimeConverter.convertPlainDateToDate('${date}') : new Date();
    const bookingDatePickerFormat = DateTimeConverter.reverseSlashDate(DateTimeConverter.convertDateToSlashDate(bookingDate));
    const after90DaysDatePickerFormat = DateTimeConverter.reverseSlashDate(DateTimeConverter.convertDateToSlashDate(new Date(Date.now() + timestampAdding90Days)));

    const myCalendar = document.getElementById('myCalendar');
    const myDatePicker = new jsCalendar.datepicker({
        target: myCalendar,
        navigatorPosition : 'right',
        monthFormat : 'month YYYY',
        language : 'ko',
        date: bookingDatePickerFormat,
        min : todayDatePickerFormat,
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

        requestTimeslots('${date}'?'${date}':DateTimeConverter.convertDateToPlainDate(new Date()));
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
        const formedDate = DateTimeConverter.convertDateToPlainDate(date);
        requestTimeslots(formedDate);
    });


    ////API Request////
    bookingBtnEl.addEventListener('click', function() {
        let escapedBookingContent = bookingContentEl.value.replace(/\n/g, "\\n");

        const bookingData = {
            'spaceNo': spaceData.spaceNo,
            'spaceBookCn': escapedBookingContent,
            'date': myCalendar.value,
            'beginTime': DateTimeConverter.convertTimeArrToString([bookingBeginTimeEl.value,0]),
            'endTime': DateTimeConverter.convertTimeArrToString([bookingEndTimeEl.value,0]),
        }

        if(!validateBooking(bookingData)) return false;

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

            const bookingDate = DateTimeConverter.convertPlainDateToDate(date);
            const bookingStartHour = spaceData.startTm[0];
            const bookingFinishHour = spaceData.finishTm[0];

            initTimeslotsSelect(bookingDate, bookingStartHour, bookingFinishHour);
            return true;
        }).catch(error => {
            console.error('Error:', error);
        });
    }

    ////Validation////
    function validateBooking(bookingData) {
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
        } else if(isBookingAfterOwn()){
            alert('본인 예약 직후 1시간 내에 새로운 예약을 할 수 없습니다.');
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
            if (bookingBeginTime <= timeslot.beginTime[0] && timeslot.beginTime[0] < bookingEndTime) {
                return false;
            }
        }
        return true;
    }

    function isBookingAfterOwn(){
        for (const timeslot of timeslots) {
            if(timeslot.selfBook && timeslot.endTime[0] === parseInt(bookingBeginTimeEl.value)){
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
        if (spaceData.weekend === 'Y') {
            weekendYnEl.innerText = '주말 예약 가능';
            weekendYnEl.classList.add('bookable');
        } else {
            weekendYnEl.innerText = '주말 예약 불가';
            weekendYnEl.classList.add('unbookable');
        }

        spaceNameEl.innerText = spaceData.spaceNm;
        maxCapacityEl.innerText = spaceData.maxCapacity + '명';
        maxBookingTimeEl.innerText = spaceData.maxRsvsTms + '시간';
        startTimeEl.innerText = DateTimeConverter.convertTimeArrToString(spaceData.startTm);
        endTimeEl.innerText = DateTimeConverter.convertTimeArrToString(spaceData.finishTm);
    }

    function initInputs(){
        myDatePicker.set('now');
        requestTimeslots(DateTimeConverter.convertDateToPlainDate(new Date()));
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

        if((bookingDate.getDay()===0||bookingDate.getDay()===6)
            && spaceData.weekend === 'N') {
            printOptions(bookingBeginTimeEl, null, null, null);
            printOptions(bookingEndTimeEl, null, null, null);
        } else {
            printOptions(bookingBeginTimeEl, timeslots, bookingStartHour, bookingFinishHour);
            printOptions(bookingEndTimeEl, timeslots, parseInt(bookingBeginTimeEl.value) + 1, parseInt(bookingFinishHour) + 1);
        }
    }

    /** 현재 시간이 예약 시작 시간 이후일 때
     * 현재 이후에 예약 가능하도록 예약 시작 시간 조정 **/
    function adjustBookingStartTime(today, bookingStartTime){
        if(today.getHours() < bookingStartTime) {
            return bookingStartTime;
        }

        return today.getHours() + 1;
    }

    function printOptions(element, timeslots, bookingStartHour, bookingFinishHour) {
        element.innerHTML = "";

        if (!timeslots) {
            element.insertAdjacentHTML('beforeend',
                `<option name='time' value="" disabled selected>주말 예약 불가</option>`);
            return;
        }

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
</script>
</body>
</html>