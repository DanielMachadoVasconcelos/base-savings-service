package se.ead.base.savings.member;

import java.util.UUID;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface MemberRepository extends ListPagingAndSortingRepository<MemberEntity, UUID>,
        ListCrudRepository<MemberEntity, UUID>, RevisionRepository<MemberEntity, UUID, Integer> {

    Page<MemberEntity> findByMemberName(Pageable pageable, String memberName);
}
