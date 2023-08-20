import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ReservarEventoFormService, ReservarEventoFormGroup } from './reservar-evento-form.service';
import { IReservarEvento } from '../reservar-evento.model';
import { ReservarEventoService } from '../service/reservar-evento.service';
import { ICliente } from 'app/entities/cliente/cliente.model';
import { ClienteService } from 'app/entities/cliente/service/cliente.service';
import { IColaborador } from 'app/entities/colaborador/colaborador.model';
import { ColaboradorService } from 'app/entities/colaborador/service/colaborador.service';

@Component({
  standalone: true,
  selector: 'jhi-reservar-evento-update',
  templateUrl: './reservar-evento-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ReservarEventoUpdateComponent implements OnInit {
  isSaving = false;
  reservarEvento: IReservarEvento | null = null;

  clientesSharedCollection: ICliente[] = [];
  colaboradorsSharedCollection: IColaborador[] = [];

  editForm: ReservarEventoFormGroup = this.reservarEventoFormService.createReservarEventoFormGroup();

  constructor(
    protected reservarEventoService: ReservarEventoService,
    protected reservarEventoFormService: ReservarEventoFormService,
    protected clienteService: ClienteService,
    protected colaboradorService: ColaboradorService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCliente = (o1: ICliente | null, o2: ICliente | null): boolean => this.clienteService.compareCliente(o1, o2);

  compareColaborador = (o1: IColaborador | null, o2: IColaborador | null): boolean => this.colaboradorService.compareColaborador(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reservarEvento }) => {
      this.reservarEvento = reservarEvento;
      if (reservarEvento) {
        this.updateForm(reservarEvento);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const reservarEvento = this.reservarEventoFormService.getReservarEvento(this.editForm);
    if (reservarEvento.id !== null) {
      this.subscribeToSaveResponse(this.reservarEventoService.update(reservarEvento));
    } else {
      this.subscribeToSaveResponse(this.reservarEventoService.create(reservarEvento));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReservarEvento>>): void {
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

  protected updateForm(reservarEvento: IReservarEvento): void {
    this.reservarEvento = reservarEvento;
    this.reservarEventoFormService.resetForm(this.editForm, reservarEvento);

    this.clientesSharedCollection = this.clienteService.addClienteToCollectionIfMissing<ICliente>(
      this.clientesSharedCollection,
      reservarEvento.clienteReservaEvento
    );
    this.colaboradorsSharedCollection = this.colaboradorService.addColaboradorToCollectionIfMissing<IColaborador>(
      this.colaboradorsSharedCollection,
      reservarEvento.colaboradorReservaEvento
    );
  }

  protected loadRelationshipsOptions(): void {
    this.clienteService
      .query()
      .pipe(map((res: HttpResponse<ICliente[]>) => res.body ?? []))
      .pipe(
        map((clientes: ICliente[]) =>
          this.clienteService.addClienteToCollectionIfMissing<ICliente>(clientes, this.reservarEvento?.clienteReservaEvento)
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
            this.reservarEvento?.colaboradorReservaEvento
          )
        )
      )
      .subscribe((colaboradors: IColaborador[]) => (this.colaboradorsSharedCollection = colaboradors));
  }
}
