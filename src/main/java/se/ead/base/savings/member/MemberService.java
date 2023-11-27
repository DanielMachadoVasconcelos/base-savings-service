package se.ead.base.savings.member;

import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class MemberService {

    MemberRepository memberRepository;
    MemberMapper memberMapper;

    public Member save(Member member) {
        MemberEntity entity = memberMapper.from(member);
        return memberMapper.from(memberRepository.save(entity));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Member update(UUID memberId, String newMemberName) {
        return memberRepository.findById(memberId)
                .map(entity -> entity.withMemberName(newMemberName))
                .map(memberRepository::save)
                .map(memberMapper::from)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
    }

    public Page<Member> findByName(Pageable pageable, String memberName) {
        return memberRepository.findByMemberName(pageable, memberName)
                .map(memberMapper::from);
    }

    public Page<Member> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(memberMapper::from);
    }

    public Member findMemberById(UUID memberId) {
        return memberRepository.findById(memberId)
                .map(memberMapper::from)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
    }

    public Page<Revision<Integer, MemberEntity>> findRevisions(UUID memberId, Pageable pageable) {
        return memberRepository.findRevisions(memberId, pageable);
    }
}
