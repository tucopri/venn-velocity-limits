package org.venn.velocitylimit.model;

public record TransactionResponse(long id, long customerId, boolean accepted) {
}
