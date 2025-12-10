import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TiptapEditorDirective } from 'ngx-tiptap';
import { Editor } from '@tiptap/core';
import StarterKit from '@tiptap/starter-kit';
import Underline from '@tiptap/extension-underline';
import TextAlign from '@tiptap/extension-text-align';
import Subscript from '@tiptap/extension-subscript';
import Superscript from '@tiptap/extension-superscript';
import Link from '@tiptap/extension-link';
import Image from '@tiptap/extension-image';

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

    constructor(private location: Location) { }

    ngOnInit(): void {
        this.editor = new Editor({
            extensions: [
                StarterKit,
                Underline,
                TextAlign.configure({
                    types: ['heading', 'paragraph'],
                }),
                Subscript,
                Superscript,
                Link,
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
        console.log('Worklow saved');
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
            } else {
                alert('Please upload a valid .docx file');
            }
        }
    }
}
