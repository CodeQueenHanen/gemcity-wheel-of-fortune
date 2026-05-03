import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';

export type ToastType = 'success' | 'error' | 'info' | 'bankrupt' | 'win';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast" [class]="'toast--' + type" [class.visible]="visible">
      <span class="icon">{{ icon }}</span>
      <span class="msg">{{ message }}</span>
    </div>
  `,
  styles: [`
    .toast {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 12px 20px;
      border-radius: 6px;
      border: 1px solid transparent;
      font-family: 'Syne', sans-serif;
      font-size: 15px;
      font-weight: 600;
      opacity: 0;
      transform: translateY(-8px);
      transition: all 0.25s ease;
      pointer-events: none;
    }

    .toast.visible {
      opacity: 1;
      transform: translateY(0);
    }

    .toast--success {
      background: rgba(0,230,118,0.1);
      border-color: rgba(0,230,118,0.35);
      color: #00e676;
    }

    .toast--error {
      background: rgba(255,71,87,0.1);
      border-color: rgba(255,71,87,0.35);
      color: #ff4757;
    }

    .toast--info {
      background: rgba(0,229,255,0.1);
      border-color: rgba(0,229,255,0.3);
      color: #00e5ff;
    }

    .toast--bankrupt {
      background: rgba(255,71,87,0.15);
      border-color: #ff4757;
      color: #ff4757;
      animation: bankrupt-flash 0.5s ease 3;
    }

    .toast--win {
      background: rgba(255,216,77,0.15);
      border-color: #ffd84d;
      color: #ffd84d;
    }

    .icon { font-size: 18px; }

    @keyframes bankrupt-flash {
      0%, 100% { background: rgba(255,71,87,0.15); }
      50%       { background: rgba(255,71,87,0.35); }
    }
  `]
})
export class ToastComponent implements OnChanges {
  @Input() message = '';
  @Input() type: ToastType = 'info';

  visible = false;
  icon = '';

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['message'] && this.message) {
      this.icon = this.getIcon();
      this.visible = false;
      setTimeout(() => this.visible = true, 10);
    }
  }

  private getIcon(): string {
    switch (this.type) {
      case 'success':  return '✓';
      case 'error':    return '✗';
      case 'bankrupt': return '💀';
      case 'win':      return '🏆';
      default:         return 'ℹ';
    }
  }
}
