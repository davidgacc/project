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
docker run --name project-app --link postgres-db -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db:5432/postgres -e SPRING_DATASOURCE_USERNAME=postgres -e SPRING_DATASOURCE_PASSWORD=postgres project-transaction
```

