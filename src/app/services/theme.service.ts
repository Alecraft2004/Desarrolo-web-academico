import { Injectable, signal } from '@angular/core';

const STORAGE_KEY = 'theme';
type Theme = 'light' | 'dark';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  theme = signal<Theme>((localStorage.getItem(STORAGE_KEY) as Theme) || 'light');

  constructor() {
    this.aplicar(this.theme());
  }

  toggle() {
    this.set(this.theme() === 'dark' ? 'light' : 'dark');
  }

  set(theme: Theme) {
    this.theme.set(theme);
    localStorage.setItem(STORAGE_KEY, theme);
    this.aplicar(theme);
  }

  private aplicar(theme: Theme) {
    document.documentElement.setAttribute('data-theme', theme);
  }
}
