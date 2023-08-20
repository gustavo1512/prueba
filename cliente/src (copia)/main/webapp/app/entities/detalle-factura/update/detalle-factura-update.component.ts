import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { DetalleFacturaFormService, DetalleFacturaFormGroup } from './detalle-factura-form.service';
import { IDetalleFactura } from '../detalle-factura.model';
import { DetalleFacturaService } from '../service/detalle-factura.service';
import { IReservarHabitacion } from 'app/entities/reservar-habitacion/reservar-habitacion.model';
import { ReservarHabitacionService } from 'app/entities/reservar-habitacion/service/reservar-habitacion.service';
import { IReservarEvento } from 'app/entities/reservar-evento/reservar-evento.model';
import { ReservarEventoService } from 'app/entities/reservar-evento/service/reservar-evento.service';

@Component({
  standalone: true,
  selector: 'jhi-detalle-factura-update',
  templateUrl: './detalle-factura-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DetalleFacturaUpdateComponent implements OnInit {
  isSaving = false;
  detalleFactura: IDetalleFactura | null = null;

  habitacionReservadasCollection: IReservarHabitacion[] = [];
  eventoReservadosCollection: IReservarEvento[] = [];

  editForm: DetalleFacturaFormGroup = this.detalleFacturaFormService.createDetalleFacturaFormGroup();

  constructor(
    protected detalleFacturaService: DetalleFacturaService,
    protected detalleFacturaFormService: DetalleFacturaFormService,
    protected reservarHabitacionService: ReservarHabitacionService,
    protected reservarEventoService: ReservarEventoService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareReservarHabitacion = (o1: IReservarHabitacion | null, o2: IReservarHabitacion | null): boolean =>
    this.reservarHabitacionService.compareReservarHabitacion(o1, o2);

  compareReservarEvento = (o1: IReservarEvento | null, o2: IReservarEvento | null): boolean =>
    this.reservarEventoService.compareReservarEvento(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ detalleFactura }) => {
      this.detalleFactura = detalleFactura;
      if (detalleFactura) {
        this.updateForm(detalleFactura);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const detalleFactura = this.detalleFacturaFormService.getDetalleFactura(this.editForm);
    if (detalleFactura.id !== null) {
      this.subscribeToSaveResponse(this.detalleFacturaService.update(detalleFactura));
    } else {
      this.subscribeToSaveResponse(this.detalleFacturaService.create(detalleFactura));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDetalleFactura>>): void {
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

  protected updateForm(detalleFactura: IDetalleFactura): void {
    this.detalleFactura = detalleFactura;
    this.detalleFacturaFormService.resetForm(this.editForm, detalleFactura);

    this.habitacionReservadasCollection = this.reservarHabitacionService.addReservarHabitacionToCollectionIfMissing<IReservarHabitacion>(
      this.habitacionReservadasCollection,
      detalleFactura.habitacionReservada
    );
    this.eventoReservadosCollection = this.reservarEventoService.addReservarEventoToCollectionIfMissing<IReservarEvento>(
      this.eventoReservadosCollection,
      detalleFactura.eventoReservado
    );
  }

  protected loadRelationshipsOptions(): void {
    this.reservarHabitacionService
      .query({ filter: 'detallefactura-is-null' })
      .pipe(map((res: HttpResponse<IReservarHabitacion[]>) => res.body ?? []))
      .pipe(
        map((reservarHabitacions: IReservarHabitacion[]) =>
          this.reservarHabitacionService.addReservarHabitacionToCollectionIfMissing<IReservarHabitacion>(
            reservarHabitacions,
            this.detalleFactura?.habitacionReservada
          )
        )
      )
      .subscribe((reservarHabitacions: IReservarHabitacion[]) => (this.habitacionReservadasCollection = reservarHabitacions));

    this.reservarEventoService
      .query({ filter: 'detallefactura-is-null' })
      .pipe(map((res: HttpResponse<IReservarEvento[]>) => res.body ?? []))
      .pipe(
        map((reservarEventos: IReservarEvento[]) =>
          this.reservarEventoService.addReservarEventoToCollectionIfMissing<IReservarEvento>(
            reservarEventos,
            this.detalleFactura?.eventoReservado
          )
        )
      )
      .subscribe((reservarEventos: IReservarEvento[]) => (this.eventoReservadosCollection = reservarEventos));
  }
}
