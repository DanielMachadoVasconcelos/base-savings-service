package se.ead.base.savings.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record Member(
    @JsonProperty("member_id") UUID id,
    @JsonProperty("member_version") Long version,
    @JsonProperty("member_name") @NotNull String name,
    @JsonProperty("member_email") @Email String email,
    @JsonProperty("member_biography") String biography
) {
}
