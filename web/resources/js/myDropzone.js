Dropzone.autoDiscover = false;

const deletedFileNames = [];
const acceptedContentFiles = ['image/jpg', 'image/jpeg', 'image/png'];

const dropzone = new Dropzone("div.dropzone", {
    url : '#',
    method: 'post',
    autoProcessQueue: false,
    clickable: true, // 클릭 가능 여부
    autoQueue: false, // 드래그 드랍 후 바로 서버로 전송
    createImageThumbnails: true, //파일 업로드 썸네일 생성
    thumbnailHeight: 120, // Upload icon size
    thumbnailWidth: 120, // Upload icon size

    maxFiles: 5, // 업로드 파일수
    maxFilesize: 1, // 최대업로드용량(MB)
    paramName: 'files', // 서버에서 사용할 formdata 이름 설정 (default는 file)
    parallelUploads: 2, // 동시파일업로드 수(이걸 지정한 수 만큼 여러파일을 한번에 넘긴다.)
    uploadMultiple: false, // 다중업로드 기능
    timeout: 300000, //커넥션 타임아웃 설정 -> 데이터가 클 경우 꼭 넉넉히 설정해주자

    addRemoveLinks: true, // 업로드 후 파일 삭제버튼 표시 여부
    dictRemoveFile: '삭제', // 삭제버튼 표시 텍스트
    acceptedFiles: '.jpeg,.jpg,.png,.jfif,.JPEG,.JPG,.PNG,.JFIF', // 이미지 파일 포맷만 허용

    init: function () {
        const files = jsonFiles;

        files.forEach((file) => {
            let callback = null;
            let crossOrigin = null;
            let resizeThumbnail = false;
            this.displayExistingFile(file, '/uploads/thumbnails/'+file.rename, callback, crossOrigin, resizeThumbnail);
        });

        let fileCountFromServer = files.length;
        this.options.maxFiles = this.options.maxFiles - fileCountFromServer;

        this.on('removedfile', function (file){
            if(file.rename) deletedFileNames.push(file.rename);
        });

        this.on('addedfiles', function(files){
            if(hasExceededMaxFileCnt(files)){
                removeFiles(files);
                alert('파일은 최대 5개까지 저장할 수 있습니다.');
                return;
            }

            for (let file of files) {
                if(isDuplicated(file)) {
                    removeFiles(files);
                    alert('중복된 파일입니다.');
                    break;
                } else if(!isValidFile(file)) {
                    removeFiles(files);
                    alert('첨부할 수 없는 파일 형식입니다.');
                    break;

                } else if(hasExceedMaxFileSize(file)){
                    removeFiles(files);
                    alert('첨부 파일 사이즈를 초과하였습니다(최대 사이즈는 1MB입니다.)');
                    break;
                } else {
                    this.emit("complete", file);
                }
            }

        });

        this.on('error', function(file){
            setTimeout(() => this.removeFile(file), 3000);
        });
    },
});

function removeFiles(files){
    for (let file of files) {
        dropzone.removeFile(file);
    }
}

function isDuplicated(file) {
    if (!dropzone.files.length) return;

    let isDuplicated = false;

    for (let i = 0; i < dropzone.files.length - 1; i++) {
        if (
            dropzone.files[i].upload.uuid !== file.upload.uuid &&
            dropzone.files[i].name === file.name &&
            dropzone.files[i].size === file.size &&
            dropzone.files[i].lastModifiedDate.toString() === file.lastModifiedDate.toString()
        ) {
            isDuplicated = true;
        }
    }

    return isDuplicated;
}

function isValidFile(file) {
    return acceptedContentFiles.includes(file.type);
}


function hasExceedMaxFileSize(file) {
    return file.size > 1024 * 1024; //1MB
}

function hasExceededMaxFileCnt(files){
    return dropzone.options.maxFiles < files.length;
}