const fileUpload = {
    CLASSES: {
        REMOVE: 'removeFileBtn',
        HEADER: 'fileListHeader',
        INFO: 'uploadInfo',
    },
    fileListEl: document.getElementById('fileListElement'),
    fileInputEl: document.getElementById('fileInputElement'),
    fileListHeaderEl: document.getElementsByClassName('fileListHeader')[0],
    fileStore: new Map(),
    allowedImageFormat : ['image/png', 'image/jpeg'],
    initialize(){
        fileUpload.addEvents();
    },
    addEvents(){
        fileUpload.removeEvents();
        fileUpload.fileInputEl.addEventListener('input', fileUpload.eventHandlers.onFileInput);
        fileUpload.fileListEl.addEventListener('click', fileUpload.eventHandlers.onListClick);
    },
    removeEvents(){
        fileUpload.fileInputEl.removeEventListener('input', fileUpload.eventHandlers.onFileInput);
        fileUpload.fileListEl.removeEventListener('click', fileUpload.eventHandlers.onListClick);
    },
    addFile(file){
        let id = fileUpload.generateId();
        fileUpload.fileStore.set(id, file);

        fileUpload.fileListHeaderEl.insertAdjacentHTML("afterend",
            `<tr id='${id}'><td>${file.name}</td><td>${file.size}</td><td><button type='button' class='removeFileBtn'>x</button></td></tr>`);
    },
    removeFile(id){
        document.getElementById(id).remove();
        fileUpload.fileStore.delete(id);
    },
    eventHandlers: {
        onFileInput(){
            // fileUpload.fileInputEl.value = null;
            let duplicatedFileCnt = 0;
            let invalidFileCnt = 0;
            let savedFileCnt = 0;
            let newFileCnt = fileUpload.fileInputEl.files.length;

            if (newFileCnt === 0) {
                fileUpload.printInfo("파일을 선택하세요(여러 개 선택 가능)");
                return false;
            }

            for (let i = 0; i < newFileCnt; i++) {
                let file = fileUpload.fileInputEl.files[i];

                if(fileUpload.isDuplicated(file)){
                    duplicatedFileCnt++;
                    continue;
                }

                if(fileUpload.isInvalidFormat(file)){
                    invalidFileCnt++;
                    continue;
                }

                fileUpload.addFile(file);
                savedFileCnt++;
            }

            if(duplicatedFileCnt || invalidFileCnt){
                alert(`${savedFileCnt}개 파일이 추가되었습니다.\n(중복 파일 ${duplicatedFileCnt}개, 업로드 불가 파일 ${invalidFileCnt}개 추가 실패)`);
            }
        },
        onListClick(event){
            event.stopImmediatePropagation();
            if (event.target.classList.contains(fileUpload.CLASSES.REMOVE)) {
                const fileId = event.target.closest('tr').id;
                fileUpload.removeFile(fileId);
            }
        }
    },
    generateId(){
        return Date.now().toString(16) + Math.random().toString(16).slice(2, 8);
    },
    printInfo(info){
        document.getElementsByClassName(fileUpload.CLASSES.INFO)[0].innerText = info;
    },
    isDuplicated(file) {
        let isDuplicated = false;
        fileUpload.fileStore.forEach((v,k) =>{
            if(v.name === file.name && v.size === file.size) isDuplicated = true;});
        return isDuplicated;
    },
    isInvalidFormat(file){
        return !this.allowedImageFormat.includes(file.type);
    }
}

fileUpload.initialize();