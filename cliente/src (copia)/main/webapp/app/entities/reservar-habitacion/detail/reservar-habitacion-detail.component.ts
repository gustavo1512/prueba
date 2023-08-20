import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IReservarHabitacion } from '../reservar-habitacion.model';

@Component({
  standalone: true,
  selector: 'jhi-reservar-habitacion-detail',
  templateUrl: './reservar-habitacion-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ReservarHabitacionDetailComponent {
  @Input() reservarHabitacion: IReservarHabitacion | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
