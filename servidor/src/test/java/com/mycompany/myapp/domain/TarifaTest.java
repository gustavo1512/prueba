package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TarifaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tarifa.class);
        Tarifa tarifa1 = new Tarifa();
        tarifa1.setId(1L);
        Tarifa tarifa2 = new Tarifa();
        tarifa2.setId(tarifa1.getId());
        assertThat(tarifa1).isEqualTo(tarifa2);
        tarifa2.setId(2L);
        assertThat(tarifa1).isNotEqualTo(tarifa2);
        tarifa1.setId(null);
        assertThat(tarifa1).isNotEqualTo(tarifa2);
    }
}
