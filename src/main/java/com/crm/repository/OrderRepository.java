package com.crm.repository;
import com.crm.entity.Order;
import com.crm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCreatedBy(User user);
    List<Order> findByStatus(Order.Status status);
    long countByStatus(Order.Status status);
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status != 'CANCELLED'")
    BigDecimal getTotalRevenue();
    @Query("SELECT COUNT(o) FROM Order o WHERE MONTH(o.createdAt) = MONTH(CURRENT_DATE) AND YEAR(o.createdAt) = YEAR(CURRENT_DATE)")
    long countThisMonth();
}
