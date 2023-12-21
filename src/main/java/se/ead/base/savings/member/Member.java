package se.ead.base.savings.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record Member(
    @JsonProperty("member_id") UUID memberId,
    @JsonProperty("member_name") @NotNull String memberName,
    @JsonProperty("member_email") @Email String email
) {

}
