import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, HttpClientModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  loginData = {
    email: '',
    password: '',
    userType: 'customer',
  };

  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private http: HttpClient, private router: Router) {}

  onSubmit() {
    if (this.isLoading) return;

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const loginRequest = {
      username: this.loginData.email,
      password: this.loginData.password,
      userType: this.loginData.userType,
    };

    this.http
      .post<any>('http://localhost:8080/api/auth/login', loginRequest)
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success) {
            this.successMessage = 'Login successful!';

            // Store user session
            localStorage.setItem('userSession', JSON.stringify(response.data));

            // Redirect based on user type
            if (this.loginData.userType === 'admin') {
              window.location.href = 'http://localhost:8080/login';
            } else {
              this.router.navigate(['/products']);
            }
          } else {
            this.errorMessage = response.message || 'Login failed';
          }
        },
        error: (error) => {
          this.isLoading = false;
          console.error('Login error:', error);
          this.errorMessage =
            error.error?.message ||
            'Please Enter Correct UserName and Password';
        },
      });
  }
}
