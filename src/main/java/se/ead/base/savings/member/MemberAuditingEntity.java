package se.ead.base.savings.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Data
@RevisionEntity
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "member_revision")
public class MemberAuditingEntity {

    @Id
    @RevisionNumber
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_revision_seq")
    @SequenceGenerator(name = "member_revision_seq", sequenceName = "member_revision_seq", allocationSize = 1)
    private int rev;

    @RevisionTimestamp
    private long revtstmp;
}
