package se.ead.base.savings.member;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import se.ead.base.savings.encryption.Encrypted;

@With
@Data
@Audited
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "member")
@Table(name = "member")
@EntityListeners(AuditingEntityListener.class)
public class MemberEntity {

    @Id
    @Column(name = "member_id")
    private UUID memberId;

    @Column(name = "member_name")
    private String memberName;

    @Lob
    @Encrypted
    @Column(name = "member_email", columnDefinition = "bytea")
    private byte[] email;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    private Date modifiedAt;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @LastModifiedBy
    @Column(name = "modified_by")
    private String modifiedBy;

    @PrePersist
    public void ensureId(){
        if (memberId == null) {
            memberId = UUID.randomUUID();
        }
    }
}
