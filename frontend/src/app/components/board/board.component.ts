import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Puzzle } from '../../models/game.models';

interface Tile {
  char: string;
  isLetter: boolean;      // needs to be guessed
  isRevealed: boolean;
  isPunct: boolean;       // braces, semicolons etc — always shown
  isSpace: boolean;
  justRevealed: boolean;  // for flash animation
}

const ALWAYS_VISIBLE = new Set(['{','}','(',')','[',']',';','.',',',':','<','>','"',"'",'+','-','*','/','!','=']);

@Component({
  selector: 'app-board',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="board-container">
      <div class="category-badge" [class.snippet]="puzzle?.category === 'SNIPPET'">
        <span class="cat-icon">{{ puzzle?.category === 'SNIPPET' ? '&lt;/&gt;' : '∑' }}</span>
        {{ puzzle?.category === 'SNIPPET' ? 'CODE SNIPPET' : 'CS CONCEPT' }}
      </div>

      <div class="hint" *ngIf="puzzle?.hint">
        <span class="hint-label">HINT</span> {{ puzzle?.hint }}
      </div>

      <div class="tiles-area">
        <div class="word-row" *ngFor="let row of tileRows">
          <div
            *ngFor="let tile of row"
            class="tile"
            [class.space]="tile.isSpace"
            [class.letter]="tile.isLetter && !tile.isSpace"
            [class.revealed]="tile.isRevealed"
            [class.punct]="tile.isPunct"
            [class.just-revealed]="tile.justRevealed"
          >
            <span *ngIf="!tile.isSpace && (tile.isRevealed || tile.isPunct)">{{ tile.char }}</span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .board-container {
      background: #111318;
      border: 1px solid #2a2f45;
      border-radius: 8px;
      padding: 24px;
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .category-badge {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      font-family: 'JetBrains Mono', monospace;
      font-size: 11px;
      font-weight: 700;
      letter-spacing: 2px;
      color: #00e5ff;
      background: rgba(0,229,255,0.08);
      border: 1px solid rgba(0,229,255,0.25);
      border-radius: 4px;
      padding: 4px 12px;
      width: fit-content;
    }

    .category-badge.snippet {
      color: #b44fff;
      background: rgba(180,79,255,0.08);
      border-color: rgba(180,79,255,0.25);
    }

    .cat-icon {
      font-size: 13px;
    }

    .hint {
      font-size: 13px;
      color: #8890b0;
      font-style: italic;
    }

    .hint-label {
      font-family: 'JetBrains Mono', monospace;
      font-size: 10px;
      font-weight: 700;
      letter-spacing: 2px;
      color: #ffd84d;
      font-style: normal;
      margin-right: 6px;
    }

    .tiles-area {
      display: flex;
      flex-direction: column;
      gap: 8px;
      align-items: center;
    }

    .word-row {
      display: flex;
      flex-wrap: wrap;
      gap: 4px;
      justify-content: center;
    }

    .tile {
      width: 36px;
      height: 42px;
      border-radius: 4px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-family: 'Syne', sans-serif;
      font-size: 18px;
      font-weight: 700;
      text-transform: uppercase;
      transition: all 0.3s;
    }

    .tile.space {
      width: 16px;
      background: transparent;
      border: none;
    }

    .tile.letter {
      background: #1a1d26;
      border: 1px solid #2a2f45;
      color: transparent;
    }

    .tile.letter.revealed {
      background: #22263a;
      border-color: #3d4468;
      color: #e8ecff;
    }

    .tile.punct {
      background: #111318;
      border: 1px solid #1a2040;
      color: #b44fff;
      font-family: 'JetBrains Mono', monospace;
      font-size: 16px;
    }

    .tile.just-revealed {
      animation: flip-in 0.4s ease;
      border-color: #00e5ff !important;
      box-shadow: 0 0 12px rgba(0,229,255,0.4);
    }

    @keyframes flip-in {
      0%   { transform: scaleY(0); background: rgba(0,229,255,0.2); }
      50%  { transform: scaleY(1.1); }
      100% { transform: scaleY(1); }
    }
  `]
})
export class BoardComponent implements OnChanges {
  @Input() puzzle: Puzzle | null = null;
  @Input() revealedChars: Set<string> = new Set();
  @Input() lastRevealedLetter = '';

  tileRows: Tile[][] = [];

  ngOnChanges(changes: SimpleChanges): void {
    this.buildTiles();
  }

  private buildTiles(): void {
    if (!this.puzzle) { this.tileRows = []; return; }

    const pattern = this.puzzle.visiblePattern;
    const allTiles: Tile[] = pattern.map(char => {
      const isPunct = ALWAYS_VISIBLE.has(char);
      const isSpace = char === ' ';
      const isLetter = !isSpace && !isPunct;
      const isRevealed = isLetter ? this.revealedChars.has(char.toUpperCase()) : false;

      return {
        char,
        isLetter,
        isRevealed,
        isPunct,
        isSpace,
        justRevealed: isRevealed && char.toUpperCase() === this.lastRevealedLetter
      };
    });

    // Break into rows of max 14 tiles (excluding spaces as word boundaries)
    this.tileRows = this.chunkIntoRows(allTiles, 14);
  }

  private chunkIntoRows(tiles: Tile[], maxPerRow: number): Tile[][] {
    const rows: Tile[][] = [];
    let current: Tile[] = [];
    let count = 0;

    for (const tile of tiles) {
      if (tile.isSpace && count >= maxPerRow) {
        rows.push(current);
        current = [];
        count = 0;
        continue;
      }
      current.push(tile);
      if (!tile.isSpace) count++;
    }

    if (current.length) rows.push(current);
    return rows;
  }
}
