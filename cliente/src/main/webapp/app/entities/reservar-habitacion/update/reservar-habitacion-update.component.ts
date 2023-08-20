import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ReservarHabitacionFormService, ReservarHabitacionFormGroup } from './reservar-habitacion-form.service';
import { IReservarHabitacion } from '../reservar-habitacion.model';
import { ReservarHabitacionService } from '../service/reservar-habitacion.service';
import { IHabitacion } from 'app/entities/habitacion/habitacion.model';
import { HabitacionService } from 'app/entities/habitacion/service/habitacion.service';
import { ICliente } from 'app/entities/cliente/cliente.model';
import { ClienteService } from 'app/entities/cliente/service/cliente.service';
import { IColaborador } from 'app/entities/colaborador/colaborador.model';
import { ColaboradorService } from 'app/entities/colaborador/service/colaborador.service';
import { IPersona } from 'app/entities/persona/persona.model';
import { PersonaService } from 'app/entities/persona/service/persona.service';

@Component({
  standalone: true,
  selector: 'jhi-reservar-habitacion-update',
  templateUrl: './reservar-habitacion-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ReservarHabitacionUpdateComponent implements OnInit {
  isSaving = false;
  reservarHabitacion: IReservarHabitacion | null = null;

  habitacionsCollection: IHabitacion[] = [];
  clientesSharedCollection: ICliente[] = [];
  colaboradorsSharedCollection: IColaborador[] = [];
  personasSharedCollection: IPersona[] = [];

  editForm: ReservarHabitacionFormGroup = this.reservarHabitacionFormService.createReservarHabitacionFormGroup();

  constructor(
    protected reservarHabitacionService: ReservarHabitacionService,
    protected reservarHabitacionFormService: ReservarHabitacionFormService,
    protected habitacionService: HabitacionService,
    protected clienteService: ClienteService,
    protected colaboradorService: ColaboradorService,
    protected personaService: PersonaService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareHabitacion = (o1: IHabitacion | null, o2: IHabitacion | null): boolean => this.habitacionService.compareHabitacion(o1, o2);

  compareCliente = (o1: ICliente | null, o2: ICliente | null): boolean => this.clienteService.compareCliente(o1, o2);

  compareColaborador = (o1: IColaborador | null, o2: IColaborador | null): boolean => this.colaboradorService.compareColaborador(o1, o2);

  comparePersona = (o1: IPersona | null, o2: IPersona | null): boolean => this.personaService.comparePersona(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reservarHabitacion }) => {
      this.reservarHabitacion = reservarHabitacion;
      if (reservarHabitacion) {
        this.updateForm(reservarHabitacion);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const reservarHabitacion = this.reservarHabitacionFormService.getReservarHabitacion(this.editForm);
    if (reservarHabitacion.id !== null) {
      this.subscribeToSaveResponse(this.reservarHabitacionService.update(reservarHabitacion));
    } else {
      this.subscribeToSaveResponse(this.reservarHabitacionService.create(reservarHabitacion));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReservarHabitacion>>): void {
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

  protected updateForm(reservarHabitacion: IReservarHabitacion): void {
    this.reservarHabitacion = reservarHabitacion;
    this.reservarHabitacionFormService.resetForm(this.editForm, reservarHabitacion);

    this.habitacionsCollection = this.habitacionService.addHabitacionToCollectionIfMissing<IHabitacion>(
      this.habitacionsCollection,
      reservarHabitacion.habitacion
    );
    this.clientesSharedCollection = this.clienteService.addClienteToCollectionIfMissing<ICliente>(
      this.clientesSharedCollection,
      reservarHabitacion.clienteReservaHabitacion
    );
    this.colaboradorsSharedCollection = this.colaboradorService.addColaboradorToCollectionIfMissing<IColaborador>(
      this.colaboradorsSharedCollection,
      reservarHabitacion.colaboradorReservaHabitacion
    );
    this.personasSharedCollection = this.personaService.addPersonaToCollectionIfMissing<IPersona>(
      this.personasSharedCollection,
      reservarHabitacion.ocupantes
    );
  }

  protected loadRelationshipsOptions(): void {
    this.habitacionService
      .query({ filter: 'reservarhabitacion-is-null' })
      .pipe(map((res: HttpResponse<IHabitacion[]>) => res.body ?? []))
      .pipe(
        map((habitacions: IHabitacion[]) =>
          this.habitacionService.addHabitacionToCollectionIfMissing<IHabitacion>(habitacions, this.reservarHabitacion?.habitacion)
        )
      )
      .subscribe((habitacions: IHabitacion[]) => (this.habitacionsCollection = habitacions));

    this.clienteService
      .query()
      .pipe(map((res: HttpResponse<ICliente[]>) => res.body ?? []))
      .pipe(
        map((clientes: ICliente[]) =>
          this.clienteService.addClienteToCollectionIfMissing<ICliente>(clientes, this.reservarHabitacion?.clienteReservaHabitacion)
        )
      )
      .subscribe((clientes: ICliente[]) => (this.clientesSharedCollection = clientes));

    this.colaboradorService
      .query()
      .pipe(map((res: HttpResponse<IColaborador[]>) => res.body ?? []))
      .pipe(
        map((colaboradors: IColaborador[]) =>
          this.colaboradorService.addColaboradorToCollectionIfMissing<IColaborador>(
            colaboradors,
            this.reservarHabitacion?.colaboradorReservaHabitacion
          )
        )
      )
      .subscribe((colaboradors: IColaborador[]) => (this.colaboradorsSharedCollection = colaboradors));

    this.personaService
      .query()
      .pipe(map((res: HttpResponse<IPersona[]>) => res.body ?? []))
      .pipe(
        map((personas: IPersona[]) =>
          this.personaService.addPersonaToCollectionIfMissing<IPersona>(personas, this.reservarHabitacion?.ocupantes)
        )
      )
      .subscribe((personas: IPersona[]) => (this.personasSharedCollection = personas));
  }
}
