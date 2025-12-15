import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { TiptapEditorDirective } from 'ngx-tiptap';
import { Editor } from '@tiptap/core';
import StarterKit from '@tiptap/starter-kit';
import Underline from '@tiptap/extension-underline';
import TextAlign from '@tiptap/extension-text-align';
import Subscript from '@tiptap/extension-subscript';
import Superscript from '@tiptap/extension-superscript';
import Link from '@tiptap/extension-link';
import Image from '@tiptap/extension-image';



interface WorkflowVariable {
    uiLabel: string;
    name: string;
    type: 'string' | 'int';
}


@Component({
    selector: 'app-workflow-editor',
    standalone: true,
    imports: [CommonModule, FormsModule, TiptapEditorDirective],
    templateUrl: './workflow-editor.component.html',
    styleUrls: ['./workflow-editor.component.css']
})


export class WorkflowEditorComponent implements OnInit, OnDestroy {
    uploadedFile: File | null = null;
    workflowName: string = 'New Workflow';
    editor!: Editor;

    variables: WorkflowVariable[] = [];
    expandedIndex: number | null = null;
    editingIndex: number | null = null;
    tempVariable: WorkflowVariable = { uiLabel: '', name: '', type: 'string' };

    constructor(private location: Location, private http: HttpClient) { }

    ngOnInit(): void {
        this.editor = new Editor({
            extensions: [
                StarterKit,
                TextAlign.configure({
                    types: ['heading', 'paragraph'],
                }),
                Subscript,
                Superscript,
                Image,
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
        console.log('Worklow saved', { name: this.workflowName, variables: this.variables });
        // Implement save logic
    }

    publish() {
        console.log('Workflow published');
        // Implement publish logic
    }

    onFileSelected(event: any) {
        const file = event.target.files[0];
        if (file) {
            if (file.name.endsWith('.docx') || file.type === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document') {
                this.uploadedFile = file;
                console.log('File selected:', file.name);
                this.uploadFile(file);
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
    addVariable() {
        const newVar: WorkflowVariable = {
            uiLabel: '',
            name: '',
            type: 'string'
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

