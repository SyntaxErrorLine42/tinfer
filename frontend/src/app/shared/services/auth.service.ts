import { Injectable } from '@angular/core';
import { createClient, SupabaseClient } from '@supabase/supabase-js';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private supabase: SupabaseClient;

  constructor() {
    this.supabase = createClient(environment.supabaseUrl, environment.supabaseAnonKey);
  }

  async register(payload: { fullName: string; email: string; password: string }) {
    const { data, error } = await this.supabase.auth.signUp({
      email: payload.email,
      password: payload.password,
      options: {
        data: {
          full_name: payload.fullName,
        },
      },
    });

    if (error) {
      throw error;
    }

    return data;
  }

  async login(payload: { email: string; password: string }) {
    const { data, error } = await this.supabase.auth.signInWithPassword({
      email: payload.email,
      password: payload.password,
    });

    if (error) {
      throw error;
    }

    return data;
  }

  async signOut() {
    await this.supabase.auth.signOut();
  }

  async getCurrentUser() {
    const { data, error } = await this.supabase.auth.getUser();

    if (error) {
      return null;
    }

    return data.user ?? null;
  }

  async getSession() {
    const { data, error } = await this.supabase.auth.getSession();

    if (error) {
      return null;
    }

    return data.session ?? null;
  }

  async signInWithOAuth(provider: 'google' | 'facebook', redirectTo?: string) {
    const { data, error } = await this.supabase.auth.signInWithOAuth({
      provider,
      options: {
        redirectTo: redirectTo || `${window.location.origin}/auth/callback`,
      },
    });

    if (error) {
      throw error;
    }

    return data;
  }
}
