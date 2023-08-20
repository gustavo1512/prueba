package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReservarEventoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReservarEvento.class);
        ReservarEvento reservarEvento1 = new ReservarEvento();
        reservarEvento1.setId(1L);
        ReservarEvento reservarEvento2 = new ReservarEvento();
        reservarEvento2.setId(reservarEvento1.getId());
        assertThat(reservarEvento1).isEqualTo(reservarEvento2);
        reservarEvento2.setId(2L);
        assertThat(reservarEvento1).isNotEqualTo(reservarEvento2);
        reservarEvento1.setId(null);
        assertThat(reservarEvento1).isNotEqualTo(reservarEvento2);
    }
}
