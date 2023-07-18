package se.ead.base.savings.categories;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record Category(
        @JsonProperty("category_id") UUID categoryId,
        @JsonProperty("category_name") String categoryName
) {
}
