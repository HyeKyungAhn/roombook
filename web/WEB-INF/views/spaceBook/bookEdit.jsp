<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-09-12
  Time: 오전 2:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <title>roombook | 공간 예약 수정</title>
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
    const spaceAndBookData = JSON.parse('${spaceAndBookData}');
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
    let bookingBeginTimeOptions = document.querySelector('select[name=bookingBeginTime]').options;
    let bookingEndTimeOptions = document.querySelector('select[name=bookingEndTime]').options;

    const timestampAdding90Days = 90 * 24 * 3600 * 1000;
    const todayDate = reverseSlashDate(convertDateToSlashDate(new Date()));
    const after90DaysDate = reverseSlashDate(convertDateToSlashDate(new Date(Date.now() + timestampAdding90Days)));

    const element = document.getElementById('myCalendar');
    const myDatePicker = new jsCalendar.datepicker({
        target: element,
        navigatorPosition : 'right',
        monthFormat : 'month YYYY',
        language : 'ko',
        date: todayDate,
        min : todayDate,
        max : after90DaysDate,
    });

    ////Initialization////
    function initSpaceData() {
        maxCapacityEl.innerText = spaceAndBookData.maxCapacity + '명';
        maxBookingTimeEl.innerText = spaceAndBookData.maxRsvsTms + '시간';
        startTimeEl.innerText = convertTimeArrToString(spaceAndBookData.startTm);
        endTimeEl.innerText = convertTimeArrToString(spaceAndBookData.finishTm);
        weekendYnEl.innerText = spaceAndBookData.weekend === 'Y' ? '주말 예약 가능' : '주말 예약 불가';
    }

    function initBookingData() {
        bookingContentEl.value = spaceAndBookData.content;
        const slashDate = convertDateArrayToSlashDate(spaceAndBookData.date);
        const reverseDate = reverseSlashDate(slashDate);
        myDatePicker.set(reverseDate);
        changeSelectedOption(bookingBeginTimeEl.name, spaceAndBookData.beginTime[0]);
        changeSelectedOption(bookingEndTimeEl.name, spaceAndBookData.endTime[0]);
    }

    function changeSelectedOption(selectElName, value) {
        const stringValue = value.toString();
        const options = document.querySelector(`select[name=\${selectElName}]`).options;
        for (let option of options) {
            if(option.value === stringValue) {
                option.selected = true; break;
            }
        }
    }

    ////Event Listener////
    myDatePicker.jsCalendar.onDateClick(function(event, date){
        const plainDate = convertDateToPlainDate(date);
        requestTimeslots(plainDate);
    });

    document.addEventListener('DOMContentLoaded', async function () {
        const plainDate = convertDateArrayToPlainDate(spaceAndBookData.date);

        initSpaceData();
        await requestTimeslots(plainDate);
        initBookingData();
    });

    bookingBeginTimeEl.addEventListener('change', function() { //e.target.value로 parseInt(bookingBeginTimeEl.value)대체?
        const bookingFinishHour = spaceAndBookData.finishTm[0];
        printOptions(bookingEndTimeEl, timeslots, parseInt(bookingBeginTimeEl.value)+1, parseInt(bookingFinishHour)+1);
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

    ////Request API////
    bookingBtnEl.addEventListener('click', function() {
        const bookingData = {
            'spaceNo': spaceAndBookData.spaceNo,
            'spaceBookCn': bookingContentEl.value,
            'date': element.value,
            'beginTime': convertTimeArrToString([bookingBeginTimeEl.value,0]),
            'endTime': convertTimeArrToString([bookingEndTimeEl.value,0]),
        }

        if(!validateBookingData(bookingData)) return false;
        let locationURL;

        fetch('<c:url value="${modifyingUrl}"/>', {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(bookingData),
        }).then(response => {
            if(response.ok){
                locationURL = response.headers.get('location');
                return response.text();
            } else {
                throw new Error();
            }
        }).then(data => {
            const jsonData = JSON.parse(data);
            alert(jsonData.errorMessage);

            if (jsonData.result === "SUCCESS") {
                location.href = locationURL;
            }
        }).catch(error => {
            console.error('Error:', error);
        });
    });

    function requestTimeslots(date) {
        return fetch(`<c:url value="${bookedTimeslotsUrl}"/>?date=\${date}`, {
            method: 'GET'
        }).then(response => {
            return response.text();
        }).then(text => {
            timeslots = JSON.parse(text);

            const bookingDate = convertPlainDateToDate(date);
            const bookingStartHour = spaceAndBookData.startTm[0];
            const bookingFinishHour = spaceAndBookData.finishTm[0];

            initTimeslotsSelect(bookingDate, bookingStartHour, bookingFinishHour);
            return true;
        }).catch(error => {
            console.error('Error:', error);
        });
    }

    function convertPlainDateToDate(plainDate) {
        const year = plainDate.substring(0,4)
        const monthIndex = parseInt(plainDate.substring(4, 6)) - 1;
        const day = plainDate.substring(6);

        return new Date(year, monthIndex, day);
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

    function canBookToday(today, bookingFinishHour){
        return today.getHours() < parseInt(bookingFinishHour);
    }

    function getLastBookingPossibleTime(){
        return parseInt(spaceAndBookData.finishTm[0]) - 1;
    }

    function isBookingUnavailable(bookingStartTime){
        return parseInt(bookingStartTime) === -1;
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

    function printOptions(element, timeslots, bookingStartHour, bookingFinishHour) {
        element.innerHTML = "";
        const editingBookingBeginHour = spaceAndBookData.beginTime[0];
        const editingBookingEndHour = spaceAndBookData.endTime[0];

        for(let time = bookingStartHour; time < bookingFinishHour; time++){
            let stringBeginTime = time.toString().padStart(2, '0');
            let isDisabled = false;
            let isSelfBooking = false;
            let isEditingBooking = false;

            for (const element of timeslots) {
                if (element.beginTime[0] <= time && time < element.endTime[0]) {
                    if(editingBookingBeginHour <= time && time < editingBookingEndHour) {
                        isEditingBooking = true;
                    } else {
                        isDisabled = true;
                    }
                    isSelfBooking = element.selfBook;
                    break;
                }
            }

            element.insertAdjacentHTML('beforeend',
                `<option name='time' value="\${time}" \${isEditingBooking ? 'class="bookedTime"' : ''} \${isDisabled?'disabled':''}>\${stringBeginTime}:00 \${isSelfBooking && !isEditingBooking?'(본인예약)':''}</option>`);
        }
    }

    function printBlockOptions(element) {
        element.innerHTML = "";

        element.insertAdjacentHTML('beforeend',
            `<option selected value="예약불가" disabled>예약 불가</option>`);
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

    ////Date/Time Conversion////

    function convertTimeArrToString(timeArr) {
        return `\${timeArr[0].toString().padStart(2, '0')}:\${timeArr[1].toString().padStart(2, '0')}`;
    }

    function convertDateArrayToSlashDate(dateArr) {
        return `\${dateArr[0]}/\${dateArr[1].toString().padStart(2, '0')}/\${dateArr[2].toString().padStart(2, '0')}`;
    }

    function convertDateArrayToPlainDate(dateArr) {
        return `\${dateArr[0]}\${dateArr[1].toString().padStart(2, '0')}\${dateArr[2].toString().padStart(2, '0')}`;
    }

    function convertDateToPlainDate(date) {
        const year = date.getFullYear();
        const month = (date.getMonth()+1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');

        return `\${year}\${month.toString().padStart(2, '0')}\${day.toString().padStart(2, '0')}`;
    }

    function convertDateToSlashDate(date) {
        const year = date.getFullYear();
        const month = (date.getMonth()+1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        return `\${year}/\${month.toString().padStart(2, '0')}/\${day.toString().padStart(2, '0')}`;
    }

    function reverseSlashDate(slashDate) {
        const dateArr = slashDate.split('/');
        return `\${dateArr[2]}/\${dateArr[1]}/\${dateArr[0]}`;
    }

    function getPlainDateFromSlashDate(slashDate) {
        const dateArr = slashDate.split('');
        return `\${dateArr[0]}\${dateArr[1]}\${dateArr[2]}`;
    }
</script>
</body>
</html>
