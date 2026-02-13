package org.venn.velocitylimit.domain;

import java.math.BigDecimal;

public record TransactionSummary(String type,  Long transactionCount, BigDecimal totalAmount) {
}
