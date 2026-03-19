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
3. `src/main/resources/db/04_rbac_schema.sql`
4. `src/main/resources/db/05_pay_channel_seed.sql`

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

## RBAC APIs

- Login: `POST /api/auth/login`
- Current user: `GET /api/auth/me`
- Menu CRUD: `GET/POST/PUT/DELETE /api/system/menus`
- Role CRUD: `GET/POST/PUT/DELETE /api/system/roles`
- User CRUD: `GET/POST/PUT/DELETE /api/system/users`
- PayChannel CRUD: `GET/POST/PUT/DELETE /api/system/pay-channels`

Permissions are read from DB tables:

- `sys_user`
- `sys_role`
- `sys_menu`
- `sys_user_role_rel`
- `sys_role_menu_rel`

`admin` is super admin and always gets all menus.

## Channel Integration Skeleton (Wechat / Alipay / Allinpay)

- Channel abstraction: `infrastructure/channel/ChannelClient.java`
- Implementations:
  - `WechatChannelClient.java`
  - `AlipayChannelClient.java`
  - `AllinpayChannelClient.java`
- Channel config reader:
  - `PayChannelMapper.java`
  - `ChannelRoutingService.java`
- High availability entry:
  - `PaymentApplicationService#createCashierOrder`
  - Multi-channel failover in same platform type
  - Timeout/retry/bulkhead in `ChannelHttpExecutor`

### Replace with official SDK signing

Current `ChannelCryptoFacade` is a pluggable signing/verify facade.
You can replace methods with official SDK calls:

- `signWechatV3 / verifyWechatV3`
- `signAlipay / verifyAlipay`
- `signAllinpay / verifyAllinpay`

## Official SDK status

- Wechat: implemented with official `wechatpay-apache-httpclient`
- Alipay: implemented with official `alipay-sdk-java`
- Allinpay: skeleton kept, waiting for your production SDK/doc

### pay_channel.api_config required keys

- `WX_NATIVE`
  - `gatewayUrl`
  - `mchId`
  - `appId`
  - `serialNo`
  - `privateKey` (PEM content)
  - `apiV3Key`
- `ALI_QR`
  - `gatewayUrl`
  - `appId`
  - `privateKey`
  - `alipayPublicKey`
  - `signType` (`RSA2`)

### HA behavior

- `createCashierOrder` supports same-platform channel failover:
  - if first active channel fails, automatically tries next active channel
- HTTP invocation has:
  - timeout
  - retry
  - bulkhead isolation per channel

### Lint & format

```bash
cd frontend
npm run lint
npm run format
```
