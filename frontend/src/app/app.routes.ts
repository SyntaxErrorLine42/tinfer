import { Routes } from '@angular/router';
import { LandingPage } from './pages/landingPage/landing.page';
import { LoginPage } from './pages/auth/login/login.page';
import { RegisterPage } from './pages/auth/register/register.page';
import { SwipePage } from './pages/swipe/swipe.page';
// import { ProfilePage } from './pages/profile/profile.page';

export const routes: Routes = [
  { path: '', component: LandingPage },
  { path: 'login', component: LoginPage },
  { path: 'register', component: RegisterPage },
  { path: 'swipe', component: SwipePage },
  // { path: 'profile', component: ProfilePage },
];
