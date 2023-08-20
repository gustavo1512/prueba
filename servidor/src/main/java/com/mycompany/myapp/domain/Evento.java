package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Evento.
 */
@Table("evento")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Evento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nombre_evento")
    private String nombreEvento;

    @Column("fecha_hora")
    private Instant fechaHora;

    @Column("capacidad_adulto")
    private Long capacidadAdulto;

    @Column("capacidad_menor")
    private Long capacidadMenor;

    @Transient
    private Tarifa tarifa;

    @Transient
    @JsonIgnoreProperties(
        value = { "reservarHabitacions", "reservarEventos", "eventos", "facturas", "tipoCargoColaborador" },
        allowSetters = true
    )
    private Colaborador encargado;

    @Column("tarifa_id")
    private Long tarifaId;

    @Column("encargado_id")
    private Long encargadoId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Evento id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreEvento() {
        return this.nombreEvento;
    }

    public Evento nombreEvento(String nombreEvento) {
        this.setNombreEvento(nombreEvento);
        return this;
    }

    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }

    public Instant getFechaHora() {
        return this.fechaHora;
    }

    public Evento fechaHora(Instant fechaHora) {
        this.setFechaHora(fechaHora);
        return this;
    }

    public void setFechaHora(Instant fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Long getCapacidadAdulto() {
        return this.capacidadAdulto;
    }

    public Evento capacidadAdulto(Long capacidadAdulto) {
        this.setCapacidadAdulto(capacidadAdulto);
        return this;
    }

    public void setCapacidadAdulto(Long capacidadAdulto) {
        this.capacidadAdulto = capacidadAdulto;
    }

    public Long getCapacidadMenor() {
        return this.capacidadMenor;
    }

    public Evento capacidadMenor(Long capacidadMenor) {
        this.setCapacidadMenor(capacidadMenor);
        return this;
    }

    public void setCapacidadMenor(Long capacidadMenor) {
        this.capacidadMenor = capacidadMenor;
    }

    public Tarifa getTarifa() {
        return this.tarifa;
    }

    public void setTarifa(Tarifa tarifa) {
        this.tarifa = tarifa;
        this.tarifaId = tarifa != null ? tarifa.getId() : null;
    }

    public Evento tarifa(Tarifa tarifa) {
        this.setTarifa(tarifa);
        return this;
    }

    public Colaborador getEncargado() {
        return this.encargado;
    }

    public void setEncargado(Colaborador colaborador) {
        this.encargado = colaborador;
        this.encargadoId = colaborador != null ? colaborador.getId() : null;
    }

    public Evento encargado(Colaborador colaborador) {
        this.setEncargado(colaborador);
        return this;
    }

    public Long getTarifaId() {
        return this.tarifaId;
    }

    public void setTarifaId(Long tarifa) {
        this.tarifaId = tarifa;
    }

    public Long getEncargadoId() {
        return this.encargadoId;
    }

    public void setEncargadoId(Long colaborador) {
        this.encargadoId = colaborador;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Evento)) {
            return false;
        }
        return id != null && id.equals(((Evento) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Evento{" +
            "id=" + getId() +
            ", nombreEvento='" + getNombreEvento() + "'" +
            ", fechaHora='" + getFechaHora() + "'" +
            ", capacidadAdulto=" + getCapacidadAdulto() +
            ", capacidadMenor=" + getCapacidadMenor() +
            "}";
    }
}
