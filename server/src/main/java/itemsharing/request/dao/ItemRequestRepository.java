package itemsharing.request.dao;

import itemsharing.request.model.ItemRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Long>, JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorId(Long requestorId);

    Optional<ItemRequest> findByIdOrderByCreatedAsc(Long itemRequestId);
}
