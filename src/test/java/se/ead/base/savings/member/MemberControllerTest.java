package se.ead.base.savings.member;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.RequestEntity.put;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static se.ead.base.savings.member.MemberController.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotEmpty;
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
        Member member = new Member(UUID.randomUUID(),
                1L,
                "Harry Potter",
                "harry.potter@gmail.com",
                "The boy who survived"
        );

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
                    jsonPath("$.member_name").value(member.name())
                );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Should be able to update a member name when providing an member id as admin")
    void shouldBeAbleToUpdateAMemberNameWhenProvidingAnMemberIdAsAdmin() throws Exception {

        // given: a new member with an incorrect member name
        Member member = new Member(UUID.randomUUID(),
            1L,
            "Harry Poter",
            "harry.potter@gmail.com",
            "The boy who survived"
        );

        // and: the new member was created with incorrect name
        mockMvc.perform(post("/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(member)))
                .andDo(print())
                .andExpect(status().isCreated());

        // when: the admin wishes to update the member name
        var response = mockMvc.perform(patch("/v1/members/%s".formatted(member.id()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(objectMapper.writeValueAsString(new UpdateMemberNameRequest(2L, "Harry Potter")))
                );

        // then: the member name is updated
        response.andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpectAll(
                    jsonPath("$.member_id").value(is(member.id().toString())),
                    jsonPath("$.member_name").value(is("Harry Potter"))
                );
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Should be able to save a new member when providing a unique member name as user")
    void shouldBeAbleToSaveANewMemberWhenProvidingAUniqueMemberNameAsUser() throws Exception {

        // given: a new member
        Member member = new Member(UUID.randomUUID(),
            1L,
            "Albus Dumbledore",
            "albus.dumbledore@gmail.com",
            "The heavy master of Hogwarts"
        );

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
                        jsonPath("$.member_name").value(member.name())
                );
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Should not be able to save a new member when providing a non-unique member email")
    void shouldNotBeAbleToSaveANewMemberWhenMemberNameAlreadyExists() throws Exception {

        // setup: creating a pre-existing member
        Member albusDumbledore = new Member(UUID.randomUUID(),
                1L,
                "Albus Dumbledore",
                "albus.dumbledore@gmail.com",
                "The heavy master of Hogwarts"
        );

        // and the member is already persisted on database
        mockMvc.perform(post("/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(albusDumbledore)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // given: a new member with the same unique member name
        Member member = new Member(UUID.randomUUID(),
            0l,
            "Lord Voldemort",
            "albus.dumbledore@gmail.com",
            "The fake heavy master of Hogwarts. Some one using the Polissuco potion"
        );

        // when: trying to create a new member with same member name
        var response = mockMvc.perform(post("/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(member)));

        // then: the request should be rejected
        response.andDo(print())
                .andExpect(status().is4xxClientError());
    }


    @Test
    @WithMockUser(username = "user")
    @DisplayName("Should not allow to update a existing member when member version do not match")
    void shouldNotAllowToUpdateAExistingMemberWhenMemberVersionDoNotMatch() throws Exception {

        // setup: creating a pre-existing member
        Member albusDumbledore = new Member(UUID.randomUUID(),
            2L,
            "Albus Dumbledore",
            "albus.dumbledore@gmail.com",
            "The heavy master of Hogwarts"
        );

        // and the member is already persisted on database
        mockMvc.perform(post("/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(albusDumbledore)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // given:
        var request = new UpdateMemberNameRequest(1L, "Albus Dumbledore");

        // when: trying to create a new member with same member name
        var response = mockMvc.perform(patch("/v1/members/%s".formatted(albusDumbledore.id()))
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(objectMapper.writeValueAsString(request))
        );

        // then: the request should be rejected
        response.andDo(print())
                .andExpect(status().is4xxClientError());
    }
}