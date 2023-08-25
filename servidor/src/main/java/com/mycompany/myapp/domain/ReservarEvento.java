package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Clase ReservarEvento. Permite reservar eventos
 */
@Table("reservar_evento")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReservarEvento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("fecha_reservacion")
    private Instant fechaReservacion;

    @Column("total_reservacion")
    private Double totalReservacion;

    @Transient
    @JsonIgnoreProperties(value = { "reservarHabitacions", "reservarEventos", "facturas" }, allowSetters = true)
    private Cliente clienteReservaEvento;

    @Transient
    @JsonIgnoreProperties(
        value = { "reservarHabitacions", "reservarEventos", "eventos", "facturas", "tipoCargoColaborador" },
        allowSetters = true
    )
    private Colaborador colaboradorReservaEvento;

    @Transient
    private DetalleFactura detalleFactura;

    @Column("cliente_reserva_evento_id")
    private Long clienteReservaEventoId;

    @Column("colaborador_reserva_evento_id")
    private Long colaboradorReservaEventoId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReservarEvento id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getFechaReservacion() {
        return this.fechaReservacion;
    }

    public ReservarEvento fechaReservacion(Instant fechaReservacion) {
        this.setFechaReservacion(fechaReservacion);
        return this;
    }

    public void setFechaReservacion(Instant fechaReservacion) {
        this.fechaReservacion = fechaReservacion;
    }

    public Double getTotalReservacion() {
        return this.totalReservacion;
    }

    public ReservarEvento totalReservacion(Double totalReservacion) {
        this.setTotalReservacion(totalReservacion);
        return this;
    }

    public void setTotalReservacion(Double totalReservacion) {
        this.totalReservacion = totalReservacion;
    }

    public Cliente getClienteReservaEvento() {
        return this.clienteReservaEvento;
    }

    public void setClienteReservaEvento(Cliente cliente) {
        this.clienteReservaEvento = cliente;
        this.clienteReservaEventoId = cliente != null ? cliente.getId() : null;
    }

    public ReservarEvento clienteReservaEvento(Cliente cliente) {
        this.setClienteReservaEvento(cliente);
        return this;
    }

    public Colaborador getColaboradorReservaEvento() {
        return this.colaboradorReservaEvento;
    }

    public void setColaboradorReservaEvento(Colaborador colaborador) {
        this.colaboradorReservaEvento = colaborador;
        this.colaboradorReservaEventoId = colaborador != null ? colaborador.getId() : null;
    }

    public ReservarEvento colaboradorReservaEvento(Colaborador colaborador) {
        this.setColaboradorReservaEvento(colaborador);
        return this;
    }

    public DetalleFactura getDetalleFactura() {
        return this.detalleFactura;
    }

    public void setDetalleFactura(DetalleFactura detalleFactura) {
        if (this.detalleFactura != null) {
            this.detalleFactura.setEventoReservado(null);
        }
        if (detalleFactura != null) {
            detalleFactura.setEventoReservado(this);
        }
        this.detalleFactura = detalleFactura;
    }

    public ReservarEvento detalleFactura(DetalleFactura detalleFactura) {
        this.setDetalleFactura(detalleFactura);
        return this;
    }

    public Long getClienteReservaEventoId() {
        return this.clienteReservaEventoId;
    }

    public void setClienteReservaEventoId(Long cliente) {
        this.clienteReservaEventoId = cliente;
    }

    public Long getColaboradorReservaEventoId() {
        return this.colaboradorReservaEventoId;
    }

    public void setColaboradorReservaEventoId(Long colaborador) {
        this.colaboradorReservaEventoId = colaborador;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReservarEvento)) {
            return false;
        }
        return id != null && id.equals(((ReservarEvento) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReservarEvento{" +
            "id=" + getId() +
            ", fechaReservacion='" + getFechaReservacion() + "'" +
            ", totalReservacion=" + getTotalReservacion() +
            "}";
    }
}
