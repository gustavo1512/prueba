import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { IHabitacion } from '../habitacion.model';
import { HabitacionService } from '../service/habitacion.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  standalone: true,
  templateUrl: './habitacion-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class HabitacionDeleteDialogComponent {
  habitacion?: IHabitacion;

  constructor(protected habitacionService: HabitacionService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.habitacionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
