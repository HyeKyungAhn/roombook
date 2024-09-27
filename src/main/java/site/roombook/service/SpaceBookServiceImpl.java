package site.roombook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.roombook.CmnCode;
import site.roombook.dao.SpaceBookDao;
import site.roombook.domain.EmplDto;
import site.roombook.domain.ServiceResult;
import site.roombook.domain.SpaceBookAndSpaceDto;
import site.roombook.domain.SpaceBookDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SpaceBookServiceImpl implements SpaceBookService{

    @Autowired
    SpaceBookDao spaceBookDao;

    @Override
    public List<SpaceBookDto> getBookedTimeslotsOfTheDay(int spaceNo, LocalDate date, String emplId) {
        SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                .spaceBookSpaceNo(spaceNo)
                .spaceBookDate(date)
                .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                .build();

        List<SpaceBookDto> list = spaceBookDao.selectTimeSlotsAtTheDate(spaceBookDto);

        return getListWithSelfBookYn(list, emplId);
    }

    @Override
    public ServiceResult bookTimeslot(SpaceBookDto spaceBookDto, EmplDto emplDto) {
        ServiceResult result = new ServiceResult();
        SpaceBookDto spaceBookDtoWithEmplDetails = SpaceBookDto.spaceBookDtoBuilder()
                .spaceBookId(UUID.randomUUID().toString())
                .spaceBookSpaceNo(spaceBookDto.getSpaceBookSpaceNo())
                .emplId(emplDto.getEmplId())
                .emplRole(emplDto.getEmplAuthNm())
                .spaceBookCn(spaceBookDto.getSpaceBookCn())
                .spaceBookDate(spaceBookDto.getSpaceBookDate())
                .spaceBookBgnTm(spaceBookDto.getSpaceBookBgnTm())
                .spaceBookEndTm(spaceBookDto.getSpaceBookEndTm())
                .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                .fstRegDtm(LocalDateTime.now())
                .lastUpdDtm(LocalDateTime.now())
                .build();

        result.setSuccessful(spaceBookDao.insert(spaceBookDtoWithEmplDetails) == 1);
        return result;
    }

    @Override
    public SpaceBookAndSpaceDto getTimeslot(String spaceBookId, String emplId) {
        SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                .spaceBookId(spaceBookId)
                .emplId(emplId)
                .build();

        return spaceBookDao.selectTimeslot(spaceBookDto);
    }

    @Override
    public List<SpaceBookDto> getPersonalTimeslots(String emplId, int offset, int limit) {
        SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                .emplId(emplId)
                .offset(offset)
                .limit(limit)
                .build();

        return spaceBookDao.selectPersonalTimeslots(spaceBookDto);
    }

    @Override
    public int getPersonalTimeslotsCount(String emplId) {
        SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                .emplId(emplId)
                .build();

        return spaceBookDao.selectPersonalTimeslotsCount(spaceBookDto);
    }

    @Override
    public ServiceResult modifyBooking(SpaceBookDto inputs, String bookId, EmplDto emplDto) {
        ServiceResult result = new ServiceResult();

        SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                .spaceBookId(bookId)
                .emplId(emplDto.getEmplId())
                .emplRole(emplDto.getEmplAuthNm())
                .spaceBookSpaceNo(inputs.getSpaceBookSpaceNo())
                .spaceBookDate(inputs.getSpaceBookDate())
                .spaceBookBgnTm(inputs.getSpaceBookBgnTm())
                .spaceBookEndTm(inputs.getSpaceBookEndTm())
                .spaceBookCn(inputs.getSpaceBookCn())
                .lastUpdDtm(LocalDateTime.now())
                .spaceBookStusCd(CmnCode.SPACE_BOOK_COMPLETE.getCode())
                .build();

        result.setSuccessful(spaceBookDao.updateTimeslot(spaceBookDto) == 1);

        return result;
    }

    @Override
    public ServiceResult cancelBooking(EmplDto emplDto, String bookId) {
        ServiceResult result = new ServiceResult();
        SpaceBookDto spaceBookDto = SpaceBookDto.spaceBookDtoBuilder()
                .emplId(emplDto.getEmplId())
                .spaceBookStusCd(CmnCode.SPACE_BOOK_CANCEL.getCode())
                .lastUpdDtm(LocalDateTime.now())
                .emplRole(emplDto.getEmplAuthNm())
                .spaceBookId(bookId)
                .build();

        result.setSuccessful(spaceBookDao.updateTimeslotStatus(spaceBookDto) == 1);
        return result;
    }


    private List<SpaceBookDto> getListWithSelfBookYn(List<SpaceBookDto> list, String emplId){
        List<SpaceBookDto> newList = new ArrayList<>();

        for (SpaceBookDto spaceBookDto : list) {
            newList.add(SpaceBookDto.spaceBookDtoBuilder()
                    .spaceBookId(spaceBookDto.getSpaceBookId())
                    .spaceBookEmplNo(spaceBookDto.getSpaceBookEmplNo())
                    .emplId(spaceBookDto.getEmplId())
                    .spaceBookSpaceNo(spaceBookDto.getSpaceBookSpaceNo())
                    .spaceBookSpaceNm(spaceBookDto.getSpaceBookSpaceNm())
                    .spaceBookLocDesc(spaceBookDto.getSpaceBookLocDesc())
                    .spaceBookDate(spaceBookDto.getSpaceBookDate())
                    .spaceBookBgnTm(spaceBookDto.getSpaceBookBgnTm())
                    .spaceBookEndTm(spaceBookDto.getSpaceBookEndTm())
                    .spaceBookCn(spaceBookDto.getSpaceBookCn())
                    .spaceBookStusCd(spaceBookDto.getSpaceBookStusCd())
                    .selfBookYN(spaceBookDto.getEmplId().equals(emplId))
                    .build());
        }
        return newList;
    }
}
