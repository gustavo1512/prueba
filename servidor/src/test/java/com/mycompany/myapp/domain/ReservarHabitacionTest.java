package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReservarHabitacionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReservarHabitacion.class);
        ReservarHabitacion reservarHabitacion1 = new ReservarHabitacion();
        reservarHabitacion1.setId(1L);
        ReservarHabitacion reservarHabitacion2 = new ReservarHabitacion();
        reservarHabitacion2.setId(reservarHabitacion1.getId());
        assertThat(reservarHabitacion1).isEqualTo(reservarHabitacion2);
        reservarHabitacion2.setId(2L);
        assertThat(reservarHabitacion1).isNotEqualTo(reservarHabitacion2);
        reservarHabitacion1.setId(null);
        assertThat(reservarHabitacion1).isNotEqualTo(reservarHabitacion2);
    }
}
