import { Component, OnInit, inject, ElementRef, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  isDropdownOpen = false;
  isNotificationOpen = false;
  user$: Observable<any> | undefined;

  private notificationService = inject(NotificationService);
  notifications = this.notificationService.allNotifications;
  unreadCount = this.notificationService.unreadCount;

  constructor(private authService: AuthService, private router: Router, private eRef: ElementRef) { }

  @HostListener('document:click', ['$event'])
  clickout(event: Event) {
    if (!this.eRef.nativeElement.contains(event.target)) {
      this.isDropdownOpen = false;
      this.isNotificationOpen = false;
    }
  }

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
    if (this.isDropdownOpen) this.isNotificationOpen = false;
  }

  toggleNotifications() {
    this.isNotificationOpen = !this.isNotificationOpen;
    if (this.isNotificationOpen) this.isDropdownOpen = false;
  }

  markAsRead(id: string) {
    this.notificationService.markAsRead(id);
  }

  markAllRead() {
    this.notificationService.markAllAsRead();
  }

  logout() {
    this.authService.logout();
  }


}
