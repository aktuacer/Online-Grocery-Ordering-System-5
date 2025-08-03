import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  featuredProducts = [
    {
      id: 1,
      name: 'Fresh Apples',
      price: 3.99,
      image: 'assets/images/apples.jpg',
      description: 'Crisp and sweet red apples'
    },
    {
      id: 2,
      name: 'Organic Bananas',
      price: 2.49,
      image: 'assets/images/bananas.jpg',
      description: 'Fresh organic bananas'
    },
    {
      id: 3,
      name: 'Fresh Milk',
      price: 4.99,
      image: 'assets/images/milk.jpg',
      description: 'Farm fresh whole milk'
    }
  ];
}
