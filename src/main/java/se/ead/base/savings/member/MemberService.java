package se.ead.base.savings.member;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MemberService {

    MemberRepository memberRepository;
    MemberMapper memberMapper;

    public Member save(Member member) {
        MemberEntity entity = memberMapper.from(member);
        return memberMapper.from(memberRepository.save(entity));
    }

    public Page<Member> findByName(Pageable pageable, String memberName) {
        return memberRepository.findByMemberName(pageable, memberName)
                .map(memberMapper::from);
    }
}
