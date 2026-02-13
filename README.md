# Velocity limit

---

Project created for venn coding challenge by the candidate Antonio Prieto

This project was created using Java, spring and maven

The main class is VelocityLimitAplication, when executed it runs as a command line runner, it uses the input and output files from on the project root. 

It reads each line as a json representing the transaction, and validates it agains the history if no limit is exceeded then the transaction is stored in an in-memory h2 db and can be used for future validations.

Most of the logic is in the VelocityLimitService class, which is responsible for invoking the repository and doing the validation.

The transaction Repository uses a simple inherited save and a native query to get the total number of transactions in two date ranges, I simplified it as only above a date as the input is in chronological orded

## Project structure
- Config, define time module to be used in jackson
- Domain jpa repository elements as the entity and the return record
- Model specific for json models and custom serializers
- Repository, transaction repository for db interaction
- Service, service layer for logic

---

## Tests

I added tests only to the VelocityLimitService, as it's the only one with business logic, the other clases are used as helpers

I did not include tests for the input and output files and json parsing.

---
