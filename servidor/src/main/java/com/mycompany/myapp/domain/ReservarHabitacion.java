package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Clase ReservarHabitacion. Gestiona la reserva de las habitaciones.
 */
@Table("reservar_habitacion")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReservarHabitacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("fecha_reserva")
    private Instant fechaReserva;

    @Column("fecha_inicio")
    private Instant fechaInicio;

    @Column("fecha_final")
    private Instant fechaFinal;

    @Column("total_reservacion")
    private Double totalReservacion;

    @Transient
    private Habitacion habitacion;

    @Transient
    @JsonIgnoreProperties(value = { "reservarHabitacions", "reservarEventos", "facturas" }, allowSetters = true)
    private Cliente clienteReservaHabitacion;

    @Transient
    @JsonIgnoreProperties(
        value = { "reservarHabitacions", "reservarEventos", "eventos", "facturas", "tipoCargoColaborador" },
        allowSetters = true
    )
    private Colaborador colaboradorReservaHabitacion;

    @Transient
    @JsonIgnoreProperties(value = { "reservarHabitacions" }, allowSetters = true)
    private Persona ocupantes;

    @Transient
    private DetalleFactura detalleFactura;

    @Column("habitacion_id")
    private Long habitacionId;

    @Column("cliente_reserva_habitacion_id")
    private Long clienteReservaHabitacionId;

    @Column("colaborador_reserva_habitacion_id")
    private Long colaboradorReservaHabitacionId;

    @Column("ocupantes_id")
    private Long ocupantesId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReservarHabitacion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getFechaReserva() {
        return this.fechaReserva;
    }

    public ReservarHabitacion fechaReserva(Instant fechaReserva) {
        this.setFechaReserva(fechaReserva);
        return this;
    }

    public void setFechaReserva(Instant fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public Instant getFechaInicio() {
        return this.fechaInicio;
    }

    public ReservarHabitacion fechaInicio(Instant fechaInicio) {
        this.setFechaInicio(fechaInicio);
        return this;
    }

    public void setFechaInicio(Instant fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Instant getFechaFinal() {
        return this.fechaFinal;
    }

    public ReservarHabitacion fechaFinal(Instant fechaFinal) {
        this.setFechaFinal(fechaFinal);
        return this;
    }

    public void setFechaFinal(Instant fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public Double getTotalReservacion() {
        return this.totalReservacion;
    }

    public ReservarHabitacion totalReservacion(Double totalReservacion) {
        this.setTotalReservacion(totalReservacion);
        return this;
    }

    public void setTotalReservacion(Double totalReservacion) {
        this.totalReservacion = totalReservacion;
    }

    public Habitacion getHabitacion() {
        return this.habitacion;
    }

    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
        this.habitacionId = habitacion != null ? habitacion.getId() : null;
    }

    public ReservarHabitacion habitacion(Habitacion habitacion) {
        this.setHabitacion(habitacion);
        return this;
    }

    public Cliente getClienteReservaHabitacion() {
        return this.clienteReservaHabitacion;
    }

    public void setClienteReservaHabitacion(Cliente cliente) {
        this.clienteReservaHabitacion = cliente;
        this.clienteReservaHabitacionId = cliente != null ? cliente.getId() : null;
    }

    public ReservarHabitacion clienteReservaHabitacion(Cliente cliente) {
        this.setClienteReservaHabitacion(cliente);
        return this;
    }

    public Colaborador getColaboradorReservaHabitacion() {
        return this.colaboradorReservaHabitacion;
    }

    public void setColaboradorReservaHabitacion(Colaborador colaborador) {
        this.colaboradorReservaHabitacion = colaborador;
        this.colaboradorReservaHabitacionId = colaborador != null ? colaborador.getId() : null;
    }

    public ReservarHabitacion colaboradorReservaHabitacion(Colaborador colaborador) {
        this.setColaboradorReservaHabitacion(colaborador);
        return this;
    }

    public Persona getOcupantes() {
        return this.ocupantes;
    }

    public void setOcupantes(Persona persona) {
        this.ocupantes = persona;
        this.ocupantesId = persona != null ? persona.getId() : null;
    }

    public ReservarHabitacion ocupantes(Persona persona) {
        this.setOcupantes(persona);
        return this;
    }

    public DetalleFactura getDetalleFactura() {
        return this.detalleFactura;
    }

    public void setDetalleFactura(DetalleFactura detalleFactura) {
        if (this.detalleFactura != null) {
            this.detalleFactura.setHabitacionReservada(null);
        }
        if (detalleFactura != null) {
            detalleFactura.setHabitacionReservada(this);
        }
        this.detalleFactura = detalleFactura;
    }

    public ReservarHabitacion detalleFactura(DetalleFactura detalleFactura) {
        this.setDetalleFactura(detalleFactura);
        return this;
    }

    public Long getHabitacionId() {
        return this.habitacionId;
    }

    public void setHabitacionId(Long habitacion) {
        this.habitacionId = habitacion;
    }

    public Long getClienteReservaHabitacionId() {
        return this.clienteReservaHabitacionId;
    }

    public void setClienteReservaHabitacionId(Long cliente) {
        this.clienteReservaHabitacionId = cliente;
    }

    public Long getColaboradorReservaHabitacionId() {
        return this.colaboradorReservaHabitacionId;
    }

    public void setColaboradorReservaHabitacionId(Long colaborador) {
        this.colaboradorReservaHabitacionId = colaborador;
    }

    public Long getOcupantesId() {
        return this.ocupantesId;
    }

    public void setOcupantesId(Long persona) {
        this.ocupantesId = persona;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReservarHabitacion)) {
            return false;
        }
        return id != null && id.equals(((ReservarHabitacion) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReservarHabitacion{" +
            "id=" + getId() +
            ", fechaReserva='" + getFechaReserva() + "'" +
            ", fechaInicio='" + getFechaInicio() + "'" +
            ", fechaFinal='" + getFechaFinal() + "'" +
            ", totalReservacion=" + getTotalReservacion() +
            "}";
    }
}
