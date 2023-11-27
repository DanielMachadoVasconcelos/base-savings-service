package se.ead.base.savings.member;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static se.ead.base.savings.member.MemberController.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import se.ead.base.savings.SpringBootIntegrationTest;

@Sql(scripts = "/test-data/delete-all-members.sql", executionPhase = AFTER_TEST_METHOD)
class MemberControllerTest extends SpringBootIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Should be able to save a new member when providing a unique member name as admin")
    void shouldBeAbleToSaveANewMemberWhenProvidingAUniqueMemberNameAsAdmin() throws Exception {

        // given: a new member
        Member member = new Member(UUID.randomUUID(), "Harry Potter");

        // when: creating a new member
        var response = mockMvc.perform(post("/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(member)))
                .andDo(print())
                .andExpect(status().isCreated());

        // then: the member is saved
        response.andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpectAll(
                    jsonPath("$.member_id").isNotEmpty(),
                    jsonPath("$.member_name").value(member.memberName())
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Should be able to update a member name when providing an member id as admin")
    void shouldBeAbleToUpdateAMemberNameWhenProvidingAnMemberIdAsAdmin() throws Exception {

        // given: a new member with an incorrect member name
        Member member = new Member(UUID.randomUUID(), "Harry Poter");

        // and: the new member was created with incorrect name
        mockMvc.perform(post("/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(member)))
                .andDo(print())
                .andExpect(status().isCreated());

        // when: the admin wishes to update the member name
        var response = mockMvc.perform(patch("/v1/members/%s".formatted(member.memberId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(objectMapper.writeValueAsString(new UpdateMemberNameRequest("Harry Potter")))
                );

        // then: the member name is updated
        response.andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpectAll(
                    jsonPath("$.member_id").value(is(member.memberId().toString())),
                    jsonPath("$.member_name").value(is("Harry Potter"))
                );
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Should be able to save a new member when providing a unique member name as user")
    void shouldBeAbleToSaveANewMemberWhenProvidingAUniqueMemberNameAsUser() throws Exception {

        // given: a new member
        Member member = new Member(UUID.randomUUID(), "Albus Dumbledore");

        // when: creating a new member
        var response = mockMvc.perform(post("/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(member)))
                .andDo(print())
                .andExpect(status().isCreated());

        // then: the member is saved
        response.andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpectAll(
                        jsonPath("$.member_id").isNotEmpty(),
                        jsonPath("$.member_name").value(member.memberName())
                );
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Should not be able to save a new member when providing a non-unique member name as user")
    void shouldNotBeAbleToSaveANewMemberWhenMemberNameAlreadyExists() throws Exception {

        // setup: creating a pre-existing member
        mockMvc.perform(post("/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(new Member(UUID.randomUUID(), "Albus Dumbledore"))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // given: a new member with the same unique member name
        Member member = new Member(UUID.randomUUID(), "Albus Dumbledore");

        // when: trying to create a new member with same member name
        var response = mockMvc.perform(post("/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(member)));

        // then: the request should be rejected
        response.andDo(print())
                .andExpect(status().is4xxClientError());
    }
}