import { Routes } from '@angular/router';
import { LandingPage } from './pages/landingPage/landing.page';
import { LoginPage } from './pages/auth/login/login.page';
import { RegisterPage } from './pages/auth/register/register.page';
import { SwipePage } from './pages/swipe/swipe.page';
import { CreateProfilePage } from './pages/profile/create-profile.page';
import { HomePage } from './pages/home/home.page';
// import { ProfilePage } from './pages/profile/profile.page';
import { authGuard } from '@shared/guards/auth.guard';

export const routes: Routes = [
  { path: '', component: LandingPage }, // Public
  { path: 'login', component: LoginPage }, // Public
  { path: 'register', component: RegisterPage }, // Public
  { path: 'create-profile', component: CreateProfilePage, canMatch: [authGuard] }, // Only authenticated users
  { path: 'home', component: HomePage, canMatch: [authGuard] }, // Main app page with conversations and swipes
  { path: 'swipe', component: SwipePage, canMatch: [authGuard] }, // Only authenticated users
  // { path: 'profile', component: ProfilePage },
];
