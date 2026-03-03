// src/main/java/br/org/postalis/training/rh/shared/domain/EventMetadata.java
package br.org.postalis.training.rh.shared.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Metadata para eventos de domínio.
 * <p>
 * Fornece informações de auditoria, rastreabilidade e diagnóstico.
 * Armazenada em campo JSONB separado do payload do evento.
 * <p>
 * Campos Obrigatórios:
 * - correlationId: Agrupa eventos da mesma requisição/fluxo
 * - source: Origem do comando (SYNC/ASYNC/BATCH)
 * - timestamp: Momento de criação da metadata
 * <p>
 * Campos Opcionais:
 * - actorId: Usuário/sistema responsável pela mudança
 * - causationId: ID do comando/evento que causou este evento
 * - commandType: Nome do comando que originou a mudança
 * - tenantId: Identificador do tenant (se multi-tenant)
 * - traceId/spanId: Integração com OpenTelemetry
 * <p>
 * Exemplo:
 * EventMetadata metadata = EventMetadata.builder()
 * .actorId(userId)
 * .correlationId(UUID.randomUUID())
 * .source("SYNC")
 * .commandType("ContratarFuncionarioCommand")
 * .build();
 */
public final class EventMetadata {

    private final String actorId;
    private final UUID correlationId;
    private final UUID causationId;
    private final String commandType;
    private final String source;
    private final String tenantId;
    private final String traceId;
    private final String spanId;
    private final Instant timestamp;

    private EventMetadata(Builder builder) {
        this.actorId = builder.actorId;
        this.correlationId = Objects.requireNonNull(
                builder.correlationId, "correlationId é obrigatório");
        this.causationId = builder.causationId;
        this.commandType = builder.commandType;
        this.source = Objects.requireNonNull(
                builder.source, "source é obrigatório");
        this.tenantId = builder.tenantId;
        this.traceId = builder.traceId;
        this.spanId = builder.spanId;
        this.timestamp = Objects.requireNonNull(
                builder.timestamp, "timestamp é obrigatório");
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getActorId() {
        return actorId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public UUID getCausationId() {
        return causationId;
    }

    public String getCommandType() {
        return commandType;
    }

    public String getSource() {
        return source;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventMetadata that = (EventMetadata) o;
        return Objects.equals(correlationId, that.correlationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(correlationId);
    }

    @Override
    public String toString() {
        return "EventMetadata{" +
                "actorId='" + actorId + '\'' +
                ", correlationId=" + correlationId +
                ", source='" + source + '\'' +
                ", commandType='" + commandType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    /**
     * Builder para construção fluente de EventMetadata.
     */
    public static final class Builder {
        private String actorId;
        private UUID correlationId;
        private UUID causationId;
        private String commandType;
        private String source;
        private String tenantId;
        private String traceId;
        private String spanId;
        private Instant timestamp;

        private Builder() {
            // Defaults sensatos
            this.correlationId = UUID.randomUUID();
            this.timestamp = Instant.now();
            this.source = "SYNC";
        }

        /**
         * ID do usuário ou sistema que executou a ação.
         */
        public Builder actorId(String actorId) {
            this.actorId = actorId;
            return this;
        }

        /**
         * ID de correlação para agrupar eventos da mesma requisição.
         */
        public Builder correlationId(UUID correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        /**
         * ID do comando ou evento que causou este evento.
         */
        public Builder causationId(UUID causationId) {
            this.causationId = causationId;
            return this;
        }

        /**
         * Tipo do comando que originou este evento.
         */
        public Builder commandType(String commandType) {
            this.commandType = commandType;
            return this;
        }

        /**
         * Origem: SYNC (síncrono), ASYNC (Service Bus), BATCH.
         */
        public Builder source(String source) {
            this.source = source;
            return this;
        }

        /**
         * ID do tenant (para sistemas multi-tenant).
         */
        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Trace ID para OpenTelemetry.
         */
        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        /**
         * Span ID para OpenTelemetry.
         */
        public Builder spanId(String spanId) {
            this.spanId = spanId;
            return this;
        }

        /**
         * Timestamp de criação da metadata.
         */
        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public EventMetadata build() {
            return new EventMetadata(this);
        }
    }
}