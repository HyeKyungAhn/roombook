package site.roombook.domain;

import lombok.Getter;

@Getter
public class PageHandler {
    private final int totalCnt; // 총 게시물 갯수
    private final int pageSize; // 한 페이지 크기
    private static final int naviSize = 5;
    private final int offset;
    private final int totalPage; //전체 페이지 갯수
    private final int currentPage;

    private final boolean showPrev; //이전 페이지로 이동하는 링크 보여줄 것인지 여부
    private final boolean showNext;

    public PageHandler(int totalCnt, int currentPage){
        this(totalCnt, currentPage, 5);
    }

    public PageHandler(int totalCnt, int currentPage, int pageSize) {
        this.totalCnt = totalCnt;
        this.currentPage = currentPage;
        this.pageSize = pageSize;

        totalPage = (int)Math.ceil((double) totalCnt/pageSize);
        offset = (currentPage-1)*5;
        showPrev = currentPage != 1;
        showNext = currentPage != totalPage;
    }

}
