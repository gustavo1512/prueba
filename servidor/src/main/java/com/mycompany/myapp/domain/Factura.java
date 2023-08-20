package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Factura.
 */
@Table("factura")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Factura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("metodo_pago")
    private String metodoPago;

    @Column("subtotal")
    private Double subtotal;

    @Column("monto_total")
    private Double montoTotal;

    @Column("impuesto")
    private Double impuesto;

    @Transient
    @JsonIgnoreProperties(value = { "habitacionReservada", "eventoReservado", "facturas" }, allowSetters = true)
    private DetalleFactura detalleFactura;

    @Transient
    @JsonIgnoreProperties(value = { "reservarHabitacions", "reservarEventos", "facturas" }, allowSetters = true)
    private Cliente clienteFactura;

    @Transient
    @JsonIgnoreProperties(
        value = { "reservarHabitacions", "reservarEventos", "eventos", "facturas", "tipoCargoColaborador" },
        allowSetters = true
    )
    private Colaborador clienteColaborador;

    @Column("detalle_factura_id")
    private Long detalleFacturaId;

    @Column("cliente_factura_id")
    private Long clienteFacturaId;

    @Column("cliente_colaborador_id")
    private Long clienteColaboradorId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Factura id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMetodoPago() {
        return this.metodoPago;
    }

    public Factura metodoPago(String metodoPago) {
        this.setMetodoPago(metodoPago);
        return this;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Double getSubtotal() {
        return this.subtotal;
    }

    public Factura subtotal(Double subtotal) {
        this.setSubtotal(subtotal);
        return this;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getMontoTotal() {
        return this.montoTotal;
    }

    public Factura montoTotal(Double montoTotal) {
        this.setMontoTotal(montoTotal);
        return this;
    }

    public void setMontoTotal(Double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public Double getImpuesto() {
        return this.impuesto;
    }

    public Factura impuesto(Double impuesto) {
        this.setImpuesto(impuesto);
        return this;
    }

    public void setImpuesto(Double impuesto) {
        this.impuesto = impuesto;
    }

    public DetalleFactura getDetalleFactura() {
        return this.detalleFactura;
    }

    public void setDetalleFactura(DetalleFactura detalleFactura) {
        this.detalleFactura = detalleFactura;
        this.detalleFacturaId = detalleFactura != null ? detalleFactura.getId() : null;
    }

    public Factura detalleFactura(DetalleFactura detalleFactura) {
        this.setDetalleFactura(detalleFactura);
        return this;
    }

    public Cliente getClienteFactura() {
        return this.clienteFactura;
    }

    public void setClienteFactura(Cliente cliente) {
        this.clienteFactura = cliente;
        this.clienteFacturaId = cliente != null ? cliente.getId() : null;
    }

    public Factura clienteFactura(Cliente cliente) {
        this.setClienteFactura(cliente);
        return this;
    }

    public Colaborador getClienteColaborador() {
        return this.clienteColaborador;
    }

    public void setClienteColaborador(Colaborador colaborador) {
        this.clienteColaborador = colaborador;
        this.clienteColaboradorId = colaborador != null ? colaborador.getId() : null;
    }

    public Factura clienteColaborador(Colaborador colaborador) {
        this.setClienteColaborador(colaborador);
        return this;
    }

    public Long getDetalleFacturaId() {
        return this.detalleFacturaId;
    }

    public void setDetalleFacturaId(Long detalleFactura) {
        this.detalleFacturaId = detalleFactura;
    }

    public Long getClienteFacturaId() {
        return this.clienteFacturaId;
    }

    public void setClienteFacturaId(Long cliente) {
        this.clienteFacturaId = cliente;
    }

    public Long getClienteColaboradorId() {
        return this.clienteColaboradorId;
    }

    public void setClienteColaboradorId(Long colaborador) {
        this.clienteColaboradorId = colaborador;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Factura)) {
            return false;
        }
        return id != null && id.equals(((Factura) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Factura{" +
            "id=" + getId() +
            ", metodoPago='" + getMetodoPago() + "'" +
            ", subtotal=" + getSubtotal() +
            ", montoTotal=" + getMontoTotal() +
            ", impuesto=" + getImpuesto() +
            "}";
    }
}
