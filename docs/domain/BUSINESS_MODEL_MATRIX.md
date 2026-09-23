# Business Model Matrix

> **Document Status:** CANONICAL REFERENCE  
> **Target Scope:** Yukelola Business Model Taxonomy, Operational Archetypes, and Capability Mapping.

---

## 1. Primary Principle: Business Model vs. Capability

$$\mathbf{Business\ Model} \ne \mathbf{Capability} \ne \mathbf{Product\ Type} \ne \mathbf{Transaction\ Mode}$$

- **Business Model:** Represents the **primary operational archetype** and business logic identity of the merchant.
- **Capability:** Specific **functional business modules** enabled on top of the primary model.
- **Product Type:** Fulfillment mechanics (`PHYSICAL`, `SERVICE`, `DIGITAL`).
- **Transaction Mode:** Operational checkout flow (`RETAIL_TRANSACTION`, `DIGITAL_TRANSACTION`, `SERVICE_ORDER_TRANSACTION`).

---

## 2. Canonical Business Model Matrix

| Business Type | Primary Model | Optional Capabilities | Primary Transaction Mode | Operational Workflow Type | Required Domain Concepts | Financial Effects | Stock Effects | UX & Navigation Context | Relevant Peripherals |
|---|---|---|---|---|---|---|---|---|---|
| **A. RETAIL_WARUNG** | `RETAIL` | `DIGITAL_SERVICE`, `DIGITAL_DEPOSIT`, `FUEL`, `CUSTOMER_DEBT`, `SUPPLIER_DEBT` | `RETAIL_TRANSACTION` | Instant Over-the-Counter POS | `Product`, `Category`, `Sale`, `SaleItem`, `CustomerDebt`, `CashRegister` | Cash inflow, Customer debt on credit | Immediate stock decrement on checkout | Fast barcode POS, quick grid, secondary digital hub | Bluetooth thermal printer, Barcode scanner |
| **B. DIGITAL_KIOSK / KONTER** | `DIGITAL_SERVICE` | `RETAIL`, `INVENTORY`, `CUSTOMER_DEBT`, `DIGITAL_DEPOSIT` | `DIGITAL_TRANSACTION` | Electronic Keypad & Inquiry Fulfillment | `DigitalTransaction`, `DigitalDepositAccount`, `DigitalDepositMutation`, `Inquiry` | Cash inflow, Digital deposit debit | No physical stock decrement for digital items | Numeric phone keypad, pinned deposit banner, secondary accessories tab | Bluetooth thermal printer (receipt) |
| **C. FOOD_BEVERAGE_CAFE** | `FOOD_BEVERAGE` | `RETAIL`, `INVENTORY`, `CUSTOMER_DEBT`, `DIGITAL_SERVICE` | `RETAIL_TRANSACTION` / Table Order | Menu Ordering & Table/Kitchen Notes | `Product` (Menu), `Sale.notes` (Table No), `Category` | Cash/QRIS inflow, optional split payment | Stock decrement for packaged goods/drinks | Menu category grid, dine-in/takeaway toggles, kitchen note modal | Thermal receipt & Kitchen slip printer |
| **D. SERVICE_WORKSHOP / BENGKEL** | `SERVICE` | `RETAIL`, `INVENTORY`, `PURCHASE`, `CUSTOMER_DEBT`, `SUPPLIER_DEBT` | `SERVICE_ORDER_TRANSACTION` / `RETAIL_TRANSACTION` | Asynchronous Vehicle Service & Spare Parts Sale | `ServiceOrder`, `VehicleContext`, `Product` (Service & Physical Spare Parts) | Labor fee + parts revenue, down payment, remaining balance | Spare parts stock decremented upon job completion | Service queue tab (Received $\rightarrow$ In Progress $\rightarrow$ Ready), vehicle plate entry | Thermal receipt & Job ticket printer |
| **E. LAUNDRY** | `SERVICE` | `RETAIL`, `CUSTOMER_DEBT`, `DIGITAL_SERVICE` | `SERVICE_ORDER_TRANSACTION` | Weight-based / Item-based Asynchronous Order | `ServiceOrder`, `LaundrySpecs` (Weight/Satuan, Fragrance, Express), `DownPayment` | Down payment inflow, remaining settlement upon pickup | Consumables untracked in basic mode, physical goods (perfume/detergent) tracked if sold | Order intake keypad, status board (Received $\rightarrow$ Washing $\rightarrow$ Ready $\rightarrow$ Picked Up) | Bluetooth receipt & Laundry tag printer |
| **F. APOTEK / PHARMACY** | `RETAIL` | `INVENTORY`, `PURCHASE`, `CUSTOMER_DEBT`, `SUPPLIER_DEBT` | `RETAIL_TRANSACTION` | Standard Over-the-Counter Medicine Retail | `Product` (Medicine), `Category` (Ethical/OTC), `Unit` (Strip, Box, Bottle) | Cash/QRIS inflow, purchase expense | Immediate unit stock decrement | Rapid SKU/Barcode search, unit selector, dosage notes | Barcode scanner, thermal receipt printer |
| **G. PERCETAKAN / PRINTING** | `SERVICE` | `RETAIL`, `INVENTORY`, `CUSTOMER_DEBT` | `SERVICE_ORDER_TRANSACTION` | Custom Print Order & Production Workflow | `ServiceOrder`, `PrintSpecs` (File, Dimensions, Paper, Finishing), `DownPayment` | Down payment, production cost, final balance settlement | Raw materials/paper decremented if tracked | Production queue, job specifications sheet, pickup status tracker | Thermal receipt & Order specification printer |
| **H. FOTOCOPY** | `RETAIL` / `SERVICE` | `RETAIL`, `INVENTORY`, `CUSTOMER_DEBT` | `RETAIL_TRANSACTION` / `SERVICE_ORDER_TRANSACTION` | Rapid Per-Page Counter Sale or Bulk Job | `Product` (Per-page copy/print service, laminating), `ServiceOrder` (Bulk) | Immediate cash inflow | Paper stock decremented if tracked | Quick counter keypad (Page count $\times$ Unit price), paper size chips | Thermal receipt printer |
| **I. ATK (Stationery)** | `RETAIL` | `INVENTORY`, `PURCHASE`, `CUSTOMER_DEBT`, `SUPPLIER_DEBT` | `RETAIL_TRANSACTION` | High-Volume Barcode Retail | `Product` (Stationery), `Category`, `Barcode`, `Unit` (Pcs, Lusin, Pack) | Immediate cash/digital inflow | Immediate stock decrement across multiple units | Barcode-first POS, rapid quantity modifiers, multi-unit pricing | USB/Bluetooth barcode scanner, receipt printer |
| **J. GENERAL_STORE** | `MIXED_RETAIL` | All modular capabilities enabled on demand | `RETAIL_TRANSACTION` | Multi-Category General Retail | `Product`, `Category`, `Sale`, `Customer`, `Supplier` | Standard commercial inflows/outflows | Standard multi-category stock tracking | Adaptive category navigation, generalized dashboard | Scanner, receipt printer |

---

## 3. Structural Decision Summary

1. **Pure Retail Archetypes (`WARUNG`, `APOTEK`, `ATK`, `GENERAL_STORE`):** Represented as `RETAIL` business model using standard `RETAIL_TRANSACTION`. No new transaction engines required.
2. **Pure Digital Archetype (`DIGITAL_KIOSK` / `KONTER`):** Represented as `DIGITAL_SERVICE` business model using `DIGITAL_TRANSACTION` with dedicated `DigitalDepositAccount`.
3. **Asynchronous Service Archetypes (`LAUNDRY`, `SERVICE_WORKSHOP`, `PERCETAKAN`):** Share a unified, extensible `ServiceOrder` abstraction (`SERVICE_ORDER_TRANSACTION`) to handle customer intake, work queue, down payment, and pickup settlement.
4. **Hybrid Micro-Counter Archetypes (`FOTOCOPY`, `F&B CAFE`):** Use `RETAIL_TRANSACTION` with contextual metadata (Table No / Page count) for instant checkout, with optional fallback to `ServiceOrder` for high-volume bulk jobs.
