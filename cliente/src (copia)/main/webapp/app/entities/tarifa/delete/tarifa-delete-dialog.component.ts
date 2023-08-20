import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITarifa } from '../tarifa.model';
import { TarifaService } from '../service/tarifa.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  standalone: true,
  templateUrl: './tarifa-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TarifaDeleteDialogComponent {
  tarifa?: ITarifa;

  constructor(protected tarifaService: TarifaService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tarifaService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
