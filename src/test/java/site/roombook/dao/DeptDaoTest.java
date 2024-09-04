package site.roombook.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.domain.BlngDeptDto;
import site.roombook.domain.DeptAndEmplDto;
import site.roombook.domain.DeptDto;
import site.roombook.domain.EmplDto;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class DeptDaoTest {
    @Autowired
    DeptDao deptDao;

    @Autowired
    BlngDeptDao blngDeptDao;

    @Autowired
    EmplDao emplDao;


    @Nested
    class InsertionTest {
        private List<DeptDto> fiveDeptList;

        @BeforeEach
        void setup(){
            List<EmplDto> threeEmplList = getThreeEmplList();
            fiveDeptList = getFiveDeptList();
            deptDao.deleteAll();
            emplDao.deleteAll();

            EmplDto emplAdmin = getEmplAdmin();
            emplDao.insertEmpl(emplAdmin);

            for (EmplDto emplDto : threeEmplList) {
                emplDao.insertEmpl(emplDto);
            }
        }


        @Test
        void insertDeptTest(){
            assertEquals(1, deptDao.insertDept(fiveDeptList.get(0)));
        }

        @Test
        @DisplayName("중복된 부서 코드 insert 테스트")
        void insertDuplicatedDeptTest(){
            EmplDto emplAdmin = getEmplAdmin();

            DeptDto existingDept = fiveDeptList.get(0);
            assertEquals(1, deptDao.insertDept(existingDept));

            DeptDto newDept = DeptDto.DeptDtoBuilder()
                    .deptCd(existingDept.getDeptCd())
                    .uppDeptCd("#")
                    .deptMngrEmplNo(null)
                    .deptNm("인사팀")
                    .engDeptNm("Human Resource Dept")
                    .deptSortOdr(2)
                    .registerId(emplAdmin.getEmplId())
                    .registerId(emplAdmin.getEmplId()).build();

            assertThrows(DuplicateKeyException.class, () -> deptDao.insertDept(newDept));
        }

        @Test

        @DisplayName("중복된 부서 이름 insert 테스트")
        void insertDuplicatedDeptNmTest(){
            EmplDto emplAdmin = getEmplAdmin();

            DeptDto existingDept = fiveDeptList.get(0);
            assertEquals(1, deptDao.insertDept(existingDept));

            DeptDto newDept = DeptDto.DeptDtoBuilder()
                    .deptCd("9999")
                    .uppDeptCd("#")
                    .deptMngrEmplNo(null)
                    .deptNm(existingDept.getDeptNm())
                    .engDeptNm(existingDept.getEngDeptNm())
                    .deptSortOdr(2)
                    .registerId(emplAdmin.getEmplId()).build();

            assertThrows(DuplicateKeyException.class, () -> deptDao.insertDept(newDept));
        }

        @Test
        @DisplayName("존재하지 않는 관리자 아이디를 사용했을 때")
        void insertDeptWithNotExistingManagerId(){
            DeptDto DeptWithInvalidManagerId = modifyEmplId(fiveDeptList.get(0), "invalidId");
            assertEquals(1, deptDao.insertDept(DeptWithInvalidManagerId));
            assertNull(deptDao.selectDept(DeptWithInvalidManagerId.getDeptCd()).getDeptMngrEmplNo());
        }
    }

    @Nested
    class UpdateTest {
        private List<DeptDto> fiveDeptList;
        private List<EmplDto> threeEmplList;

        @BeforeEach
        void setup(){
            deptDao.deleteAll();
            emplDao.deleteAll();

            threeEmplList = getThreeEmplList();
            fiveDeptList = getFiveDeptList();

            EmplDto emplAdmin = getEmplAdmin();
            emplDao.insertEmpl(emplAdmin);


            for (EmplDto emplDto : threeEmplList) {
                emplDao.insertEmpl(emplDto);
            }

            for (int i = 0; i < fiveDeptList.size(); i++) {
                if (i == 0) {
                    deptDao.insertDept(addManager(fiveDeptList.get(i), emplAdmin.getEmplId())); // 0번째 부서는 관리자가 있음
                } else {
                    deptDao.insertDept(fiveDeptList.get(i));
                }
            }
        }

        @Test
        @DisplayName("부서 관리자 수정 테스트")
        void updateManagerTest() {
            DeptDto deptToUpdate = fiveDeptList.get(0);

            Map<String, String> map = new HashMap<>();
            map.put("manager", threeEmplList.get(0).getEmplId());
            map.put("empId", "bbbb");
            map.put("deptCd", deptToUpdate.getDeptCd());

            assertEquals(1, deptDao.updateManager(map));
        }


        /*
         *  <변경 후 부서 조직도>
         *   #
         *   L 영업부(index: 0)
         *       L 영업기획팀(index: 2)
         *   L 기획부(index: 1)
         *       L 영업지원팀(index: 3)
         *           L 고객만족팀(index: 4)
         * */
        @Test
        @DisplayName("복수 개 부서 순서 수정 테스트")
        void updateAllDeptTreeDataTest() {
            EmplDto emplAdmin = getEmplAdmin();
            DeptDto deptToUpdate1 = moveDept(fiveDeptList.get(3), "11112222", 0, emplAdmin.getEmplId());
            DeptDto deptToUpdate2 = moveDept(fiveDeptList.get(4), "22222233", 0, emplAdmin.getEmplId());

            List<DeptDto> list = List.of(
                    fiveDeptList.get(0)
                    ,fiveDeptList.get(1)
                    ,fiveDeptList.get(2)
                    ,deptToUpdate1
                    ,deptToUpdate2
            );

            int rowCnt = deptDao.updateAllDeptTreeOdrData(list);
            assertEquals(2, rowCnt);
        }
    }

    private DeptDto moveDept(DeptDto deptDto, String uppDeptCd, int deptSortDor, String modifierId) {

        return DeptDto.DeptDtoBuilder()
                .deptCd(deptDto.getDeptCd())
                .uppDeptCd(uppDeptCd)
                .deptMngrEmplNo(deptDto.getDeptMngrEmplNo())
                .deptNm(deptDto.getDeptNm())
                .engDeptNm(deptDto.getEngDeptNm())
                .deptSortOdr(deptSortDor)
                .lastUpdDtm(LocalDateTime.now())
                .modifierId(modifierId).build();
    }

    @Nested
    class DeletionTest {
        private List<DeptDto> fiveDeptList;
        private List<EmplDto> threeEmplList;

        @BeforeEach
        void setup(){
            deptDao.deleteAll();
            emplDao.deleteAll();
            blngDeptDao.deleteAllBlngDept();

            //관리자 추가
            EmplDto emplAdmin = getEmplAdmin();
            emplDao.insertEmpl(emplAdmin);

            //3명 사원 추가
            threeEmplList = getThreeEmplList();

            for (EmplDto emplDto : threeEmplList) {
                emplDao.insertEmpl(emplDto);
            }

            //5개 부서 추가
            fiveDeptList = getFiveDeptList();

            for (int i = 0; i < fiveDeptList.size(); i++) {
                if (i == 0) {
                    deptDao.insertDept(addManager(fiveDeptList.get(i), emplAdmin.getEmplId())); // 0번째 부서는 관리자가 있음
                } else {
                    deptDao.insertDept(fiveDeptList.get(i));
                }
            }

            //소속 부서 추가
        }

        @Test
        @DisplayName("구성원이 있고 하위부서는 없는 부서 삭제 실패")
        void deleteDeptWithEmpl(){
            DeptDto DeptToRemvoe = fiveDeptList.get(1);
            EmplDto emplInDeptToRemove = threeEmplList.get(0);

            EmplDto adminEmpl = getEmplAdmin();
            BlngDeptDto blngDeptDto = BlngDeptDto.BlngDeptDtoBuilder()
                    .blngDeptCd(DeptToRemvoe.getDeptCd())
                    .blngEmplId(emplInDeptToRemove.getEmplId())
                    .repDeptYn('N')
                    .registerId(adminEmpl.getEmplId()).build();

            assertEquals(1, blngDeptDao.insertBlngDept(blngDeptDto));
            assertEquals(0, deptDao.deleteDeptWithNoEmpl(DeptToRemvoe.getDeptCd()));
        }

        @Test
        @DisplayName("구성원이 없는 부서 삭제 성공 테스트")
        void deleteDeptWithNoEmpl(){
            assertEquals(1, deptDao.deleteDeptWithNoEmpl(fiveDeptList.get(4).getDeptCd()));
        }

        @Test
        @DisplayName("모든 부서 삭제 테스트")
        void deleteAllTest() {
            assertEquals(fiveDeptList.size(), deptDao.deleteAll());
            assertEquals(0, deptDao.selectAllDeptCnt());
        }
    }

    @Nested
    class SelectionTest {
        private List<DeptDto> fiveDeptList;

        @BeforeEach
        void setup(){
            List<EmplDto> threeEmplList = getThreeEmplList();
            fiveDeptList = getFiveDeptList();
            deptDao.deleteAll();
            emplDao.deleteAll();

            EmplDto emplAdmin = getEmplAdmin();
            emplDao.insertEmpl(emplAdmin);

            for (EmplDto emplDto : threeEmplList) {
                emplDao.insertEmpl(emplDto);
            }

            for (int i = 0; i < fiveDeptList.size(); i++) {
                if (i == 0) {
                    deptDao.insertDept(addManager(fiveDeptList.get(i), emplAdmin.getEmplId())); // 0번째 부서는 관리자가 있음
                } else {
                    deptDao.insertDept(fiveDeptList.get(i));
                }
            }
        }

        @Test
        @DisplayName("부서명으로 부서 수 조회 테스트")
        void selectDeptNmTest(){
            assertEquals(1, deptDao.selectDeptCntWithNm(fiveDeptList.get(0).getDeptNm()));
        }

        @Test
        @DisplayName("하위 부서 개수 조회 테스트")
        void selectChildrenDeptCount(){
            assertEquals(1, deptDao.selectCdrDeptCnt(fiveDeptList.get(2).getDeptCd()));
        }

        @Test
        @DisplayName("하위 부서 개수 조회 테스트")
        void selectChildrenDeptCountZeroTest(){
            assertEquals(0, deptDao.selectCdrDeptCnt(fiveDeptList.get(4).getDeptCd()));
        }

        @Test
        @DisplayName("부서 매니저 정보가 없을 때")
        void selectDeptAndItsManager(){
            DeptAndEmplDto deptWithNoChildren = deptDao.selectOneDeptAndMngrAndCdrDeptCnt(fiveDeptList.get(1).getDeptCd());
            assertNull(deptWithNoChildren.getEmplId());
        }

        @Test
        @DisplayName("부서 매니저 정보가 없을 때")
        void selectOneDeptTest(){
            DeptAndEmplDto deptWithNoChildren = deptDao.selectOneDeptAndMngrAndCdrDeptCnt(fiveDeptList.get(0).getDeptCd());
            assertEquals(getEmplAdmin().getEmplId(), deptWithNoChildren.getEmplId());
        }

        @Test
        @DisplayName("모든 부서 수 조회 테스트")
        void selectAllDeptCntTest(){
            assertEquals(fiveDeptList.size(), deptDao.selectAllDeptCnt());
        }
    }

    @Nested
    class UpdateDeptTest {
        List<EmplDto> threeEmplList;
        DeptDto dummyDept;
        @BeforeEach
        void setup() {
            deptDao.deleteAll();
            emplDao.deleteAll();

            assertEquals(1, emplDao.insertEmpl(getEmplAdmin()));

            threeEmplList = getThreeEmplList();
            assertEquals(1, emplDao.insertEmpl(threeEmplList.get(0)));
            assertEquals(1, emplDao.insertEmpl(threeEmplList.get(1)));
            assertEquals(1, emplDao.insertEmpl(threeEmplList.get(2)));

            dummyDept = DeptDto.DeptDtoBuilder().deptCd("1234").uppDeptCd("#").deptMngrEmplNo(null).deptNm("a").engDeptNm("engA").deptSortOdr(0).registerId(getEmplAdmin().getEmplId()).build();
//            dummyDept = new DeptDto("1234", "#", null, "a", "engA", 0, "asdf", "asdf");
            assertEquals(1, deptDao.insertDept(dummyDept));
        }

        @Test
        @DisplayName("수정 사항 없는 업데이트 시 수정 성공")
        void updateDeptWithSameValueTest() {
            EmplDto adminEmpl = getEmplAdmin();

            DeptDto newDept = DeptDto.DeptDtoBuilder()
                    .deptCd(dummyDept.getDeptCd())
                    .emplId(threeEmplList.get(0).getEmplId())
                    .deptNm(dummyDept.getDeptNm())
                    .engDeptNm(dummyDept.getEngDeptNm())
                    .lastUpdDtm(LocalDateTime.now())
                    .modifierId(adminEmpl.getEmplId())
                    .build();

            assertEquals(1, deptDao.updateDept(newDept));
        }

        @Test
        @DisplayName("부서 수정 테스트 성공")
        void updateDeptTest() {
            DeptDto deptDto = DeptDto.DeptDtoBuilder()
                    .emplId(threeEmplList.get(0).getEmplId())
                    .deptNm("바뀐이름")
                    .engDeptNm("changedName")
                    .lastUpdDtm(LocalDateTime.now())
                    .modifierId(getEmplAdmin().getEmplId())
                    .deptCd(dummyDept.getDeptCd())
                    .build();

            assertEquals(1, deptDao.updateDept(deptDto));
        }

        @Test
        @DisplayName("유효하지 않은 관리자 아이디 사용")
        void updateDeptFailTest() {
            DeptDto deptDto = DeptDto.DeptDtoBuilder()
                    .emplId(threeEmplList.get(0).getEmplId())
                    .deptNm("바뀐이름")
                    .engDeptNm("changedName")
                    .lastUpdDtm(LocalDateTime.now())
                    .modifierId("modifierId")
                    .deptCd(dummyDept.getDeptCd())
                    .build();

            assertThrows(DataIntegrityViolationException.class, () -> deptDao.updateDept(deptDto));
        }
    }

    private EmplDto getEmplAdmin() {
        return EmplDto.EmplDtoBuilder().emplNo("44444444").emplId("adminId").pwd("1234").email("testid1@gmail.com")
                .pwdErrTms(0).rnm("감자영").engNm("gamja").entDt("2024-01-01").emplAuthNm("ROLE_EMPL_ADMIN").brdt("2000-01-01")
                .wncomTelno("111111").empno(1111).msgrId(null).prfPhotoPath(null)
                .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build();
    }

    private List<EmplDto> getThreeEmplList() {
        return List.of(EmplDto.EmplDtoBuilder().emplNo("11111111").emplId("jy123").pwd("1234").email("jy123@gmail.com")
                        .pwdErrTms(0).rnm("김지영").engNm("darwin").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("123123").empno(1111).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("22222222").emplId("Mogu").pwd("1234").email("mogu@gmail.com")
                        .pwdErrTms(0).rnm("최모구").engNm("mogu").entDt("1980-10-10").emplAuthNm("ROLE_USER").brdt("1980-10-10")
                        .wncomTelno("222222").empno(22222).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("33333333").emplId("dongju").pwd("1234").email("dj@gmail.com")
                        .pwdErrTms(0).rnm("박동주").engNm("dongju").entDt("2020-01-01").emplAuthNm("ROLE_USER").brdt("1980-10-10")
                        .wncomTelno("333333").empno(33333).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build()
        );
    }

    /*
    *   #
    *   L 영업부(index: 0)
    *       L 영업기획팀(index: 2)
    *           L 고객만족팀(index: 4)
    *       L 영업지원팀(index: 3)
    *   L 기획부(index: 1)
    * */
    private List<DeptDto> getFiveDeptList() {
        return List.of(
                DeptDto.DeptDtoBuilder().deptCd("11111111").uppDeptCd("#").deptMngrEmplNo(null).deptNm("영업부").engDeptNm("Sales Dept").deptSortOdr(0).registerId(getEmplAdmin().getEmplId()).build(),
                DeptDto.DeptDtoBuilder().deptCd("11112222").uppDeptCd("#").deptMngrEmplNo(null).deptNm("기획부").engDeptNm("Planning Dept").deptSortOdr(1).registerId(getEmplAdmin().getEmplId()).build(),
                DeptDto.DeptDtoBuilder().deptCd("22222222").uppDeptCd("11111111").deptMngrEmplNo(null).deptNm("영업기획팀").engDeptNm("Sales Planning Team").deptSortOdr(0).registerId(getEmplAdmin().getEmplId()).build(),
                DeptDto.DeptDtoBuilder().deptCd("22222233").uppDeptCd("11111111").deptMngrEmplNo(null).deptNm("영업지원팀").engDeptNm("Sales Support Team").deptSortOdr(1).registerId(getEmplAdmin().getEmplId()).build(),
                DeptDto.DeptDtoBuilder().deptCd("33333333").uppDeptCd("22222222").deptMngrEmplNo(null).deptNm("고객만족팀").engDeptNm("Customer Satisfaction Team").deptSortOdr(0).registerId(getEmplAdmin().getEmplId()).build());
    }

    private DeptDto modifyEmplId(DeptDto existingDeptDto, String deptName) {
        return DeptDto.DeptDtoBuilder()
                .deptCd(existingDeptDto.getDeptCd())
                .uppDeptCd(existingDeptDto.getUppDeptCd())
                .deptMngrEmplNo(existingDeptDto.getDeptMngrEmplNo())
                .deptNm(deptName)
                .engDeptNm(existingDeptDto.getEngDeptNm())
                .deptSortOdr(existingDeptDto.getDeptSortOdr())
                .registerId(existingDeptDto.getRegisterId()).build();
    }

    private DeptDto addManager(DeptDto deptDto, String adminId) {
        return DeptDto.DeptDtoBuilder()
                .deptCd(deptDto.getDeptCd())
                .uppDeptCd(deptDto.getUppDeptCd())
                .emplId(adminId)
                .deptNm(deptDto.getDeptNm())
                .engDeptNm(deptDto.getEngDeptNm())
                .deptSortOdr(deptDto.getDeptSortOdr())
                .registerId(deptDto.getRegisterId())
                .build();
    }
}