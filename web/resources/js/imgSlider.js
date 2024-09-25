const slider = {
    showcaseEl: document.getElementById('imgShowcase'),
    selectEl: document.getElementById('imgSelect'),
    init(files) {
        if(!files) return;

        slider.addShowcase(files);
        slider.addSelect(files);

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

    addShowcase(files) {
        slider.showcaseEl.innerHTML = files.map((file, index)=>{
            return `<img alt='공간 사진' src='${imgPath}/${file.rename}'/>`
        }).join('');
    },
    addSelect(files){
        slider.selectEl.innerHTML = files.map((file, index) => {
            return `<div class = 'img-item'>
                            <a href = '#' data-id = '${index+1}'>
                                <img alt='공간 사진' src='${imgPath}/${file.rename}'/>
                            </a>
                        </div>`
        }).join('');
    },
    slideImage(imgId){
        const displayWidth = document.querySelector('.img-showcase img:first-child').clientWidth;

        document.querySelector('.img-showcase').style.transform = `translateX(${- (imgId - 1) * displayWidth}px)`;
    }
}

slider.init(jsonData.files);