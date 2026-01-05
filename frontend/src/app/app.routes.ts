import { Routes } from '@angular/router';
import { LandingPage } from './pages/landingPage/landing.page';
import { LoginPage } from './pages/auth/login/login.page';
import { RegisterPage } from './pages/auth/register/register.page';
import { CreateProfilePage } from './pages/auth/create-profile/create-profile.page';
import { CallbackPage } from './pages/auth/callback/callback.page';
import { SwipePage } from './pages/swipe/swipe.page';
import { ProfilePage } from './pages/profile/profile.page';
import { ConversationsPage } from './pages/conversations/conversations.page';
import { authGuard } from '@shared/guards/auth.guard';

export const routes: Routes = [
  { path: '', component: LandingPage }, // Public
  { path: 'login', component: LoginPage }, // Public
  { path: 'register', component: RegisterPage }, // Public
  { path: 'auth/callback', component: CallbackPage }, // OAuth callback
  { path: 'create-profile', component: CreateProfilePage, canMatch: [authGuard] }, // Only authenticated
  { path: 'swipe', component: SwipePage, canMatch: [authGuard] }, // Only authenticated users
  { path: 'profile', component: ProfilePage, canMatch: [authGuard] }, // Only authenticated users
  { path: 'profile/:id', component: ProfilePage, canMatch: [authGuard] },  // View other user's profile
  { path: 'conversations', component: ConversationsPage, canMatch: [authGuard] }, // Chat/conversations
];
