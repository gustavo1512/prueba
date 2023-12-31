import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EventoComponent } from './list/evento.component';
import { EventoDetailComponent } from './detail/evento-detail.component';
import { EventoUpdateComponent } from './update/evento-update.component';
import EventoResolve from './route/evento-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const eventoRoute: Routes = [
  {
    path: '',
    component: EventoComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EventoDetailComponent,
    resolve: {
      evento: EventoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EventoUpdateComponent,
    resolve: {
      evento: EventoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EventoUpdateComponent,
    resolve: {
      evento: EventoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default eventoRoute;
