import { Directive, EventEmitter, HostListener, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Directive({
    selector: '[appDebounceInput]',
    standalone: true
})
export class DebounceInputDirective implements OnInit, OnDestroy {
    @Input() debounceTime = 300;
    @Output() debouncedInput = new EventEmitter<string>();

    private subject = new Subject<string>();
    private subscription: Subscription | undefined;

    ngOnInit() {
        this.subscription = this.subject.pipe(
            debounceTime(this.debounceTime),
            distinctUntilChanged()
        ).subscribe(value => {
            this.debouncedInput.emit(value);
        });
    }

    ngOnDestroy() {
        this.subscription?.unsubscribe();
    }

    @HostListener('input', ['$event'])
    onInput(event: Event) {
        const target = event.target as HTMLInputElement;
        this.subject.next(target.value);
    }
}
