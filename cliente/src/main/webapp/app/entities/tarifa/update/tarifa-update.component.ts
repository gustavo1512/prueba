import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TarifaFormService, TarifaFormGroup } from './tarifa-form.service';
import { ITarifa } from '../tarifa.model';
import { TarifaService } from '../service/tarifa.service';

@Component({
  standalone: true,
  selector: 'jhi-tarifa-update',
  templateUrl: './tarifa-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TarifaUpdateComponent implements OnInit {
  isSaving = false;
  tarifa: ITarifa | null = null;

  editForm: TarifaFormGroup = this.tarifaFormService.createTarifaFormGroup();

  constructor(
    protected tarifaService: TarifaService,
    protected tarifaFormService: TarifaFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tarifa }) => {
      this.tarifa = tarifa;
      if (tarifa) {
        this.updateForm(tarifa);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tarifa = this.tarifaFormService.getTarifa(this.editForm);
    if (tarifa.id !== null) {
      this.subscribeToSaveResponse(this.tarifaService.update(tarifa));
    } else {
      this.subscribeToSaveResponse(this.tarifaService.create(tarifa));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITarifa>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(tarifa: ITarifa): void {
    this.tarifa = tarifa;
    this.tarifaFormService.resetForm(this.editForm, tarifa);
  }
}
