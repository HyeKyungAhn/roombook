package store.roombook.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("공간")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class PageHandlerTest {

    @Test
    @DisplayName("전체 게시글 수 27, 현재 페이지 3인 페이지네이션 테스트")
    void pageTest(){
        PageHandler handler = new PageHandler(27, 3, 5);
        assertEquals(6, handler.getTotalPage());
        assertTrue(handler.isShowPrev());
        assertTrue(handler.isShowNext());
    }

    @Test
    @DisplayName("전체 게시글 수 27, 현재 페이지 6인 페이지네이션 테스트")
    void pageTest1(){
        PageHandler handler = new PageHandler(27, 1, 5);
        assertEquals(6, handler.getTotalPage());
        assertFalse(handler.isShowPrev());
        assertTrue(handler.isShowNext());
    }

    @Test
    @DisplayName("전체 게시글 수 32, 현재 페이지 5인 페이지네이션 테스트")
    void pageTest2(){
        PageHandler handler = new PageHandler(32, 7);
        assertEquals(7, handler.getTotalPage());
        assertEquals(5, handler.getPageSize());
        assertEquals(30, handler.getOffset());
        assertTrue(handler.isShowPrev());
        assertFalse(handler.isShowNext());
    }

    @Test
    @DisplayName("전체 게시글 수 105 현재 페이지 1, 한 페이지 당 글 갯수 10개 테스트")
    void pageTest3() {
        PageHandler handler = new PageHandler(105, 1, 10);
        assertEquals(11, handler.getTotalPage());
        assertEquals(10, handler.getPageSize());
        assertEquals(0, handler.getOffset());
        assertFalse(handler.isShowPrev());
        assertTrue(handler.isShowNext());
    }

    @Test
    @DisplayName("전체 게시글 수 105 현재 페이지 5, 한 페이지 당 글 갯수 10개 테스트")
    void pageTest4() {
        PageHandler handler = new PageHandler(105, 5, 10);
        assertEquals(11, handler.getTotalPage());
        assertEquals(10, handler.getPageSize());
        assertEquals(40, handler.getOffset());
        assertTrue(handler.isShowPrev());
        assertTrue(handler.isShowNext());
    }
}