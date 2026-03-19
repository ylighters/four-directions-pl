# Payment Platform (MyBatis + ShardingSphere + RocketMQ Outbox)

## What is implemented

- MyBatis persistence for `pay_order`, `pay_order_index`, and `mq_outbox`
- ShardingSphere real table routing for `pay_order_00 ~ pay_order_63` (built-in `HASH_MOD`)
- Transactional order flow: order/status log/outbox written in one DB transaction
- Outbox scanner + RocketMQ delivery + retry
- Payment state machine and event-driven transition

## Key files

- App config: `src/main/resources/application.yml`
- ShardingSphere config: `src/main/resources/sharding.yaml`
- Base schema: `src/main/resources/db/01_base_schema.sql`
- Sharding DDL: `src/main/resources/db/02_order_sharding.sql`
- Order repository: `src/main/java/com/example/payment/infrastructure/persistence/repository/MybatisPaymentOrderRepository.java`
- Outbox publisher: `src/main/java/com/example/payment/infrastructure/mq/OutboxPaymentEventPublisher.java`
- Outbox dispatcher: `src/main/java/com/example/payment/infrastructure/mq/outbox/OutboxDispatcher.java`
- Payment service: `src/main/java/com/example/payment/application/service/PaymentApplicationService.java`

## Local dependencies

1. MySQL on `127.0.0.1:3306`, user/password `root/root`
2. RocketMQ NameServer on `127.0.0.1:9876`

## DB initialization

Run SQL in order:

1. `src/main/resources/db/01_base_schema.sql`
2. `src/main/resources/db/02_order_sharding.sql`

The second script creates `pay_order_00` to `pay_order_63` by procedure.

## Current routing strategy

- Logical table: `pay_order`
- Physical tables: `pay_order_00 ~ pay_order_63`
- Sharding key: `merchant_no`
- Routing rule: ShardingSphere `HASH_MOD` with `sharding-count=64`
- Lookup optimization: `pay_order_index(order_no -> merchant_no)` avoids full-shard scan

## Transactional message chain (Outbox pattern)

1. Business transaction writes:
   - `pay_order_xx`
   - `pay_order_status_log`
   - `mq_outbox(send_status=INIT)`
2. Scheduled task scans outbox rows (`INIT/RETRY`)
3. Dispatcher sends to RocketMQ topic/tag
4. Success: mark `SENT`; failure: mark `RETRY` and set `next_retry_time`

## API

### Create order

```bash
curl -X POST "http://localhost:8080/api/payments/orders" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantNo":"M10001",
    "appId":"APP10001",
    "merchantOrderNo":"MO202603190001",
    "amount":99.99,
    "subject":"test order",
    "notifyUrl":"https://merchant.test/notify",
    "returnUrl":"https://merchant.test/return",
    "currency":"CNY",
    "payScene":"API",
    "payWay":"WX"
  }'
```

### Trigger state event

```bash
curl -X POST "http://localhost:8080/api/payments/orders/{orderNo}/events?eventType=CHANNEL_SUCCESS&operator=channel-callback"
```

## Notes

- `mvn` is not available in this execution environment, so compile/run was not executed here.
- If your local Maven is ready, run `mvn spring-boot:run`.

## Frontend (React + TypeScript + Tailwind)

Frontend project path: `frontend`

### Start

```bash
cd frontend
npm install
npm run dev
```

Vite dev server: `http://localhost:5173` (proxy `/api` to `http://localhost:8080`)

### Auth test accounts

- `admin / admin123` -> role `ADMIN`
- `operator / operator123` -> role `OPERATOR`
- `auditor / auditor123` -> role `AUDITOR`

Different roles show different menus on home page.

### Lint & format

```bash
cd frontend
npm run lint
npm run format
```
