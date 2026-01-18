import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { SignupComponent } from './auth/signup/signup.component';
import { HomeComponent } from './home/home.component';
import { RepositoryComponent } from './repository/repository.component';
import { WorkflowDesignerComponent } from './workflow-designer/workflow-designer.component';
import { WorkflowEditorComponent } from './workflow-designer/editor/workflow-editor.component';
import { InsightsComponent } from './insights/insights.component';
import { LandingPageComponent } from './home/landing-page/landing-page.component';
import { GuestGuard } from './guards/guest.guard';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
    { path: '', component: LandingPageComponent, canActivate: [GuestGuard] },
    // { path: 'login', component: LoginComponent, canActivate: [GuestGuard] }, // Local login replaced by SSO
    { path: 'signup', component: SignupComponent, canActivate: [GuestGuard] },
    { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
    { path: 'repository', component: RepositoryComponent, canActivate: [AuthGuard] },
    { path: 'workflow-designer', component: WorkflowDesignerComponent, canActivate: [AuthGuard] },
    { path: 'workflow-designer/editor', component: WorkflowEditorComponent, canActivate: [AuthGuard] },
    { path: 'insights', component: InsightsComponent, canActivate: [AuthGuard] }
];
