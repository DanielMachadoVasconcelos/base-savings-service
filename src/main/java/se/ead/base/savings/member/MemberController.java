package se.ead.base.savings.member;

import static org.springframework.data.domain.Sort.Direction.ASC;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/v1/members")
public class MemberController {

    MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public Member createMember(@RequestBody Member member) {
        return memberService.save(member);
    }

    @GetMapping
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public Page<Member> findByMemberName(
        @RequestParam(value = "member_name") @NotEmpty String memberName,
        @PageableDefault(size = 20, sort = "memberName", direction = ASC) Pageable pageable
    ) {
        return memberService.findByName(pageable, memberName);
    }
}
