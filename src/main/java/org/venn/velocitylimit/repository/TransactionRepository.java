package org.venn.velocitylimit.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.venn.velocitylimit.domain.Transaction;
import org.venn.velocitylimit.domain.TransactionSummary;

public interface TransactionRepository extends Repository<Transaction, Long> {
  Transaction save(Transaction transaction);

  @Query(value = """
      SELECT 'day' as type, COUNT(1) as transactionCount, SUM(t.load_amount) totalAmount
            FROM transaction t
                  WHERE timestamp >= :dayRange AND customer_id = :customerId
      UNION ALL
       SELECT 'week' as type, COUNT(1) as transactionCount, SUM(t.load_amount) totalAmount 
            FROM transaction t
                  WHERE timestamp >= :weekRange AND customer_id = :customerId
      """, nativeQuery = true)
  List<TransactionSummary> findVelocityLimit(Long customerId, LocalDateTime dayRange, LocalDateTime weekRange);
}
