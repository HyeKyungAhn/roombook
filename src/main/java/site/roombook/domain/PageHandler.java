package site.roombook.domain;

import lombok.Getter;

@Getter
public class PageHandler {
    private final int totalCnt; // 총 게시물 갯수
    private final int pageSize; // 한 페이지 크기
    private final int naviSize;
    private final int offset; //네비게이션 시작
    private final int totalPage; //전체 페이지 갯수
    private final int currentPage;

    private final boolean showPrev; //이전 페이지로 이동하는 링크 보여줄 것인지 여부
    private final boolean showNext;

    public PageHandler(int totalCnt, int currentPage){
        this(totalCnt, currentPage, 5);
    }

    public PageHandler(int totalCnt, int currentPage, int pageSize) {
        this.totalCnt = totalCnt;
        this.currentPage = Math.max(currentPage, 1);
        this.pageSize = pageSize;
        this.naviSize = 5;

        totalPage = (int)Math.ceil((double) totalCnt/pageSize);
        int tempOffset = (this.currentPage-1) * pageSize;
        offset = totalCnt < tempOffset ? 0 : tempOffset;
        showPrev = this.currentPage != 1;
        showNext = this.currentPage != totalPage;
    }

}
