# Stock Effect Matrix

> **Document Status:** CANONICAL REFERENCE (REVISION: RETAIL_HEALTH / APOTEK + TOKO OBAT)  
> **Target Scope:** Inventory tracking, stock mutation triggers, physical vs. non-physical handling, and scope boundaries.

---

## 1. Inventory Mutation Triggers

| Business Context | Product Classification | Inventory Tracking | Stock Deduction Trigger | Stock Addition Trigger | Special Considerations |
|---|---|---|---|---|---|
| **RETAIL_WARUNG** | Physical Goods (Snacks, Groceries) | Tracked (`trackStock = true`) | Completed `Sale` | Completed `Purchase` | Reorder alert when `stock <= minStock`. |
| | Fuel (Bensin Eceran / Pertalite) | Tracked | Completed `Sale` (in Liters) | Purchase from SPBU | Decimal stock values supported. |
| | Auxiliary Medicine (Warung + Obat) | Tracked | Completed `Sale` (in Pcs/Strip) | Wholesaler Purchase | Tracked as standard physical SKU. |
| | Digital Products (Pulsa, Token) | Untracked (`trackStock = false`) | None (Electronic balance) | None | Deducts from `DigitalDepositAccount`, not inventory. |
| **DIGITAL_KIOSK / KONTER** | Digital Vouchers & Top-Ups | Untracked | None | None | Handled via digital deposit ledger. |
| | Physical Accessories (Charger, Case) | Tracked | Completed `Sale` | Completed `Purchase` | Barcode/SKU tracking. |
| **RETAIL_HEALTH (APOTEK / TOKO OBAT)** | OTC Medicines, Vitamins, Supplements | Tracked | Completed `Sale` | Completed `Purchase` | Multi-unit conversion (Box $\leftrightarrow$ Strip $\leftrightarrow$ Tablet). |
| | Ethical / Prescription Medicines | Tracked | Completed `Sale` | Completed `Purchase` (from PBF) | Multi-unit conversion. **Batch & Expiry remain OUT OF SCOPE.** |
| | Medical Devices / Non-Medicine Retail | Tracked | Completed `Sale` | Completed `Purchase` | Standard SKU tracking. |
| **FOOD_BEVERAGE_CAFE**| Packaged Drinks & Snacks | Tracked | Completed `Sale` | Completed `Purchase` | Pre-packaged inventory. |
| | Cooked Menu / Beverages | Untracked (in basic mode) | None | None | Complex Recipe/BOM is OUT OF SCOPE. |
| **SERVICE_WORKSHOP** | Mechanic Labor Service | Untracked | None | None | Pure service line item. |
| | Physical Spare Parts (Oli, Busi, Ban)| Tracked | Completed `ServiceOrder` / `Sale`| Completed `Purchase` | Deducts stock upon work order completion. |
| **LAUNDRY** | Washing / Ironing Services | Untracked | None | None | Service priced by kg or unit. |
| | Retail Detergent / Perfume Bottles | Tracked (if sold retail) | Completed `Sale` | Completed `Purchase` | Sold as retail items. |
| **PERCETAKAN** | Custom Printing Service | Untracked | None | None | Service labor. |
| | Raw Materials (Paper sheets, Banner) | Tracked (optional) | Completed `ServiceOrder` | Completed `Purchase` | Deducted per job if raw stock tracking is enabled. |
| **FOTOCOPY** | Copy / Print Service | Untracked / Tracked Paper | Completed `Sale` | Paper ream `Purchase` | Paper ream / sheet deduction if enabled. |
| **ATK** | Stationery Items | Tracked | Completed `Sale` | Completed `Purchase` | Multi-unit conversion (e.g. 1 Lusin = 12 Pcs). |
| **GENERAL_STORE** | Multi-Category Merchandise | Tracked | Completed `Sale` | Completed `Purchase` | Standard multi-category retail tracking. |

---

## 2. Strict Scope Boundaries (Out of Scope for Baseline)

In strict adherence to the locked Product & Domain Contract v1.1.0:
1. **Warehouse Bin Management:** Multi-warehouse bin/shelf allocation is **OUT OF SCOPE**.
2. **Batch Numbering & Expiry Tracking (FEFO/FIFO):** Batch/lot tracking and expiration date alarms are **OUT OF SCOPE**. Pharmacy operations use standard on-hand physical stock counts.
3. **Multi-Level Bill of Materials (BOM) Manufacturing:** Composite recipe ingredient deduction (e.g., deducting 15g coffee beans + 200ml milk on selling 1 Latte) is **OUT OF SCOPE**.
4. **Serial Number Warranty Engine:** Serialized hardware device tracking is **OUT OF SCOPE**.
