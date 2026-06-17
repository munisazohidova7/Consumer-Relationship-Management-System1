package com.crm.repository;
import com.crm.entity.Client;
import com.crm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByAssignedTo(User user);
    Page<Client> findByAssignedTo(User user, Pageable pageable);
    Page<Client> findByStatus(Client.Status status, Pageable pageable);
    long countByStatus(Client.Status status);
    List<Client> findByCompanyNameContainingIgnoreCaseOrContactPersonContainingIgnoreCase(String name, String contact);
    Page<Client> findByCompanyNameContainingIgnoreCaseOrContactPersonContainingIgnoreCase(String name, String contact, Pageable pageable);
}
