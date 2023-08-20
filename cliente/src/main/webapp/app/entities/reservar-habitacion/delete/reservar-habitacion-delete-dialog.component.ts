import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { IReservarHabitacion } from '../reservar-habitacion.model';
import { ReservarHabitacionService } from '../service/reservar-habitacion.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  standalone: true,
  templateUrl: './reservar-habitacion-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ReservarHabitacionDeleteDialogComponent {
  reservarHabitacion?: IReservarHabitacion;

  constructor(protected reservarHabitacionService: ReservarHabitacionService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.reservarHabitacionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
