import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { TiptapEditorDirective, TiptapBubbleMenuDirective } from 'ngx-tiptap';
import { Editor } from '@tiptap/core';
import StarterKit from '@tiptap/starter-kit';
import Underline from '@tiptap/extension-underline';
import TextAlign from '@tiptap/extension-text-align';
import Subscript from '@tiptap/extension-subscript';
import Superscript from '@tiptap/extension-superscript';
import Link from '@tiptap/extension-link';
import Image from '@tiptap/extension-image';
import BubbleMenu from '@tiptap/extension-bubble-menu';
import { ApiService } from '../../services/api.service';

type Section = 'home' | 'contracts' | 'details';

interface WorkflowVariable {
    uiLabel: string;
    name: string;
    type: 'STRING' | 'INTEGER';
}

@Component({
    selector: 'app-workflow-editor',
    standalone: true,
    imports: [CommonModule, FormsModule, TiptapEditorDirective, TiptapBubbleMenuDirective],
    templateUrl: './workflow-editor.component.html',
    styleUrls: ['./workflow-editor.component.css']
})


export class WorkflowEditorComponent implements OnInit, OnDestroy {
    uploadedFile: File | null = null;
    workflowName: string = 'New Workflow';
    workflowDescription: string = '';
    editor!: Editor;

    variables: WorkflowVariable[] = [];
    expandedIndex: number | null = null;
    editingIndex: number | null = null;
    tempVariable: WorkflowVariable = { uiLabel: '', name: '', type: 'STRING' };
    breadcrumbs = [
        { label: 'Upload Document', key: 'home' as Section },
        { label: 'Editor', key: 'contracts' as Section },
        { label: 'Finalize', key: 'details' as Section }
    ];
    constructor(private location: Location, private http: HttpClient, private apiService: ApiService) { }

    current = 'home' as Section;


    isDisabled(key: Section): boolean {
        if (key === 'home') return false;
        return !this.uploadedFile;
    }

    selectTab(key: Section) {
        if (this.isDisabled(key)) return;
        this.current = key;
    }

    ngOnInit(): void {
        this.editor = new Editor({
            extensions: [
                StarterKit,
                TextAlign.configure({
                    types: ['heading', 'paragraph'],
                }),
                Subscript,
                Superscript,
                Image.configure({
                    inline: true,
                    allowBase64: true,
                }),
                BubbleMenu,
            ],
            content: '<p>Start editing your workflow documentation here...</p>',
        });
    }

    addImage() {
        const url = window.prompt('URL');
        if (url) {
            this.editor.chain().focus().setImage({ src: url }).run();
        }
    }

    ngOnDestroy(): void {
        this.editor.destroy();
    }

    goBack() {
        this.location.back();
    }

    save() {
        // 1. Log metadata as before
        const workflowData = {
            name: this.workflowName,
            description: this.workflowDescription,
            currentStatus: 'DRAFT',
            variables: this.variables.map(v => ({
                variableName: v.name,
                dataType: v.type.toUpperCase(),
                label: v.uiLabel,
                required: false,
                defaultValue: '',
                sortValue: 0
            }))
        };
        console.log('Workflow Creation Request JSON:', JSON.stringify(workflowData, null, 2));

        // 2. Convert and Download DOCX LOCAL (Save Draft basically)
        const htmlContent = this.editor.getHTML();
        const fullHtml = `<html><body>${htmlContent}</body></html>`;

        this.apiService.convertDocument(fullHtml).subscribe({
            next: (blob: Blob) => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `${this.workflowName.replace(/\s+/g, '_')}.docx`;
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
                console.log('DOCX Downloaded');
            },
            error: (err) => console.error('Failed to convert document', err)
        });
    }

    publish() {
        console.log('Publishing Workflow...');

        // 1. Construct Metadata
        const workflowData = {
            name: this.workflowName,
            description: this.workflowDescription,
            currentStatus: 'PUBLISHED', // Or whatever backend expects
            variables: this.variables.map(v => ({
                variableName: v.name,
                dataType: v.type.toUpperCase(),
                label: v.uiLabel,
                required: false,
                defaultValue: '',
                sortValue: 0
            }))
        };

        // 2. Generate DOCX from Editor HTML for submission
        const htmlContent = this.editor.getHTML();
        const fullHtml = `<html><body>${htmlContent}</body></html>`;

        this.apiService.convertDocument(fullHtml).subscribe({
            next: (docxBlob: Blob) => {
                // 3. Prepare FormData
                const formData = new FormData();
                formData.append('file', docxBlob, `${this.workflowName.replace(/\s+/g, '_')}.docx`);

                // Append 'data' as JSON Blob
                const jsonBlob = new Blob([JSON.stringify(workflowData)], { type: 'application/json' });
                formData.append('data', jsonBlob);

                // 4. Send to Backend
                this.apiService.createWorkflow(formData).subscribe({
                    next: (res) => {
                        console.log('Workflow Published Successfully', res);
                        alert('Workflow Published Successfully!');
                    },
                    error: (err) => {
                        console.error('Failed to publish workflow', err);
                        alert('Failed to publish workflow. Check console for details.');
                    }
                });
            },
            error: (err) => {
                console.error('Failed to parse HTML to DOCX for publishing', err);
                alert('Conversion failed. Cannot publish.');
            }
        });
    }

    onFileSelected(event: any) {
        const file = event.target.files[0];
        if (file) {
            if (file.name.endsWith('.docx') || file.type === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document') {
                this.uploadedFile = file;
                console.log('File selected:', file.name);
                this.uploadFile(file);
                // Automatically move to editor? Maybe let user decide in new UI. 
                // For now, staying on home to show metadata as requested.
            } else {
                alert('Please upload a valid .docx file');
            }
        }
    }

    uploadFile(file: File) {
        const formData = new FormData();
        formData.append('file', file);

        this.http.post<any>('/api/documents/upload', formData).subscribe({
            next: (response: any) => {
                if (response.html) {
                    console.log('Received HTML length:', response.html.length);
                    console.log('First 500 chars:', response.html.substring(0, 500));
                    this.editor.commands.setContent(response.html);
                }
            },
            error: (error: any) => {
                console.error('Upload failed', error);
                alert('Failed to upload and parse document.');
            }
        });
    }

    // Variable Management
    addPageBreak() {
        if (this.editor) {
            this.editor.chain().focus().insertContent('<div class="page-break" data-page-break="true"></div>').run();
        }
    }

    addVariable() {
        const newVar: WorkflowVariable = {
            uiLabel: '',
            name: '',
            type: 'STRING'
        };
        this.variables.unshift(newVar);
        this.expandedIndex = 0;
        this.startEdit(0); // Immediately enter edit mode
    }

    toggleExpand(index: number) {
        if (this.expandedIndex === index) {
            this.expandedIndex = null;
        } else {
            this.expandedIndex = index;
            this.cancelEdit(); // Cancel any pending edits when switching cards
        }
    }

    startEdit(index: number, event?: Event) {
        if (event) event.stopPropagation();
        this.editingIndex = index;
        this.tempVariable = { ...this.variables[index] };
    }

    insertVariable(variable: WorkflowVariable, event: Event) {
        event.stopPropagation();
        if (variable.name) {
            const placeholder = `{{${variable.name}}}`;
            this.editor.chain().focus().insertContent(placeholder).run();
        }
    }

    cancelEdit() {
        this.editingIndex = null;
    }

    saveEdit() {
        if (this.editingIndex !== null) {
            if (!this.tempVariable.uiLabel || !this.tempVariable.name) {
                alert('Label and Name are required');
                return;
            }
            this.variables[this.editingIndex] = { ...this.tempVariable };
            this.editingIndex = null;
            this.expandedIndex = null; // Auto-collapse on save
        }
    }

    deleteVariable(index: number, event: Event) {
        event.stopPropagation();
        this.variables.splice(index, 1);
        if (this.expandedIndex === index) {
            this.expandedIndex = null;
        } else if (this.expandedIndex !== null && this.expandedIndex > index) {
            this.expandedIndex--;
        }
        if (this.editingIndex === index) {
            this.editingIndex = null;
        } else if (this.editingIndex !== null && this.editingIndex > index) {
            this.editingIndex--;
        }
    }

    onLabelChange() {
        if (this.tempVariable.uiLabel) {
            this.tempVariable.name = this.sanitizeName(this.tempVariable.uiLabel);
        }
    }

    onNameInput() {
        this.tempVariable.name = this.sanitizeName(this.tempVariable.name);
    }

    private sanitizeName(input: string): string {
        return input.toLowerCase()
            .replace(/[^a-z0-9_]/g, '_')
            .replace(/_+/g, '_')
            .replace(/^_+|_+$/g, '');
    }
}

