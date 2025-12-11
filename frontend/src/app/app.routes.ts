import { Routes } from '@angular/router';
import { LandingPage } from './pages/landingPage/landing.page';
import { LoginPage } from './pages/auth/login/login.page';
import { RegisterPage } from './pages/auth/register/register.page';
import { SwipePage } from './pages/swipe/swipe.page';
// import { ProfilePage } from './pages/profile/profile.page';
import { authGuard } from '@shared/guards/auth.guard';

export const routes: Routes = [
  { path: '', component: LandingPage }, // Public
  { path: 'login', component: LoginPage }, // Public
  { path: 'register', component: RegisterPage }, // Public
  { path: 'swipe', component: SwipePage, canMatch: [authGuard] }, // Only authenticated users
  // { path: 'profile', component: ProfilePage },
];
