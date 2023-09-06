package se.ead.base.savings.member;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface MemberRepository extends ListPagingAndSortingRepository<MemberEntity, UUID>, ListCrudRepository<MemberEntity, UUID> {

    Page<MemberEntity> findByMemberName(Pageable pageable, String memberName);
}
