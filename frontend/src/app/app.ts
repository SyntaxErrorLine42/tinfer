import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { DarkModeService } from '@shared/services/darkmode.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrls: ['./app.css'],
})
export class App implements OnInit {
  protected readonly title = signal('hii');

  private readonly darkModeService = inject(DarkModeService);

  ngOnInit(): void {
    this.darkModeService.initTheme(); // initialize theme on app startup
  }
}
