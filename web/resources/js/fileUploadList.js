const fileUpload = {
    CLASSES: {
        REMOVE: 'removeFileBtn',
        HEADER: 'fileListHeader',
        INFO: 'uploadInfo',
        RED: 'fontColor_red',
    },
    fileListEl: document.getElementById('fileListElement'),
    fileInputEl: document.getElementById('fileInputElement'),
    fileListHeaderEl: document.getElementsByClassName('fileListHeader')[0],
    uploadInfoEl: document.getElementById('uploadInfoElement'),
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
            `<tr id='${id}' class="fileUploadTableRow"><td>${file.name}</td><td>${file.size}</td><td><button type='button' class='removeFileBtn'>x</button></td></tr>`);
    },
    removeFile(id){
        document.getElementById(id).remove();
        fileUpload.fileStore.delete(id);
    },
    eventHandlers: {
        onFileInput(){
            fileUpload.printInfo(``);
            fileUpload.unColorInfo(fileUpload.CLASSES.RED);

            let duplicatedFileCnt = 0;
            let invalidFileCnt = 0;
            let exceedFileCnt = 0;
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
                } else if(fileUpload.isInvalidFormat(file)){
                    invalidFileCnt++;
                    continue;
                } else if(fileUpload.isExceedCapacity(file)){
                    exceedFileCnt++;
                    continue;
                }

                fileUpload.addFile(file);
                savedFileCnt++;
            }

            if(duplicatedFileCnt) {
                fileUpload.colorInfo(fileUpload.CLASSES.RED);
                fileUpload.printInfo(`중복 파일은 추가할 수 없습니다.`);
            } else if(invalidFileCnt) {
                fileUpload.colorInfo(fileUpload.CLASSES.RED);
                fileUpload.printInfo(`${fileUpload.allowedImageFormat.toString()} 형식만 추가할 수 있습니다.`);
            } else if(exceedFileCnt) {
                fileUpload.colorInfo(fileUpload.CLASSES.RED);
                fileUpload.printInfo('개별 파일 최대 용량은 1MB 입니다.');
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
    generateId() {
        return Date.now().toString(16) + Math.random().toString(16).slice(2, 8);
    },
    printInfo(info) {
        document.getElementsByClassName(fileUpload.CLASSES.INFO)[0].innerText = info;
    },
    colorInfo(color) {
        fileUpload.uploadInfoEl.classList.toggle(color, true);
    },
    unColorInfo(color) {
        fileUpload.uploadInfoEl.classList.toggle(color, false);
    },
    isDuplicated(file) {
        let isDuplicated = false;
        fileUpload.fileStore.forEach((v,k) =>{
            if(v.name === file.name && v.size === file.size) isDuplicated = true;});
        return isDuplicated;
    },
    isInvalidFormat(file) {
        return !this.allowedImageFormat.includes(file.type);
    },
    isExceedCapacity(file) {
        return file.size > 1024 * 1024;
    }
}

fileUpload.initialize();