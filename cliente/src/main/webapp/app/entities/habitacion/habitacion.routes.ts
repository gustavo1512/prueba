import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { HabitacionComponent } from './list/habitacion.component';
import { HabitacionDetailComponent } from './detail/habitacion-detail.component';
import { HabitacionUpdateComponent } from './update/habitacion-update.component';
import HabitacionResolve from './route/habitacion-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const habitacionRoute: Routes = [
  {
    path: '',
    component: HabitacionComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: HabitacionDetailComponent,
    resolve: {
      habitacion: HabitacionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: HabitacionUpdateComponent,
    resolve: {
      habitacion: HabitacionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: HabitacionUpdateComponent,
    resolve: {
      habitacion: HabitacionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default habitacionRoute;
