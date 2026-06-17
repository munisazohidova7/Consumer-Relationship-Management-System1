package com.crm.service;
import com.crm.entity.Order;
import com.crm.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public List<Order> findAll() {
        return orderRepository.findAll(Sort.by("createdAt").descending());
    }

    public Page<Order> findAllPaged(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Page<Order> findByStatusPaged(Order.Status status, int page, int size) {
        return orderRepository.findByStatus(status, PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Page<Order> searchPaged(String search, int page, int size) {
        return orderRepository.findByOrderNumberContainingIgnoreCaseOrProductTypeContainingIgnoreCase(
            search, search, PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    public long countAll() {
        return orderRepository.count();
    }

    public long countByStatus(Order.Status status) {
        return orderRepository.countByStatus(status);
    }

    public long countThisMonth() {
        LocalDateTime start = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return orderRepository.countByCreatedAtAfter(start);
    }

    public BigDecimal getTotalRevenue() {
        BigDecimal total = orderRepository.getTotalRevenue();
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<Order> findRecent(int limit) {
        return orderRepository.findAll(PageRequest.of(0, limit, Sort.by("createdAt").descending())).getContent();
    }
}
