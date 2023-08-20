import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'reservar-habitacion',
        data: { pageTitle: 'clienteApp.reservarHabitacion.home.title' },
        loadChildren: () => import('./reservar-habitacion/reservar-habitacion.routes'),
      },
      {
        path: 'evento',
        data: { pageTitle: 'clienteApp.evento.home.title' },
        loadChildren: () => import('./evento/evento.routes'),
      },
      {
        path: 'detalle-factura',
        data: { pageTitle: 'clienteApp.detalleFactura.home.title' },
        loadChildren: () => import('./detalle-factura/detalle-factura.routes'),
      },
      {
        path: 'persona',
        data: { pageTitle: 'clienteApp.persona.home.title' },
        loadChildren: () => import('./persona/persona.routes'),
      },
      {
        path: 'cliente',
        data: { pageTitle: 'clienteApp.cliente.home.title' },
        loadChildren: () => import('./cliente/cliente.routes'),
      },
      {
        path: 'reservar-evento',
        data: { pageTitle: 'clienteApp.reservarEvento.home.title' },
        loadChildren: () => import('./reservar-evento/reservar-evento.routes'),
      },
      {
        path: 'colaborador',
        data: { pageTitle: 'clienteApp.colaborador.home.title' },
        loadChildren: () => import('./colaborador/colaborador.routes'),
      },
      {
        path: 'tipo-cargo',
        data: { pageTitle: 'clienteApp.tipoCargo.home.title' },
        loadChildren: () => import('./tipo-cargo/tipo-cargo.routes'),
      },
      {
        path: 'tarifa',
        data: { pageTitle: 'clienteApp.tarifa.home.title' },
        loadChildren: () => import('./tarifa/tarifa.routes'),
      },
      {
        path: 'habitacion',
        data: { pageTitle: 'clienteApp.habitacion.home.title' },
        loadChildren: () => import('./habitacion/habitacion.routes'),
      },
      {
        path: 'factura',
        data: { pageTitle: 'clienteApp.factura.home.title' },
        loadChildren: () => import('./factura/factura.routes'),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
