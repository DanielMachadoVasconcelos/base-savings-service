package se.ead.base.savings.member;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import se.ead.base.savings.encryption.StringToBytesMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        uses = {StringToBytesMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface MemberMapper {

    MemberEntity from(Member member);

    Member from(MemberEntity entity);

    @AfterMapping
    default void updateDefaultValues(@MappingTarget MemberEntity entity) {
        if(entity.getVersion() != null || entity.getVersion() < 1)  {
            entity.setVersion(1L);
        }
    }
}
