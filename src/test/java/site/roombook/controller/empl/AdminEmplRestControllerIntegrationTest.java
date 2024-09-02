package site.roombook.controller.empl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.dao.EmplDao;
import site.roombook.domain.EmplDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/testContext.xml"})
@DisplayName("사원 관리자 API 통합 테스트")
class AdminEmplRestControllerIntegrationTest {

    @Autowired
    private AdminEmplRestController adminEmplRestController;

    @Autowired
    private EmplDao emplDao;

    private MockMvc mockMvc;

    private List<EmplDto> sixDummyEmpls;

    @Nested
    @DisplayName("검색어")
    class SearchTest {
        @BeforeEach
        void setup() {
            MockitoAnnotations.openMocks(this);
            mockMvc = MockMvcBuilders.standaloneSetup(adminEmplRestController).build();
            sixDummyEmpls = createSixEmplList();

            emplDao.deleteAll();

            emplDao.insertEmpl(sixDummyEmpls.get(0));
            emplDao.insertEmpl(sixDummyEmpls.get(1));
            emplDao.insertEmpl(sixDummyEmpls.get(2));
            emplDao.insertEmpl(sixDummyEmpls.get(3));
            emplDao.insertEmpl(sixDummyEmpls.get(4));
            emplDao.insertEmpl(sixDummyEmpls.get(5));
        }

        @Test
        @DisplayName("유효하지 않은 권한이름")
        void invalidAuthName() throws Exception {
            mockMvc.perform(get("/api/admin/empls")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("page", "1")
                            .param("option", "authName")
                            .param("optionValue", "관리자잉"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.msg", is("검색어를 다시 입력해주세요.")))
                    .andReturn();
        }

        @Test
        @DisplayName("유효한 권한 이름")
        void validAuthName() throws Exception {
            mockMvc.perform(get("/api/admin/empls")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("page", "1")
                            .param("option", "authName")
                            .param("optionValue", "관리자"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.ph.totalCnt", is(3)))
                    .andExpect(jsonPath("$.ph.offset", is(0)))
                    .andReturn();
        }
    }

    @Nested
    @DisplayName("검색 목록 페이징 테스트")
    class SearchListPaginationTest {
        @BeforeEach
        void setup() {
            MockitoAnnotations.openMocks(this);
            mockMvc = MockMvcBuilders.standaloneSetup(adminEmplRestController).build();
            sixDummyEmpls = createSixEmplList();

            emplDao.deleteAll();

            emplDao.insertEmpl(sixDummyEmpls.get(0));
            emplDao.insertEmpl(sixDummyEmpls.get(1));
            emplDao.insertEmpl(sixDummyEmpls.get(2));
            emplDao.insertEmpl(sixDummyEmpls.get(3));
            emplDao.insertEmpl(sixDummyEmpls.get(4));
            emplDao.insertEmpl(sixDummyEmpls.get(5));
            emplDao.insertEmpl(sixDummyEmpls.get(6));
            emplDao.insertEmpl(sixDummyEmpls.get(7));
            emplDao.insertEmpl(sixDummyEmpls.get(8));
            emplDao.insertEmpl(sixDummyEmpls.get(9));
            emplDao.insertEmpl(sixDummyEmpls.get(10));
            emplDao.insertEmpl(sixDummyEmpls.get(11));
        }

        @Test
        @DisplayName("존재하지 않는 페이지 요청")
        void requestNonExistingPage() throws Exception {
            mockMvc.perform(get("/api/admin/empls")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("page", "100")
                            .param("option", "authName")
                            .param("optionValue", "슈퍼관리자"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.ph.totalCnt", is(2)))
                    .andExpect(jsonPath("$.ph.offset", is(0)))
                    .andExpect(jsonPath("$.ph.totalPage", is(1)))
                    .andReturn();
        }

        @Test
        @DisplayName("음수 페이지 번호 요청")
        void requestNegativeNumberPage() throws Exception {
            mockMvc.perform(get("/api/admin/empls")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("page", "-1")
                            .param("option", "authName")
                            .param("optionValue", "슈퍼관리자"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.ph.totalCnt", is(2)))
                    .andExpect(jsonPath("$.ph.offset", is(0)))
                    .andExpect(jsonPath("$.ph.totalPage", is(1)))
                    .andReturn();
        }

        @Test
        @DisplayName("유효하지 않은 페이지 번호 요청")
        void requestInvalidPage() throws Exception {
            mockMvc.perform(get("/api/admin/empls")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("page", "invalid")
                            .param("option", "authName")
                            .param("optionValue", "슈퍼관리자"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.msg", is("INVALID_REQUEST")))
                    .andReturn();
        }
    }

    List<EmplDto> createSixEmplList(){
        return List.of(
                EmplDto.EmplDtoBuilder().emplNo("0000001").emplId("aaaa").pwd("aaaa").email("aaaa@asdf.com")
                        .pwdErrTms(0).rnm("aaa").engNm("aaa").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(1111).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000002").emplId("bbbb").pwd("bbbb").email("bbbb@asdf.com")
                        .pwdErrTms(0).rnm("bbb").engNm("bbb").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(2222).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000003").emplId("cccc").pwd("cccc").email("cccc@asdf.com")
                        .pwdErrTms(0).rnm("ccc").engNm("ccc").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(3333).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000004").emplId("dddd").pwd("dddd").email("dddd@asdf.com")
                        .pwdErrTms(0).rnm("ddd").engNm("ddd").entDt("2024-01-01").emplAuthNm("ROLE_SUPER_ADMIN").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(4444).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000005").emplId("eeee").pwd("eeee").email("eeee@asdf.com")
                        .pwdErrTms(0).rnm("eee").engNm("eee").entDt("2024-01-01").emplAuthNm("ROLE_EMPL_ADMIN").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(5555).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000006").emplId("ffff").pwd("ffff").email("ffff@asdf.com")
                        .pwdErrTms(0).rnm("fff").engNm("fff").entDt("2024-01-01").emplAuthNm("ROLE_RSC_ADMIN").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(6666).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000010").emplId("gggg").pwd("gggg").email("gggg@asdf.com")
                        .pwdErrTms(0).rnm("gggg").engNm("gggg").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(1111).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000011").emplId("hhhh").pwd("hhhh").email("hhhh@asdf.com")
                        .pwdErrTms(0).rnm("hhhh").engNm("hhhh").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(2222).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000012").emplId("iiii").pwd("iiii").email("iiii@asdf.com")
                        .pwdErrTms(0).rnm("iiii").engNm("iiii").entDt("2024-01-01").emplAuthNm("ROLE_USER").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(3333).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000013").emplId("jjjj").pwd("jjjj").email("jjjj@asdf.com")
                        .pwdErrTms(0).rnm("jjjj").engNm("jjjj").entDt("2024-01-01").emplAuthNm("ROLE_SUPER_ADMIN").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(4444).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000014").emplId("kkkk").pwd("kkkk").email("kkkk@asdf.com")
                        .pwdErrTms(0).rnm("kkkk").engNm("kkkk").entDt("2024-01-01").emplAuthNm("ROLE_EMPL_ADMIN").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(5555).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build(),
                EmplDto.EmplDtoBuilder().emplNo("0000015").emplId("llll").pwd("llll").email("llll@asdf.com")
                        .pwdErrTms(0).rnm("llll").engNm("llll").entDt("2024-01-01").emplAuthNm("ROLE_RSC_ADMIN").brdt("2000-01-01")
                        .wncomTelno("01123123").empno(6666).msgrId(null).prfPhotoPath(null)
                        .subsCertiYn('Y').termsAgreYn('Y').subsAprvYn('Y').secsnYn('N').build()
        );
    }
}