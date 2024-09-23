# Getting Started

### 1. Docker Build

```bash
docker build -t project-transaction .
```


### 2. Run the PostgreSQL Container

```bash
docker run --name postgres-db -e POSTGRES_DB=postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:latest
```

### Create some rows
Create User
```bash
INSERT INTO users (name) VALUES
('John Doe');
```

Create Account
```bash
INSERT INTO accounts (id, user_id) VALUES
(1, 1);

```

Create Balance
```bash
INSERT INTO balances (account_id, category, amount) VALUES
(1, 'FOOD', 150.00),  -- Balance for account with ID 1
(1, 'MEAL', 200.50)  -- Another balance for the same account
(1, 'CASH', 300.00)  -- Another balance for the same account
```

### 3. Run Your Application Container
```bash
docker run -[kotlin](src%2Fmain%2Fkotlin)-name project-app --link postgres-db -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db:5432/postgres -e SPRING_DATASOURCE_USERNAME=postgres -e SPRING_DATASOURCE_PASSWORD=postgres project-transaction
```

### 4. Test endpoint

In Postman : POST http://localhost:8080/api/authorize

```bash
{
    "account": 1, 
    "totalAmount": 100.00, 
    "mcc": "5811", 
    "merchant": "PADARIA DO ZE SAO PAULO BR"
}
```

### 5. L4

If we have millions accounts into a reasonable number of partitions (e.g., 1,000, 2000 , 5000 or 10,000 partitions).
Each partition can still process transactions sequentially for the accounts it manages.
Use some hashing mechanism based on account IDs to determine which partition an account belongs to, ensuring that all transactions for the same account are routed to the same partition.
For example Kafka supports partitioned topics, where each partition can act as a queue. The idea is to make sure that transactions for the same account always go to the same partition, guaranteeing sequential processing within that partition.
