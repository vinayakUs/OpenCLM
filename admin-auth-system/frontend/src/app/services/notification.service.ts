import { Injectable, signal, computed } from '@angular/core';

export interface AppNotification {
    id: string;
    title: string;
    message: string;
    type: 'info' | 'success' | 'warning' | 'error' | 'progress';
    read: boolean;
    progress?: number; // 0-100
    createdAt: Date;
}

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private notifications = signal<AppNotification[]>([
        {
            id: '1',
            title: 'Workflow Published',
            message: 'MSA Master Template has been successfully published.',
            type: 'success',
            read: false,
            createdAt: new Date()
        },
        {
            id: '2',
            title: 'Generating PDF',
            message: 'Contract #4402 export in progress...',
            type: 'progress',
            read: false,
            progress: 65,
            createdAt: new Date(Date.now() - 1000 * 60 * 5) // 5 mins ago
        },
        {
            id: '3',
            title: 'Version Conflict',
            message: 'Changes detected in external document.',
            type: 'warning',
            read: true,
            createdAt: new Date(Date.now() - 1000 * 60 * 60 * 2) // 2 hours ago
        },
        {
            id: '4',
            title: 'Review Request',
            message: 'Alice requested review on NDA Template.',
            type: 'info',
            read: true,
            createdAt: new Date(Date.now() - 1000 * 60 * 60 * 5)
        },
        {
            id: '5',
            title: 'System Update',
            message: 'Scheduled maintenance in 24h.',
            type: 'info',
            read: true,
            createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24)
        },
        {
            id: '6',
            title: 'Workflow Approved',
            message: 'HR Onboarding workflow approved.',
            type: 'success',
            read: true,
            createdAt: new Date(Date.now() - 1000 * 60 * 60 * 48)
        }
    ]);

    // Computed signals for easy consumption
    allNotifications = computed(() => this.notifications());
    unreadCount = computed(() => this.notifications().filter(n => !n.read).length);

    constructor() { }

    markAsRead(id: string) {
        this.notifications.update(list =>
            list.map(n => n.id === id ? { ...n, read: true } : n)
        );
    }

    markAllAsRead() {
        this.notifications.update(list =>
            list.map(n => ({ ...n, read: true }))
        );
    }

    // For demo: Add a notification
    addNotification(notification: AppNotification) {
        this.notifications.update(list => [notification, ...list]);
    }
}
