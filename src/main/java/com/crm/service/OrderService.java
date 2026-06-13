package com.crm.service;
import com.crm.entity.Order;
import com.crm.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    public Order save(Order order) {
        if (order.getOrderNumber() == null || order.getOrderNumber().isEmpty()) {
            order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        return orderRepository.save(order);
    }
    public Optional<Order> findById(Long id) { return orderRepository.findById(id); }
    public List<Order> findAll() { return orderRepository.findAll(); }
    public void delete(Long id) { orderRepository.deleteById(id); }
    public long countAll() { return orderRepository.count(); }
    public long countByStatus(Order.Status status) { return orderRepository.countByStatus(status); }
    public long countThisMonth() { return orderRepository.countThisMonth(); }
    public BigDecimal getTotalRevenue() {
        BigDecimal r = orderRepository.getTotalRevenue();
        return r != null ? r : BigDecimal.ZERO;
    }
}
