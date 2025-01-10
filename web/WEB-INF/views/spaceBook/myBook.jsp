<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-09-13
  Time: 오후 10:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE>
<html lang="kr">
<head>
    <title></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/spaceBook.css">
</head>
<body>
    <div class="myBookRoot">
        <div class="myBookHeader">
            <h1>내 예약 목록</h1>
        </div>
        <div id="myBookList" class="myBookList"></div>
        <div id="loadMoreTrigger" class="loadMoreTrigger"></div>
    </div>
<script>
    const myBookListEl = document.getElementById("myBookList");
    let cancelBtns;
    let hasMoreData = true;
    let page = 1;
    let cancelURL;

    async function loadMoreData() {
        if(typeof page !== 'number'){
            alert('새로고침 후 다시 시도해주세요.');
        }

        let response = await fetch(`<c:url value="${timeslotsRequestUri}"/>?page=\${page}`);

        if(response.status === 200) {
            let jsonData = await response.json();

            if(jsonData.hasNext){
                page++;
            } else {
                hasMoreData = false
                observer.unobserve(target);
            }
            appendTimeslots(jsonData);
        } else if(response.status === 204) {
            informNoTimeslots();
            hasMoreData = false
            observer.unobserve(target);
        } else {
            console.log("에러 발생");
        }
    }

    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if(entry.isIntersecting && hasMoreData) {
                loadMoreData();
            }
        });
    });

    const target = document.getElementById('loadMoreTrigger');
    observer.observe(target);

    function informNoTimeslots() {
        myBookListEl.insertAdjacentHTML('beforeend',
            `<div class="marginTop80">
                <div class="textAlignCenter">공간 예약 기록이 없습니다.</div>
                <div class="btnWrapper">
                    <a href="${pageContext.request.contextPath}/spaces" class="btnM3 bg_yellow">공간 구경하러 가기</a>
                </div>
            </div>`
        );
    }

    function appendTimeslots(jsonData) {
        const modificationURL = jsonData.links.find((link) => link.rel === 'modification').href;
        cancelURL = jsonData.links.find((link) => link.rel === 'cancel').href;

        const myBookList = jsonData.bookList.map((item) => {
            modificationURL.replace('{spaceBookId}', item.bookId);
            const outdated = isOutdated(item.date, item.beginTime);

            return `<div class="myBookItem \${outdated ? 'outdatedBook' : ''}">
                <div class="myBookContent">
                    <div class="dateTimeWrapper">
                        <span class="date">\${item.date[0]}.\${item.date[1].toString().padStart(2, '0')}.\${item.date[2].toString().padStart(2, '0')}\${item.date?'('+getKoreanDayOfTheWeek(item.date)+')':''}</span>
                        <div>
                            <span class="beginTime">\${item.beginTime[0].toString().padStart(2, '0')}:\${item.beginTime[1].toString().padStart(2, '0')}</span>
                            <span>-</span>
                            <span class="endTime">\${item.endTime[0].toString().padStart(2, '0')}:\${item.endTime[1].toString().padStart(2, '0')}</span>
                        </div>
                    </div>
                    <div>
                        <a class="spaceName">\${item.spaceName}</a>
                    </div>
                    <div>
                        <span class="spaceLocation">\${item.location}</span>
                    </div>
                </div>
                <div class="menuDropdown">
                    <span class="dropBtn"></span>
                    <div class="dropdownContent dropdown">
                        <a href="\${modificationURL.replace('{bookId}', item.bookId)}">예약 수정</a>
                        <span id="cancelBooking" data-bookId="\${item.bookId}" class="cancelBooking">예약 취소</span>
                    </div>
                </div>
            </div>`;
        }).join('');

        myBookListEl.insertAdjacentHTML('beforeend', myBookList);
        updateElement();
    }

    function isOutdated(dateArr, beginTimeArr) {
        const now = new Date();
        const bookingDateTime = new Date(dateArr[0], dateArr[1]-1, dateArr[2], beginTimeArr[0], beginTimeArr[1]);
        return now > bookingDateTime;
    }

    function updateElement() {
        cancelBtns = document.getElementsByClassName('cancelBooking');
        for (let btn of cancelBtns) {
            btn.addEventListener('click', requestCancel);
        }
    }

    function requestCancel(e) {
        const bookId = e.target.dataset.bookid;
        const url = cancelURL.replace('{bookId}',bookId);
        fetch(url, {
            method: 'PATCH'
        }).then(response => {
            if (response.ok) {
                return response.text();
            }

            throw Error();
        }).then(data => {
            const jsonData = JSON.parse(data);
            alert(jsonData.errorMessage);

            if (jsonData.result === "SUCCESS") {
                location.reload();
            }
        }).catch(error => {
            console.error('error : ', error);
        });
    }

    myBookListEl.addEventListener('click', function(event) {
        if (event.target.matches('.dropBtn')) {
            hideMenuDropdowns();
            const dropdownContent = event.target.parentElement.querySelector('.dropdownContent');
            showElement(dropdownContent);

            event.stopPropagation();
        }
    });

    function showElement(element) {
        element.classList.toggle('show');
    }

    window.addEventListener('click', function (event) {
        if (!event.target.matches('.dropdownContent')) {
            hideMenuDropdowns();
        }
    });

    function hideMenuDropdowns() {
        const dropdowns = document.getElementsByClassName('dropdownContent');
        for (let i = 0; i < dropdowns.length; i++) {
            let openDropdown = dropdowns[i];
            if (openDropdown.classList.contains('show')) {
                openDropdown.classList.remove('show');
            }
        }
    }

    function getKoreanDayOfTheWeek(dateArr) {
        const day = new Date(dateArr[0], dateArr[1]-1, dateArr[2]).getDay();
        let koreanDayOfTheWeek;
        switch(day) {
            case 0 : koreanDayOfTheWeek = '일'; break;
            case 1 : koreanDayOfTheWeek = '월'; break;
            case 2 : koreanDayOfTheWeek = '화'; break;
            case 3 : koreanDayOfTheWeek = '수'; break;
            case 4 : koreanDayOfTheWeek = '목'; break;
            case 5 : koreanDayOfTheWeek = '금'; break;
            case 6 : koreanDayOfTheWeek = '토'; break;
            default : koreanDayOfTheWeek = '';
        }

        return koreanDayOfTheWeek;
    }
</script>
</body>
</html>
