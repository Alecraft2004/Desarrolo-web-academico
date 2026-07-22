import {
  Component, ElementRef, EventEmitter, HostListener, Input, OnDestroy, Output, Renderer2, ViewChild
} from '@angular/core';
import { CommonModule } from '@angular/common';

export interface GlassOption {
  value: number;
  label: string;
  disabled?: boolean;
  badge?: string;
}

@Component({
  selector: 'app-glass-select',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './glass-select.html'
})
export class GlassSelectComponent implements OnDestroy {
  @Input() options: GlassOption[] = [];
  @Input() placeholder = 'Selecciona';
  @Input() value: number | null = null;
  @Output() valueChange = new EventEmitter<number | null>();

  @ViewChild('trigger', { static: true }) triggerRef!: ElementRef<HTMLButtonElement>;
  @ViewChild('panel') set panelRef(ref: ElementRef<HTMLElement> | undefined) {
    this.panelEl = ref ? ref.nativeElement : null;
    if (this.panelEl) this.renderer.appendChild(document.body, this.panelEl);
  }

  abierto = false;
  panelTop = 0;
  panelLeft = 0;
  panelWidth = 0;

  private panelEl: HTMLElement | null = null;

  constructor(private el: ElementRef<HTMLElement>, private renderer: Renderer2) {}

  get etiquetaSeleccionada(): string {
    const opt = this.options.find(o => o.value === this.value);
    return opt ? opt.label : this.placeholder;
  }

  toggle() {
    this.abierto = !this.abierto;
    if (this.abierto) this.actualizarPosicion();
  }

  private actualizarPosicion() {
    const rect = this.triggerRef.nativeElement.getBoundingClientRect();
    this.panelTop = rect.bottom + 8;
    this.panelLeft = rect.left;
    this.panelWidth = rect.width;
  }

  elegir(opt: GlassOption) {
    if (opt.disabled) return;
    this.value = opt.value;
    this.valueChange.emit(opt.value);
    this.abierto = false;
  }

  @HostListener('document:click', ['$event'])
  onDocClick(ev: MouseEvent) {
    const target = ev.target as Node;
    const dentroHost = this.el.nativeElement.contains(target);
    const dentroPanel = this.panelEl ? this.panelEl.contains(target) : false;
    if (!dentroHost && !dentroPanel) this.abierto = false;
  }

  @HostListener('window:scroll')
  @HostListener('window:resize')
  onScrollOrResize() {
    if (this.abierto) this.actualizarPosicion();
  }

  @HostListener('keydown.escape')
  onEscape() {
    this.abierto = false;
  }

  ngOnDestroy() {
    if (this.panelEl && this.panelEl.parentNode) {
      this.panelEl.parentNode.removeChild(this.panelEl);
    }
  }
}
