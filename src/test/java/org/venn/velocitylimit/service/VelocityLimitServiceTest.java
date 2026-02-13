package org.venn.velocitylimit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.venn.velocitylimit.domain.Transaction;
import org.venn.velocitylimit.domain.TransactionSummary;
import org.venn.velocitylimit.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class VelocityLimitServiceTest {
  private static final Long ID = 1L;
  private static final Long CUSTOMER_ID = 2L;
  @Mock
  TransactionRepository transactionRepository;

  @InjectMocks
  VelocityLimitService sut;

  @Test
  void processTransactionNoHistory() {
    sut.processTransaction(new ArrayList<>(), Transaction.builder()
        .id(ID)
        .customerId(CUSTOMER_ID)
        .loadAmount(BigDecimal.valueOf(1000d))
        .timestamp(LocalDateTime.now())
        .build());

    verify(transactionRepository).save(any());
  }

  @Test
  void processTransactionNoHistoryOverLimit() {
    sut.processTransaction(new ArrayList<>(), Transaction.builder()
        .id(ID)
        .customerId(CUSTOMER_ID)
        .loadAmount(BigDecimal.valueOf(6000d))
        .timestamp(LocalDateTime.now())
        .build());

    verify(transactionRepository, never()).save(any());
  }

  @Test
  void processTransactionDailyNumberOfTransactionLimit() {
    when(transactionRepository.findVelocityLimit(any(), any(), any())).thenReturn(
        List.of(new TransactionSummary("day", 2L, BigDecimal.valueOf(1d))));

    sut.processTransaction(new ArrayList<>(), Transaction.builder()
        .id(ID)
        .customerId(CUSTOMER_ID)
        .loadAmount(BigDecimal.valueOf(1d))
        .timestamp(LocalDateTime.now())
        .build());

    verify(transactionRepository).save(any());
  }

  @Test
  void processTransactionDailyNumberOfTransactionLimitExceeded() {
    when(transactionRepository.findVelocityLimit(any(), any(), any())).thenReturn(
        List.of(new TransactionSummary("day", 3L, BigDecimal.valueOf(1d))));

    sut.processTransaction(new ArrayList<>(), Transaction.builder()
        .id(ID)
        .customerId(CUSTOMER_ID)
        .loadAmount(BigDecimal.valueOf(1d))
        .timestamp(LocalDateTime.now())
        .build());

    verify(transactionRepository, never()).save(any());
  }

  @Test
  void processTransactionExactDailyValueLimit() {
    when(transactionRepository.findVelocityLimit(any(), any(), any())).thenReturn(
        List.of(new TransactionSummary("day", 1L, BigDecimal.valueOf(4999d))));

    sut.processTransaction(new ArrayList<>(), Transaction.builder()
        .id(ID)
        .customerId(CUSTOMER_ID)
        .loadAmount(BigDecimal.valueOf(1d))
        .timestamp(LocalDateTime.now())
        .build());

    verify(transactionRepository).save(any());
  }

  @Test
  void processTransactionDailyValueLimitExceeded() {
    when(transactionRepository.findVelocityLimit(any(), any(), any())).thenReturn(
        List.of(new TransactionSummary("day", 1L, BigDecimal.valueOf(4999d))));

    sut.processTransaction(new ArrayList<>(), Transaction.builder()
        .id(ID)
        .customerId(CUSTOMER_ID)
        .loadAmount(BigDecimal.valueOf(2d))
        .timestamp(LocalDateTime.now())
        .build());

    verify(transactionRepository, never()).save(any());
  }

  @Test
  void processTransactionExactWeeklyLimit() {
    when(transactionRepository.findVelocityLimit(any(), any(), any())).thenReturn(
        List.of(new TransactionSummary("week", 1L, BigDecimal.valueOf(19999d))));

    sut.processTransaction(new ArrayList<>(), Transaction.builder()
        .id(ID)
        .customerId(CUSTOMER_ID)
        .loadAmount(BigDecimal.valueOf(1d))
        .timestamp(LocalDateTime.now())
        .build());

    verify(transactionRepository).save(any());
  }

  @Test
  void processTransactionWeeklyLimitExceeded() {
    when(transactionRepository.findVelocityLimit(any(), any(), any())).thenReturn(
        List.of(new TransactionSummary("week", 1L, BigDecimal.valueOf(19999d))));

    sut.processTransaction(new ArrayList<>(), Transaction.builder()
        .id(ID)
        .customerId(CUSTOMER_ID)
        .loadAmount(BigDecimal.valueOf(2d))
        .timestamp(LocalDateTime.now())
        .build());

    verify(transactionRepository, never()).save(any());
  }

  @Test
  void processTransactionLimitCombination() {
    when(transactionRepository.findVelocityLimit(any(), any(), any())).thenReturn(
        List.of(new TransactionSummary("day", 1L, BigDecimal.valueOf(1d)),
            new TransactionSummary("week", 1L, BigDecimal.valueOf(19999d))));

    sut.processTransaction(new ArrayList<>(), Transaction.builder()
        .id(ID)
        .customerId(CUSTOMER_ID)
        .loadAmount(BigDecimal.valueOf(1d))
        .timestamp(LocalDateTime.now())
        .build());

    verify(transactionRepository).save(any());
  }

  @Test
  void processTransactionLimitCombinationExceed() {
    when(transactionRepository.findVelocityLimit(any(), any(), any())).thenReturn(
        List.of(new TransactionSummary("day", 1L, BigDecimal.valueOf(1d)),
            new TransactionSummary("week", 1L, BigDecimal.valueOf(19999d))));

    sut.processTransaction(new ArrayList<>(), Transaction.builder()
        .id(ID)
        .customerId(CUSTOMER_ID)
        .loadAmount(BigDecimal.valueOf(2d))
        .timestamp(LocalDateTime.now())
        .build());

    verify(transactionRepository, never()).save(any());
  }

}
