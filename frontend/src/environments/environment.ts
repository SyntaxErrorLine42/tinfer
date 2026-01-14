// Production environment configuration
// This file is used when building with --configuration production
export const environment = {
  production: true,
  // Backend API URL - will be replaced during Docker build or set via environment variable
  apiUrl: 'https://tinfer-backend.onrender.com',  // UPDATE WITH YOUR ACTUAL BACKEND URL
  supabaseUrl: 'https://jxyhzfzdomfhhlzcbvil.supabase.co',
  supabaseAnonKey: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imp4eWh6Znpkb21maGhsemNidmlsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjA2NDAyOTUsImV4cCI6MjA3NjIxNjI5NX0.Is1OoUUgjxkLGSKZM4_X9zAXSDLMpFH__W7xmRwwrwI',
};
