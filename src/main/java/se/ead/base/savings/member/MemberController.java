package se.ead.base.savings.member;

import static org.springframework.data.domain.Sort.Direction.ASC;

import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@Observed(name = "member_controller")
public class MemberController {

    MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public Member createMember(@RequestBody Member member) {
        return memberService.save(member);
    }

    @PatchMapping("/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public Member updateMember(@PathVariable(name = "memberId") @NotNull UUID memberId,
                               @RequestBody UpdateMemberNameRequest request) {
        return memberService.update(memberId, request.memberName());
    }

    @GetMapping("/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public Member findMemberById(@PathVariable(name = "memberId") @NotNull UUID memberId) {
        return memberService.findMemberById(memberId);
    }

    @GetMapping("/search")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public Page<Member> findByMemberName(
            @RequestParam(value = "member_name") @NotEmpty String memberName,
            @PageableDefault(size = 20, sort = "member_name", direction = ASC) Pageable pageable
    ) {
        return memberService.findByName(pageable, memberName);
    }

    @GetMapping
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public Page<Member> findAllMembers(@PageableDefault(size = 20, sort = "memberName", direction = ASC) Pageable pageable) {
        return memberService.findAll(pageable);
    }

    @GetMapping("/{memberId}/revisions")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public Page<Revision<Integer, MemberEntity>> getMemberAudits(@PathVariable UUID memberId,
                                                                 @PageableDefault(size = 20, sort = "memberName", direction = ASC) Pageable pageable) {
        return memberService.findRevisions(memberId, pageable);
    }

    public record UpdateMemberNameRequest(@NotEmpty String memberName) {}
}
