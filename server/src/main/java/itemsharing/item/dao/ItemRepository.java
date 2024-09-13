package itemsharing.item.dao;

import itemsharing.item.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwnerIdOrderByIdAsc(Long userId);

    @Query("SELECT i FROM Item i WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1,'%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1,'%'))) AND i.available = true")
    Collection<Item> findByNameOrDescriptionContaining(String text);
}
