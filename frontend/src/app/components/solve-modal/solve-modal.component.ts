import { Component, Input, Output, EventEmitter, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-solve-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="overlay" *ngIf="visible" (click)="onOverlayClick($event)">
      <div class="modal">
        <div class="modal-header">
          <span class="modal-icon">&lt;?&gt;</span>
          <h2>SOLVE THE PUZZLE</h2>
        </div>

        <p class="modal-hint">Type your full answer below</p>

        <input
          #solveInput
          class="solve-input"
          [(ngModel)]="attempt"
          (keydown.enter)="onSubmit()"
          placeholder="Your answer…"
          autocomplete="off"
          autocorrect="off"
          autocapitalize="characters"
          spellcheck="false"
        />

        <div class="modal-actions">
          <button class="btn-cancel" (click)="onCancel()">CANCEL</button>
          <button class="btn-submit" [disabled]="!attempt.trim()" (click)="onSubmit()">SUBMIT</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .overlay {
      position: fixed;
      inset: 0;
      background: rgba(0,0,0,0.75);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 100;
      backdrop-filter: blur(4px);
    }

    .modal {
      background: #111318;
      border: 1px solid #3d4468;
      border-radius: 10px;
      padding: 32px;
      width: 480px;
      max-width: 90vw;
      display: flex;
      flex-direction: column;
      gap: 16px;
      box-shadow: 0 0 60px rgba(0,229,255,0.15);
    }

    .modal-header {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .modal-icon {
      font-family: 'JetBrains Mono', monospace;
      font-size: 22px;
      color: #00e5ff;
    }

    h2 {
      font-family: 'Syne', sans-serif;
      font-size: 20px;
      font-weight: 800;
      letter-spacing: 3px;
      color: #e8ecff;
    }

    .modal-hint {
      font-size: 13px;
      color: #4a5070;
    }

    .solve-input {
      width: 100%;
      padding: 14px 16px;
      background: #0a0b0f;
      border: 1px solid #3d4468;
      border-radius: 6px;
      color: #e8ecff;
      font-family: 'JetBrains Mono', monospace;
      font-size: 18px;
      font-weight: 500;
      text-transform: uppercase;
      outline: none;
      transition: border-color 0.2s;
    }

    .solve-input:focus {
      border-color: #00e5ff;
      box-shadow: 0 0 12px rgba(0,229,255,0.2);
    }

    .modal-actions {
      display: flex;
      gap: 12px;
      justify-content: flex-end;
      margin-top: 4px;
    }

    button {
      font-family: 'Syne', sans-serif;
      font-size: 13px;
      font-weight: 700;
      letter-spacing: 2px;
      padding: 10px 24px;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.15s;
    }

    .btn-cancel {
      background: transparent;
      border: 1px solid #2a2f45;
      color: #4a5070;
    }

    .btn-cancel:hover {
      border-color: #4a5070;
      color: #8890b0;
    }

    .btn-submit {
      background: rgba(0,229,255,0.1);
      border: 1px solid #00e5ff;
      color: #00e5ff;
    }

    .btn-submit:hover:not(:disabled) {
      background: rgba(0,229,255,0.2);
      box-shadow: 0 0 16px rgba(0,229,255,0.3);
    }

    .btn-submit:disabled {
      opacity: 0.3;
      cursor: not-allowed;
    }
  `]
})
export class SolveModalComponent {
  @Input() visible = false;
  @Output() submitted = new EventEmitter<string>();
  @Output() cancelled = new EventEmitter<void>();
  @ViewChild('solveInput') inputEl!: ElementRef<HTMLInputElement>;

  attempt = '';

  onSubmit(): void {
    if (!this.attempt.trim()) return;
    this.submitted.emit(this.attempt.trim());
    this.attempt = '';
  }

  onCancel(): void {
    this.attempt = '';
    this.cancelled.emit();
  }

  onOverlayClick(e: MouseEvent): void {
    if ((e.target as HTMLElement).classList.contains('overlay')) {
      this.onCancel();
    }
  }
}
