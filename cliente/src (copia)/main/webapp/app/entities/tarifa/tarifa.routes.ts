import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TarifaComponent } from './list/tarifa.component';
import { TarifaDetailComponent } from './detail/tarifa-detail.component';
import { TarifaUpdateComponent } from './update/tarifa-update.component';
import TarifaResolve from './route/tarifa-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const tarifaRoute: Routes = [
  {
    path: '',
    component: TarifaComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TarifaDetailComponent,
    resolve: {
      tarifa: TarifaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TarifaUpdateComponent,
    resolve: {
      tarifa: TarifaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TarifaUpdateComponent,
    resolve: {
      tarifa: TarifaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default tarifaRoute;
