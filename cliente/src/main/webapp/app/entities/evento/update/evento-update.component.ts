import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { EventoFormService, EventoFormGroup } from './evento-form.service';
import { IEvento } from '../evento.model';
import { EventoService } from '../service/evento.service';
import { ITarifa } from 'app/entities/tarifa/tarifa.model';
import { TarifaService } from 'app/entities/tarifa/service/tarifa.service';
import { IColaborador } from 'app/entities/colaborador/colaborador.model';
import { ColaboradorService } from 'app/entities/colaborador/service/colaborador.service';

@Component({
  standalone: true,
  selector: 'jhi-evento-update',
  templateUrl: './evento-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class EventoUpdateComponent implements OnInit {
  isSaving = false;
  evento: IEvento | null = null;

  tarifasCollection: ITarifa[] = [];
  colaboradorsSharedCollection: IColaborador[] = [];

  editForm: EventoFormGroup = this.eventoFormService.createEventoFormGroup();

  constructor(
    protected eventoService: EventoService,
    protected eventoFormService: EventoFormService,
    protected tarifaService: TarifaService,
    protected colaboradorService: ColaboradorService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTarifa = (o1: ITarifa | null, o2: ITarifa | null): boolean => this.tarifaService.compareTarifa(o1, o2);

  compareColaborador = (o1: IColaborador | null, o2: IColaborador | null): boolean => this.colaboradorService.compareColaborador(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ evento }) => {
      this.evento = evento;
      if (evento) {
        this.updateForm(evento);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const evento = this.eventoFormService.getEvento(this.editForm);
    if (evento.id !== null) {
      this.subscribeToSaveResponse(this.eventoService.update(evento));
    } else {
      this.subscribeToSaveResponse(this.eventoService.create(evento));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEvento>>): void {
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

  protected updateForm(evento: IEvento): void {
    this.evento = evento;
    this.eventoFormService.resetForm(this.editForm, evento);

    this.tarifasCollection = this.tarifaService.addTarifaToCollectionIfMissing<ITarifa>(this.tarifasCollection, evento.tarifa);
    this.colaboradorsSharedCollection = this.colaboradorService.addColaboradorToCollectionIfMissing<IColaborador>(
      this.colaboradorsSharedCollection,
      evento.encargado
    );
  }

  protected loadRelationshipsOptions(): void {
    this.tarifaService
      .query({ filter: 'evento-is-null' })
      .pipe(map((res: HttpResponse<ITarifa[]>) => res.body ?? []))
      .pipe(map((tarifas: ITarifa[]) => this.tarifaService.addTarifaToCollectionIfMissing<ITarifa>(tarifas, this.evento?.tarifa)))
      .subscribe((tarifas: ITarifa[]) => (this.tarifasCollection = tarifas));

    this.colaboradorService
      .query()
      .pipe(map((res: HttpResponse<IColaborador[]>) => res.body ?? []))
      .pipe(
        map((colaboradors: IColaborador[]) =>
          this.colaboradorService.addColaboradorToCollectionIfMissing<IColaborador>(colaboradors, this.evento?.encargado)
        )
      )
      .subscribe((colaboradors: IColaborador[]) => (this.colaboradorsSharedCollection = colaboradors));
  }
}
