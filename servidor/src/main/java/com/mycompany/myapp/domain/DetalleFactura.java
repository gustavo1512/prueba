package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A DetalleFactura.
 */
@Table("detalle_factura")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DetalleFactura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("fecha_emitido")
    private Long fechaEmitido;

    @Transient
    private ReservarHabitacion habitacionReservada;

    @Transient
    private ReservarEvento eventoReservado;

    @Transient
    @JsonIgnoreProperties(value = { "detalleFactura", "clienteFactura", "clienteColaborador" }, allowSetters = true)
    private Set<Factura> facturas = new HashSet<>();

    @Column("habitacion_reservada_id")
    private Long habitacionReservadaId;

    @Column("evento_reservado_id")
    private Long eventoReservadoId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DetalleFactura id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFechaEmitido() {
        return this.fechaEmitido;
    }

    public DetalleFactura fechaEmitido(Long fechaEmitido) {
        this.setFechaEmitido(fechaEmitido);
        return this;
    }

    public void setFechaEmitido(Long fechaEmitido) {
        this.fechaEmitido = fechaEmitido;
    }

    public ReservarHabitacion getHabitacionReservada() {
        return this.habitacionReservada;
    }

    public void setHabitacionReservada(ReservarHabitacion reservarHabitacion) {
        this.habitacionReservada = reservarHabitacion;
        this.habitacionReservadaId = reservarHabitacion != null ? reservarHabitacion.getId() : null;
    }

    public DetalleFactura habitacionReservada(ReservarHabitacion reservarHabitacion) {
        this.setHabitacionReservada(reservarHabitacion);
        return this;
    }

    public ReservarEvento getEventoReservado() {
        return this.eventoReservado;
    }

    public void setEventoReservado(ReservarEvento reservarEvento) {
        this.eventoReservado = reservarEvento;
        this.eventoReservadoId = reservarEvento != null ? reservarEvento.getId() : null;
    }

    public DetalleFactura eventoReservado(ReservarEvento reservarEvento) {
        this.setEventoReservado(reservarEvento);
        return this;
    }

    public Set<Factura> getFacturas() {
        return this.facturas;
    }

    public void setFacturas(Set<Factura> facturas) {
        if (this.facturas != null) {
            this.facturas.forEach(i -> i.setDetalleFactura(null));
        }
        if (facturas != null) {
            facturas.forEach(i -> i.setDetalleFactura(this));
        }
        this.facturas = facturas;
    }

    public DetalleFactura facturas(Set<Factura> facturas) {
        this.setFacturas(facturas);
        return this;
    }

    public DetalleFactura addFactura(Factura factura) {
        this.facturas.add(factura);
        factura.setDetalleFactura(this);
        return this;
    }

    public DetalleFactura removeFactura(Factura factura) {
        this.facturas.remove(factura);
        factura.setDetalleFactura(null);
        return this;
    }

    public Long getHabitacionReservadaId() {
        return this.habitacionReservadaId;
    }

    public void setHabitacionReservadaId(Long reservarHabitacion) {
        this.habitacionReservadaId = reservarHabitacion;
    }

    public Long getEventoReservadoId() {
        return this.eventoReservadoId;
    }

    public void setEventoReservadoId(Long reservarEvento) {
        this.eventoReservadoId = reservarEvento;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DetalleFactura)) {
            return false;
        }
        return id != null && id.equals(((DetalleFactura) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DetalleFactura{" +
            "id=" + getId() +
            ", fechaEmitido=" + getFechaEmitido() +
            "}";
    }
}
