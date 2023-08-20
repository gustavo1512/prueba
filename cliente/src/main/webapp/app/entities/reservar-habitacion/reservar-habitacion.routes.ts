import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ReservarHabitacionComponent } from './list/reservar-habitacion.component';
import { ReservarHabitacionDetailComponent } from './detail/reservar-habitacion-detail.component';
import { ReservarHabitacionUpdateComponent } from './update/reservar-habitacion-update.component';
import ReservarHabitacionResolve from './route/reservar-habitacion-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const reservarHabitacionRoute: Routes = [
  {
    path: '',
    component: ReservarHabitacionComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ReservarHabitacionDetailComponent,
    resolve: {
      reservarHabitacion: ReservarHabitacionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ReservarHabitacionUpdateComponent,
    resolve: {
      reservarHabitacion: ReservarHabitacionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ReservarHabitacionUpdateComponent,
    resolve: {
      reservarHabitacion: ReservarHabitacionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default reservarHabitacionRoute;
