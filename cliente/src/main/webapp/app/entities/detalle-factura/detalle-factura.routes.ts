import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DetalleFacturaComponent } from './list/detalle-factura.component';
import { DetalleFacturaDetailComponent } from './detail/detalle-factura-detail.component';
import { DetalleFacturaUpdateComponent } from './update/detalle-factura-update.component';
import DetalleFacturaResolve from './route/detalle-factura-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const detalleFacturaRoute: Routes = [
  {
    path: '',
    component: DetalleFacturaComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DetalleFacturaDetailComponent,
    resolve: {
      detalleFactura: DetalleFacturaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DetalleFacturaUpdateComponent,
    resolve: {
      detalleFactura: DetalleFacturaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DetalleFacturaUpdateComponent,
    resolve: {
      detalleFactura: DetalleFacturaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default detalleFacturaRoute;
