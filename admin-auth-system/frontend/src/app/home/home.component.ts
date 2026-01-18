import { CommonModule } from '@angular/common';
import { Component, OnInit, signal, computed, ElementRef, HostListener, ViewChild } from '@angular/core';
import { ApiService } from '../services/api.service';
import { AuthService } from '../services/auth.service';
import { FormsModule } from '@angular/forms';
import {
    LucideAngularModule,
    Bell,
    Search,
    ChevronDown,
    Filter,
    Columns,
    MoreHorizontal,
    Layout,
    FileText,
    Users,
    Settings,
    PanelLeft,
    Clock,
    CheckCircle2,
    Inbox,
    List,
    ChevronRight,
    User as UserIcon,
    X,
    Calendar
} from 'lucide-angular';

interface WorkflowDef {
    id: string;
    name: string;
    description: string;
    fields: {
        label: string;
        type: 'text' | 'email' | 'date';
        placeholder?: string;
        defaultValue?: string;
        required: boolean;
        helpText?: string;
    }[];
}

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        LucideAngularModule
    ],
    providers: [
        LucideAngularModule.pick({
            Bell,
            Search,
            ChevronDown,
            Filter,
            Columns,
            MoreHorizontal,
            Layout,
            FileText,
            Users,
            Settings,
            PanelLeft,
            Clock,
            CheckCircle2,
            Inbox,
            List,
            ChevronRight,
            User: UserIcon,
            X,
            Calendar
        }).providers || []
    ],

    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

    user: { fullName: string; email: string } = { fullName: '', email: '' }; // Default empty

    isWorkflowMenuOpen = signal(false);
    activeModalWorkflowId = signal<string | null>(null);
    modalSearchQuery = signal('');

    // Mock Data matching the requested screenshot
    workflows = [
        {
            id: 1,
            name: 'Order Form with Burnley FC',
            stage: 'Sign',
            stageProgress: 3,
            turn: null,
            numTurns: null,
            assignees: [{ initial: 'D', color: 'bg-rose-100 text-rose-600' }],
            activity: 'Signature requested',
            time: '4 months ago'
        },
        {
            id: 2,
            name: 'Order Form with Patagonia',
            stage: 'Sign',
            stageProgress: 3,
            turn: null,
            numTurns: null,
            assignees: [{ initial: 'J', color: 'bg-purple-100 text-purple-600' }],
            activity: 'Signature collected',
            time: '4 months ago'
        },
        {
            id: 3,
            name: 'Order Form with Tesla',
            stage: 'Completed',
            stageProgress: 4,
            turn: null,
            numTurns: 1,
            assignees: [],
            activity: 'Contract auto-archived',
            time: '6 months ago'
        },
        {
            id: 4,
            name: 'Order Form with Tesla',
            stage: 'Completed',
            stageProgress: 4,
            turn: null,
            numTurns: 1,
            assignees: [],
            activity: 'Contract auto-archived',
            time: '6 months ago'
        },
        {
            id: 5,
            name: 'MNDA with Agreeable, Inc.',
            stage: 'Review',
            stageProgress: 2,
            turn: null,
            numTurns: null,
            assignees: [{ initial: 'N', color: 'bg-indigo-100 text-indigo-600' }],
            activity: 'Information refreshed',
            time: '1 year ago'
        },
        {
            id: 6,
            name: 'MNDA with Agreeable, Inc.',
            stage: 'Review',
            stageProgress: 2,
            turn: { text: 'Sedona Networks (Ironclad...', time: '2y' },
            numTurns: 2,
            assignees: [
                { initial: 'C', color: 'bg-teal-100 text-teal-600' },
                { initial: 'L', color: 'bg-pink-100 text-pink-600' }
            ],
            activity: 'Information refreshed',
            time: '1 year ago'
        },
        {
            id: 7,
            name: 'MNDA with Amazon UK Ltd',
            stage: 'Sign',
            stageProgress: 3,
            turn: null,
            numTurns: null,
            assignees: [{ initial: 'L', color: 'bg-yellow-100 text-yellow-600' }],
            activity: 'Signature requested',
            time: '2 months ago'
        },
        {
            id: 8,
            name: 'MNDA with Amazon UK Ltd',
            stage: 'Sign',
            stageProgress: 3,
            turn: null,
            numTurns: 1,
            assignees: [{ initial: 'C', color: 'bg-green-100 text-green-600' }],
            activity: 'Signature collected',
            time: '2 months ago'
        },
        {
            id: 9,
            name: 'Order Form with Patagonia West',
            stage: 'Review',
            stageProgress: 2,
            turn: null,
            numTurns: null,
            assignees: [{ initial: 'J', color: 'bg-purple-100 text-purple-600' }],
            activity: 'Information error',
            time: '4 months ago'
        }
    ];

    // Mock Data for Modal Definitions
    availableWorkflows: WorkflowDef[] = [
        {
            id: 'legal-review',
            name: 'Contract for Legal Review',
            description: 'Generate a placeholder record for an existing contract that needs legal review.',
            fields: [
                { label: 'Contract Title', type: 'text', placeholder: 'e.g. Master Services Agreement', required: true },
                { label: 'Counterparty', type: 'text', placeholder: 'Company Name', required: true },
                { label: 'Upload Date', type: 'date', required: true }
            ]
        },
        {
            id: 'mnda',
            name: 'MNDA',
            description: 'Use this workflow to send our form Mutual Non-Disclosure Agreement (MNDA) to a counterparty.',
            fields: [
                {
                    label: 'Counterparty Name *',
                    type: 'text',
                    placeholder: '',
                    helpText: 'Please include the full legal name (i.e. "Inc.", "LLC", etc).',
                    required: true
                },
                {
                    label: 'Counterparty Signer Name *',
                    type: 'text',
                    placeholder: '',
                    helpText: 'Please tell us the Name of the person signing',
                    required: true
                },
                {
                    label: 'Counterparty Signer Email *',
                    type: 'email',
                    placeholder: '',
                    helpText: 'Please tell us the Email Address of the person signing',
                    defaultValue: 'demo@gildedrecords.com',
                    required: true
                },
                {
                    label: 'Effective Date *',
                    type: 'date',
                    defaultValue: '2025-05-21',
                    required: true
                }
            ]
        },
        {
            id: 'sales-agreement',
            name: 'Sales Agreement',
            description: 'Initiate a standard sales agreement for new customers.',
            fields: [
                { label: 'Customer Name', type: 'text', required: true },
                { label: 'Deal Value', type: 'text', placeholder: '$0.00', required: true },
                { label: 'Sales Rep', type: 'text', defaultValue: '', required: true }
            ]
        }
    ];

    filteredWorkflows = computed(() => {
        const query = this.modalSearchQuery().toLowerCase();
        return this.availableWorkflows.filter(w =>
            w.name.toLowerCase().includes(query)
        );
    });

    selectedWorkflow = computed(() => {
        const id = this.activeModalWorkflowId();
        return this.availableWorkflows.find(w => w.id === id) || this.availableWorkflows[1];
    });

    dropdownSearchQuery = signal('');

    @ViewChild('dropdownContainer') dropdownContainer!: ElementRef;
    @ViewChild('menuTrigger') menuTrigger!: ElementRef;

    @HostListener('document:click', ['$event'])
    onDocumentClick(event: MouseEvent) {
        if (this.isWorkflowMenuOpen()) {
            const clickedInsideRegex = this.dropdownContainer?.nativeElement.contains(event.target);
            const clickedTrigger = this.menuTrigger?.nativeElement.contains(event.target);
            if (!clickedInsideRegex && !clickedTrigger) {
                this.isWorkflowMenuOpen.set(false);
            }
        }
    }

    constructor(
        public authService: AuthService,
        private apiService: ApiService,
        private elementRef: ElementRef
    ) { }

    ngOnInit(): void {
        this.authService.getUser().subscribe(user => {
            if (user) {
                this.user = {
                    fullName: user.name || user.fullName || 'User',
                    email: user.email || 'user@example.com'
                };
            }
        });

        // Initial load
        this.loadWorkflows();
    }

    loadWorkflows(search: string = '') {
        this.apiService.getWorkflows(0, 10, search).subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    this.availableWorkflows = res.data.content.map(wf => ({
                        id: wf.id,
                        name: wf.name,
                        description: wf.description || 'No description provided',
                        fields: (wf.variableResponse || []).map((v: any) => ({
                            label: v.label || v.variableName,
                            type: v.dataType === 'INTEGER' ? 'text' : 'text', // Simplify to text for now
                            placeholder: '',
                            defaultValue: v.defaultValue,
                            required: v.required,
                            helpText: ''
                        }))
                    }));
                }
            },
            error: (err) => console.error('Failed to load workflows', err)
        });
    }

    onDropdownSearch(event: Event) {
        const target = event.target as HTMLInputElement;
        this.dropdownSearchQuery.set(target.value);
        this.loadWorkflows(target.value);
    }

    clearDropdownSearch() {
        this.dropdownSearchQuery.set('');
        this.loadWorkflows('');
    }

    handleLaunchModal(id: string) {
        this.activeModalWorkflowId.set(id);
        this.isWorkflowMenuOpen.set(false);
        this.modalSearchQuery.set('');
    }

    closeModal() {
        this.activeModalWorkflowId.set(null);
    }

    getInitials(name: string): string {
        if (!name) return '';
        return name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
    }

    logout(): void {
        this.authService.logout();
    }
}
