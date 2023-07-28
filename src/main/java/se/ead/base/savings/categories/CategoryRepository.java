package se.ead.base.savings.categories;

import java.util.UUID;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface  CategoryRepository extends ListPagingAndSortingRepository<CategoryEntity, UUID>, ListCrudRepository<CategoryEntity, UUID> {
}
