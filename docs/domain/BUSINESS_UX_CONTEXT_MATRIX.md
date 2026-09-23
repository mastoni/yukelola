# Business UX Context Matrix

> **Document Status:** CANONICAL REFERENCE  
> **Target Scope:** Dashboard layout, navigation hierarchy, primary CTAs, queue tabs, and adaptive tablet behavior per business model.

---

## 1. UX Navigation Hierarchy Matrix

| Business Model | Primary Screen | Primary Bottom Nav (Phone) | Navigation Rail (Tablet) | Primary Floating CTA | Top Dashboard Banner |
|---|---|---|---|---|---|
| **RETAIL_WARUNG** | Barcode / Grid POS | Beranda, Jualan, Produk, Pembelian, Kas, Laporan | Expanded Sidebar with Quick Scanner | `[+ Transaksi Baru]` | Daily Sales Counter + Low Stock Pill |
| **DIGITAL_KIOSK** | Keypad / Operator Selector | Beranda, Layanan Digital, Deposit, Riwayat, Pelanggan | Expanded Sidebar with Keypad | `[+ Transaksi Pulsa]` | **Pinned Digital Deposit Balance Banner** |
| **WARUNG + DIGITAL** | Retail POS + Digital Hub Tab | Beranda, Jualan, Layanan Digital, Produk, Kas, Laporan | Sidebar with Retail + Digital Tabs | `[+ Jualan]` | Daily Sales + Digital Deposit Widget |
| **LAUNDRY** | Active Order Queue Board | Beranda, Order Masuk, Proses, Siap Ambil, Kas, Laporan | Sidebar with Queue Stages | `[+ Terima Cucian]` | Active Orders in Process + Ready to Pickup Count |
| **SERVICE_WORKSHOP** | Service Work Order Queue | Beranda, Antrean Servis, Sparepart, Kas, Pelanggan | Sidebar with Service Bays | `[+ Daftar Servis Baru]` | Active Vehicle Work Orders in Progress |
| **PERCETAKAN** | Print Job Order Board | Beranda, Order Cetak, Produksi, Siap Ambil, Kas | Sidebar with Production Board | `[+ Order Cetak]` | Jobs in Production + Pickup Queue |
| **F&B CAFE** | Menu Grid + Table Selector | Beranda, Order Menu, Meja, Dapur, Kas, Laporan | Sidebar with Table Grid | `[+ Order Meja Baru]` | Active Table Bills + Open Orders |
| **APOTEK & ATK** | Barcode Search POS | Beranda, Kasir Barcode, Obat/ATK, Pembelian, Kas | Sidebar with Barcode Focus | `[+ Scan Barcode]` | Daily Sales + Low Stock Alerts |
| **GENERAL_STORE** | Adaptive Category POS | Beranda, Kasir, Produk, Pembelian, Kas, Laporan | Sidebar with Category Filter | `[+ Transaksi]` | Multi-Category Daily Summary |

---

## 2. Phone vs. Tablet Adaptive Layout Specifications

```
PHONE (Compact < 600dp)            TABLET PORTRAIT (600–839dp)        TABLET LANDSCAPE (>= 840dp)
┌──────────────────────┐           ┌────────────────────────────┐    ┌──────────────┬──────────────────────────────┐
│ Top App Bar          │           │ Top App Bar + Banner       │    │              │                              │
├──────────────────────┤           ├────────────────────────────┤    │ Navigation   │   Two-Pane Split Workspace   │
│                      │           │ 3-Column Catalog Grid      │    │ Rail / Drawer│                              │
│ 2-Column Catalog Grid│           │                            │    │              │ Left: Catalog / Job Queue    │
│                      │           ├────────────────────────────┤    │              │ Right: Persistent Live Cart /│
├──────────────────────┤           │ Collapsible Cart Bar       │    │              │        Order Detail Panel    │
│ Bottom Cart Bar      │           └────────────────────────────┘    │              │                              │
└──────────────────────┘                                             └──────────────┴──────────────────────────────┘
```

### Flow-by-Flow Adaptive Behavior
1. **Retail POS:**
   - *Phone:* 2-column catalog $\rightarrow$ Bottom floating cart bar $\rightarrow$ Checkout bottom sheet.
   - *Tablet Landscape:* 4-column catalog on left (65% width) with persistent active cart panel on right (35% width).
2. **Digital Services:**
   - *Phone:* Keypad modal with denomination picker.
   - *Tablet Landscape:* Number input & denomination grid on left; live Inquiry result card & confirmation button on right.
3. **Laundry & Workshop Service Queue:**
   - *Phone:* Tabbed list (`Diterima`, `Diproses`, `Siap Ambil`).
   - *Tablet Landscape:* 3-column Kanban board displaying all order stages simultaneously with drag/tap status progression.
4. **Reports & Analytics:**
   - *Phone:* Stacked metric cards.
   - *Tablet Landscape:* Multi-column KPI dashboard with side-by-side comparative charts.
