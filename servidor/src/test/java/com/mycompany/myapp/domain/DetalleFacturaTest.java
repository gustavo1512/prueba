package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DetalleFacturaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DetalleFactura.class);
        DetalleFactura detalleFactura1 = new DetalleFactura();
        detalleFactura1.setId(1L);
        DetalleFactura detalleFactura2 = new DetalleFactura();
        detalleFactura2.setId(detalleFactura1.getId());
        assertThat(detalleFactura1).isEqualTo(detalleFactura2);
        detalleFactura2.setId(2L);
        assertThat(detalleFactura1).isNotEqualTo(detalleFactura2);
        detalleFactura1.setId(null);
        assertThat(detalleFactura1).isNotEqualTo(detalleFactura2);
    }
}
