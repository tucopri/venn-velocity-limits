package org.venn.velocitylimit;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.venn.velocitylimit.service.VelocityLimitService;

@SpringBootApplication
@RequiredArgsConstructor
public class VelocityLimitApplication implements CommandLineRunner {
  private static final String INPUT_PATH = "input.txt";
  private static final String OUTPUT_PATH = "output.txt";

  private final VelocityLimitService velocityLimitService;

  public static void main(String[] args) {
    SpringApplication.run(VelocityLimitApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    velocityLimitService.processInput(INPUT_PATH, OUTPUT_PATH);
  }

}
