const slider = {
    showcaseEl: document.getElementById('imgShowcase'),
    selectEl: document.getElementById('imgSelect'),

    init(imgPath, noImgPath, files) {
        if(files && files.length !== 0) {
            slider.addShowcase(imgPath, files);
            slider.addSelect(imgPath, files);
        } else {
            const noImgFileArray = [{'rename': noImgPath}];
            slider.addShowcase(null, noImgFileArray);
            slider.addSelect(null, noImgFileArray);
        }

        const imgs = document.querySelectorAll('.img-select a');
        const imgBtns = [...imgs];
        let imgId = 1;

        imgBtns.forEach((imgItem) => {
            imgItem.addEventListener('click', (event) => {
                event.preventDefault();
                imgId = imgItem.dataset.id;
                slider.slideImage(imgId);
            });
        });

        window.addEventListener('resize', slider.slideImage);
    },
    addShowcase(imgPath, files) {
        slider.showcaseEl.innerHTML = files.map((file, index)=>{
            return `<img alt='공간 사진' src='${imgPath?imgPath+"/":''}${file.rename}'/>`
        }).join('');
    },
    addSelect(imgPath, files){
        slider.selectEl.innerHTML = files.map((file, index) => {
            return `<div class = 'img-item'>
                            <a href = '#' data-id = '${index+1}'>
                                <img alt='공간 사진' src='${imgPath?imgPath+"/":''}${file.rename}'/>
                            </a>
                        </div>`
        }).join('');
    },
    slideImage(imgId){
        const displayWidth = document.querySelector('.img-showcase img:first-child').clientWidth;

        document.querySelector('.img-showcase').style.transform = `translateX(${- (imgId - 1) * displayWidth}px)`;
    }
}

slider.init(imgPath, noImgPath, jsonData.files);