package org.venn.velocitylimit.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.venn.velocitylimit.model.serializer.CurrencyDeserializer;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long myId;

  private Long id;

  @JsonProperty("customer_id")
  private Long customerId;

  @JsonProperty("load_amount")
  @JsonDeserialize(using = CurrencyDeserializer.class)
  private BigDecimal loadAmount;

  @JsonProperty("time")
  private LocalDateTime timestamp;
}
