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
 * Clase cliente almacena todos los clientes que deseen realizar una reservación
 */
@Table("cliente")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Cliente implements Serializable {

  // The code you provided is a Java class representing a domain object called "Cliente".
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nombre")
    private String nombre;

    @Column("apellido")
    private String apellido;

    @Column("direccion")
    private String direccion;

    @Column("correo")
    private String correo;

    @Column("telefono")
    private String telefono;

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
    @JsonIgnoreProperties(value = { "detalleFactura", "clienteFactura", "clienteColaborador" }, allowSetters = true)
    private Set<Factura> facturas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    /**
     * Get y set de la clase
     * 
     * @return The method is returning a Long value, which is the id of the object.
     */
    public Long getId() {
        return this.id;
    }

    public Cliente id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Cliente nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return this.apellido;
    }

    public Cliente apellido(String apellido) {
        this.setApellido(apellido);
        return this;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public Cliente direccion(String direccion) {
        this.setDireccion(direccion);
        return this;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return this.correo;
    }

    public Cliente correo(String correo) {
        this.setCorreo(correo);
        return this;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public Cliente telefono(String telefono) {
        this.setTelefono(telefono);
        return this;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Set<ReservarHabitacion> getReservarHabitacions() {
        return this.reservarHabitacions;
    }

    public void setReservarHabitacions(Set<ReservarHabitacion> reservarHabitacions) {
        if (this.reservarHabitacions != null) {
            this.reservarHabitacions.forEach(i -> i.setClienteReservaHabitacion(null));
        }
        if (reservarHabitacions != null) {
            reservarHabitacions.forEach(i -> i.setClienteReservaHabitacion(this));
        }
        this.reservarHabitacions = reservarHabitacions;
    }

    public Cliente reservarHabitacions(Set<ReservarHabitacion> reservarHabitacions) {
        this.setReservarHabitacions(reservarHabitacions);
        return this;
    }

    public Cliente addReservarHabitacion(ReservarHabitacion reservarHabitacion) {
        this.reservarHabitacions.add(reservarHabitacion);
        reservarHabitacion.setClienteReservaHabitacion(this);
        return this;
    }

    public Cliente removeReservarHabitacion(ReservarHabitacion reservarHabitacion) {
        this.reservarHabitacions.remove(reservarHabitacion);
        reservarHabitacion.setClienteReservaHabitacion(null);
        return this;
    }

    public Set<ReservarEvento> getReservarEventos() {
        return this.reservarEventos;
    }

    public void setReservarEventos(Set<ReservarEvento> reservarEventos) {
        if (this.reservarEventos != null) {
            this.reservarEventos.forEach(i -> i.setClienteReservaEvento(null));
        }
        if (reservarEventos != null) {
            reservarEventos.forEach(i -> i.setClienteReservaEvento(this));
        }
        this.reservarEventos = reservarEventos;
    }

    public Cliente reservarEventos(Set<ReservarEvento> reservarEventos) {
        this.setReservarEventos(reservarEventos);
        return this;
    }

    public Cliente addReservarEvento(ReservarEvento reservarEvento) {
        this.reservarEventos.add(reservarEvento);
        reservarEvento.setClienteReservaEvento(this);
        return this;
    }

    public Cliente removeReservarEvento(ReservarEvento reservarEvento) {
        this.reservarEventos.remove(reservarEvento);
        reservarEvento.setClienteReservaEvento(null);
        return this;
    }

    public Set<Factura> getFacturas() {
        return this.facturas;
    }

    public void setFacturas(Set<Factura> facturas) {
        if (this.facturas != null) {
            this.facturas.forEach(i -> i.setClienteFactura(null));
        }
        if (facturas != null) {
            facturas.forEach(i -> i.setClienteFactura(this));
        }
        this.facturas = facturas;
    }

    public Cliente facturas(Set<Factura> facturas) {
        this.setFacturas(facturas);
        return this;
    }

    public Cliente addFactura(Factura factura) {
        this.facturas.add(factura);
        factura.setClienteFactura(this);
        return this;
    }

    public Cliente removeFactura(Factura factura) {
        this.facturas.remove(factura);
        factura.setClienteFactura(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cliente)) {
            return false;
        }
        return id != null && id.equals(((Cliente) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cliente{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", apellido='" + getApellido() + "'" +
            ", direccion='" + getDireccion() + "'" +
            ", correo='" + getCorreo() + "'" +
            ", telefono='" + getTelefono() + "'" +
            "}";
    }
}
