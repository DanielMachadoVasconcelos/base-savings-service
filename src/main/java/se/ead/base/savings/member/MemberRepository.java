package se.ead.base.savings.member;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends ListPagingAndSortingRepository<MemberEntity, UUID>,
        ListCrudRepository<MemberEntity, UUID>, RevisionRepository<MemberEntity, UUID, Integer> {

    @Query("SELECT m FROM member m WHERE m.memberName ILIKE :name")
    Page<MemberEntity> findByMemberNameLike(Pageable pageable, @Param("name") String memberName);

}
