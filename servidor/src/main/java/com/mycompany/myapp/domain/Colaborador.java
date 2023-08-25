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
 * Gestiona los colaboradores de la aplicacion
 */
@Table("colaborador")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Colaborador implements Serializable {

    private static final long serialVersionUID = 1L;

// Define los atributos de la clase.

    @Id
    @Column("id")
    private Long id;

    @Column("nombre_colaborador")
    private String nombreColaborador;

    @Column("num_telefono")
    private Long numTelefono;

    @Column("correo")
    private String correo;

    @Transient
    @JsonIgnoreProperties(
        value = { "habitacion", "clienteReservaHabitacion", "colaboradorReservaHabitacion", "ocupantes", "detalleFactura" },
        allowSetters = true
    )
    private Set<ReservarHabitacion> reservarHabitacions = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "clienteReservaEvento", "colaboradorReservaEvento", "detalleFactura" }, allowSetters = true)
    private Set<ReservarEvento> reservarEventos = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "tarifa", "encargado" }, allowSetters = true)
    private Set<Evento> eventos = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "detalleFactura", "clienteFactura", "clienteColaborador" }, allowSetters = true)
    private Set<Factura> facturas = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "colaboradors" }, allowSetters = true)
    private TipoCargo tipoCargoColaborador;

    @Column("tipo_cargo_colaborador_id")
    private Long tipoCargoColaboradorId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    // The above code is a Java class representing a "Colaborador" (collaborator) entity. It has various
// Metodos set y get de las propiedades de la clase.

    public Long getId() {
        return this.id;
    }

    public Colaborador id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreColaborador() {
        return this.nombreColaborador;
    }

    public Colaborador nombreColaborador(String nombreColaborador) {
        this.setNombreColaborador(nombreColaborador);
        return this;
    }

    public void setNombreColaborador(String nombreColaborador) {
        this.nombreColaborador = nombreColaborador;
    }

    public Long getNumTelefono() {
        return this.numTelefono;
    }

    public Colaborador numTelefono(Long numTelefono) {
        this.setNumTelefono(numTelefono);
        return this;
    }

    public void setNumTelefono(Long numTelefono) {
        this.numTelefono = numTelefono;
    }

    public String getCorreo() {
        return this.correo;
    }

    public Colaborador correo(String correo) {
        this.setCorreo(correo);
        return this;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Set<ReservarHabitacion> getReservarHabitacions() {
        return this.reservarHabitacions;
    }

    public void setReservarHabitacions(Set<ReservarHabitacion> reservarHabitacions) {
        if (this.reservarHabitacions != null) {
            this.reservarHabitacions.forEach(i -> i.setColaboradorReservaHabitacion(null));
        }
        if (reservarHabitacions != null) {
            reservarHabitacions.forEach(i -> i.setColaboradorReservaHabitacion(this));
        }
        this.reservarHabitacions = reservarHabitacions;
    }

    public Colaborador reservarHabitacions(Set<ReservarHabitacion> reservarHabitacions) {
        this.setReservarHabitacions(reservarHabitacions);
        return this;
    }

    public Colaborador addReservarHabitacion(ReservarHabitacion reservarHabitacion) {
        this.reservarHabitacions.add(reservarHabitacion);
        reservarHabitacion.setColaboradorReservaHabitacion(this);
        return this;
    }

    public Colaborador removeReservarHabitacion(ReservarHabitacion reservarHabitacion) {
        this.reservarHabitacions.remove(reservarHabitacion);
        reservarHabitacion.setColaboradorReservaHabitacion(null);
        return this;
    }

    public Set<ReservarEvento> getReservarEventos() {
        return this.reservarEventos;
    }

    public void setReservarEventos(Set<ReservarEvento> reservarEventos) {
        if (this.reservarEventos != null) {
            this.reservarEventos.forEach(i -> i.setColaboradorReservaEvento(null));
        }
        if (reservarEventos != null) {
            reservarEventos.forEach(i -> i.setColaboradorReservaEvento(this));
        }
        this.reservarEventos = reservarEventos;
    }

    public Colaborador reservarEventos(Set<ReservarEvento> reservarEventos) {
        this.setReservarEventos(reservarEventos);
        return this;
    }

    public Colaborador addReservarEvento(ReservarEvento reservarEvento) {
        this.reservarEventos.add(reservarEvento);
        reservarEvento.setColaboradorReservaEvento(this);
        return this;
    }

    public Colaborador removeReservarEvento(ReservarEvento reservarEvento) {
        this.reservarEventos.remove(reservarEvento);
        reservarEvento.setColaboradorReservaEvento(null);
        return this;
    }

    public Set<Evento> getEventos() {
        return this.eventos;
    }

    public void setEventos(Set<Evento> eventos) {
        if (this.eventos != null) {
            this.eventos.forEach(i -> i.setEncargado(null));
        }
        if (eventos != null) {
            eventos.forEach(i -> i.setEncargado(this));
        }
        this.eventos = eventos;
    }

    public Colaborador eventos(Set<Evento> eventos) {
        this.setEventos(eventos);
        return this;
    }

    public Colaborador addEvento(Evento evento) {
        this.eventos.add(evento);
        evento.setEncargado(this);
        return this;
    }

    public Colaborador removeEvento(Evento evento) {
        this.eventos.remove(evento);
        evento.setEncargado(null);
        return this;
    }

    public Set<Factura> getFacturas() {
        return this.facturas;
    }

    public void setFacturas(Set<Factura> facturas) {
        if (this.facturas != null) {
            this.facturas.forEach(i -> i.setClienteColaborador(null));
        }
        if (facturas != null) {
            facturas.forEach(i -> i.setClienteColaborador(this));
        }
        this.facturas = facturas;
    }

    public Colaborador facturas(Set<Factura> facturas) {
        this.setFacturas(facturas);
        return this;
    }

    public Colaborador addFactura(Factura factura) {
        this.facturas.add(factura);
        factura.setClienteColaborador(this);
        return this;
    }

    public Colaborador removeFactura(Factura factura) {
        this.facturas.remove(factura);
        factura.setClienteColaborador(null);
        return this;
    }

    public TipoCargo getTipoCargoColaborador() {
        return this.tipoCargoColaborador;
    }

    public void setTipoCargoColaborador(TipoCargo tipoCargo) {
        this.tipoCargoColaborador = tipoCargo;
        this.tipoCargoColaboradorId = tipoCargo != null ? tipoCargo.getId() : null;
    }

    public Colaborador tipoCargoColaborador(TipoCargo tipoCargo) {
        this.setTipoCargoColaborador(tipoCargo);
        return this;
    }

    public Long getTipoCargoColaboradorId() {
        return this.tipoCargoColaboradorId;
    }

    public void setTipoCargoColaboradorId(Long tipoCargo) {
        this.tipoCargoColaboradorId = tipoCargo;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here
//metodos de la clase
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Colaborador)) {
            return false;
        }
        return id != null && id.equals(((Colaborador) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Colaborador{" +
            "id=" + getId() +
            ", nombreColaborador='" + getNombreColaborador() + "'" +
            ", numTelefono=" + getNumTelefono() +
            ", correo='" + getCorreo() + "'" +
            "}";
    }
}
