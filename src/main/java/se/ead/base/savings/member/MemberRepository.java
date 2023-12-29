package se.ead.base.savings.member;

import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;

@Transactional
public interface MemberRepository extends ListPagingAndSortingRepository<MemberEntity, UUID>,
        ListCrudRepository<MemberEntity, UUID>, RevisionRepository<MemberEntity, UUID, Integer> {

    @Query(nativeQuery = true,
        value = "SELECT * FROM public.member WHERE SIMILARITY(member.name, ?) > 0.4",
        countQuery = "SELECT COUNT(*) FROM public.member WHERE SIMILARITY(member.name, ?) > 0.4"
    )
    Page<MemberEntity> findByMemberNameLike(Pageable pageable, String memberName);

}
