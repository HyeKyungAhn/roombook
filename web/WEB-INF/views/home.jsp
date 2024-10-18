<%--
  Created by IntelliJ IDEA.
  User: hka
  Date: 2024-08-12
  Time: 오후 4:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE>
<html lang="kr">
<head>
    <title>roombook</title>
</head>
<body>
    <div>
        <div id="carouselWrapper" class="carousel">
            <div class="carousel">
                <div class="carouselItem fade">
                    <a class="carouselItemLink" href="${moreSpaceUri}">
                        <img alt="대여 공간 예시 사진" src="/img/aleksandrs-karevs-ZCDA1-cih6o-unsplash-min.jpg"/>
                        <div class="carouselItemText">
                            <div class="mainText">roombook 회의실 예약</div>
                            <div class="subText">공간 예약하러 가기</div>
                        </div>
                    </a>
                </div>
                <div class="carouselItem fade">
                    <a class="carouselItemLink" href="${moreSpaceUri}">
                        <img alt="대여 공간 예시 사진" src="/img/jiran-family-0rqS3f0txBQ-unsplash-min.jpg"/>
                        <div class="carouselItemText">
                            <div class="mainText">roombook 회의실 예약</div>
                            <div class="subText">공간 예약하러 가기</div>
                        </div>
                    </a>
                </div>
                <div class="carouselItem fade">
                    <a class="carouselItemLink" href="${moreSpaceUri}">
                        <img alt="대여 공간 예시 사진" src="/img/m-monk-E813FON0wDQ-unsplash-min.jpg"/>
                        <div class="carouselItemText">
                            <div class="mainText">roombook 회의실 예약</div>
                            <div class="subText">공간 예약하러 가기</div>
                        </div>
                    </a>
                </div>
                <!-- Next and previous buttons-->
                <a id="carouselPrevBtn" class="prev">&#10094;</a>
                <a id="carouselNextBtn" class="next">&#10095;</a>
            </div>
            <!-- The dot/circles-->
            <ol class="carousel-indicators">
                <li data-slide-to="0"></li>
                <li data-slide-to="1"></li>
                <li data-slide-to="2"></li>
            </ol>
        </div>
        <div class="spaceSection">
            <h2 class="sectionTitle">찾는 공간이 있나요?</h2>
            <div id="spaceCollectionGrid" class="spaceCollectionGrid"></div>
            <div class="btnWrapper">
                <a href="${moreSpaceUri}">
                    <span class="moreSpaceBtn roundBtnM2 bg_yellow">더 보기</span>
                </a>
            </div>
        </div>
    </div>
</body>
<script>
    const spaces = JSON.parse('${spaces}');
    const imgPath = '${imgPath}';
    const noImgPath = '${noImgPath}';
    const spaceDetailUri = '${spaceDetailUri}';
    const spaceCollectionGrid = document.getElementById('spaceCollectionGrid');


    document.addEventListener('DOMContentLoaded', function() {
        spaceCollectionGrid.innerHTML = spaces.map((space) => {
            const thumbnailSrc = space.fileName ? `\${imgPath}/\${space.fileName}` : `\${noImgPath}`;
            return `
                <div class="spaceCollectionTile">
                    <a href="\${spaceDetailUri.replace('{spaceNo}', space.spaceNo)}" class="spaceTileLink">
                        <div class="spaceTileImageWrapper">
                            <img class="spaceTileImage" src="\${thumbnailSrc}" alt="공간 사진"/>
                        </div>
                    </a>
                    <div class="spaceTileBriefInfo">
                        <span class="spaceTileCapacity">\${space.maxCapacity}</span>
                        <div class="spaceTileUsgTime">
                            <span class="spaceTileStartTime">\${space.startTm[0].toString().padStart(2,'0')}:00</span><span>-</span class="spaceTileFinishTime">\${space.finishTm[0].toString().padStart(2,'0')}:00<span></span>
                        </div>
                    </div>
                    <a href="\${spaceDetailUri.replace('{spaceNo}', space.spaceNo)}" class="spaceTileName">\${space.spaceNm}</a>
                    <p class="spaceTileDesc">\${space.spaceDesc}</p>
                </div>
            `
        }).join('');

        slidePlay(0);
    });

    let carouselIndex,showSlides1;
    // Auto slide play
    function slidePlay(a){
        carouselIndex = a;
        showSlides(carouselIndex);
        showSlides1 = setInterval(showSlides,5000);
    }


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
    const carouselIndicators = document.querySelectorAll('.carousel-indicators li');
    const carouselMainTexts = document.getElementsByClassName('mainText');
    const carouselSubTests = document.getElementsByClassName('subText');
    const carouselPrevBtn = document.getElementById('carouselPrevBtn');
    const carouselNextBtn = document.getElementById('carouselNextBtn');

    carouselPrevBtn.addEventListener('click', function() {
        plusSlides(-1);
    });

    carouselNextBtn.addEventListener('click', function() {
        plusSlides(1);
    });

    carouselIndicators.forEach((element) => {
       element.addEventListener('click', function(e) {
           currentSlide(e.target.dataset.slideTo);
       });
    });

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
    }

    function checkSlide(){
        const activeCarousel = document.querySelector(".activeCarousel");


        setTimeout(function(){
            activeCarousel.querySelector('.mainText').classList.add('activeText');
        },0);
        setTimeout(function(){
            activeCarousel.querySelector('.subText').classList.add('activeText');
        },0);
    }
</script>
</html>
