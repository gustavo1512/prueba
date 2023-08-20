import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { FacturaFormService, FacturaFormGroup } from './factura-form.service';
import { IFactura } from '../factura.model';
import { FacturaService } from '../service/factura.service';
import { IDetalleFactura } from 'app/entities/detalle-factura/detalle-factura.model';
import { DetalleFacturaService } from 'app/entities/detalle-factura/service/detalle-factura.service';
import { ICliente } from 'app/entities/cliente/cliente.model';
import { ClienteService } from 'app/entities/cliente/service/cliente.service';
import { IColaborador } from 'app/entities/colaborador/colaborador.model';
import { ColaboradorService } from 'app/entities/colaborador/service/colaborador.service';

@Component({
  standalone: true,
  selector: 'jhi-factura-update',
  templateUrl: './factura-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class FacturaUpdateComponent implements OnInit {
  isSaving = false;
  factura: IFactura | null = null;

  detalleFacturasSharedCollection: IDetalleFactura[] = [];
  clientesSharedCollection: ICliente[] = [];
  colaboradorsSharedCollection: IColaborador[] = [];

  editForm: FacturaFormGroup = this.facturaFormService.createFacturaFormGroup();

  constructor(
    protected facturaService: FacturaService,
    protected facturaFormService: FacturaFormService,
    protected detalleFacturaService: DetalleFacturaService,
    protected clienteService: ClienteService,
    protected colaboradorService: ColaboradorService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareDetalleFactura = (o1: IDetalleFactura | null, o2: IDetalleFactura | null): boolean =>
    this.detalleFacturaService.compareDetalleFactura(o1, o2);

  compareCliente = (o1: ICliente | null, o2: ICliente | null): boolean => this.clienteService.compareCliente(o1, o2);

  compareColaborador = (o1: IColaborador | null, o2: IColaborador | null): boolean => this.colaboradorService.compareColaborador(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ factura }) => {
      this.factura = factura;
      if (factura) {
        this.updateForm(factura);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const factura = this.facturaFormService.getFactura(this.editForm);
    if (factura.id !== null) {
      this.subscribeToSaveResponse(this.facturaService.update(factura));
    } else {
      this.subscribeToSaveResponse(this.facturaService.create(factura));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFactura>>): void {
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

  protected updateForm(factura: IFactura): void {
    this.factura = factura;
    this.facturaFormService.resetForm(this.editForm, factura);

    this.detalleFacturasSharedCollection = this.detalleFacturaService.addDetalleFacturaToCollectionIfMissing<IDetalleFactura>(
      this.detalleFacturasSharedCollection,
      factura.detalleFactura
    );
    this.clientesSharedCollection = this.clienteService.addClienteToCollectionIfMissing<ICliente>(
      this.clientesSharedCollection,
      factura.clienteFactura
    );
    this.colaboradorsSharedCollection = this.colaboradorService.addColaboradorToCollectionIfMissing<IColaborador>(
      this.colaboradorsSharedCollection,
      factura.clienteColaborador
    );
  }

  protected loadRelationshipsOptions(): void {
    this.detalleFacturaService
      .query()
      .pipe(map((res: HttpResponse<IDetalleFactura[]>) => res.body ?? []))
      .pipe(
        map((detalleFacturas: IDetalleFactura[]) =>
          this.detalleFacturaService.addDetalleFacturaToCollectionIfMissing<IDetalleFactura>(detalleFacturas, this.factura?.detalleFactura)
        )
      )
      .subscribe((detalleFacturas: IDetalleFactura[]) => (this.detalleFacturasSharedCollection = detalleFacturas));

    this.clienteService
      .query()
      .pipe(map((res: HttpResponse<ICliente[]>) => res.body ?? []))
      .pipe(
        map((clientes: ICliente[]) => this.clienteService.addClienteToCollectionIfMissing<ICliente>(clientes, this.factura?.clienteFactura))
      )
      .subscribe((clientes: ICliente[]) => (this.clientesSharedCollection = clientes));

    this.colaboradorService
      .query()
      .pipe(map((res: HttpResponse<IColaborador[]>) => res.body ?? []))
      .pipe(
        map((colaboradors: IColaborador[]) =>
          this.colaboradorService.addColaboradorToCollectionIfMissing<IColaborador>(colaboradors, this.factura?.clienteColaborador)
        )
      )
      .subscribe((colaboradors: IColaborador[]) => (this.colaboradorsSharedCollection = colaboradors));
  }
}
