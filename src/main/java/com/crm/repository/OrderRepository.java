package com.crm.repository;
import com.crm.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByStatus(Order.Status status, Pageable pageable);
    long countByStatus(Order.Status status);
    long countByCreatedAtAfter(LocalDateTime date);
    Page<Order> findByOrderNumberContainingIgnoreCaseOrProductTypeContainingIgnoreCase(String orderNum, String product, Pageable pageable);
    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    BigDecimal getTotalRevenue();
}
