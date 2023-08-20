import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { HabitacionFormService, HabitacionFormGroup } from './habitacion-form.service';
import { IHabitacion } from '../habitacion.model';
import { HabitacionService } from '../service/habitacion.service';
import { ITarifa } from 'app/entities/tarifa/tarifa.model';
import { TarifaService } from 'app/entities/tarifa/service/tarifa.service';

@Component({
  standalone: true,
  selector: 'jhi-habitacion-update',
  templateUrl: './habitacion-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class HabitacionUpdateComponent implements OnInit {
  isSaving = false;
  habitacion: IHabitacion | null = null;

  tarifasCollection: ITarifa[] = [];

  editForm: HabitacionFormGroup = this.habitacionFormService.createHabitacionFormGroup();

  constructor(
    protected habitacionService: HabitacionService,
    protected habitacionFormService: HabitacionFormService,
    protected tarifaService: TarifaService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTarifa = (o1: ITarifa | null, o2: ITarifa | null): boolean => this.tarifaService.compareTarifa(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ habitacion }) => {
      this.habitacion = habitacion;
      if (habitacion) {
        this.updateForm(habitacion);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const habitacion = this.habitacionFormService.getHabitacion(this.editForm);
    if (habitacion.id !== null) {
      this.subscribeToSaveResponse(this.habitacionService.update(habitacion));
    } else {
      this.subscribeToSaveResponse(this.habitacionService.create(habitacion));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IHabitacion>>): void {
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

  protected updateForm(habitacion: IHabitacion): void {
    this.habitacion = habitacion;
    this.habitacionFormService.resetForm(this.editForm, habitacion);

    this.tarifasCollection = this.tarifaService.addTarifaToCollectionIfMissing<ITarifa>(this.tarifasCollection, habitacion.tarifa);
  }

  protected loadRelationshipsOptions(): void {
    this.tarifaService
      .query({ filter: 'habitacion-is-null' })
      .pipe(map((res: HttpResponse<ITarifa[]>) => res.body ?? []))
      .pipe(map((tarifas: ITarifa[]) => this.tarifaService.addTarifaToCollectionIfMissing<ITarifa>(tarifas, this.habitacion?.tarifa)))
      .subscribe((tarifas: ITarifa[]) => (this.tarifasCollection = tarifas));
  }
}
