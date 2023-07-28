package se.ead.base.savings.categories;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/v1/categories")
public class CategoryController {

    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @PostMapping
    @RolesAllowed("ROLE_ADMIN")
    public Category createCategory(@RequestBody CategoryEntity entity) {
        return categoryMapper.from(categoryRepository.save(entity));
    }

    @GetMapping
    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    public Page<Category> getCategories() {
        return categoryRepository.findAll(Pageable.ofSize(10))
                .map(categoryMapper::from);
    }

    @PatchMapping("/{categoryId}")
    @RolesAllowed("ROLE_ADMIN")
    public Category updateCategoryName(@PathVariable @NotNull UUID categoryId,
                                       @RequestBody UpdateCategoryNameRequest request) {

        return categoryRepository.findById(categoryId)
                .map(category -> category.withCategoryName(request.categoryName()))
                .map(categoryRepository::save)
                .map(categoryMapper::from)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category id: %s".formatted(categoryId)));
    }

    private record UpdateCategoryNameRequest(@NotEmpty String categoryName) { }
}
