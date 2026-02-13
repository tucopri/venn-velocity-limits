package org.venn.velocitylimit.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.venn.velocitylimit.domain.Transaction;
import org.venn.velocitylimit.domain.TransactionSummary;
import org.venn.velocitylimit.model.TransactionResponse;
import org.venn.velocitylimit.repository.TransactionRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class VelocityLimitService {

  private static final String TYPE_DAY = "day";
  private static final double DAY_LIMIT = 5000d;
  private static final double WEEK_LIMIT = 20000d;
  private static final int DAILY_TRANSACTION_COUNT_LIMIT = 3;

  private final TransactionRepository transactionRepository;
  private final ObjectMapper mapper;

  public void processInput(String inputPath, String outputPath) {
    List<TransactionResponse> transactionResponses = new ArrayList<>();
    int lineNumber = 0;

    try (BufferedReader br = new BufferedReader(new java.io.FileReader(inputPath))) {
      String line;
      while ((line = br.readLine()) != null) {
        lineNumber++;
        try {
          Transaction transaction = mapper.readValue(line, Transaction.class);

          processTransaction(transactionResponses, transaction);
        } catch (Exception e) {
          log.error("Error processing line {}: {}", lineNumber, e.getMessage());
        }
      }
      printOutput(outputPath, transactionResponses);
    } catch (Exception e) {
      log.error("Error reading input file: {}", e.getMessage());
    }
  }

  protected void processTransaction(List<TransactionResponse> transactionResponses, Transaction transaction) {
    TransactionResponse transactionResponse = checkVelocityLimits(transaction);

    transactionResponses.add(transactionResponse);

    if (transactionResponse.accepted()) {
      transactionRepository.save(transaction);
    }
  }

  private TransactionResponse checkVelocityLimits(Transaction transaction) {
    boolean acceptTransaction = true;

    var transactionValue = transaction.getLoadAmount().doubleValue();

    if (transactionValue > DAY_LIMIT) {
      acceptTransaction = false;
    } else {
      var transactionDate = transaction.getTimestamp().toLocalDate();

      var dayRange = transactionDate.atStartOfDay();
      var weekRange = transactionDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();

      List<TransactionSummary> history =
          transactionRepository.findVelocityLimit(transaction.getCustomerId(), dayRange, weekRange);
      for (TransactionSummary summary : history) {
        if (TYPE_DAY.equals(summary.type()) && summary.totalAmount() != null) {
          if (summary.transactionCount() >= DAILY_TRANSACTION_COUNT_LIMIT) {
            acceptTransaction = false;
          }
          if (summary.totalAmount().doubleValue() + transactionValue > DAY_LIMIT) {
            acceptTransaction = false;
          }
        } else {
          //week
          if (summary.totalAmount() != null && summary.totalAmount().doubleValue() + transactionValue > WEEK_LIMIT) {
            acceptTransaction = false;
          }
        }
      }
    }

    return new TransactionResponse(transaction.getId(), transaction.getCustomerId(), acceptTransaction);
  }

  public void printOutput(String outputPath, List<TransactionResponse> transactionResponses) throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath))) {
      for (TransactionResponse transactionResponse : transactionResponses) {
        writer.write(mapper.writeValueAsString(transactionResponse) + "\n");
      }
    }
  }
}
