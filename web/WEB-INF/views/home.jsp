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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/globalStyle.css"/>
</head>
<body>
    <div>
        <div class="banner"></div>
        <div class="spaceSection">
            <div id="spaceCollectionGrid" class="spaceCollectionGrid"></div>
            <div>
                <div class="moreSpaceBtn">
                    <a href="${moreSpaceUri}">더 보기</a>
                </div>
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
                    <div class="">
                        <span class="spaceTileCapacity">\${space.maxCapacity}</span>
                        <div class="spaceTileUsgTime">
                            <span class="spaceTileTime"></span><span class="spaceTileStartTime">\${space.startTm[0].toString().padStart(2,'0')}:00</span><span>-</span class="spaceTileFinishTime">\${space.finishTm[0].toString().padStart(2,'0')}:00<span></span>
                        </div>
                    </div>
                    <p class="spaceTileName">\${space.spaceNm}</p>
                    <p class="spaceTileDesc">\${space.spaceDesc}</p>
                </div>
            `
        }).join('');
    });
</script>
</html>
