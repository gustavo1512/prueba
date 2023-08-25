package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Clase Persona. Gestionas las personas en la aplicación
 */
@Table("persona")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Persona implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
 * Propiedades de laClase.
 */
    @Id
    @Column("id")
    private Long id;

    @Column("fecha_nacimiento")
    private Instant fechaNacimiento;

    @Column("tipo")
    private String tipo;

    @Transient
    @JsonIgnoreProperties(
        value = { "habitacion", "clienteReservaHabitacion", "colaboradorReservaHabitacion", "ocupantes", "detalleFactura" },
        allowSetters = true
    )
    private Set<ReservarHabitacion> reservarHabitacions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Persona id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getFechaNacimiento() {
        return this.fechaNacimiento;
    }

    public Persona fechaNacimiento(Instant fechaNacimiento) {
        this.setFechaNacimiento(fechaNacimiento);
        return this;
    }

    public void setFechaNacimiento(Instant fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTipo() {
        return this.tipo;
    }

    public Persona tipo(String tipo) {
        this.setTipo(tipo);
        return this;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Set<ReservarHabitacion> getReservarHabitacions() {
        return this.reservarHabitacions;
    }

    public void setReservarHabitacions(Set<ReservarHabitacion> reservarHabitacions) {
        if (this.reservarHabitacions != null) {
            this.reservarHabitacions.forEach(i -> i.setOcupantes(null));
        }
        if (reservarHabitacions != null) {
            reservarHabitacions.forEach(i -> i.setOcupantes(this));
        }
        this.reservarHabitacions = reservarHabitacions;
    }

    public Persona reservarHabitacions(Set<ReservarHabitacion> reservarHabitacions) {
        this.setReservarHabitacions(reservarHabitacions);
        return this;
    }

    public Persona addReservarHabitacion(ReservarHabitacion reservarHabitacion) {
        this.reservarHabitacions.add(reservarHabitacion);
        reservarHabitacion.setOcupantes(this);
        return this;
    }

    public Persona removeReservarHabitacion(ReservarHabitacion reservarHabitacion) {
        this.reservarHabitacions.remove(reservarHabitacion);
        reservarHabitacion.setOcupantes(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Persona)) {
            return false;
        }
        return id != null && id.equals(((Persona) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Persona{" +
            "id=" + getId() +
            ", fechaNacimiento='" + getFechaNacimiento() + "'" +
            ", tipo='" + getTipo() + "'" +
            "}";
    }
}
