package com.crm.repository;
import com.crm.entity.Client;
import com.crm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByAssignedTo(User user);
    List<Client> findByStatus(Client.Status status);
    List<Client> findByCompanyNameContainingIgnoreCase(String name);
    long countByStatus(Client.Status status);
    @Query("SELECT c.status, COUNT(c) FROM Client c GROUP BY c.status")
    List<Object[]> countByStatusGroup();
}
