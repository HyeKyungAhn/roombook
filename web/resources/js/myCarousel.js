let carouselIndex,showSlides1;
// Auto slide play
function slidePlay(a){
    carouselIndex = a;
    showSlides(carouselIndex);
    showSlides1 = setInterval(showSlides,6000);
}
slidePlay(0);

// Next/previous controls
function plusSlides(n) {
    stopAutoSlide();
    slidePlay(carouselIndex);
}

// Thumbnail image controls
function currentSlide(n) {
    stopAutoSlide();
    carouselIndex = n;
    showSlides(carouselIndex);
    showSlides1 = setInterval(showSlides,6000);
}

const carouselItems = document.getElementsByClassName('carouselItem');
const carouselIndicators = document.querySelector('.carousel-indicators li');
const carouselMainTexts = document.getElementsByClassName('mainText');
const carouselSubTests = document.getElementsByClassName('subText');
const carouselBtns = document.getElementsByClassName('carouselBtn');

function showSlides(n) {
    for (const element of carouselItems) {
        element.className = element.className.replace(" activeCarousel", "");
    }

    carouselIndex++;
    if (carouselIndex > carouselItems.length) { //overflow
        carouselIndex = 1 // 처음으로
    }
    if (carouselIndex < 1) { //처음
        carouselIndex = carouselItems.length //마지막으로
    }
    for (const element of carouselIndicators) {
        element.className = element.className.replace(" active", "");
    }

    carouselItems[carouselIndex-1].className += " activeCarousel";
    carouselIndicators[carouselIndex-1].className += " active";

    clearText();
    checkSlide()
}

function stopAutoSlide(){
    if(showSlides1) {
        clearInterval(showSlides1);
    }
}

function clearText(){
    Array.from(carouselMainTexts).forEach((item) => {
        item.classList.remove('activeText');
    });
    Array.from(carouselSubTests).forEach((item) => {
        item.classList.remove('activeText');
    });
    Array.from(carouselBtns).forEach((item) => {
        item.classList.remove('activeText');
    });
}


function checkSlide(){
    const aictiveCarousel = document.querySelector(".activeCarousel");
}