const MAX_TAG_NUM = 10;
const tagifyInput = document.querySelector('input[name=spaceFacility]');
const infoEl = document.getElementById("facilityInfo");

const myTagify = {
    controller: '',
    timeout: null,
    hasFacilityInfo: false,
    clickDebounce: '',
    init(){
        if(jsonRescs!==''){
            tagify.addTags(jsonData.resources);
        }
    }
    ,
    onClickHandler(e){
        const {tag:tagElm, data:tagData} = e.detail;

        clearTimeout(myTagify.clickDebounce);
        myTagify.clickDebounce = setTimeout(() => {
            tagData.color = myTagify.getRandomColor();
            tagData.style = "--tag-bg:" + tagData.color;
            tagify.replaceTag(tagElm, tagData);
        }, 200);
    },

    ondblclickHandler(e){
        clearTimeout(myTagify.clickDebounce);
        // when ㅇouble clicking, do not change the color of the tag
    },

    onChange(e){
        if(myTagify.hasFacilityInfo){
            myTagify.printFacilityInfo('');
            myTagify.hasFacilityInfo = false;
        }
    },

    onInvalidTag(e){
        myTagify.printFacilityInfo(e.detail.message);
    },

    printFacilityInfo(text){
        myTagify.infoEl.innerText = text;
        myTagify.hasFacilityInfo = true;
    },

    onKeyDown(e) {
        clearTimeout(myTagify.timeout);

        myTagify.timeout = setTimeout(function() {
            myTagify.onInput(e.detail.value);
        }, 500); // 100ms 동안 대기
    },

    onInput(keyword){
        if(keyword === '') return false;

        tagify.whitelist = null;

        myTagify.controller && myTagify.controller.abort();
        myTagify.controller = new AbortController();

        //show loading animation
        tagify.loading(true);

        fetch('/spaces/rescs?keyword='+keyword, {
            signal: myTagify.controller.signal,
            headers: {'Content-Type': 'application/json'},
        }).then(RES => RES.json()
        ).then(function(newWhitelist){
            // newWhitelist.forEach(tag => tag.isNew=false);
            tagify.whitelist = newWhitelist;
            tagify.loading(false).dropdown.show(keyword)
        }).catch(err => tagify.dropdown.hide())
    },

    getRandomColor(){
        function rand(min, max) {
            return min + Math.random() * (max - min);
        }

        let h = rand(1, 360)|0,
            s = rand(40, 70)|0,
            l = rand(65, 72)|0;

        return 'hsl(' + h + ',' + s + '%,' + l + '%)';
    },

    transformTag( tagData ){
        tagData.color = myTagify.getRandomColor();
        tagData.style = "--tag-bg:" + tagData.color;

        if( tagData.value.toLowerCase() === 'shit' )
            tagData.value = 's✲✲t'
    }
}

const tagify = new Tagify(tagifyInput, {
    pattern: /^.{0,20}$/,
    trim: true,
    whitelist:[],
    transformTag: myTagify.transformTag,
    editTags: false,
    placeholder: '옵션을 입력하세요(최대 10개까지)',
    maxTags: MAX_TAG_NUM,
    texts: {
        empty      : '값을 입력해주세요.',
        exceed     : MAX_TAG_NUM+'개 이상의 태그를 입력할 수 없습니다.',
        pattern    : '유효하지 않은 입력입니다.',
        duplicate  : "이미 존재하는 옵션입니다.",
        notAllowed : "허용되지 않는 입력입니다."
    },
    dropdown : {
        enabled: 1,
        fuzzySearch: false,
        caseSensitive: true,
    },
});

tagify.on('input', myTagify.onKeyDown);
tagify.on('invalid', myTagify.onInvalidTag);
tagify.on('change', myTagify.onChange);
tagify.on('add', myTagify.onAddHandler);
tagify.on('click', myTagify.onClickHandler);
tagify.on('dblclick', myTagify.ondblclickHandler);

myTagify.init();

