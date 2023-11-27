package se.ead.base.savings.member;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import se.ead.base.savings.SpringBootIntegrationTest;

@Sql(scripts = "/test-data/delete-all-members.sql", executionPhase = AFTER_TEST_METHOD)
class MemberControllerSearchTest extends SpringBootIntegrationTest {

    private final UUID expectedMemberId = UUID.randomUUID();
    private final String expectedMemberName = "Harry Potter";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    @DisplayName("Must have an already existing member persisted in the database")
    void setUp() throws Exception {
        // given: a new member
        Member member = new Member(expectedMemberId, expectedMemberName);

        // when: creating a new member
        Authentication authentication = new TestingAuthenticationToken("admin", "password", "ROLE_ADMIN");
        var response = mockMvc.perform(post("/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .with(authentication(authentication))
                        .content(objectMapper.writeValueAsString(member))
                );

        // then: the member is saved
        response.andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    @DisplayName("Should be able to find a member by member name")
    void shouldBeAbleToFindAMemberByMemberName() throws Exception {

        // when: searching for a member by member name
        var response = mockMvc.perform(get("/v1/members/search")
                        .param("member_name", expectedMemberName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                );

        // then: the member is found
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(
                    jsonPath("$.content").isArray(),
                    jsonPath("$.content[0].member_name").value(expectedMemberName)
                );
    }

    @Test
    @WithMockUser
    @DisplayName("Should be able to find a member when searching by member id")
    void shouldBeAbleToFindAMemberWhenSearchingByMemberId() throws Exception {

        // when: searching for a member by member id
        var response = mockMvc.perform(get("/v1/members/{memberId}", expectedMemberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                );

        // then: the member is found
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(
                    jsonPath("$.member_id").value(is(expectedMemberId.toString())),
                    jsonPath("$.member_name").value(is(expectedMemberName))
                );
    }
}
