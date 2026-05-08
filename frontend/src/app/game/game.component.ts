import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GameService } from '../services/game.service';
import { Puzzle, WheelSegment, GamePhase } from '../models/game.models';
import { WheelComponent } from '../components/wheel/wheel.component';
import { BoardComponent } from '../components/board/board.component';
import { KeyboardComponent } from '../components/keyboard/keyboard.component';
import { ScorePanelComponent } from '../components/score-panel/score-panel.component';
import { ToastComponent, ToastType } from '../components/toast/toast.component';
import { SolveModalComponent } from '../components/solve-modal/solve-modal.component';

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [
    CommonModule,
    WheelComponent,
    BoardComponent,
    KeyboardComponent,
    ScorePanelComponent,
    ToastComponent,
    SolveModalComponent
  ],
  template: `
    <div class="game-layout">

      <!-- Header -->
      <header class="game-header">
        <div class="logo">
          <span class="logo-gem">◆</span>
          <span class="logo-text">GemCity <span class="logo-accent">TECH</span></span>
          <span class="logo-sub">Wheel of Fortune: Coder Edition</span>
        </div>
        <button class="new-puzzle-btn" (click)="loadNewPuzzle()">
          NEW PUZZLE
        </button>
      </header>

      <!-- Score bar -->
      <app-score-panel
        [score]="score"
        [currentSegment]="currentSegment"
        [puzzleNumber]="puzzleNumber"
      />

      <!-- Toast feedback -->
      <app-toast [message]="toastMessage" [type]="toastType" />

      <!-- Main game area -->
      <div class="game-body">

        <!-- Left: wheel -->
        <div class="wheel-section">
          <div class="section-label">SPIN THE WHEEL</div>
          <app-wheel
            [segments]="wheelSegments"
            [disabled]="phase !== 'SPIN' || loading"
            (spinComplete)="onSpinComplete($event)"
          />
        </div>

        <!-- Right: board + controls -->
        <div class="board-section">

          <app-board
            [puzzle]="puzzle"
            [currentPattern]="currentPattern"
            [lastRevealedLetter]="lastRevealedLetter"
          />

          <div class="action-row" *ngIf="phase !== 'SOLVED'">
            <button
              class="action-btn solve-btn"
              [disabled]="!puzzle || loading || phase === 'SPIN'"
              (click)="openSolveModal()"
            >
              SOLVE PUZZLE
            </button>
          </div>

          <app-keyboard
            [usedLetters]="usedLetters"
            [correctLetters]="correctLetters"
            [canGuess]="phase === 'GUESS' && !loading"
            [freeVowelMode]="freeVowelMode"
            (letterGuessed)="onLetterGuessed($event)"
          />

          <!-- Solved overlay -->
          <div class="solved-banner" *ngIf="phase === 'SOLVED'">
            <div class="solved-title">PUZZLE SOLVED!</div>
            <div class="solved-score">+{{ lastPointsEarned }} pts</div>
            <button class="action-btn" (click)="loadNewPuzzle()">NEXT PUZZLE →</button>
          </div>

        </div>
      </div>
    </div>

    <!-- Solve modal -->
    <app-solve-modal
      [visible]="showSolveModal"
      (submitted)="onSolveSubmitted($event)"
      (cancelled)="showSolveModal = false"
    />

    <!-- Loading overlay -->
    <div class="loading-overlay" *ngIf="loading && !puzzle">
      <div class="loading-text">LOADING<span class="dots">...</span></div>
    </div>
  `,
  styles: [`
    .game-layout {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      gap: 16px;
      padding: 20px 24px 32px;
      max-width: 1100px;
      margin: 0 auto;
    }

    /* ── Header ── */
    .game-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding-bottom: 16px;
      border-bottom: 1px solid #2a2f45;
    }

    .logo {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .logo-gem {
      color: #00e5ff;
      font-size: 22px;
      filter: drop-shadow(0 0 8px #00e5ff);
    }

    .logo-text {
      font-family: 'Syne', sans-serif;
      font-size: 22px;
      font-weight: 800;
      color: #e8ecff;
      letter-spacing: 1px;
    }

    .logo-accent {
      color: #00e5ff;
    }

    .logo-sub {
      font-size: 12px;
      color: #4a5070;
      font-family: 'JetBrains Mono', monospace;
      border-left: 1px solid #2a2f45;
      padding-left: 10px;
      margin-left: 2px;
    }

    .new-puzzle-btn {
      font-family: 'JetBrains Mono', monospace;
      font-size: 11px;
      font-weight: 700;
      letter-spacing: 2px;
      padding: 8px 16px;
      background: transparent;
      border: 1px solid #2a2f45;
      color: #4a5070;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.2s;
    }

    .new-puzzle-btn:hover {
      border-color: #b44fff;
      color: #b44fff;
    }

    /* ── Body layout ── */
    .game-body {
      display: grid;
      grid-template-columns: 320px 1fr;
      gap: 28px;
      align-items: start;
      flex: 1;
    }

    .wheel-section {
      display: flex;
      flex-direction: column;
      gap: 16px;
      align-items: center;
      position: sticky;
      top: 20px;
    }

    .board-section {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .section-label {
      font-family: 'JetBrains Mono', monospace;
      font-size: 10px;
      font-weight: 700;
      letter-spacing: 3px;
      color: #2a2f45;
    }

    /* ── Action row ── */
    .action-row {
      display: flex;
      justify-content: flex-end;
    }

    .action-btn {
      font-family: 'Syne', sans-serif;
      font-size: 12px;
      font-weight: 700;
      letter-spacing: 2px;
      padding: 10px 20px;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.15s;
      background: transparent;
      border: 1px solid #3d4468;
      color: #8890b0;
    }

    .action-btn:hover:not(:disabled) {
      border-color: #ffd84d;
      color: #ffd84d;
    }

    .action-btn:disabled {
      opacity: 0.3;
      cursor: not-allowed;
    }

    /* ── Solved banner ── */
    .solved-banner {
      background: rgba(255,216,77,0.08);
      border: 1px solid rgba(255,216,77,0.4);
      border-radius: 8px;
      padding: 24px;
      display: flex;
      align-items: center;
      gap: 20px;
      animation: slide-in 0.4s ease;
    }

    .solved-title {
      font-family: 'Syne', sans-serif;
      font-size: 22px;
      font-weight: 800;
      color: #ffd84d;
      letter-spacing: 2px;
    }

    .solved-score {
      font-family: 'JetBrains Mono', monospace;
      font-size: 18px;
      color: #00e676;
      flex: 1;
    }

    @keyframes slide-in {
      from { opacity: 0; transform: translateY(12px); }
      to   { opacity: 1; transform: translateY(0); }
    }

    /* ── Loading ── */
    .loading-overlay {
      position: fixed;
      inset: 0;
      background: #0a0b0f;
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 200;
    }

    .loading-text {
      font-family: 'JetBrains Mono', monospace;
      font-size: 24px;
      color: #00e5ff;
      letter-spacing: 4px;
    }

    .dots {
      animation: blink 1.2s step-end infinite;
    }

    @keyframes blink {
      0%,100% { opacity: 1; }
      33%      { opacity: 0.3; }
      66%      { opacity: 0.6; }
    }

    /* ── Responsive ── */
    @media (max-width: 768px) {
      .game-body {
        grid-template-columns: 1fr;
      }
      .wheel-section {
        position: static;
      }
      .logo-sub { display: none; }
    }
  `]
})
export class GameComponent implements OnInit {
  // State
  puzzle: Puzzle | null = null;
  score = 0;
  puzzleNumber = 1;
  phase: GamePhase = 'SPIN';
  loading = false;

  currentSegment: WheelSegment | null = null;
  freeVowelMode = false;

  currentPattern: string[] = [];
  usedLetters = new Set<string>();
  correctLetters = new Set<string>();
  lastRevealedLetter = '';
  lastPointsEarned = 0;

  // UI
  toastMessage = '';
  toastType: ToastType = 'info';
  showSolveModal = false;
  wheelSegments: WheelSegment[] = [];

  constructor(private gameService: GameService) {}

  ngOnInit(): void {
    this.wheelSegments = this.gameService.getWheelSegments();
    this.loadNewPuzzle();
  }

  loadNewPuzzle(): void {
    this.loading = true;
    this.currentPattern = [];
    this.usedLetters = new Set();
    this.correctLetters = new Set();
    this.currentSegment = null;
    this.lastRevealedLetter = '';
    this.freeVowelMode = false;
    this.phase = 'SPIN';
    this.toastMessage = '';

    this.gameService.getRandomPuzzle().subscribe({
      next: puzzle => {
        this.puzzle = puzzle;
        this.loading = false;
        this.showToast('New puzzle loaded — spin to start!', 'info');
      },
      error: () => {
        this.loading = false;
        this.showToast('Failed to load puzzle. Check the backend.', 'error');
      }
    });
  }

  onSpinComplete(event: { segment: WheelSegment; index: number }): void {
    this.currentSegment = event.segment;

    if (event.segment.type === 'BANKRUPT') {
      const lost = this.score;
      this.score = 0;
      this.phase = 'SPIN';
      this.showToast(`BANKRUPT! Lost ${lost} points.`, 'bankrupt');
      return;
    }

    if (event.segment.type === 'FREE_VOWEL') {
      this.freeVowelMode = true;
      this.phase = 'GUESS';
      this.showToast('Free vowel! Pick a vowel to reveal.', 'info');
      return;
    }

    this.freeVowelMode = false;
    this.phase = 'GUESS';
    this.showToast(`Landed on ${event.segment.points} pts — pick a letter!`, 'info');
  }

  onLetterGuessed(letter: string): void {
    if (!this.puzzle || this.phase !== 'GUESS') return;

    this.usedLetters = new Set([...this.usedLetters, letter]);
    this.loading = true;

    const pointsPerHit = this.freeVowelMode ? 0 : (this.currentSegment?.points ?? 0);

    this.gameService.guessLetter(this.puzzle.id, letter, pointsPerHit, this.correctLetters).subscribe({
      next: result => {
        this.loading = false;
        this.lastRevealedLetter = result.correct ? letter : '';

        if (result.correct) {
          this.correctLetters = new Set([...this.correctLetters, letter]);
          this.currentPattern = result.updatedPattern;
          this.score += result.pointsEarned;
          this.lastPointsEarned = result.pointsEarned;

          if (result.puzzleSolved) {
            this.phase = 'SOLVED';
            this.puzzleNumber++;
            this.showToast('PUZZLE SOLVED! 🏆', 'win');
          } else {
            const bonus = this.freeVowelMode ? ' (free)' : ` +${result.pointsEarned} pts`;
            this.showToast(`"${letter}" — ${result.matchCount} match${result.matchCount !== 1 ? 'es' : ''}${bonus}`, 'success');
            this.freeVowelMode = false;
            this.phase = 'SPIN';
          }
        } else {
          this.showToast(`"${letter}" — not in the puzzle`, 'error');
          this.freeVowelMode = false;
          this.phase = 'SPIN';
        }
      },
      error: () => {
        this.loading = false;
        this.showToast('Network error — try again', 'error');
      }
    });
  }

  openSolveModal(): void {
    this.showSolveModal = true;
  }

  onSolveSubmitted(attempt: string): void {
    if (!this.puzzle) return;
    this.showSolveModal = false;
    this.loading = true;

    this.gameService.solvePuzzle(this.puzzle.id, attempt).subscribe({
      next: result => {
        this.loading = false;
        if (result.correct) {
          this.score += result.pointsEarned;
          this.lastPointsEarned = result.pointsEarned;
          this.currentPattern = result.updatedPattern;
          this.phase = 'SOLVED';
          this.puzzleNumber++;
          this.showToast('CORRECT! Puzzle solved! 🏆', 'win');
        } else {
          this.showToast('Incorrect — keep guessing!', 'error');
          this.phase = 'SPIN';
        }
      },
      error: () => {
        this.loading = false;
        this.showToast('Network error — try again', 'error');
      }
    });
  }

  private showToast(message: string, type: ToastType): void {
    this.toastMessage = '';
    setTimeout(() => {
      this.toastMessage = message;
      this.toastType = type;
    }, 10);
  }
}