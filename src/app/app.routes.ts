import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';
import { DashboardComponent } from './components/dashboard/dashboard';
import { EstudiantesComponent } from './components/estudiantes/estudiantes';
import { CursosComponent } from './components/cursos/cursos';
import { ProfesoresComponent } from './components/profesores/profesores';
import { InscripcionesComponent } from './components/inscripciones/inscripciones';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'estudiantes', pathMatch: 'full' },
      { path: 'estudiantes', component: EstudiantesComponent },
      { path: 'cursos', component: CursosComponent },
      { path: 'profesores', component: ProfesoresComponent },
      { path: 'inscripciones', component: InscripcionesComponent }
    ]
  }
];
