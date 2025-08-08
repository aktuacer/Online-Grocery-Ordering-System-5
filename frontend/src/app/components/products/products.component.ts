import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css',
})
export class ProductsComponent implements OnInit {
  products: any[] = [];
  searchTerm = '';
  isLoading = false;
  isAddingToCart = false;
  errorMessage = '';
  successMessage = '';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.isLoading = true;
    this.errorMessage = '';

    this.http.get<any>('http://localhost:8080/api/products').subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.products = response.data.map((product: any) => ({
            ...product,
            selectedQuantity: 1,
          }));
        } else {
          this.errorMessage = response.message || 'Failed to load products';
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error loading products:', error);
        this.errorMessage = 'Failed to load products. Please try again.';
      },
    });
  }

  searchProducts() {
    if (!this.searchTerm.trim()) {
      this.loadProducts();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.http
      .get<any>(
        `http://localhost:8080/api/products/search?name=${encodeURIComponent(
          this.searchTerm
        )}`
      )
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success) {
            this.products = response.data.map((product: any) => ({
              ...product,
              selectedQuantity: 1,
            }));
          } else {
            this.products = [];
            this.errorMessage = response.message || 'No products found';
          }
        },
        error: (error) => {
          this.isLoading = false;
          console.error('Error searching products:', error);
          this.products = [];
          this.errorMessage = 'Search failed. Please try again.';
        },
      });
  }

  clearSearch() {
    this.searchTerm = '';
    this.loadProducts();
  }

  addToCart(product: any) {
    // Check if user is logged in
    const userSession = localStorage.getItem('userSession');
    if (!userSession) {
      this.errorMessage = 'Please login to add items to cart';
      return;
    }

    this.isAddingToCart = true;
    this.errorMessage = '';
    this.successMessage = '';

    const quantity = product.selectedQuantity || 1;

    // For now, just show success message (cart functionality can be implemented later)
    setTimeout(() => {
      this.isAddingToCart = false;
      this.successMessage = `Added ${quantity} ${product.productName}(s) to cart!`;

      // Clear success message after 3 seconds
      setTimeout(() => {
        this.successMessage = '';
      }, 3000);
    }, 1000);
  }
}
