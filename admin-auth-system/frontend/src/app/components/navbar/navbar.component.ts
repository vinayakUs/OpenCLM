import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  isDropdownOpen = false;
  user$: Observable<any> | undefined;

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    this.user$ = this.authService.getUser().pipe(
      tap(user => console.log('User Details:', user)),
      map(user => ({
        ...user,
        name: user.name || user.attributes?.sub || 'User',
        email: user.email || user.attributes?.sub || ''
      }))
    );
  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  logout() {
    this.authService.logout();
  }
}
