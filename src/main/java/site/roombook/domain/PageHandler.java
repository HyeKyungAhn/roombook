package site.roombook.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PageHandler {
    private int totalCnt; // 총 게시물 갯수
    private int pageSize; // 한 페이지 크기
    private int naviSize;
    private int offset; //네비게이션 시작
    private int totalPage; //전체 페이지 갯수
    private int currentPage;

    private boolean showPrev; //이전 페이지로 이동하는 링크 보여줄 것인지 여부
    private boolean showNext;

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
