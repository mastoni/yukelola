# Yukelola Design System & UX Foundation

> **Module:** `:core:designsystem`  
> **Status:** FOUNDATION ESTABLISHED (MOBILE-FIRST + TABLET-FIRST ADAPTIVE)  
> **Target:** Reusable UI tokens, adaptive components, themes, and UX patterns for Yukelola Android (Phones, Foldables, and 7"–12"+ Tablets).

---

## 1. Visual Identity & Brand Hierarchy

Yukelola's design language communicates simplicity, speed, trustworthiness, and warmth tailored for Indonesian UMKM merchants.

### Primary Brand Palette
- **Yukelola Navy (`#0A2540`):** Anchors primary containers, high-contrast dark surfaces, and navigation bars.
- **Yukelola Cyan (`#00A3FF`):** Interactive focal point for primary action buttons (CTAs), focus rings, and digital accents.
- **Yukelola Yellow (`#FFB800`):** Dynamic accent for cash highlights, alert badges, and pending state notices.

### Meaning-Driven Semantic Colors
- **Sales / Success Green (`#00C853`):** Completed retail transactions, positive cash inflow, settled debt.
- **Digital / Information Blue (`#2979FF`):** Digital service category, inquiry confirmations, validation cards.
- **Return / Error / Attention Red (`#D50000`):** Overdue receivables, out-of-stock notices, failed transactions.
- **Inventory / Operational Orange (`#FF6D00`):** Stock opname adjustments, pending purchase invoices.
- **Reports / Analytics Purple (`#7C4DFF`):** Gross profit charts, business summaries, analytics views.

---

## 2. Design Tokens Overview

| Token Category | Token Names | Value / Usage |
|---|---|---|
| **Brand Colors** | `yukelola_navy`, `yukelola_cyan`, `yukelola_yellow` | Main application theme attributes. |
| **Semantic Colors** | `yukelola_semantic_sales_green`, `digital_blue`, `error_red`, `inventory_orange`, `reports_purple` | Status-driven and capability-driven badges. |
| **Surfaces** | `yukelola_surface_white`, `surface_light_blue`, `surface_light_neutral`, `surface_dark_navy` | Scaffold backgrounds and card surfaces. |
| **Spacing** | `spacing_xs` (4dp), `spacing_sm` (8dp), `spacing_md` (12dp), `spacing_lg` (16dp), `spacing_xl` (24dp) | Consistent 4dp-grid spacing system. |
| **Corner Radius** | `radius_xs` (4dp), `radius_sm` (8dp), `radius_md` (12dp), `radius_lg` (16dp), `radius_pill` (999dp) | Friendly, modern rounded card and button geometry. |
| **Touch Targets** | `min_touch_target` (48dp), `touch_target_lg` (56dp) | Accessible mobile interaction targets. |

---

## 3. Reusable UI Components

1. **YukelolaButton:** High-contrast, rounded (12dp radius) action buttons in Primary (Cyan), Secondary (Navy), and Yellow variants.
2. **YukelolaCard:** Clean white surface container with 16dp corner radius, subtle 1dp border or soft 4dp elevation.
3. **YukelolaStatusBadge:** Semantic pill badge mapping domain states to colors (`bg_badge_sales`, `bg_badge_digital`, `bg_badge_warning`, `bg_badge_inventory`, `bg_badge_reports`).
4. **YukelolaInquiryUiState:** Visual state contract encapsulating `Idle`, `Checking`, `Success` (read-only subscriber info), and `Error`.
5. **YukelolaCurrencyFormatter:** Clean Indonesian Rupiah formatting (`Rp 150.000` and compact `Rp 1,5Jt`).

---

## 4. Business UX Context Compatibility

- **WARUNG (Retail-First):** Primary screen defaults to physical product grid / barcode POS. Digital services reside in a dedicated secondary section.
- **WARUNG + DIGITAL SERVICE:** Digital deposit widget renders inside Cash & Drawer overview; digital services get a distinct tab.
- **KONTER / DIGITAL KIOSK (Digital-First):** Primary screen opens directly to phone/token keypad with pinned digital deposit balance banner. Physical accessories remain in secondary catalog tab.

---

## 5. Tablet-First Adaptive UX Architecture

The Yukelola Design System establishes an adaptive layout model supporting phones and tablets from a single shared codebase without duplicating screens or business logic.

```
PHONE (Compact < 600dp)            TABLET (Medium / Expanded >= 600dp)
┌──────────────────────┐           ┌──────────────┬──────────────────────────────┐
│ Top App Bar          │           │              │                              │
├──────────────────────┤           │ Navigation   │       Main Content /         │
│                      │           │ Rail / Drawer│       Two-Pane Workspace     │
│ Main Content         │           │              │                              │
│                      │           │              │                              │
├──────────────────────┤           │              │                              │
│ Bottom Navigation    │           └──────────────┴──────────────────────────────┘
└──────────────────────┘
```

### Adaptive Layout Strategy
- **Window Size Classes:** Centralized `YukelolaWindowSizeHelper` evaluates `COMPACT` (< 600dp), `MEDIUM` (600dp–839dp), and `EXPANDED` (>= 840dp).
- **Adaptive Qualifiers:** 
  - `values/` — Phone baseline (2-column grid, 1-column forms, bottom navigation).
  - `values-sw600dp/` — 7"–8" Tablet (3-column grid, 2-column forms, 24dp screen padding).
  - `values-sw720dp/` — 10"–12"+ Tablet (4-column grid, 2-pane POS, 32dp screen padding, max content width constraints).
- **POS Two-Pane Layout:**
  - *Phone:* Single vertical flow (Catalog $\rightarrow$ Bottom Cart Bar $\rightarrow$ Checkout Sheet).
  - *Tablet (Landscape / Wide):* Split two-pane workspace (Left: Product/Category Catalog Grid, Right: Persistent Active Cart Panel with live totals and checkout CTA).
- **Digital Transaction Two-Pane Layout:**
  - *Tablet:* Left side houses the destination number keypad and denomination picker; Right side houses the live Inquiry verification result card and confirmation button.
- **No Duplicated Code:** Phone and tablet layouts share identical ViewModels, UseCases, Repositories, and Domain models.
