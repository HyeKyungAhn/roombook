<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-05-10
  Time: 오후 8:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <title>roombook | 공간 입력</title>
    <script src="https://cdn.jsdelivr.net/npm/@yaireo/tagify"></script>
    <script src="https://cdn.jsdelivr.net/npm/@yaireo/tagify/dist/tagify.polyfills.min.js"></script>
    <link href="https://cdn.jsdelivr.net/npm/@yaireo/tagify/dist/tagify.css" rel="stylesheet" type="text/css" />
    <link href="${pageContext.request.contextPath}/css/tagifyCustom.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <h1>공간 정보 입력</h1>
    <form id="spaceForm">
        <div>
            <label>
                공간명
                <input type="text" name="spaceNm" id="nameInputElement" >
            </label>
        </div>
        <div>
            <label>
                위치(20자 이내)
                <input type="text" name="spaceLoc" id="locationInputElement">
            </label>
        </div>
        <div>
            <label>
                공간 설명(100자)
                <textarea name="spaceDesc" id="descriptionInputElement"></textarea>
            </label>
        </div>
        <div id="fileUploadContainer">
            <div>
                <label>
                    사진 업로드
                    <input type="file" name="files" multiple id="fileInputElement" class="customLook" size="50">
                </label>
                <span class="uploadInfo"></span>
            </div>
            <table id="fileListElement">
                <caption>파일 관련 정보</caption>
                <tr class="fileListHeader">
                    <th>파일 이름</th>
                    <th>파일 크기(bite)</th>
                    <th></th>
                </tr>
            </table>
        </div>
        <div>
            <label>
                최대 연속 예약 가능 시간(시간 단위)
                <input type="number" name="maxRsvsTms" id="maxTimeInputElement">
            </label>
        </div>
        <div>
            <label>
                <label for="weekendInputElement">공간 주말 이용 가능 여부</label>
                <input type="checkbox" name="weekend" id="weekendInputElement">
            </label>
        </div>
        <div>
            <label>
                이용시간
                <input type="time" name="startTm" id="startTmInputElement" placeholder="시작시간"><span>-</span><input type="time" name="finishTm" id="finishTmInputElement" placeholder="종료시간">
            </label>
        </div>
        <div>
            <label>
                최대 수용인원
                <input type="number" name="maxCapacity" id="maxCapacityInputElement">
            </label>
        </div>
        <div>
            <p><label for="facilityInputElement">옵션(facility)</label><span id="facilityInfo" class="info"></span></p>
            <input name="spaceFacility" id="facilityInputElement" class="customLook" >
        </div>
        <div>
            <label>
                <label for="hideInputElement">목록 숨김 여부</label>
                <input type="checkbox" name="hide" id="hideInputElement" value="Y">
            </label>
        </div>
        <div>
            <button type="submit" id="saveBtn">저장</button>
        </div>
    </form>
<script>
    const spaceForm = document.getElementById("spaceForm");
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

        console.log(space.startTm);
        if(!validate(space)) return false;

        formData.append('space', JSON.stringify(space));
        formData.append('spaceFacility', JSON.stringify(tagify.value));

        for(let file of fileUpload.fileStore.values()){
            formData.append('files', file, file.name);
        }

        fetch('<c:url value="${spaceSaveRequestUrl}"/>', {
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
<script src="${pageContext.request.contextPath}/js/fileUploadList.js"></script>
<script src="${pageContext.request.contextPath}/js/myTagify.js"></script>
</body>
</html>
