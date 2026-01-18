import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
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
import ResizableImage from 'tiptap-extension-resize-image';
import BubbleMenu from '@tiptap/extension-bubble-menu';
import Highlight from '@tiptap/extension-highlight';
import { TextStyle } from '@tiptap/extension-text-style';
import FontFamily from '@tiptap/extension-font-family';
import { Color } from '@tiptap/extension-color';
import { FontSize } from './extensions/font-size.extension';
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
    imports: [CommonModule, FormsModule, TiptapEditorDirective],
    templateUrl: './workflow-editor.component.html',
    styleUrls: ['./workflow-editor.component.css']
})


export class WorkflowEditorComponent implements OnInit, OnDestroy {
    uploadedFile: File | null = null;
    workflowName: string = 'New Workflow';
    workflowDescription: string = '';
    workflowStatus: 'DRAFT' | 'PUBLISHED' = 'DRAFT';
    editor!: Editor;

    variables: WorkflowVariable[] = [];

    // Sidebar State
    activeSection: number | null = 0; // 0=Properties, 1=Clauses

    // Variable Editing State
    editingIndex: number | null = null;
    tempVariable: WorkflowVariable = { uiLabel: '', name: '', type: 'STRING' };

    breadcrumbs = [
        { label: 'Upload Document', key: 'home' as Section },
        { label: 'Editor', key: 'contracts' as Section },
        { label: 'Finalize', key: 'details' as Section }
    ];
    constructor(private location: Location, private http: HttpClient, private apiService: ApiService, private router: Router) { }

    current = 'home' as Section;


    isDisabled(key: Section): boolean {
        if (key === 'home') return false;
        return !this.uploadedFile;
    }

    selectTab(key: Section) {
        if (this.isDisabled(key)) return;
        this.current = key;
    }

    zoomLevel: number = 100;
    editMode: 'TAG' | 'EDIT' = 'EDIT';

    ngOnInit(): void {
        this.editor = new Editor({
            extensions: [
                StarterKit,
                Underline,
                Highlight,
                TextStyle,
                FontFamily,
                FontSize,
                Color,
                TextAlign.configure({
                    types: ['heading', 'paragraph'],
                }),
                Subscript,
                Superscript,
                Link.configure({
                    openOnClick: false,
                }),
                ResizableImage.configure({
                    inline: true,
                    allowBase64: true,
                }),
                BubbleMenu,
            ],
            content: '<p>Start editing your workflow documentation here...</p>',
            editorProps: {
                attributes: {
                    class: 'prosemirror-editor',
                },
                handleDOMEvents: {
                    contextmenu: (view, event) => {
                        return this.onContextMenu(event);
                    }
                }
            },
        });

        // Close context menu on global click
        document.addEventListener('click', () => {
            this.contextMenu.visible = false;
        });
    }

    // Context Menu State
    contextMenu = {
        visible: false,
        x: 0,
        y: 0,
        selectedText: ''
    };

    onContextMenu(event: MouseEvent): boolean {
        const selection = this.editor.state.selection;

        // Only show if there is text selected
        if (!selection.empty) {
            event.preventDefault();

            // Get selected text
            const { from, to } = selection;
            this.contextMenu.selectedText = this.editor.state.doc.textBetween(from, to);

            this.contextMenu.visible = true;
            this.contextMenu.x = event.clientX;
            this.contextMenu.y = event.clientY;
            return true;
        }

        this.contextMenu.visible = false;
        return false;
    }

    insertVariableFromContext(variable: WorkflowVariable) {
        if (variable.name) {
            // Wrap in code block for highlighting
            const placeholder = `<code>{{${variable.name}}}</code>`;
            this.editor.chain().focus().insertContent(placeholder).run();
        }
        this.contextMenu.visible = false;
    }

    // Create Variable Modal State
    createModalVisible: boolean = false;
    newVariableData: WorkflowVariable = { uiLabel: '', name: '', type: 'STRING' };

    createVariableFromSelection() {
        if (!this.contextMenu.selectedText) return;

        const text = this.contextMenu.selectedText;

        // Pre-fill data
        this.newVariableData = {
            uiLabel: text.length > 50 ? text.substring(0, 50) : text,
            name: this.sanitizeName(text),
            type: 'STRING'
        };

        // Show Modal
        this.createModalVisible = true;
        this.contextMenu.visible = false;
    }

    closeCreateVariableModal() {
        this.createModalVisible = false;
    }

    confirmCreateVariable() {
        if (!this.newVariableData.uiLabel || !this.newVariableData.name) {
            alert('Label and Name are required');
            return;
        }

        const newVar: WorkflowVariable = { ...this.newVariableData };

        // Add to list
        this.variables.unshift(newVar);

        // Open attributes sidebar if not open
        this.activeSection = 0;

        // Replace text in editor with variable placeholder wrapped in code tag
        this.editor.chain().focus().insertContent(`<code>{{${newVar.name}}}</code>`).run();

        this.createModalVisible = false;

        // Immediately open "Edit" mode in sidebar is redundant since we just filled the modal.
        // Just ensure the list is refreshed or visible.
        this.activeSection = 0;
    }

    onNewVarLabelChange() {
        if (this.newVariableData.uiLabel) {
            this.newVariableData.name = this.sanitizeName(this.newVariableData.uiLabel);
        }
    }

    onNewVarNameInput() {
        this.newVariableData.name = this.sanitizeName(this.newVariableData.name);
    }

    // Font & Color Actions
    onFontFamilyChange(event: Event) {
        const target = event.target as HTMLSelectElement;
        if (target) {
            this.editor.chain().focus().setFontFamily(target.value).run();
        }
    }

    onFontSizeChange(event: Event) {
        const target = event.target as HTMLSelectElement;
        if (target) {
            this.editor.chain().focus().setFontSize(target.value).run();
        }
    }

    onColorChange(event: Event) {
        const target = event.target as HTMLInputElement;
        if (target) {
            this.editor.chain().focus().setColor(target.value).run();
        }
    }

    unsetColor() {
        this.editor.chain().focus().unsetColor().run();
    }

    // Toolbar Actions
    setMode(mode: 'TAG' | 'EDIT') {
        this.editMode = mode;
        this.editor.setEditable(mode === 'EDIT');
    }

    addLink() {
        const previousUrl = this.editor.getAttributes('link')['href'];
        const url = window.prompt('URL', previousUrl);

        // cancelled
        if (url === null) {
            return;
        }

        // empty
        if (url === '') {
            this.editor.chain().focus().extendMarkRange('link').unsetLink().run();
            return;
        }

        // update link
        this.editor.chain().focus().extendMarkRange('link').setLink({ href: url }).run();
    }

    adjustZoom(amount: number) {
        const newZoom = this.zoomLevel + amount;
        if (newZoom >= 50 && newZoom <= 200) {
            this.zoomLevel = newZoom;
        }
    }

    // Placeholder for Indent - requires Tiptap Indent extension usually, 
    // but we can map to sinkListItem for lists for now.
    indent() {
        if (this.editor.can().sinkListItem('listItem')) {
            this.editor.chain().focus().sinkListItem('listItem').run();
        }
    }

    outdent() {
        if (this.editor.can().liftListItem('listItem')) {
            this.editor.chain().focus().liftListItem('listItem').run();
        }
    }

    onImageSelected(event: any) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e: any) => {
                this.editor.chain().focus().setImage({ src: e.target.result }).run();
            };
            reader.readAsDataURL(file);
        }
        // Reset input value to allow selecting same file again
        event.target.value = '';
    }

    ngOnDestroy(): void {
        this.editor.destroy();
    }

    goBack() {
        this.location.back();
    }

    save() {
        // 1. Log metadata
        this.workflowStatus = 'DRAFT';
        const workflowData = {
            name: this.workflowName,
            description: this.workflowDescription,
            currentStatus: this.workflowStatus,
            variables: this.variables.map(v => ({
                variableName: v.name,
                dataType: v.type.toUpperCase(),
                label: v.uiLabel,
                required: false,
                defaultValue: '',
                sortValue: 0
            }))
        };
        console.log('Saving Draft metadata:', workflowData);

        // 2. Convert and Download DOCX LOCAL (Save Draft basically)
        if (!this.editor) return;

        const htmlContent = this.editor.getHTML();
        const fullHtml = `<html><body>${htmlContent}</body></html>`;

        this.apiService.convertDocument(fullHtml).subscribe({
            next: (blob: Blob) => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `${this.workflowName.replace(/\s+/g, '_')}_draft.docx`;
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
                console.log('DOCX Draft Downloaded');
            },
            error: (err) => {
                console.error('Failed to convert document', err);
                alert('Failed to save draft. Check console.');
            }
        });
    }

    publish() {
        console.log('Publishing Workflow...');

        // 1. Construct Metadata
        this.workflowStatus = 'PUBLISHED';
        const workflowData = {
            name: this.workflowName,
            description: this.workflowDescription,
            currentStatus: this.workflowStatus,
            variables: this.variables.map(v => ({
                variableName: v.name,
                dataType: v.type.toUpperCase(),
                label: v.uiLabel,
                required: false,
                defaultValue: '',
                sortValue: 0
            }))
        };

        console.log('Publish Payload:', workflowData);

        // 2. Generate DOCX from Editor HTML for submission
        if (!this.editor) return;

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
                        this.router.navigate(['/workflow-designer']);
                    },
                    error: (err) => {
                        console.error('Failed to publish workflow', err);
                        alert('Failed to publish. Check console for details.');
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
            this.uploadedFile = file;
            this.uploadFile(file);
        }
    }

    uploadFile(file: File) {
        const formData = new FormData();
        formData.append('file', file);

        this.apiService.uploadDocument(formData).subscribe({
            next: (response: any) => {
                if (response.html) {
                    this.editor.commands.setContent(response.html);
                }
            },
            error: (error: any) => {
                console.error('Upload failed', error);
                alert('Failed to upload document.');
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
            uiLabel: 'New Variable',
            name: 'new_variable',
            type: 'STRING'
        };
        this.variables.unshift(newVar);
        this.activeSection = 0; // Ensure properties section is open
        this.startEdit(0);
    }

    toggleSection(index: number) {
        if (this.activeSection === index) {
            this.activeSection = null;
        } else {
            this.activeSection = index;
        }
    }

    startEdit(index: number, event?: Event) {
        if (event) event.stopPropagation();
        this.editingIndex = index;
        this.tempVariable = { ...this.variables[index] };
    }

    onVariableDragStart(variable: WorkflowVariable, event: DragEvent) {
        if (variable.name && event.dataTransfer) {
            const placeholder = `<code>{{${variable.name}}}</code>`;
            event.dataTransfer.setData('text/html', placeholder);
            event.dataTransfer.setData('text/plain', `{{${variable.name}}}`); // Fallback
            event.dataTransfer.effectAllowed = 'copy';
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
        }
    }

    deleteVariable(index: number) { // Removed event arg to simplify call from modal
        this.variables.splice(index, 1);
        this.editingIndex = null;
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
            .replace(/[^a-z0-9_]/g, '') // stricter sanitization
            .replace(/^_+|_+$/g, '');
    }
}
