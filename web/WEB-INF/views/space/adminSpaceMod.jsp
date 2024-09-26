<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-05-24
  Time: 오후 3:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <title>roombook | 공간 수정</title>
    <script src="https://cdn.jsdelivr.net/npm/@yaireo/tagify"></script>
    <script src="https://cdn.jsdelivr.net/npm/@yaireo/tagify/dist/tagify.polyfills.min.js"></script>
    <link href="https://cdn.jsdelivr.net/npm/@yaireo/tagify/dist/tagify.css" rel="stylesheet" type="text/css" />
    <script src="https://unpkg.com/dropzone@5/dist/min/dropzone.min.js"></script>
    <link rel="stylesheet" href="https://unpkg.com/dropzone@5/dist/min/dropzone.min.css" type="text/css"/>
    <link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/space.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tagifyCustom.css"/>
</head>
<body>
    <h1>공간 정보 수정</h1>
    <form action="<c:url value="${modificationRequestUrl}"/>" method="post" id="spaceForm">
        <div>
            <label>
                공간명
                <input type="text" name="spaceNm" id="name" value="">
            </label>
        </div>
        <div>
            <label>
                위치(20자 이내)
                <input type="text" name="spaceLoc" id="location" value="">
            </label>
        </div>
        <div>
            <label>
                공간 설명(100자)
                <textarea name="spaceDesc" id="description"></textarea>
            </label>
        </div>
        <div id="dropzone">
            <div class="dropzone needsclick" id="demo-upload">
                <div class="dz-message needsclick">
                    <span class="text">
                        <img src="http://www.freeiconspng.com/uploads/------------------------------iconpngm--22.png" alt="Camera" />
                            파일 업로드를 위해 클릭 또는 드래그 하세요
                    </span>
                    <span class="plus">+</span>
                </div>
            </div>
        </div>
        <div>
            <label>
                최대 연속 예약 가능 시간(시간 단위)
                <input type="number" name="maxRsvsTms" id="maxTime" value="">
            </label>
        </div>
        <div>
            <label>
                <label for="weekend">공간 주말 이용 가능 여부</label>
                <input type="checkbox" name="weekend" id="weekend">
            </label>
        </div>
        <div>
            <label>
                이용시간
                <input type="time" name="startTm" id="startTime" placeholder="시작시간" value="">
                <span>-</span>
                <input type="time" name="finishTm" id="finishTime" placeholder="종료시간" value="">
            </label>
        </div>
        <div>
            <label>
                최대 수용인원
                <input type="number" name="maxCapacity" id="capacity" value="">
            </label>
        </div>
        <div>
            <p><label for="facilityInputElement">옵션(facility)</label><span id="facilityInfo" class="info"></span></p>
            <input name="spaceFacility" id="facilityInputElement" class="customLook" >
        </div>
        <div>
            <label>
                <label for="hideYn">목록 숨김 여부</label>
                <input type="checkbox" name="hide" id="hideYn">
            </label>
        </div>
        <div>
            <button type="submit" id="saveBtn">저장</button>
            <button type="button" id="cancelBtn">취소</button>
        </div>
    </form>
<script>
    const jsonData = JSON.parse('${jsonSpace}');
    const jsonRescs = jsonData.resources;
    const jsonFiles = jsonData.files;

    const nameEl = document.getElementById('name');
    const locationEl = document.getElementById('location');
    const descriptionEl = document.getElementById('description');
    const maxTimeEl = document.getElementById('maxTime');
    const weekendEl = document.getElementById('weekend');
    const startTimeEl = document.getElementById('startTime');
    const finishTimeEl = document.getElementById('finishTime');
    const capacityEl = document.getElementById('capacity');
    const hideYnEl = document.getElementById('hideYn');
    const cancelBtnEl = document.getElementById('cancelBtn');

    const spaceForm = document.getElementById("spaceForm");

    document.addEventListener('DOMContentLoaded', function() {
        renderSpaceInfo();
    });

    cancelBtnEl.addEventListener('click', function() {
        history.back();
    });

    function renderSpaceInfo(){
        nameEl.value = jsonData.spaceNm;
        locationEl.value = jsonData.spaceLoc;
        descriptionEl.innerText = jsonData.spaceDesc;
        maxTimeEl.value = jsonData.maxRsvsTms;
        weekendEl.value = jsonData.weekend === 'Y';
        startTimeEl.value = `\${jsonData.startTm[0].toString().padStart(2,'0')}:\${jsonData.startTm[1].toString().padStart(2,'0')}`;
        finishTimeEl.value = `\${jsonData.finishTm[0].toString().padStart(2,'0')}:\${jsonData.finishTm[1].toString().padStart(2,'0')}`;
        capacityEl.value = jsonData.maxCapacity;
        hideYnEl.checked = jsonData.hide === 'Y';
    }

    spaceForm.addEventListener('submit', function(e){
        e.preventDefault();

        let formData = new FormData();

        const space = {
            spaceNm: e.target.spaceNm.value,
            spaceLoc: e.target.spaceLoc.value,
            spaceDesc: e.target.spaceDesc.value,
            maxRsvsTms: e.target.maxRsvsTms.value,
            weekend: (e.target.weekend.checked ? 'Y':'N'),
            startTm: e.target.startTm.value,
            finishTm: e.target.finishTm.value,
            maxCapacity: e.target.maxCapacity.value,
            hide: e.target.hide.checked ? 'Y':'N',
        };

        if(!validate(space)) return false;

        formData.append('space', JSON.stringify(space));

        formData.append('spaceFacility', JSON.stringify(tagify.value));
        formData.append('deletedFileNames', JSON.stringify(deletedFileNames));

        dropzone.files.forEach(file => {
            if(file.status!=='error') formData.append('newFiles', file, file.name);
        });

        fetch('<c:url value="${modificationRequestUrl}"/>', {
            method: 'POST',
            body: formData,
        }).then(response => {
            if(response.ok){
                location.href = response.headers.get('location');
            } else {
                return response.text().then(errorData => {
                    throw new Error(errorData);
                });
            }
        }).catch(error => {
            alert(error.message);
        });
    });

    function validate(space){
        if (!space.spaceNm) {
            alert("이름을 입력해주세요.");
            return false;
        } else if (!space.spaceLoc) {
            alert("위치를 입력해주세요");
            return false;
        } else if (!space.spaceDesc) {
            alert("장소 설명을 입력해주세요");
            return false;
        } else if (!space.maxRsvsTms || parseInt(space.maxRsvsTms) <= 0) {
            alert("최대 연속 예약 가능 시간을 입력해주세요");
        } else if(!space.startTm || !space.finishTm){
            alert("유효한 예약 가능 시간을 입력해주세요.")
        } else if (!space.maxCapacity || parseInt(space.maxCapacity) <= 0) {
            alert("최대 수용 인원을 입력해주세요");
            return false;
        }
        return true;
    }
</script>
<script src="${pageContext.request.contextPath}/js/myDropzone.js"></script>
<script src="${pageContext.request.contextPath}/js/myTagify.js"></script>
</body>
</html>
