# banquito-switch-routing-service

## Nota de arquitectura objetivo

Este servicio no llama a Core y mantiene su rol asincrono por RabbitMQ. Durante la migracion debe transportar `reservationUuid` cuando llegue desde batch-service y dejar `coreFundingId` solo como campo legacy/transicional.

## Responsabilidad

Servicio responsable de consumir `PaymentLineRequestedEvent`, clasificar cada linea por `routingCode`, registrar la trazabilidad local e iniciar el siguiente tramo asincrono del flujo.

No expone endpoints REST funcionales. No acredita beneficiarios, no llama al Core Bancario, no genera archivo de compensacion, no calcula comisiones y no modifica saldos.

## Ejecucion local

Desde la raiz del workspace:

```powershell
docker compose build routing-service
docker compose up -d postgres rabbitmq routing-service
```

Compilar sin Maven global:

```powershell
docker run --rm -v "${PWD}\banquito-switch-routing-service:/workspace" -w /workspace maven:3.9.9-eclipse-temurin-21 mvn -q test
```

Puerto por defecto: `8082`.

Health:

```http
GET http://localhost:8082/actuator/health
```

## Variables de entorno

- `SERVER_PORT`
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`
- `RABBIT_EXCHANGE_BATCH`
- `RABBIT_QUEUE_ROUTING_PAYMENT_LINES`
- `RABBIT_ROUTING_KEY_PAYMENT_LINE_REQUESTED`
- `RABBIT_EXCHANGE_ROUTING`
- `RABBIT_QUEUE_SETTLEMENT_ON_US`
- `RABBIT_QUEUE_CLEARING_OFF_US`
- `RABBIT_QUEUE_ROUTING_REJECTED`
- `RABBIT_ROUTING_KEY_ROUTED_ON_US`
- `RABBIT_ROUTING_KEY_ROUTED_OFF_US`
- `RABBIT_ROUTING_KEY_LINE_REJECTED`

## Base de datos

Usa `SWITCH_ENRUTAMIENTO_DB`. La base se inicializa localmente con:

```text
src/main/resources/db/init/001_create_routing_tables.sql
```

Hibernate se mantiene en `spring.jpa.hibernate.ddl-auto=validate`; no genera ni actualiza tablas.

Tablas propias:

- `"ENRUTAMIENTO_LINEA_PAGO"`
- `"DECISION_ENRUTAMIENTO"`
- `"ERROR_ENRUTAMIENTO"`

## Catalogo de routing

| Routing code | Institucion      | Tipo   |
| ------------ | ---------------- | ------ |
| `10`         | Banco BanQuito   | ON_US  |
| `30`         | Banco Pichincha  | OFF_US |
| `32`         | Banco Guayaquil  | OFF_US |
| `35`         | Banco Pacifico   | OFF_US |

## Eventos

Consume:

- Exchange: `rabbit.exchange.batch`
- Queue: `rabbit.queue.routing.payment-lines`
- Routing key: `rabbit.routing-key.payment-line-requested`
- Evento: `PaymentLineRequestedEvent`

Publica:

- `PaymentLineRoutedOnUsEvent` en `rabbit.exchange.routing` con `rabbit.routing-key.routed-on-us`
- `PaymentLineRoutedOffUsEvent` en `rabbit.exchange.routing` con `rabbit.routing-key.routed-off-us`
- `PaymentLineRejectedEvent` en `rabbit.exchange.routing` con `rabbit.routing-key.line-rejected`

El servicio declara tambien las colas locales de salida para pruebas y fases posteriores:

- `rabbit.queue.settlement.on-us`
- `rabbit.queue.clearing.off-us`
- `rabbit.queue.routing.rejected`

## Idempotencia

La idempotencia se controla por `lineId` en `"ENRUTAMIENTO_LINEA_PAGO"`. Si llega nuevamente una linea ya registrada, el servicio no publica otro evento ni crea otra decision.

## Prueba manual basica

1. Levantar infraestructura, Core mock, batch y routing:

```powershell
docker compose up -d postgres rabbitmq batch-service routing-service
```

2. Cargar un lote valido desde `batch-service`:

```powershell
$response = Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8081/api/v1/batches/upload" `
  -Form @{ file = Get-Item ".\docs\examples\files\valid_mixed_batch.csv"; channel = "WEB"; receivedBy = "local" }
```

3. Verificar en RabbitMQ que `switch.routing.payment-lines.queue` quede sin mensajes y que los eventos salgan hacia `switch.settlement.on-us.queue` o `switch.clearing.off-us.queue`.

4. Verificar persistencia en `SWITCH_ENRUTAMIENTO_DB`:

```powershell
docker exec banquito-switch-postgres psql -U postgres -d SWITCH_ENRUTAMIENTO_DB -c "select "TIPO_ENRUTAMIENTO", count(*) from "DECISION_ENRUTAMIENTO" group by "TIPO_ENRUTAMIENTO";"
```

## Decisiones tecnicas

- El listener es deliberadamente pequeno y delega en `RoutingService`.
- Los eventos se modelan como DTOs propios del servicio en `dto.event`; no se reutilizan entidades JPA ni clases de batch.
- El converter JSON de RabbitMQ infiere el DTO del listener para evitar acoplamiento al package Java del publicador.
- Los rechazos generados por routing usan `finalStatus=RECHAZADA` y `billable=false`.
