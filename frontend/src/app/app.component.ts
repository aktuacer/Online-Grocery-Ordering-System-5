import { Component, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  title = 'Online Grocery Store';
  isLoggedIn = false;
  currentUser: any = null;

  ngOnInit() {
    // Check if user is logged in
    this.checkAuthStatus();
  }

  checkAuthStatus() {
    // Check localStorage for user session
    const userSession = localStorage.getItem('userSession');
    if (userSession) {
      try {
        this.currentUser = JSON.parse(userSession);
        this.isLoggedIn = true;
      } catch (error) {
        console.error('Error parsing user session:', error);
        localStorage.removeItem('userSession');
      }
    }
  }

  logout() {
    // Clear user session
    localStorage.removeItem('userSession');
    this.isLoggedIn = false;
    this.currentUser = null;

    // Redirect to home page
    window.location.href = '/';
  }
}
