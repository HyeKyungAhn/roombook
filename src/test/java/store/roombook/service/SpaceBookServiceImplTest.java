package store.roombook.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import store.roombook.CmnCode;
import store.roombook.dao.SpaceBookDao;
import store.roombook.domain.SpaceBookDto;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/testContext.xml"})
class SpaceBookServiceImplTest {

    @Autowired
    @InjectMocks
    private SpaceBookService spaceBookService;

    @Mock
    private SpaceBookDao spaceBookDao;

    @Nested
    class TimeSlotTest {
        @BeforeEach
        void setup(){
            MockitoAnnotations.openMocks(this);
        }

        @Test
        @WithMockUser(roles = "USER")
        void test() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookSpaceNo(123123)
                    .spaceBookDate(LocalDate.of(2024,9,10))
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .build();

            when(spaceBookDao.selectTimeSlotsAtTheDate(spaceBookDto))
                    .thenReturn(List.of(SpaceBookDto.spaceBookDtoBuilder().emplId("user").build()));

            assertNotNull(spaceBookService.getBookedTimeslotsOfTheDay(123123, LocalDate.of(2024,9,10), "user"));
        }
    }

}