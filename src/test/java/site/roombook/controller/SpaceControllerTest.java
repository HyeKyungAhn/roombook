package site.roombook.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import site.roombook.dao.SpaceDao;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(locations = {"file:web/WEB-INF/spring/**/applicationContext.xml"})
class SpaceControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    SpaceController spaceController;

    @Mock
    SpaceDao spaceDao;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(spaceController).build();
    }

    @Test
    @Transactional
    @WithMockUser(roles = "EMPL_ADMIN")
    @DisplayName("space값이 다 안들어오는 경우")
    void invalidSpaceTest() throws Exception {
        String spaceFacility = "[{\"rescNo\":1,\"value\":\"wifi\",\"color\":\"hsl(282,48%,71%)\",\"style\":\"--tag-bg:hsl(282,48%,71%)\",\"__isValid\":true,\"__tagId\":\"9cca6978-ca3c-478b-a51f-b5271ce21a94\"}]";

        when(spaceDao.update(any())).thenReturn(1);

         mockMvc.perform(post("/api/admin/spaces/123123")
                .param("space", "{'spaceNm': '이름'}")
                .param("spaceFacility", spaceFacility)
                .contentType("mulitpart/form-data"))
                .andExpect(status().is4xxClientError());
    }
}