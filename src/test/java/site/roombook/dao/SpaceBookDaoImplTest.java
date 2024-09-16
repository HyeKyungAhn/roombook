package site.roombook.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.CmnCode;
import site.roombook.domain.EmplDto;
import site.roombook.domain.SpaceBookDto;
import site.roombook.domain.SpaceDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ExtendWith({SpringExtension.class})
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/testContext.xml"})
class SpaceBookDaoImplTest {
    @Autowired
    SpaceBookDao spaceBookDao;

    @Autowired
    EmplDao emplDao;

    @Autowired
    SpaceDao spaceDao;


    @Nested
    @DisplayName("예약 추가 테스트")
    class InsertionTest {
        List<EmplDto> twoEmplList;
        EmplDto dummyRscAdminEmpl;
        SpaceDto dummySpace;

        @BeforeEach
        void setup() {
            emplDao.deleteAll();
            spaceDao.deleteAll();
            spaceBookDao.deleteAll();

            twoEmplList = createTwoEmplList();
            for (EmplDto emplDto : twoEmplList) {
                emplDao.insertEmpl(emplDto);
            }


            dummyRscAdminEmpl = getRscAdmin();
            assertEquals(1, emplDao.insertEmpl(dummyRscAdminEmpl));

            dummySpace = new SpaceDto.Builder()
                    .spaceNo(999999)
                    .spaceNm("회의실A")
                    .spaceMaxPsonCnt(10)
                    .spaceLocDesc("2층 정수기 옆")
                    .spaceAdtnDesc("쾌적한 환경의 회의실입니다")
                    .spaceMaxRsvdTms(2)
                    .spaceUsgPosblBgnTm(LocalTime.of(9,0))
                    .spaceUsgPosblEndTm(LocalTime.of(18,0))
                    .spaceWkendUsgPosblYn('N')
                    .spaceHideYn('N')
                    .fstRegDtm(LocalDateTime.now())
                    .fstRegrIdnfNo(dummyRscAdminEmpl.getEmplNo()).build();

            assertEquals(1, spaceDao.insertSpace(dummySpace));


            SpaceBookDto existingSpaceBooking1 = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(0).getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(9, 0))
                    .spaceBookEndTm(LocalTime.of(11, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            SpaceBookDto existingSpaceBooking2 = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(0).getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(14, 0))
                    .spaceBookEndTm(LocalTime.of(16, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.insert(existingSpaceBooking1));
            assertEquals(1, spaceBookDao.insert(existingSpaceBooking2));
        }

        @Test
        @DisplayName("앞 예약과 예약 시간이 겹침")
        void overlappedBookingWithPreviousBooking() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(1).getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(10, 0))
                    .spaceBookEndTm(LocalTime.of(12, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.insert(spaceBookDto));
        }

        @Test
        @DisplayName("뒷 예약과 예약 시간이 겹침")
        void overlappedBookingWithNextBooking() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(1).getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(13, 0))
                    .spaceBookEndTm(LocalTime.of(15, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.insert(spaceBookDto));
        }

        @Test
        @DisplayName("다른 사원의 예약과 앞 예약 시간이 맞닿아있음")
        void backToBackTimeSlotsWithPreviousBooking() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(1).getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(11, 0))
                    .spaceBookEndTm(LocalTime.of(13, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.insert(spaceBookDto));
        }

        @Test
        @DisplayName("다른 사원의 예약과 뒷 예약 시간이 맞닿아있음")
        void backToBackTimeSlotsWithNextBooking() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(1).getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(12, 0))
                    .spaceBookEndTm(LocalTime.of(14, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.insert(spaceBookDto));
        }

        @Test
        @DisplayName("예약 가능 시작시간 이전에 예약")
        void bookingBeforeAvailableTimeSlot() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(1).getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(8, 0))
                    .spaceBookEndTm(LocalTime.of(12, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.insert(spaceBookDto));
        }

        @Test
        @DisplayName("예약 가능 시작시간 이후에 예약")
        void bookingAfterAvailableTimeSlot() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(1).getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(17, 0))
                    .spaceBookEndTm(LocalTime.of(19, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.insert(spaceBookDto));
        }

        @Test
        @DisplayName("주말예약 불가인데 주말예약 시")
        void bookingUnavailableWeekendTimeSlot() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(1).getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 12))
                    .spaceBookBgnTm(LocalTime.of(13, 0))
                    .spaceBookEndTm(LocalTime.of(15, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.insert(spaceBookDto));
        }

        @Test
        @DisplayName("최대 연속 가능한 시간 초과")
        void exceedMaxBookingTimes() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(1).getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 11))
                    .spaceBookBgnTm(LocalTime.of(13, 0))
                    .spaceBookEndTm(LocalTime.of(18, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.insert(spaceBookDto));
        }

        @Test
        @DisplayName("관리자일 때 최대 연속 가능한 시간 초과 가능")
        void adminExceedBookingTimes(){
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(1).getEmplId())
                    .emplRole("ROLE_RSC_ADMIN")
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 11))
                    .spaceBookBgnTm(LocalTime.of(13, 0))
                    .spaceBookEndTm(LocalTime.of(18, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.insert(spaceBookDto));
        }

        @Test
        @DisplayName("관리자일 때 3개월 이후 예약 가능")
        void testAvailabilityThreeMonthLaterBookingOnAdmin() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(1).getEmplId())
                    .emplRole("ROLE_RSC_ADMIN")
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2025, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(13, 0))
                    .spaceBookEndTm(LocalTime.of(14, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.insert(spaceBookDto));
        }

        @Test
        @DisplayName("관리자일 때 연속 예약 가능")
        void continuousBookingAvailableToAdmin() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(1).getEmplId())
                    .emplRole("ROLE_RSC_ADMIN")
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 11))
                    .spaceBookBgnTm(LocalTime.of(13, 0))
                    .spaceBookEndTm(LocalTime.of(14, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            SpaceBookDto spaceBookDto2 = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(1).getEmplId())
                    .emplRole("ROLE_RSC_ADMIN")
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 11))
                    .spaceBookBgnTm(LocalTime.of(14, 0))
                    .spaceBookEndTm(LocalTime.of(15, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.insert(spaceBookDto));
            assertEquals(1, spaceBookDao.insert(spaceBookDto2));
        }

        @Test
        @DisplayName("관리자일 때 주말 예약 가능")
        void testAdminBookingWeekend() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(1).getEmplId())
                    .emplRole("ROLE_RSC_ADMIN")
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2025, 10, 12))
                    .spaceBookBgnTm(LocalTime.of(13, 0))
                    .spaceBookEndTm(LocalTime.of(14, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.insert(spaceBookDto));
        }

        @Test
        @DisplayName("오늘 이전 예약")
        void outdatedBooking() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(1).getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(13, 0))
                    .spaceBookEndTm(LocalTime.of(15, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.insert(spaceBookDto));
        }

        @Test
        @DisplayName("직전 예약이 본인 예약")
        void testBookingRightAfterOwn() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(0).getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(11, 0))
                    .spaceBookEndTm(LocalTime.of(13, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.insert(spaceBookDto));
        }

        @Test
        @DisplayName("직후 예약이 본인 예약")
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void testBookingRightBeforeOwn() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(0).getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(12, 0))
                    .spaceBookEndTm(LocalTime.of(14, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.insert(spaceBookDto));
        }

        @Test
        @DisplayName("예약 성공")
        void success() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(UUID.randomUUID().toString())
                    .emplId(twoEmplList.get(1).getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(16, 0))
                    .spaceBookEndTm(LocalTime.of(17, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.insert(spaceBookDto));
        }
    }

    @Nested
    @DisplayName("예약 수정 테스트")
    class UpdateTest {
        EmplDto dummyEmpl;
        EmplDto dummyRscAdminEmpl;
        SpaceDto dummySpace;
        String firstBookingId;
        String secondBookingId;
        String thirdBookingId;

        @BeforeEach
        void setup() {
            emplDao.deleteAll();
            spaceDao.deleteAll();
            spaceBookDao.deleteAll();

            dummyEmpl = EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("science").pwd("science").email("science@asdf.com")
                    .pwdErrTms(0).rnm("science").engNm("science").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                    .wncomTelno("6543321").empno(584393).msgrId(null).prfPhotoPath(null)
                    .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();

            emplDao.insertEmpl(dummyEmpl);

            dummyRscAdminEmpl = getRscAdmin();
            assertEquals(1, emplDao.insertEmpl(dummyRscAdminEmpl));

            dummySpace = new SpaceDto.Builder()
                    .spaceNo(999999)
                    .spaceNm("회의실A")
                    .spaceMaxPsonCnt(10)
                    .spaceLocDesc("2층 정수기 옆")
                    .spaceAdtnDesc("쾌적한 환경의 회의실입니다")
                    .spaceMaxRsvdTms(2)
                    .spaceUsgPosblBgnTm(LocalTime.of(9,0))
                    .spaceUsgPosblEndTm(LocalTime.of(18,0))
                    .spaceWkendUsgPosblYn('N')
                    .spaceHideYn('N')
                    .fstRegDtm(LocalDateTime.now())
                    .fstRegrIdnfNo(dummyRscAdminEmpl.getEmplNo()).build();

            assertEquals(1, spaceDao.insertSpace(dummySpace));


            firstBookingId = UUID.randomUUID().toString();
            secondBookingId = UUID.randomUUID().toString();
            thirdBookingId = UUID.randomUUID().toString();
            SpaceBookDto existingSpaceBooking1 = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(firstBookingId)
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(11, 0))
                    .spaceBookEndTm(LocalTime.of(13, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            SpaceBookDto existingSpaceBooking2 = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(secondBookingId)
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(14, 0))
                    .spaceBookEndTm(LocalTime.of(16, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            SpaceBookDto existingSpaceBooking3 = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(thirdBookingId)
                    .emplId(dummyRscAdminEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 15))
                    .spaceBookBgnTm(LocalTime.of(12, 0))
                    .spaceBookEndTm(LocalTime.of(14, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.insert(existingSpaceBooking1));
            assertEquals(1, spaceBookDao.insert(existingSpaceBooking2));
            assertEquals(1, spaceBookDao.insert(existingSpaceBooking3));
        }

        @Test
        @DisplayName("앞 예약과 예약 시간이 겹침")
        void overlappedBookingWithPreviousBooking() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(firstBookingId)
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 15))
                    .spaceBookBgnTm(LocalTime.of(13, 0))
                    .spaceBookEndTm(LocalTime.of(15, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.updateTimeslot(spaceBookDto));
        }

        @Test
        @DisplayName("뒷 예약과 예약 시간이 겹침")
        void overlappedBookingWithNextBooking() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(firstBookingId)
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 15))
                    .spaceBookBgnTm(LocalTime.of(11, 0))
                    .spaceBookEndTm(LocalTime.of(13, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.updateTimeslot(spaceBookDto));
        }

        @Test
        @DisplayName("다른 사원의 예약과 앞 예약 시간이 맞닿아있음")
        void backToBackTimeSlotsWithPreviousBooking() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(firstBookingId)
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 15))
                    .spaceBookBgnTm(LocalTime.of(14, 0))
                    .spaceBookEndTm(LocalTime.of(16, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.updateTimeslot(spaceBookDto));
        }

        @Test
        @DisplayName("다른 사원의 예약과 뒷 예약 시간이 맞닿아있음")
        void backToBackTimeSlotsWithNextBooking() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(firstBookingId)
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 15))
                    .spaceBookBgnTm(LocalTime.of(10, 0))
                    .spaceBookEndTm(LocalTime.of(12, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.updateTimeslot(spaceBookDto));
        }

        @Test
        @DisplayName("예약 가능 시작시간 이전으로 예약 수정")
        void bookingBeforeAvailableTimeSlot() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(firstBookingId)
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(8, 0))
                    .spaceBookEndTm(LocalTime.of(12, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.updateTimeslot(spaceBookDto));
        }

        @Test
        @DisplayName("예약 가능 시작시간 이후에 예약")
        void bookingAfterAvailableTimeSlot() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(firstBookingId)
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(17, 0))
                    .spaceBookEndTm(LocalTime.of(19, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.updateTimeslot(spaceBookDto));
        }

        @Test
        @DisplayName("주말예약 불가인데 주말예약 시")
        void bookingUnavailableWeekendTimeSlot() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(firstBookingId)
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 12))
                    .spaceBookBgnTm(LocalTime.of(13, 0))
                    .spaceBookEndTm(LocalTime.of(15, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.updateTimeslot(spaceBookDto));
        }

        @Test
        @DisplayName("최대 연속 가능한 시간 초과")
        void exceedMaxBookingTimes() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(firstBookingId)
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 11))
                    .spaceBookBgnTm(LocalTime.of(9, 0))
                    .spaceBookEndTm(LocalTime.of(17, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.updateTimeslot(spaceBookDto));
        }

        @Test
        @DisplayName("관리자일 때 최대 연속 가능한 시간 초과 가능")
        void adminExceedBookingTimes(){
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(secondBookingId)
                    .emplId(dummyRscAdminEmpl.getEmplId())
                    .emplRole(dummyRscAdminEmpl.getEmplAuthNm())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 11))
                    .spaceBookBgnTm(LocalTime.of(9, 0))
                    .spaceBookEndTm(LocalTime.of(18, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.updateTimeslot(spaceBookDto));
        }

        @Test
        @DisplayName("관리자일 때 3개월 이후 예약 가능")
        void testAvailabilityThreeMonthLaterBookingOnAdmin() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(secondBookingId)
                    .emplId(dummyRscAdminEmpl.getEmplId())
                    .emplRole(dummyRscAdminEmpl.getEmplAuthNm())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2025, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(13, 0))
                    .spaceBookEndTm(LocalTime.of(14, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.updateTimeslot(spaceBookDto));
        }

        @Test
        @DisplayName("관리자일 때 연속 예약 가능")
        void continuousBookingAvailableToAdmin() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(secondBookingId)
                    .emplId(dummyRscAdminEmpl.getEmplId())
                    .emplRole(dummyRscAdminEmpl.getEmplAuthNm())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 15))
                    .spaceBookBgnTm(LocalTime.of(14, 0))
                    .spaceBookEndTm(LocalTime.of(15, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.updateTimeslot(spaceBookDto));
        }

        @Test
        @DisplayName("관리자일 때 주말 예약으로 수정 가능")
        void testAdminBookingWeekend() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(secondBookingId)
                    .emplId(dummyRscAdminEmpl.getEmplId())
                    .emplRole(dummyRscAdminEmpl.getEmplAuthNm())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 12))
                    .spaceBookBgnTm(LocalTime.of(13, 0))
                    .spaceBookEndTm(LocalTime.of(14, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.updateTimeslot(spaceBookDto));
        }

        @Test
        @DisplayName("오늘 이전으로 예약 수정")
        void outdatedBooking() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(firstBookingId)
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 6, 20))
                    .spaceBookBgnTm(LocalTime.of(13, 0))
                    .spaceBookEndTm(LocalTime.of(15, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.updateTimeslot(spaceBookDto));
        }

        @Test
        @DisplayName("직전 예약이 본인 예약")
        void testBookingRightAfterOwn() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(firstBookingId)
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(16, 0))
                    .spaceBookEndTm(LocalTime.of(17, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.updateTimeslot(spaceBookDto));
        }

        @Test
        @DisplayName("직후 예약이 본인 예약")
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void testBookingRightBeforeOwn() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(firstBookingId)
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(12, 0))
                    .spaceBookEndTm(LocalTime.of(14, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(0, spaceBookDao.updateTimeslot(spaceBookDto));
        }

        @Test
        @DisplayName("예약 수정 성공")
        void success() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(firstBookingId)
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(9, 0))
                    .spaceBookEndTm(LocalTime.of(11, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.updateTimeslot(spaceBookDto));
        }
    }

    @Nested
    class UpdateTimeslotStatusTest {
        EmplDto dummyEmpl;
        EmplDto dummyRscAdminEmpl;
        SpaceDto dummySpace;
        String spaceBookId;

        @BeforeEach
        void setup() {
            emplDao.deleteAll();
            spaceDao.deleteAll();
            spaceBookDao.deleteAll();

            dummyEmpl = EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("science").pwd("science").email("science@asdf.com")
                    .pwdErrTms(0).rnm("science").engNm("science").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                    .wncomTelno("6543321").empno(584393).msgrId(null).prfPhotoPath(null)
                    .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();
            emplDao.insertEmpl(dummyEmpl);

            dummyRscAdminEmpl = getRscAdmin();
            assertEquals(1, emplDao.insertEmpl(dummyRscAdminEmpl));

            dummySpace = new SpaceDto.Builder()
                    .spaceNo(999999)
                    .spaceNm("회의실A")
                    .spaceMaxPsonCnt(10)
                    .spaceLocDesc("2층 정수기 옆")
                    .spaceAdtnDesc("쾌적한 환경의 회의실입니다")
                    .spaceMaxRsvdTms(2)
                    .spaceUsgPosblBgnTm(LocalTime.of(9,0))
                    .spaceUsgPosblEndTm(LocalTime.of(18,0))
                    .spaceWkendUsgPosblYn('N')
                    .spaceHideYn('N')
                    .fstRegDtm(LocalDateTime.now())
                    .fstRegrIdnfNo("admin").build();

            assertEquals(1, spaceDao.insertSpace(dummySpace));

            spaceBookId = UUID.randomUUID().toString();
            SpaceBookDto existingSpaceBooking1 = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(spaceBookId)
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookSpaceNo(dummySpace.getSpaceNo())
                    .spaceBookDate(LocalDate.of(2024, 10, 10))
                    .spaceBookBgnTm(LocalTime.of(9, 0))
                    .spaceBookEndTm(LocalTime.of(11, 0))
                    .spaceBookCn("회의")
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                    .fstRegDtm(LocalDateTime.now())
                    .lastUpdDtm(LocalDateTime.now())
                    .build();

            assertEquals(1, spaceBookDao.insert(existingSpaceBooking1));
        }

        @Test
        @DisplayName("예약 취소")
        void test() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_CANCEL.getCode())
                    .lastUpdDtm(LocalDateTime.now())
                    .spaceBookId(spaceBookId)
                    .build();

            assertEquals(1, spaceBookDao.updateTimeslotStatus(spaceBookDto));
        }
    }

    @Nested
    @DisplayName("조회 테스트")
    class SelectionTest {
        EmplDto dummyEmpl;
        List<String> dummySpaceBookIdList;


        @BeforeEach
        void setup() {
            emplDao.deleteAll();
            spaceDao.deleteAll();
            spaceBookDao.deleteAll();

            dummyEmpl = EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("science").pwd("science").email("science@asdf.com")
                    .pwdErrTms(0).rnm("science").engNm("science").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                    .wncomTelno("6543321").empno(584393).msgrId(null).prfPhotoPath(null)
                    .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();
            emplDao.insertEmpl(dummyEmpl);


            EmplDto dummyRscAdminEmpl = getRscAdmin();
            assertEquals(1, emplDao.insertEmpl(dummyRscAdminEmpl));

            SpaceDto dummySpace = new SpaceDto.Builder()
                    .spaceNo(999999)
                    .spaceNm("회의실A")
                    .spaceMaxPsonCnt(10)
                    .spaceLocDesc("2층 정수기 옆")
                    .spaceAdtnDesc("쾌적한 환경의 회의실입니다")
                    .spaceMaxRsvdTms(2)
                    .spaceUsgPosblBgnTm(LocalTime.of(9,0))
                    .spaceUsgPosblEndTm(LocalTime.of(18,0))
                    .spaceWkendUsgPosblYn('Y')
                    .spaceHideYn('N')
                    .fstRegDtm(LocalDateTime.now())
                    .fstRegrIdnfNo(dummyRscAdminEmpl.getEmplNo()).build();

            assertEquals(1, spaceDao.insertSpace(dummySpace));

            List<String> idList = new ArrayList<>();

            for (int i = 1; i < 30; i++) {
                String id = UUID.randomUUID().toString();
                SpaceBookDto existingSpaceBooking = SpaceBookDto.spaceBookDtoBuilder()
                        .spaceBookId(id)
                        .emplId(dummyEmpl.getEmplId())
                        .spaceBookSpaceNo(dummySpace.getSpaceNo())
                        .spaceBookDate(LocalDate.of(2024, 11, i))
                        .spaceBookBgnTm(LocalTime.of(9, 0))
                        .spaceBookEndTm(LocalTime.of(11, 0))
                        .spaceBookCn("회의")
                        .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                        .fstRegDtm(LocalDateTime.now())
                        .lastUpdDtm(LocalDateTime.now())
                        .build();

                assertEquals(1, spaceBookDao.insert(existingSpaceBooking));
                idList.add(id);
            }
        }

        @Test
        @DisplayName("한 건 예약 조회")
        void selectOneTimeslot() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(dummySpaceBookIdList.get(0))
                    .emplId(dummyEmpl.getEmplId())
                    .build();

            assertNotNull(spaceBookDao.selectTimeslot(spaceBookDto));
        }

        @Test
        @DisplayName("개인 예약 5건 조회")
        void select5PersonalTimeslots() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                            .emplId(dummyEmpl.getEmplId())
                            .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                            .limit(5)
                            .offset(0)
                            .build();

            List<SpaceBookDto> personalTimeslots = spaceBookDao.selectPersonalTimeslots(spaceBookDto);
            assertEquals(5, personalTimeslots.size());
            assertEquals(29, personalTimeslots.get(0).getSpaceBookDate().getDayOfMonth());
        }

        @Test
        @DisplayName("개인 예약 수 조회")
        void selectPersonalTimeslotsCount() {
            SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                    .emplId(dummyEmpl.getEmplId())
                    .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode()).build();

            assertEquals(29, spaceBookDao.selectPersonalTimeslotsCount(spaceBookDto));
        }
    }

    List<EmplDto> createTwoEmplList() {
        return List.of(
                EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("science").pwd("science").email("science@asdf.com")
                        .pwdErrTms(0).rnm("science").engNm("science").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("6543321").empno(584393).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo(UUID.randomUUID().toString()).emplId("mathematical").pwd("mathematical").email("mathematical@asdf.com")
                        .pwdErrTms(0).rnm("mathematical").engNm("mathematical").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("1111111").empno(385737).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build()
        );
    }

    private EmplDto getRscAdmin() {
        return EmplDto.EmplDtoBuilder().emplNo("8457294").emplId("adminId").pwd("1234").email("testid1@gmail.com")
                .pwdErrTms(0).rnm("감자영").engNm("gamja").entDt("2024-01-01").emplAuthNm("ROLE_RSC_ADMIN").brdt("2000-01-01")
                .wncomTelno("111111").empno(1111).msgrId(null).prfPhotoPath(null)
                .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();
    }
}