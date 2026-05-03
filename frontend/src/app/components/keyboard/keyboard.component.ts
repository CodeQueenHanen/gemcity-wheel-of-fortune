import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

const ROWS = [
  ['Q','W','E','R','T','Y','U','I','O','P'],
  ['A','S','D','F','G','H','J','K','L'],
  ['Z','X','C','V','B','N','M']
];

const VOWELS = new Set(['A','E','I','O','U']);

@Component({
  selector: 'app-keyboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="keyboard">
      <div class="kb-row" *ngFor="let row of rows">
        <button
          *ngFor="let key of row"
          class="key"
          [class.used]="usedLetters.has(key)"
          [class.correct]="correctLetters.has(key)"
          [class.vowel]="isVowel(key)"
          [class.free-vowel]="freeVowelMode && isVowel(key) && !usedLetters.has(key)"
          [disabled]="!canGuess || usedLetters.has(key)"
          (click)="onKey(key)"
        >
          {{ key }}
        </button>
      </div>
    </div>
  `,
  styles: [`
    .keyboard {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 6px;
    }

    .kb-row {
      display: flex;
      gap: 5px;
    }

    .key {
      width: 42px;
      height: 46px;
      border-radius: 5px;
      border: 1px solid #2a2f45;
      background: #1a1d26;
      color: #e8ecff;
      font-family: 'Syne', sans-serif;
      font-size: 14px;
      font-weight: 700;
      cursor: pointer;
      transition: all 0.15s;
      letter-spacing: 0;
    }

    .key:hover:not(:disabled) {
      background: #22263a;
      border-color: #00e5ff;
      color: #00e5ff;
      box-shadow: 0 0 10px rgba(0,229,255,0.25);
      transform: translateY(-1px);
    }

    .key:active:not(:disabled) {
      transform: translateY(0);
    }

    .key.used {
      background: #0d0f14;
      border-color: #1a1d26;
      color: #2a2f45;
      cursor: not-allowed;
    }

    .key.correct {
      background: rgba(0,230,118,0.15);
      border-color: #00e676;
      color: #00e676;
    }

    .key.vowel {
      border-color: #3d2060;
    }

    .key.free-vowel {
      border-color: #ffd84d;
      color: #ffd84d;
      background: rgba(255,216,77,0.08);
      animation: glow-key 1s ease-in-out infinite;
    }

    @keyframes glow-key {
      0%, 100% { box-shadow: 0 0 8px rgba(255,216,77,0.3); }
      50%       { box-shadow: 0 0 16px rgba(255,216,77,0.6); }
    }
  `]
})
export class KeyboardComponent {
  @Input() usedLetters: Set<string> = new Set();
  @Input() correctLetters: Set<string> = new Set();
  @Input() canGuess = false;
  @Input() freeVowelMode = false;
  @Output() letterGuessed = new EventEmitter<string>();

  rows = ROWS;

  isVowel(letter: string): boolean {
    return VOWELS.has(letter);
  }

  onKey(letter: string): void {
    if (!this.canGuess || this.usedLetters.has(letter)) return;
    // In free vowel mode, only allow vowels
    if (this.freeVowelMode && !this.isVowel(letter)) return;
    this.letterGuessed.emit(letter);
  }
}
