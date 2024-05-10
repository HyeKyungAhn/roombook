const search = {
    CLASSES: {
        HIDE: 'hide',
        INPUT: 'searchInput',
        MODAL: 'searchResult',
    },
    isModalVisible: false,
    isSearchVisible: false,
    modalEl: document.getElementById("searchResult"),
    inputEl: document.getElementById("searchInput"),
    searchEl: document.getElementById("searchMngr"),
    timeout: null,
    initialize() {
        search.addEvents();
    },
    showSearch(){
        search.searchEl.classList.remove(search.CLASSES.HIDE);
        search.isSearchVisible = true;
    },
    hideSearch(){
        search.searchEl.classList.add(search.CLASSES.HIDE);
        search.isSearchVisible = false;
    },
    showModal() {
        search.modalEl.classList.remove(search.CLASSES.HIDE);
        search.isModalVisible = true;
    },
    hideModal() {
        search.modalEl.classList.add(search.CLASSES.HIDE);
        search.isModalVisible = false;
    },
    addEvents() {
        search.removeEvents();
        search.inputEl.addEventListener('keydown', search.eventHandlers.onKeyDown);
        search.inputEl.addEventListener('click', search.eventHandlers.onInputClick);
        search.modalEl.addEventListener('click', search.eventHandlers.onModalClick);
        window.addEventListener('click', search.eventHandlers.onWindowClick);
    },
    removeEvents(){
        search.inputEl.removeEventListener('keydown', search.eventHandlers.onKeyDown);
        search.inputEl.addEventListener('click', search.eventHandlers.onInputClick);
        search.modalEl.removeEventListener('click', search.eventHandlers.onModalClick);
        window.removeEventListener('click', search.eventHandlers.onWindowClick);
    },
    eventHandlers: {
        onKeyDown() {
            clearTimeout(search.timeout);

            search.timeout = setTimeout(function() {
                console.log(search.inputEl.value);
                search.searchRealTime(search.inputEl.value);
            }, 100); // 100ms 동안 대기
        },
        onModalClick(event) {
            event.stopImmediatePropagation();
            if(event.target.id==='searchResult') return; //empl 정보 사이 간격 클릭 시

            const clickedEmpl = event.target.closest('.searchedEmpl');
            const emplId = clickedEmpl.getAttribute('data-id');
            const emplImg = clickedEmpl.querySelector('img').getAttribute('src');
            const emplNm = clickedEmpl.querySelector('.emplNm').innerText;
            const emplEngNm = clickedEmpl.querySelector('.emplEngNm').innerText;
            const emplEmail = clickedEmpl.querySelector('.emplEmail').innerText;

            profile.modifyProfileEmpl(emplId, emplImg, emplNm, emplEngNm, emplEmail);
            console.log(emplId, emplImg, emplNm, '('+emplEngNm+')', emplEmail);
            search.clearModal();
            search.hideModal();
            search.hideSearch();
            profile.show();
        },
        onWindowClick(event) {
            if(!search.isSearchVisible) {
                return;
            }

            if(event.target !== search.inputEl){
                search.hideModal();
            }
        },
        onInputClick() {
            if(search.inputEl.value.trim()===''){
                search.modalEl.innerHTML = '';
                search.hideModal();
            } else if(!search.isModalVisible){
                search.searchRealTime(search.inputEl.value);
            }
        }
    },
    searchRealTime(keyword) {
        if(keyword.trim()===''){
            search.clearModal();
            search.hideModal();
            return;
        }

        let url = '/dept/memSrch?keyword='+keyword;

        fetch(url, {
            method : 'GET',
            headers: {'Content-Type': 'application/json;charset=utf-8'},
        }).then(response => {
            return response.json();
        }).then(objs => {
            search.addSearchResults(objs);
        }).catch(error => {
            console.error('Error sending data:', error);
        });
    },
    addSearchResults(objs) {
        if(objs.length===0){
            search.clearModal();
            return;
        }
        search.modalEl.innerHTML = objs.map((r, index) => {
            return `<div id='searchedEmpl${index}' class='searchedEmpl emplProfile' data-id='${r.empl_ID}'>
                                <div class='imgContainer'>
                                    <img src=${r.prf_PHOTO_PATH?r.prf_PHOTO_PATH:''} class='emplImg profileImg' alt="프로필 사진"/>
                                </div>
                                <div>
                                    <p class='profileNm'><span class='emplNm'>${r.rnm}</span><span class='emplEngNm'>${r.eng_NM?'('+r.eng_NM+')':''}</span></p>
                                    <p class='emplEmail profileEmail'>${r.email}</p>
                                </div>
                            </div>`
        }).join('');

        search.showModal();
    },
    clearModal(){
        search.modalEl.innerHTML = '';
    }
}

const profile = {
    CLASSES : {
        PHOTO: 'profilePhoto',
        CLOSE: 'closeBtn',
        MANAGER_NAME: 'nm',
        HIDE: 'hide'
    },
    hasMngr: false,
    isVisible: false,
    mngrIdEL: document.getElementById('mngrId'),
    closeBtnEl: document.getElementById('closeBtn'),
    profileEl: document.getElementById('mngrProfile'),
    initialize() {
        profile.addEvents();
        profile.checkMngr();

        if(profile.hasMngr){
            profile.show();
        } else {
            search.showSearch();
        }
    },
    checkMngr(){
        const mngrNm = document.querySelector('.'+profile.CLASSES.MANAGER_NAME).innerText;
        if(mngrNm!==''){
            profile.hasMngr = true;
        }
    },
    show() {
        profile.profileEl.classList.remove(profile.CLASSES.HIDE);
        profile.isVisible = true;
    },
    hide() {
        profile.profileEl.classList.add(profile.CLASSES.HIDE);
        profile.isVisible = false;
    },
    addEvents() {
        profile.removeEvents();
        profile.closeBtnEl.addEventListener('click', profile.eventHandlers.onClose)
    },
    removeEvents(){
        profile.closeBtnEl.removeEventListener('click', profile.eventHandlers.onClose)

    },
    modifyProfileEmpl(id, imgPath, nm, engNm, email){
        profile.modifyMngrId(id);
        profile.profileEl.querySelector('img').setAttribute('src', imgPath);
        profile.profileEl.querySelector('.profileNm>.nm').innerHTML = nm;
        profile.profileEl.querySelector('.profileNm>.engNm').innerHTML = engNm;
        profile.profileEl.querySelector('.profileEmail').innerHTML = email;
    },
    clearMngrId(){
        profile.modifyMngrId('');
    },
    modifyMngrId(value){
        profile.mngrIdEL.value = value;
    },
    eventHandlers: {
        onClose() {
            profile.hide();
            profile.clearMngrId();
            search.showSearch();
        }
    }
}
profile.initialize();
search.initialize();