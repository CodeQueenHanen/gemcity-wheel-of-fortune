import {
  Component, Input, Output, EventEmitter,
  OnChanges, SimpleChanges, ElementRef, ViewChild
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { WheelSegment } from '../../models/game.models';

@Component({
  selector: 'app-wheel',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="wheel-container">
      <div class="wheel-wrapper" [class.spinning]="isSpinning">
        <svg #wheelSvg class="wheel-svg"
             viewBox="-110 -110 220 220"
             xmlns="http://www.w3.org/2000/svg">
          <defs>
            <filter id="glow">
              <feGaussianBlur stdDeviation="3" result="coloredBlur"/>
              <feMerge><feMergeNode in="coloredBlur"/><feMergeNode in="SourceGraphic"/></feMerge>
            </filter>
          </defs>

          <!-- Outer ring -->
          <circle r="108" fill="none" stroke="#2a2f45" stroke-width="2"/>

          <!-- Segments -->
          <g *ngFor="let seg of segments; let i = index">
            <path
              [attr.d]="getSegmentPath(i)"
              [attr.fill]="seg.color"
              [attr.stroke]="getStrokeColor(seg)"
              stroke-width="1"
              [class.highlighted]="i === landedIndex && !isSpinning"
            />
            <text
              [attr.transform]="getLabelTransform(i)"
              [attr.fill]="getTextColor(seg)"
              font-family="'Syne', sans-serif"
              font-size="7"
              font-weight="700"
              text-anchor="middle"
              dominant-baseline="central"
              letter-spacing="0.5"
            >{{ seg.label }}</text>
          </g>

          <!-- Center hub -->
          <circle r="14" fill="#0a0b0f" stroke="#3d4468" stroke-width="1.5"/>
          <circle r="6" fill="#00e5ff" [class.hub-glow]="!isSpinning && landedIndex >= 0" filter="url(#glow)"/>
        </svg>

        <!-- Pointer arrow -->
        <div class="pointer">▼</div>
      </div>

      <button
        class="spin-btn"
        (click)="onSpin()"
        [disabled]="isSpinning || disabled"
        [class.ready]="!isSpinning && !disabled"
      >
        <span *ngIf="!isSpinning">SPIN</span>
        <span *ngIf="isSpinning" class="spinning-text">SPINNING…</span>
      </button>
    </div>
  `,
  styles: [`
    .wheel-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 16px;
    }

    .wheel-wrapper {
      position: relative;
      width: 280px;
      height: 280px;
    }

    .wheel-svg {
      width: 100%;
      height: 100%;
      transition: transform 0s;
    }

    .wheel-wrapper.spinning .wheel-svg {
      animation: spin-wheel var(--spin-duration, 3s) cubic-bezier(0.17,0.67,0.12,0.99) forwards;
    }

    @keyframes spin-wheel {
      from { transform: rotate(0deg); }
      to   { transform: rotate(var(--spin-degrees, 1440deg)); }
    }

    .pointer {
      position: absolute;
      top: -8px;
      left: 50%;
      transform: translateX(-50%);
      font-size: 20px;
      color: #00e5ff;
      filter: drop-shadow(0 0 8px #00e5ff);
      z-index: 2;
    }

    .highlighted {
      filter: brightness(1.4);
    }

    .hub-glow {
      animation: pulse 1.5s ease-in-out infinite;
    }

    @keyframes pulse {
      0%, 100% { opacity: 1; }
      50%       { opacity: 0.4; }
    }

    .spin-btn {
      font-family: 'Syne', sans-serif;
      font-size: 15px;
      font-weight: 800;
      letter-spacing: 3px;
      padding: 12px 40px;
      background: transparent;
      color: #00e5ff;
      border: 2px solid #00e5ff;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.2s;
      text-transform: uppercase;
    }

    .spin-btn.ready:hover {
      background: rgba(0,229,255,0.1);
      box-shadow: 0 0 20px rgba(0,229,255,0.4);
    }

    .spin-btn:disabled {
      opacity: 0.4;
      cursor: not-allowed;
    }

    .spinning-text {
      animation: blink 0.8s step-end infinite;
    }

    @keyframes blink {
      0%, 100% { opacity: 1; }
      50%       { opacity: 0.3; }
    }
  `]
})
export class WheelComponent implements OnChanges {
  @Input() segments: WheelSegment[] = [];
  @Input() disabled = false;
  @Output() spinComplete = new EventEmitter<{ segment: WheelSegment; index: number }>();

  @ViewChild('wheelSvg') wheelSvg!: ElementRef<SVGElement>;

  isSpinning = false;
  landedIndex = -1;

  private readonly RADIUS = 95;
  private currentRotation = 0;

  ngOnChanges(changes: SimpleChanges): void {}

  getSegmentPath(index: number): string {
    const count = this.segments.length;
    const angle = (2 * Math.PI) / count;
    const startAngle = index * angle - Math.PI / 2;
    const endAngle = startAngle + angle;
    const r = this.RADIUS;

    const x1 = r * Math.cos(startAngle);
    const y1 = r * Math.sin(startAngle);
    const x2 = r * Math.cos(endAngle);
    const y2 = r * Math.sin(endAngle);

    return `M 0 0 L ${x1.toFixed(2)} ${y1.toFixed(2)} A ${r} ${r} 0 0 1 ${x2.toFixed(2)} ${y2.toFixed(2)} Z`;
  }

  getLabelTransform(index: number): string {
    const count = this.segments.length;
    const angle = (2 * Math.PI) / count;
    const midAngle = index * angle - Math.PI / 2 + angle / 2;
    const r = this.RADIUS * 0.68;
    const x = r * Math.cos(midAngle);
    const y = r * Math.sin(midAngle);
    const deg = (midAngle * 180 / Math.PI) + 90;
    return `translate(${x.toFixed(2)},${y.toFixed(2)}) rotate(${deg.toFixed(1)})`;
  }

  getStrokeColor(seg: WheelSegment): string {
    if (seg.type === 'BANKRUPT') return '#ff4757';
    if (seg.type === 'FREE_VOWEL') return '#ffd84d';
    return '#2a2f45';
  }

  getTextColor(seg: WheelSegment): string {
    if (seg.type === 'BANKRUPT') return '#ff9fa5';
    if (seg.type === 'FREE_VOWEL') return '#ffd84d';
    if (seg.points >= 750) return '#00e5ff';
    return '#c8d0f0';
  }

  onSpin(): void {
    if (this.isSpinning || this.disabled) return;

    const landedIndex = Math.floor(Math.random() * this.segments.length);
    const segmentAngle = 360 / this.segments.length;

    // Calculate degrees so pointer (top) lands on selected segment
    const targetAngle = landedIndex * segmentAngle + segmentAngle / 2;
    const extraSpins = (4 + Math.floor(Math.random() * 4)) * 360;
    const spinDegrees = extraSpins + (360 - targetAngle - this.currentRotation % 360);
    this.currentRotation += spinDegrees;

    const svg = this.wheelSvg.nativeElement;
    svg.style.setProperty('--spin-degrees', `${this.currentRotation}deg`);
    svg.style.setProperty('--spin-duration', '3.2s');
    svg.style.transform = `rotate(${this.currentRotation}deg)`;

    this.isSpinning = true;
    this.landedIndex = -1;

    setTimeout(() => {
      this.isSpinning = false;
      this.landedIndex = landedIndex;
      this.spinComplete.emit({ segment: this.segments[landedIndex], index: landedIndex });
    }, 3300);
  }
}
