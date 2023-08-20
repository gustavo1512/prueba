package com.mycompany.myapp.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Tarifa.
 */
@Table("tarifa")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Tarifa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("tipo_tarifa")
    private String tipoTarifa;

    @Column("tarifa_adulto")
    private Double tarifaAdulto;

    @Column("tarifa_menor")
    private Double tarifaMenor;

    @Transient
    private Evento evento;

    @Transient
    private Habitacion habitacion;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tarifa id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoTarifa() {
        return this.tipoTarifa;
    }

    public Tarifa tipoTarifa(String tipoTarifa) {
        this.setTipoTarifa(tipoTarifa);
        return this;
    }

    public void setTipoTarifa(String tipoTarifa) {
        this.tipoTarifa = tipoTarifa;
    }

    public Double getTarifaAdulto() {
        return this.tarifaAdulto;
    }

    public Tarifa tarifaAdulto(Double tarifaAdulto) {
        this.setTarifaAdulto(tarifaAdulto);
        return this;
    }

    public void setTarifaAdulto(Double tarifaAdulto) {
        this.tarifaAdulto = tarifaAdulto;
    }

    public Double getTarifaMenor() {
        return this.tarifaMenor;
    }

    public Tarifa tarifaMenor(Double tarifaMenor) {
        this.setTarifaMenor(tarifaMenor);
        return this;
    }

    public void setTarifaMenor(Double tarifaMenor) {
        this.tarifaMenor = tarifaMenor;
    }

    public Evento getEvento() {
        return this.evento;
    }

    public void setEvento(Evento evento) {
        if (this.evento != null) {
            this.evento.setTarifa(null);
        }
        if (evento != null) {
            evento.setTarifa(this);
        }
        this.evento = evento;
    }

    public Tarifa evento(Evento evento) {
        this.setEvento(evento);
        return this;
    }

    public Habitacion getHabitacion() {
        return this.habitacion;
    }

    public void setHabitacion(Habitacion habitacion) {
        if (this.habitacion != null) {
            this.habitacion.setTarifa(null);
        }
        if (habitacion != null) {
            habitacion.setTarifa(this);
        }
        this.habitacion = habitacion;
    }

    public Tarifa habitacion(Habitacion habitacion) {
        this.setHabitacion(habitacion);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tarifa)) {
            return false;
        }
        return id != null && id.equals(((Tarifa) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tarifa{" +
            "id=" + getId() +
            ", tipoTarifa='" + getTipoTarifa() + "'" +
            ", tarifaAdulto=" + getTarifaAdulto() +
            ", tarifaMenor=" + getTarifaMenor() +
            "}";
    }
}
