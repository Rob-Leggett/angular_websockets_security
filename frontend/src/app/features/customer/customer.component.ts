import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export interface Customer {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
}

@Component({
  selector: 'app-customer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container mt-4">
      <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <h4 class="mb-0">Customer Management</h4>
          <button class="btn btn-primary btn-sm" (click)="showAddForm = true">
            <i class="bi bi-plus-lg"></i> Add Customer
          </button>
        </div>
        
        <div class="card-body">
          <!-- Search -->
          <div class="mb-3">
            <div class="input-group">
              <input 
                type="text" 
                class="form-control" 
                placeholder="Search customers..."
                [(ngModel)]="searchQuery"
                (input)="search()"
              >
              <button class="btn btn-outline-secondary" (click)="loadCustomers()">
                <i class="bi bi-arrow-clockwise"></i>
              </button>
            </div>
          </div>
          
          <!-- Add/Edit Form -->
          @if (showAddForm || editingCustomer) {
            <div class="card mb-3 bg-light">
              <div class="card-body">
                <h5>{{ editingCustomer ? 'Edit' : 'Add' }} Customer</h5>
                <form (ngSubmit)="saveCustomer()" #customerForm="ngForm">
                  <div class="row">
                    <div class="col-md-3 mb-2">
                      <input 
                        type="text" 
                        class="form-control" 
                        placeholder="First Name"
                        [(ngModel)]="currentCustomer.firstName"
                        name="firstName"
                        required
                      >
                    </div>
                    <div class="col-md-3 mb-2">
                      <input 
                        type="text" 
                        class="form-control" 
                        placeholder="Last Name"
                        [(ngModel)]="currentCustomer.lastName"
                        name="lastName"
                        required
                      >
                    </div>
                    <div class="col-md-3 mb-2">
                      <input 
                        type="email" 
                        class="form-control" 
                        placeholder="Email"
                        [(ngModel)]="currentCustomer.email"
                        name="email"
                      >
                    </div>
                    <div class="col-md-3 mb-2">
                      <input 
                        type="text" 
                        class="form-control" 
                        placeholder="Phone"
                        [(ngModel)]="currentCustomer.phone"
                        name="phone"
                      >
                    </div>
                  </div>
                  <div class="mt-2">
                    <button type="submit" class="btn btn-success btn-sm" [disabled]="!customerForm.form.valid">
                      Save
                    </button>
                    <button type="button" class="btn btn-secondary btn-sm ms-2" (click)="cancelEdit()">
                      Cancel
                    </button>
                  </div>
                </form>
              </div>
            </div>
          }
          
          <!-- Customer Table -->
          <table class="table table-striped customer-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>First Name</th>
                <th>Last Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (customer of customers; track customer.id) {
                <tr>
                  <td>{{ customer.id }}</td>
                  <td>{{ customer.firstName }}</td>
                  <td>{{ customer.lastName }}</td>
                  <td>{{ customer.email }}</td>
                  <td>{{ customer.phone }}</td>
                  <td>
                    <div class="btn-group">
                      <button class="btn btn-outline-primary" (click)="editCustomer(customer)">
                        Edit
                      </button>
                      <button class="btn btn-outline-danger" (click)="deleteCustomer(customer.id!)">
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              } @empty {
                <tr>
                  <td colspan="6" class="text-center text-muted">No customers found</td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class CustomerComponent implements OnInit {
  private http = inject(HttpClient);

  customers: Customer[] = [];
  searchQuery = '';
  showAddForm = false;
  editingCustomer: Customer | null = null;
  currentCustomer: Customer = this.emptyCustomer();

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.http.get<Customer[]>(`${environment.apiUrl}/customers`).subscribe({
      next: (customers) => this.customers = customers,
      error: (err) => console.error('Failed to load customers:', err)
    });
  }

  search(): void {
    if (this.searchQuery.trim()) {
      this.http.get<Customer[]>(`${environment.apiUrl}/customers/search?query=${this.searchQuery}`).subscribe({
        next: (customers) => this.customers = customers,
        error: (err) => console.error('Search failed:', err)
      });
    } else {
      this.loadCustomers();
    }
  }

  saveCustomer(): void {
    if (this.editingCustomer) {
      // Update
      this.http.put<Customer>(`${environment.apiUrl}/customers/${this.editingCustomer.id}`, this.currentCustomer).subscribe({
        next: () => {
          this.loadCustomers();
          this.cancelEdit();
        },
        error: (err) => console.error('Update failed:', err)
      });
    } else {
      // Create
      this.http.post<Customer>(`${environment.apiUrl}/customers`, this.currentCustomer).subscribe({
        next: () => {
          this.loadCustomers();
          this.cancelEdit();
        },
        error: (err) => console.error('Create failed:', err)
      });
    }
  }

  editCustomer(customer: Customer): void {
    this.editingCustomer = customer;
    this.currentCustomer = { ...customer };
    this.showAddForm = false;
  }

  deleteCustomer(id: number): void {
    if (confirm('Are you sure you want to delete this customer?')) {
      this.http.delete(`${environment.apiUrl}/customers/${id}`).subscribe({
        next: () => this.loadCustomers(),
        error: (err) => console.error('Delete failed:', err)
      });
    }
  }

  cancelEdit(): void {
    this.showAddForm = false;
    this.editingCustomer = null;
    this.currentCustomer = this.emptyCustomer();
  }

  private emptyCustomer(): Customer {
    return { firstName: '', lastName: '', email: '', phone: '' };
  }
}
