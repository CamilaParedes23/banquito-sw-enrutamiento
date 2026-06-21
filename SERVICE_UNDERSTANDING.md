# Entendimiento del servicio

`banquito-switch-routing-service` decide el camino operativo de cada linea publicada por `batch-service`.

## Nota de migracion vigente

Routing no se integra con Core. Su cambio principal sera propagar `reservationUuid` hacia eventos On-Us/Off-Us cuando batch-service migre a Core REST/Kong. `coreFundingId` debe considerarse legacy/transicional.

## Responsabilidad en lenguaje simple

Recibe una linea ya validada y fondeada, mira el `routingCode` y decide si va al flujo On-Us, al flujo Off-Us o si debe rechazarse por datos insuficientes o catalogo invalido.

## Flujo interno principal

1. `PaymentLineRequestedListener` consume mensajes desde `switch.routing.payment-lines.queue`.
2. `RoutingServiceImpl` valida identificadores y revisa idempotencia por `lineId`.
3. El servicio registra el snapshot en `"ENRUTAMIENTO_LINEA_PAGO"`.
4. Se consulta el catalogo local de routing codes.
5. Si el codigo es `10`, se publica `PaymentLineRoutedOnUsEvent`.
6. Si el codigo es `30`, `32` o `35`, se publica `PaymentLineRoutedOffUsEvent`.
7. Si faltan datos minimos o el codigo no existe, se publica `PaymentLineRejectedEvent`.
8. La decision o rechazo queda registrado en tablas propias del servicio.

## Paquetes importantes

- `config`: declaracion de exchanges, queues, bindings y converter JSON de RabbitMQ.
- `listener`: listener RabbitMQ pequeno, sin reglas de negocio.
- `dto.event`: eventos consumidos y publicados.
- `enums`: catalogo local de instituciones, tipo de ruta y estado de routing.
- `mapper`: conversion manual entre eventos y entidades.
- `model`: entidades JPA propias del routing-service.
- `repository`: acceso a tablas propias.
- `service` y `service.impl`: interfaces e implementaciones de aplicacion.

## Clases principales

- `PaymentLineRequestedListener`: recibe eventos de linea y delega.
- `RoutingServiceImpl`: aplica idempotencia, clasifica y registra decisiones.
- `RabbitRoutingEventPublisher`: publica eventos en `switch.routing.exchange`.
- `RoutingEventMapper`: construye entidades y eventos sin acceder a repositorios.
- `RoutingInstitution`: catalogo local de routing codes vigente.

## Tablas propias

- `"ENRUTAMIENTO_LINEA_PAGO"`: snapshot de la linea y estado de enrutamiento.
- `"DECISION_ENRUTAMIENTO"`: decision On-Us u Off-Us publicada.
- `"ERROR_ENRUTAMIENTO"`: rechazo funcional generado por routing.

No existen foreign keys hacia otros servicios ni hacia el Core Bancario.

## Eventos propios

Consume:

- `PaymentLineRequestedEvent`

Publica:

- `PaymentLineRoutedOnUsEvent`
- `PaymentLineRoutedOffUsEvent`
- `PaymentLineRejectedEvent`

## Integraciones externas

Solo integra con RabbitMQ y PostgreSQL. No llama al Core Bancario y no consume servicios REST/gRPC internos.

## Que NO hace

- No expone endpoints REST de negocio.
- No acredita beneficiarios.
- No procesa lineas On-Us contra el Core.
- No genera archivos de compensacion Off-Us.
- No calcula comisiones.
- No genera reportes finales ni notificaciones.
- No afecta saldos ni contabilidad.

## Pendientes o limitaciones conocidas

- `on-us-settlement-service`, clearing Off-Us, billing y reporting aun deben implementar sus consumidores reales.
- El catalogo de instituciones esta fijo en enum para esta fase; en una fase posterior puede moverse a tabla parametrica propia del servicio si se define el contrato.
- No se implementaron dead-letter queues; quedan para una fase de resiliencia avanzada.
