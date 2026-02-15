import {Component, computed, ElementRef, inject, OnInit, signal, ViewChild} from '@angular/core';
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
import {ApiService} from '../../services/api.service';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
interface WorkflowDef {
  id: string;
  name: string;
  description: string;
  fields?: {
    label: string;
    variableName: string;
    type: string;
    placeholder?: string;
    defaultValue?: string;
    required: boolean;
    helpText?: string;
  }[];
}
enum WorkflowStatus {
  DRAFT = "DRAFT",
  PUBLISHED = "PUBLISHED",
  ARCHIVED = "ARCHIVED"
}
interface Workflow {
  id: number;
  name: string;
  stage: string;
  stageProgress: number;
  turn: { text: string; time: string } | null;
  numTurns: number | null;
  assignees: { initial: string; color: string }[];
  activity: string;
  time: string;
}
export interface WorkflowDetails {
  id: string;
  name: string;
  description: string;
  templateFileId: string;
  currentStatus: WorkflowStatus;
  version: number;
  createdBy: string;
  createdAt: string; // ISO date string
  updatedAt: string; // ISO date string
  variableResponse: WorkflowVariable[];
}

interface WorkflowVariable {
  id: string;
  variableName: string;
  dataType: VariableDataType;
  label: string;
  required: boolean;
  defaultValue: string | null;
  sortOrder: number;
  createdAt: string | null;
}
enum VariableDataType {
  STRING = "STRING",
  NUMBER = "NUMBER",
  BOOLEAN = "BOOLEAN",
  DATE = "DATE"
}

interface ContractCreationRequest {
  "workflowId": string,
  "contractName": string,
  "formData": Record<string, any>
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    LucideAngularModule,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
  host: {
    '(document:click)': 'onDocumentClick($event)'
  },
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
  ]
})


class Dashboard  {

  isWorkflowMenuOpen = signal(false);
  dropdownSearchQuery = signal('');
  isWorkflowSearchLoading = signal<boolean>(false); // start workflow loading
  availableWorkflows = signal<WorkflowDef[]>([]);
  activeModalWorkflowId = signal<string | null>(null);
  private readonly apiService = inject(ApiService);
  private readonly fb = inject(FormBuilder);
  modalSearchQuery = signal('');
  isWorkflowLoading = signal<boolean>(false);
  selectedWorkflowDetails = signal<WorkflowDetails|null>(null);
  @ViewChild('dropdownContainer') dropdownContainer!: ElementRef;
  @ViewChild('menuTrigger') menuTrigger!: ElementRef;
  form = this.fb.group({
    workflowId: [''],
    name: ['']
  });


  constructor() {
  }

  // Mock Data matching the requested screenshot
  workflows: Workflow[] = [
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
    }
  ];



  loadWorkflows(search: string = '') {
    this.isWorkflowSearchLoading.set(true);

    console.log(this.isWorkflowSearchLoading);
    this.apiService.getWorkflowsSearchRes(0, 10, search).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          const workflows = res.data.content.map((wf: any) => ({
            id: wf.id,
            name: wf.name,
            description: wf.description || 'No description provided',
            fields: (wf.variableResponse || []).map((v: any) => ({
              label: v.label || v.variableName,
              variableName: v.variableName,
              type: v.dataType === 'INTEGER' ? 'number' : 'text',
              placeholder: '',
              defaultValue: v.defaultValue,
              required: v.required,
              helpText: ''
            }))
          }));
          this.availableWorkflows.set(workflows);
        }
      },
      error: (err) => {
        this.isWorkflowSearchLoading.set(false);
        console.error('Failed to load workflows', err);
      }
    });

    this.isWorkflowSearchLoading.set(false);

  }

  protected onStartWorkflowClick() {
    this.isWorkflowMenuOpen.set(!this.isWorkflowMenuOpen());
    this.loadWorkflows();

  }

  loadWorkflowDetails(id: string) {
    this.isWorkflowLoading.set(true);

    this.apiService.getWorkflowById(id).subscribe({
      next: (res) => {
        if (res.success) {
          this.selectedWorkflowDetails.set(res.data);
          console.log(res)
          this.buildForm(res.data.id);
        }
        this.isWorkflowLoading.set(false);
      },
      error: () => {
        this.selectedWorkflowDetails.set(null);
        this.isWorkflowLoading.set(false);
      }
    });
  }

  handleLaunchModal(id: string) {
    console.log('handleLaunchModal called with id:', id);
    this.activeModalWorkflowId.set(id);
    this.isWorkflowMenuOpen.set(false);
    this.modalSearchQuery.set('');

    // Reset name for new workflow
    this.form.get('name')?.setValue('');

    this.loadWorkflowDetails(id);
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

  filteredWorkflows = computed(() => {
    const query = this.modalSearchQuery().toLowerCase();
    return this.availableWorkflows().filter(w =>
      w.name.toLowerCase().includes(query)
    );
  });

  private mapDataTypeToInputType(dataType: VariableDataType): string {
    switch (dataType) {
      case VariableDataType.NUMBER: return 'number';
      case VariableDataType.DATE: return 'date';
      case VariableDataType.BOOLEAN: return 'checkbox';
      default: return 'text';
    }
  }
  onDocumentClick(event: MouseEvent) {
    if (this.isWorkflowMenuOpen()) {
      const clickedInsideRegex = this.dropdownContainer?.nativeElement.contains(event.target);
      const clickedTrigger = this.menuTrigger?.nativeElement.contains(event.target);
      if (!clickedInsideRegex && !clickedTrigger) {
        this.isWorkflowMenuOpen.set(false);
      }
    }
  }
  displayedFields = computed(() => {
    const details = this.selectedWorkflowDetails();
    if (details?.variableResponse) {
      return details.variableResponse.map(v => ({
        label: v.label || v.variableName,
        variableName: v.variableName,
        type: this.mapDataTypeToInputType(v.dataType),
        placeholder: v.label || v.variableName,
        defaultValue: v.defaultValue,
        required: v.required,
        helpText: ''
      }));
    }

    // Fallback to the list data if details aren't loaded yet, though unlikely to have fields
    const listWorkflow = this.selectedWorkflow();
    return listWorkflow?.fields || [];
  });

  onWorkflowSelect(id: string) {
    this.activeModalWorkflowId.set(id);

    // Reset name for new workflow
    this.form.get('name')?.setValue('');

    this.loadWorkflowDetails(id);
  }

  buildForm(workflowId: string ) {
    const currentName = this.form.get('name')?.value || '';
    const group: any = {
    };

    group['workflowId'] = [workflowId]
    group['name'] = [currentName]


    for(const f of this.selectedWorkflowDetails()?.variableResponse ??[] ){
      group[f.variableName] = [f.defaultValue ?? ''];
    }
    this.form = this.fb.group(group);
  }

  selectedWorkflow = computed(() => {
    const id = this.activeModalWorkflowId();
    if (!id) return null;
    return this.availableWorkflows().find(w => w.id === id) || null;
  });


  closeModal() {
    this.activeModalWorkflowId.set(null);
  }


  submitWorkflowCreation() {
    console.log('submitWorkflowCreation called');
    const name = this.form.get('name')!.value;


    if(name === null || name === '') {
      alert("Contract Name required");
      return;
    }



    const request: ContractCreationRequest = {
      workflowId: this.activeModalWorkflowId()!,
      contractName: name,
      formData: this.form.value
    };


    this.apiService.createContract(request).subscribe(
      {
        next:(data)=>{
          if(data.success){

            alert("Contract Created");
            this.closeModal()

          }
        },
        error:(err)=>{
          console.log(err);
          alert(err.error.error.status + '\n' +err.error.error.message);
        }
      }
    );

  }

}

export default Dashboard
