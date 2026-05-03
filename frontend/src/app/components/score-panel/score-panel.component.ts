import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WheelSegment } from '../../models/game.models';

@Component({
  selector: 'app-score-panel',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="score-panel">
      <div class="score-block">
        <div class="label">SCORE</div>
        <div class="value primary">{{ score | number }}</div>
      </div>

      <div class="divider"></div>

      <div class="score-block" *ngIf="currentSegment">
        <div class="label">THIS SPIN</div>
        <div class="value"
          [class.bankrupt]="currentSegment.type === 'BANKRUPT'"
          [class.free]="currentSegment.type === 'FREE_VOWEL'"
          [class.points]="currentSegment.type === 'POINTS'"
        >
          <span *ngIf="currentSegment.type === 'POINTS'">{{ currentSegment.points }} pts</span>
          <span *ngIf="currentSegment.type === 'BANKRUPT'">BANKRUPT</span>
          <span *ngIf="currentSegment.type === 'FREE_VOWEL'">FREE VOWEL</span>
        </div>
      </div>

      <div class="divider"></div>

      <div class="score-block">
        <div class="label">ROUND</div>
        <div class="value accent">{{ puzzleNumber }}</div>
      </div>
    </div>
  `,
  styles: [`
    .score-panel {
      display: flex;
      align-items: center;
      gap: 0;
      background: #111318;
      border: 1px solid #2a2f45;
      border-radius: 8px;
      overflow: hidden;
    }

    .score-block {
      flex: 1;
      padding: 14px 20px;
      text-align: center;
    }

    .divider {
      width: 1px;
      height: 60px;
      background: #2a2f45;
    }

    .label {
      font-family: 'JetBrains Mono', monospace;
      font-size: 10px;
      font-weight: 700;
      letter-spacing: 2px;
      color: #4a5070;
      margin-bottom: 4px;
    }

    .value {
      font-family: 'Syne', sans-serif;
      font-size: 22px;
      font-weight: 800;
      color: #8890b0;
    }

    .value.primary {
      color: #e8ecff;
      font-size: 28px;
    }

    .value.accent {
      color: #b44fff;
    }

    .value.bankrupt {
      color: #ff4757;
      font-size: 16px;
      animation: shake 0.4s ease;
    }

    .value.free {
      color: #ffd84d;
      font-size: 14px;
    }

    .value.points {
      color: #00e676;
    }

    @keyframes shake {
      0%, 100% { transform: translateX(0); }
      25%       { transform: translateX(-4px); }
      75%       { transform: translateX(4px); }
    }
  `]
})
export class ScorePanelComponent {
  @Input() score = 0;
  @Input() currentSegment: WheelSegment | null = null;
  @Input() puzzleNumber = 1;
}
