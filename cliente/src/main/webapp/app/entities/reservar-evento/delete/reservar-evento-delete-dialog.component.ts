import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { IReservarEvento } from '../reservar-evento.model';
import { ReservarEventoService } from '../service/reservar-evento.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  standalone: true,
  templateUrl: './reservar-evento-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ReservarEventoDeleteDialogComponent {
  reservarEvento?: IReservarEvento;

  constructor(protected reservarEventoService: ReservarEventoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.reservarEventoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
