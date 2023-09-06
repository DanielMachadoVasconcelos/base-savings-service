package se.ead.base.savings.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record Member(
    @JsonProperty("member_id") UUID memberId,
    @JsonProperty("member_name") String memberName
) {

}
