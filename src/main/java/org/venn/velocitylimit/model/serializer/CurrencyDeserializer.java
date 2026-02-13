package org.venn.velocitylimit.model.serializer;

import com.fasterxml.jackson.databind.JsonDeserializer;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

@Slf4j
public class CurrencyDeserializer extends JsonDeserializer<BigDecimal> {
  @Override
  public BigDecimal deserialize(com.fasterxml.jackson.core.JsonParser parser,
      com.fasterxml.jackson.databind.DeserializationContext ctx) throws java.io.IOException {
    String value = parser.getText();
    if (value == null) {
      return BigDecimal.ZERO;
    }
    String numericValue = value.replaceAll("[^0-9.]", "");

    if (StringUtils.isBlank(numericValue)) {
      return BigDecimal.ZERO;
    }

    try {
      return new BigDecimal(numericValue);
    } catch (NumberFormatException e) {
      log.error("load amount is not a valid number: {}", value);
      return BigDecimal.ZERO;
    }
  }
}
