package se.ead.base.savings.categories;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface CategoryRepository extends ListPagingAndSortingRepository<CategoryEntity, String>, ListCrudRepository<CategoryEntity, String> {
}
