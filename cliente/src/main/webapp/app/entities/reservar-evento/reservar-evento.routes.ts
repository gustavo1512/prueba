import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ReservarEventoComponent } from './list/reservar-evento.component';
import { ReservarEventoDetailComponent } from './detail/reservar-evento-detail.component';
import { ReservarEventoUpdateComponent } from './update/reservar-evento-update.component';
import ReservarEventoResolve from './route/reservar-evento-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const reservarEventoRoute: Routes = [
  {
    path: '',
    component: ReservarEventoComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ReservarEventoDetailComponent,
    resolve: {
      reservarEvento: ReservarEventoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ReservarEventoUpdateComponent,
    resolve: {
      reservarEvento: ReservarEventoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ReservarEventoUpdateComponent,
    resolve: {
      reservarEvento: ReservarEventoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default reservarEventoRoute;
