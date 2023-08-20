package com.mycompany.myapp.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Habitacion.
 */
@Table("habitacion")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Habitacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("tipo")
    private String tipo;

    @Column("capacidad_adulto")
    private Long capacidadAdulto;

    @Column("capacidad_menor")
    private Long capacidadMenor;

    @Column("disponible")
    private Boolean disponible;

    @Transient
    private Tarifa tarifa;

    @Transient
    private ReservarHabitacion reservarHabitacion;

    @Column("tarifa_id")
    private Long tarifaId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Habitacion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return this.tipo;
    }

    public Habitacion tipo(String tipo) {
        this.setTipo(tipo);
        return this;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getCapacidadAdulto() {
        return this.capacidadAdulto;
    }

    public Habitacion capacidadAdulto(Long capacidadAdulto) {
        this.setCapacidadAdulto(capacidadAdulto);
        return this;
    }

    public void setCapacidadAdulto(Long capacidadAdulto) {
        this.capacidadAdulto = capacidadAdulto;
    }

    public Long getCapacidadMenor() {
        return this.capacidadMenor;
    }

    public Habitacion capacidadMenor(Long capacidadMenor) {
        this.setCapacidadMenor(capacidadMenor);
        return this;
    }

    public void setCapacidadMenor(Long capacidadMenor) {
        this.capacidadMenor = capacidadMenor;
    }

    public Boolean getDisponible() {
        return this.disponible;
    }

    public Habitacion disponible(Boolean disponible) {
        this.setDisponible(disponible);
        return this;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public Tarifa getTarifa() {
        return this.tarifa;
    }

    public void setTarifa(Tarifa tarifa) {
        this.tarifa = tarifa;
        this.tarifaId = tarifa != null ? tarifa.getId() : null;
    }

    public Habitacion tarifa(Tarifa tarifa) {
        this.setTarifa(tarifa);
        return this;
    }

    public ReservarHabitacion getReservarHabitacion() {
        return this.reservarHabitacion;
    }

    public void setReservarHabitacion(ReservarHabitacion reservarHabitacion) {
        if (this.reservarHabitacion != null) {
            this.reservarHabitacion.setHabitacion(null);
        }
        if (reservarHabitacion != null) {
            reservarHabitacion.setHabitacion(this);
        }
        this.reservarHabitacion = reservarHabitacion;
    }

    public Habitacion reservarHabitacion(ReservarHabitacion reservarHabitacion) {
        this.setReservarHabitacion(reservarHabitacion);
        return this;
    }

    public Long getTarifaId() {
        return this.tarifaId;
    }

    public void setTarifaId(Long tarifa) {
        this.tarifaId = tarifa;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Habitacion)) {
            return false;
        }
        return id != null && id.equals(((Habitacion) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Habitacion{" +
            "id=" + getId() +
            ", tipo='" + getTipo() + "'" +
            ", capacidadAdulto=" + getCapacidadAdulto() +
            ", capacidadMenor=" + getCapacidadMenor() +
            ", disponible='" + getDisponible() + "'" +
            "}";
    }
}
