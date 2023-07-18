package se.ead.base.savings.categories;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
    public Category createCategory(@RequestBody CategoryEntity entity) {
        return categoryMapper.from(categoryRepository.save(entity));
    }

    @GetMapping
    public Page<Category> getCategories() {
        return categoryRepository.findAll(Pageable.ofSize(10))
                .map(categoryMapper::from);
    }
}
