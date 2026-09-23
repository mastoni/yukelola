# Yukelola Product & Domain Contract

> **Contract Version:** 1.2.0  
> **Status:** LOCKED / CANONICAL SOURCE OF TRUTH  
> **Canonical Identity:** `id.yukelola`  
> **Target Scope:** Android Modular Monolith Core Domain (`:core:domain`), Room Database Entities (`:core:database`), Repository Contracts, UseCases, Multi-Branch Local-First Architecture, and Future Backend/PPOB Boundaries.  
> **Consolidated Sources:** Product Contract v1.1.0, YK-DOMAIN-03 Domain Logic Matrix, and YK-DOMAIN-04 Multi-Branch Architecture.

---

## 1. Product Vision & Operational Boundary

Yukelola is a streamlined, transaction-oriented, local-first business operating system designed for Indonesian micro, small, and medium enterprises (UMKM).

### Core Philosophy
- **Simple & Practical:** Fast, intuitive, and accessible to business owners and staff with varying levels of technical and financial literacy.
- **Local-First & Offline-First:** Every branch maintains an independent, authoritative local database. Day-to-day point-of-sale, inventory tracking, cash drawer management, and service queues function 100% offline with zero reliance on an active internet connection or a central cloud database.
- **Transaction-Centric:** Driven by real-world operational workflows (instant retail sales, digital fulfillment, and asynchronous service orders) rather than complex enterprise ERP or double-entry general ledger accounting.
- **Strict Domain Core:** Business logic, capabilities, invariants, and aggregate lifecycles are encapsulated in pure Kotlin domain models (`:core:domain`), decoupled from UI frameworks, database engines, and hardware drivers.

---

## 2. Domain Glossary

All domain entities, repository interfaces, UseCases, and database schemas **must** adhere strictly to the English business terminology defined below. Indonesian terms are reserved exclusively for UI presentation and localization strings.

| Canonical Domain Term | Meaning & Purpose | Localized Synonym (ID) |
|---|---|---|
| **Business** | Aggregate root representing the enterprise / legal tenant entity. | Usaha / Bisnis |
| **Branch** | Physical store, outlet, or operational branch unit under a Business. | Cabang / Outlet |
| **BusinessProfile** | Operational metadata, contact info, and receipt settings scoped to a Branch. | Profil Cabang / Usaha |
| **BusinessModel** | Primary operational identity and workflow archetype of a Branch. | Model Bisnis / Jenis Usaha |
| **Capability** | Modular functional feature set enabled for a Branch. | Fitur / Kemampuan Usaha |
| **User** | System operator or staff member with role-based authority. | Pengguna / Staf |
| **Role** | Authority tier (`OWNER`, `MANAGER`, `CASHIER`). | Peran Pengguna |
| **Device** | Physical Android terminal registered and paired to a Branch. | Perangkat / Terminal |
| **CashierSession** | Active shift session binding a user, device, and cash drawer. | Sesi Kasir / Shift |
| **Product** | Global sellable catalog definition (SKU, barcode, name, category, unit). | Master Produk |
| **BranchProductOverride** | Branch-specific operational product state (stock, pricing, availability). | Stok & Harga Cabang |
| **ProductType** | Fulfillment classification (`PHYSICAL`, `SERVICE`, `DIGITAL`). | Jenis Produk |
| **Category** | Organizational classification for catalog items. | Kategori |
| **Unit** | Measurement unit for physical goods (e.g., PCS, KG, STRIP, BOX, LITER). | Satuan |
| **StockAdjustment** | Manual inventory count correction (opname, damage, loss). | Penyesuaian Stok |
| **Sale** | Record of a completed customer retail transaction. | Penjualan / Struk |
| **SaleItem** | Line item in a sale referencing product snapshot and pricing. | Item Penjualan |
| **ServiceOrder** | Asynchronous service/job aggregate (Laundry, Workshop, Printing). | Order Layanan / SPK |
| **ServiceOrderItem** | Line item in a ServiceOrder (labor service or consumed spare parts). | Item Layanan |
| **ServiceOrderStatus** | Operational state (`RECEIVED`, `IN_PROGRESS`, `READY`, `COMPLETED`, `CANCELLED`). | Status Order |
| **DownPaymentRecord** | Upfront financial deposit collected upon service intake. | Uang Muka / DP |
| **Purchase** | Record of inventory replenishment from a supplier. | Pembelian / Kulakan |
| **PurchaseItem** | Line item in a purchase record with quantity and cost price. | Item Pembelian |
| **Payment** | Financial settlement event applied to a Sale, Purchase, Debt, or ServiceOrder. | Pembayaran |
| **Discount** | Monetary or percentage reduction applied to an item or entire transaction. | Diskon / Potongan |
| **Customer** | Buyer profile with optional credit ledger balance. | Pelanggan |
| **Supplier** | Vendor profile with optional payable balance. | Pemasok / Supplier |
| **CustomerDebt** | Outstanding receivable owed by a customer (Piutang). | Piutang Pelanggan / Kasbon |
| **SupplierDebt** | Outstanding payable owed to a supplier (Hutang). | Hutang Pemasok |
| **DebtPayment** | Financial event reducing an outstanding customer or supplier debt. | Pembayaran Hutang / Piutang |
| **CashRegister** | Cash drawer manager tracking physical cash balance and mutations. | Buku Kas / Kasir |
| **CashMutation** | Non-sale / non-purchase cash adjustment (inflow, outflow, expense). | Mutasi Kas / Pengeluaran |
| **DigitalDepositAccount** | Dedicated agent deposit balance used exclusively for digital fulfillment. | Saldo Deposit Digital |
| **DigitalDepositMutation** | Traceable historical movement of digital deposit funds. | Mutasi Deposit Digital |
| **DigitalTransaction** | Dedicated transaction record for electronic vouchers, tokens, and bills. | Transaksi Digital |
| **Inquiry** | Read-only validation check prior to digital bill settlement. | Cek Tagihan / Cek Nomor |
| **License** | Software entitlement model granting branch seats and capabilities. | Lisensi Aplikasi |

---

## 3. Entity Catalog & Domain Hierarchy

Yukelola structures enterprise data through a clear hierarchy: **Business $\rightarrow$ Branch $\rightarrow$ Operational Aggregates**.

```
                               ┌─────────────────────────────┐
                               │          Business           │ (Tenant Root)
                               └──────────────┬──────────────┘
                                              │ 1:*
                               ┌──────────────▼──────────────┐
                               │           Branch            │ (Operational Unit)
                               ├─────────────────────────────┤
                               │ • BusinessProfile           │
                               │ • BusinessModel             │
                               │ • Enabled Capabilities      │
                               └──────────────┬──────────────┘
                                              │
         ┌──────────────────┬─────────────────┼──────────────────┬──────────────────┐
         │ 1:*              │ 1:*             │ 1:*              │ 1:*              │ 1:1
         ▼                  ▼                 ▼                  ▼                  ▼
  ┌──────────────┐   ┌──────────────┐  ┌──────────────┐   ┌──────────────┐   ┌─────────────────────┐
  │BranchProduct │   │   Customer   │  │   Supplier   │   │ CashRegister │   │DigitalDepositAccount│
  │   Override   │   └──────┬───────┘  └──────┬───────┘   └──────┬───────┘   └──────────┬──────────┘
  └──────┬───────┘          │                 │                  │ Impacts              │ 1:*
         │                  │                 │                  ▼                      ▼
         │ 1:*              │ 0..1            │ 0..1      ┌──────────────┐   ┌─────────────────────┐
         ▼                  ▼                 │           │ CashMutation │   │DigitalDepositMutat'n│
  ┌──────────────┐   ┌──────────────┐         │           └──────────────┘   └─────────────────────┘
  │     Sale     │   │ ServiceOrder │         │
  ├──────────────┤   ├──────────────┤         │ 1:*
  │ • SaleItems  │   │ • OrderItems │         ▼
  │ • Payments   │   │ • DownPayment│  ┌──────────────┐
  │ • Debt (opt) │   │ • Debt (opt) │  │   Purchase   │
  └──────────────┘   └──────────────┘  ├──────────────┤
                                       │ • Items      │
                                       │ • Debt (opt) │
                                       └──────────────┘
```

### Entity Catalog Definitions
1. **`Business`:** Aggregate Root representing the merchant enterprise (`id`, `legalName`, `ownerUserId`, `createdAt`, `isActive`).
2. **`Branch`:** Operational outlet entity (`id`, `businessId`, `code`, `name`, `businessProfile`, `businessModel`, `enabledCapabilities`, `isActive`).
3. **`BusinessProfile`:** Scoped branch metadata (`name`, `phone`, `address`, `receiptHeader`, `receiptFooter`, `currency`, `timezone`).
4. **`User` & `Role`:** Staff entity (`id`, `businessId`, `branchId?`, `username`, `fullName`, `role` [`OWNER`, `MANAGER`, `CASHIER`], `pinHash`, `isActive`).
5. **`Device`:** Hardware terminal paired to a branch (`id`, `businessId`, `branchId`, `deviceName`, `deviceType` [`MAIN_HOST_TABLET`, `SECONDARY_CASHIER_TERMINAL`, `MANAGER_PHONE`, `OWNER_PHONE`], `isActive`).
6. **`CashierSession`:** Active drawer shift (`id`, `branchId`, `userId`, `deviceId`, `openingBalance`, `closingBalance?`, `openedAt`, `closedAt?`, `status`).
7. **`Product`:** Global catalog definition (`id`, `businessId`, `categoryId`, `sku`, `barcode`, `name`, `productType`, `baseUnit`, `defaultCostPrice`, `defaultSellingPrice`, `isActive`).
8. **`BranchProductOverride`:** Branch inventory & pricing ledger (`branchId`, `productId`, `stock`, `minStock`, `localCostPrice?`, `localSellingPrice?`, `isAvailable`).
9. **`Category`:** Global classification taxonomy (`id`, `businessId`, `name`, `color`, `icon`, `sortOrder`).
10. **`Customer`:** Buyer profile (`id`, `businessId`, `branchId?`, `name`, `phone`, `debtBalance`, `isActive`).
11. **`Supplier`:** Vendor profile (`id`, `businessId`, `name`, `phone`, `debtBalance`, `isActive`).
12. **`Sale`:** Instant retail transaction aggregate (`id`, `businessId`, `branchId`, `userId`, `deviceId`, `cashierSessionId?`, `saleNumber`, `transactionMode`, `customerId?`, `subtotal`, `discountAmount`, `taxAmount`, `totalAmount`, `paidAmount`, `paymentStatus`, `fulfillmentStatus`, `createdAt`).
13. **`SaleItem`:** Immutable historical line item (`id`, `saleId`, `productId`, `productName`, `unit`, `unitPrice`, `costPrice`, `quantity`, `discountAmount`, `subtotal`).
14. **`ServiceOrder`:** Asynchronous service order aggregate (`id`, `businessId`, `branchId`, `userId`, `deviceId`, `orderNumber`, `customerId?`, `orderType` [`LAUNDRY`, `WORKSHOP`, `PRINTING`, `CUSTOM`], `fulfillmentStatus` [`RECEIVED`, `IN_PROGRESS`, `READY`, `COMPLETED`, `CANCELLED`], `totalAmount`, `downPaymentAmount`, `remainingBalance`, `contextMetadataJson?`, `estimatedCompletionDate?`, `createdAt`, `updatedAt`).
15. **`ServiceOrderItem`:** Line item in ServiceOrder (`id`, `orderId`, `productId`, `productName`, `itemType` [`SERVICE_LABOR`, `PHYSICAL_PART`], `unitPrice`, `costPrice`, `quantity`, `subtotal`).
16. **`Purchase`:** Inventory replenishment aggregate (`id`, `businessId`, `branchId`, `userId`, `purchaseNumber`, `supplierId?`, `totalAmount`, `paidAmount`, `paymentStatus`, `createdAt`).
17. **`PurchaseItem`:** Purchase line item (`id`, `purchaseId`, `productId`, `productName`, `unitCost`, `quantity`, `subtotal`).
18. **`Payment`:** Financial settlement record (`id`, `businessId`, `branchId`, `transactionType` [`SALE`, `SERVICE_ORDER`, `PURCHASE`, `CUSTOMER_DEBT`, `SUPPLIER_DEBT`], `referenceId`, `paymentMethod`, `amount`, `createdAt`).
19. **`CustomerDebt`:** Customer credit receivable (`id`, `businessId`, `branchId`, `customerId`, `referenceType` [`SALE`, `SERVICE_ORDER`], `referenceId`, `originalAmount`, `remainingAmount`, `status`, `dueDate?`, `createdAt`).
20. **`SupplierDebt`:** Supplier credit payable (`id`, `businessId`, `branchId`, `supplierId`, `purchaseId`, `originalAmount`, `remainingAmount`, `status`, `dueDate?`, `createdAt`).
21. **`DebtPayment`:** Debt reduction event (`id`, `businessId`, `branchId`, `debtType` [`CUSTOMER`, `SUPPLIER`], `debtId`, `amount`, `paymentMethod`, `notes`, `createdAt`).
22. **`CashRegister`:** Branch cash drawer manager (`id`, `businessId`, `branchId`, `name`, `currentBalance`, `updatedAt`).
23. **`CashMutation`:** Cash drawer movement (`id`, `businessId`, `branchId`, `registerId`, `cashierSessionId?`, `mutationType` [`INFLOW`, `OUTFLOW`], `category`, `amount`, `source`, `referenceId?`, `notes`, `createdAt`).
24. **`DigitalDepositAccount`:** Branch agent fulfillment fund (`id`, `businessId`, `branchId`, `currentBalance`, `updatedAt`).
25. **`DigitalDepositMutation`:** Traceable deposit log (`id`, `businessId`, `branchId`, `accountId`, `mutationType` [`TOP_UP`, `DIGITAL_SALE`, `REFUND`, `REVERSAL`, `ADJUSTMENT`], `amount`, `balanceBefore`, `balanceAfter`, `referenceId?`, `notes`, `createdAt`).
26. **`DigitalTransaction`:** Electronic transaction record (`id`, `businessId`, `branchId`, `userId`, `deviceId`, `saleId?`, `targetNumber`, `productCode`, `denomination`, `costPrice`, `sellingPrice`, `depositMutationId?`, `fulfillmentStatus` [`INITIATED`, `PENDING`, `SUCCESS`, `FAILED`, `REVERSED`], `providerReference?`, `failureReason?`, `createdAt`, `updatedAt`).

---

## 4. Business Models & Operational Logic

$$\mathbf{Business\ Model} \ne \mathbf{Capability} \ne \mathbf{Product\ Type} \ne \mathbf{Transaction\ Mode}$$

A **Business Model** defines the primary operational identity, default navigation hierarchy, and terminal workflow of a **Branch**. Capabilities extend functionality modularly.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       YUKELOLA BUSINESS MODEL TAXONOMY                      │
├──────────────────────┬────────────────────────┬─────────────────────────────┤
│ BUSINESS MODEL       │ OPERATIONAL ARCHETYPE  │ PRIMARY TRANSACTION MODE    │
├──────────────────────┼────────────────────────┼─────────────────────────────┤
│ 1. RETAIL_WARUNG     │ Over-the-counter POS   │ RETAIL_TRANSACTION          │
│ 2. DIGITAL_KIOSK     │ Electronic Operator    │ DIGITAL_TRANSACTION         │
│ 3. FOOD_BEVERAGE_CAFE│ Menu & Table Tabs      │ RETAIL_TRANSACTION          │
│ 4. SERVICE_WORKSHOP  │ Vehicle Repair & Parts │ SERVICE_ORDER_TRANSACTION   │
│ 5. LAUNDRY           │ Weight/Item Drop-off   │ SERVICE_ORDER_TRANSACTION   │
│ 6. RETAIL_HEALTH     │ Pharmaceutical POS     │ RETAIL_TRANSACTION          │
│ 7. PERCETAKAN        │ Custom Print Job Order │ SERVICE_ORDER_TRANSACTION   │
│ 8. FOTOCOPY          │ Counter Calc / Bulk Job│ RETAIL / SERVICE_ORDER      │
│ 9. ATK               │ Multi-Unit Barcode POS │ RETAIL_TRANSACTION          │
│ 10. GENERAL_STORE    │ Multi-Category POS     │ RETAIL_TRANSACTION          │
└──────────────────────┴────────────────────────┴─────────────────────────────┘
```

### Business Context Rules
1. **`RETAIL_HEALTH` (Apotek & Toko Obat):**
   - Represents dedicated medicine and healthcare retail.
   - Operates via `RETAIL_TRANSACTION` with multi-unit conversions (Box $\leftrightarrow$ Strip $\leftrightarrow$ Tablet).
   - Profile `APOTEK`: Supports optional non-blocking prescription/doctor metadata notes.
   - Profile `TOKO_OBAT`: Standard over-the-counter health merchandise.
   - **Scope Boundary:** Batch numbering and expiry date tracking remain strictly **OUT OF SCOPE** for the core v1.x baseline.
2. **`WARUNG + OBAT` Distinction:**
   - A `RETAIL_WARUNG` selling auxiliary blister-pack medicine remains `RETAIL_WARUNG`.
   - Medicines are managed as standard physical grocery SKUs without triggering pharmaceutical search or prescription metadata workflows.
3. **`FOTOCOPY` Hybrid Execution:**
   - Walk-in per-page copies execute via instant `RETAIL_TRANSACTION` (Page count $\times$ Rate).
   - Large volume book printing or binding jobs route through `SERVICE_ORDER_TRANSACTION`.

---

## 5. Transaction Modes & Settlement Mechanics

Yukelola establishes three distinct operational transaction engines:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                             TRANSACTION MODES                               │
├──────────────────────────┬───────────────────────────┬──────────────────────┤
│ 1. RETAIL_TRANSACTION    │ 2. DIGITAL_TRANSACTION    │ 3. SERVICE_ORDER_TX  │
├──────────────────────────┼───────────────────────────┼──────────────────────┤
│ • Instant Checkout       │ • Electronic Operator     │ • Asynchronous Queue │
│ • Immediate Stock Decrement│ • Digital Deposit Debit │ • Down Payment + Rem │
│ • Cash/QRIS/Kasbon       │ • Read-Only Inquiry       │ • Work Order Progress│
└──────────────────────────┴───────────────────────────┴──────────────────────┘
```

### Transaction Mode Rules
1. **`RETAIL_TRANSACTION`:**
   - Synchronous instant execution.
   - Decrements `BranchProductOverride.stock` for `PHYSICAL` products.
   - Increases `CashRegister` by cash paid; creates `CustomerDebt` if `paidAmount < totalAmount`.
2. **`DIGITAL_TRANSACTION`:**
   - Debits `DigitalDepositAccount` by distributor `costPrice`.
   - Physical stock count is **UNTOUCHED**.
   - Inquiry checks are strictly read-only and idempotent (never debit cash, deposit, or create sales).
3. **`SERVICE_ORDER_TRANSACTION`:**
   - Manages asynchronous job lifecycle: `RECEIVED` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `READY` $\rightarrow$ `COMPLETED` (or `CANCELLED`).
   - Down payment records immediate cash inflow in `CashRegister`.
   - Consumed spare parts/materials are deducted from branch stock upon work progress/completion.
   - Remaining balance is settled upon customer pickup.

---

## 6. Financial Ledgers & Core Invariants

Yukelola strictly preserves mathematical separation between distinct financial assets:

$$\mathbf{CashRegister} \ne \mathbf{DigitalDepositAccount} \ne \mathbf{CustomerDebt} \ne \mathbf{SupplierDebt}$$

$$\mathbf{Cash\ Balance} \ne \mathbf{Business\ Profit}$$
$$\mathbf{Deposit\ Top\text{-}Up} \ne \mathbf{Revenue}$$

### Core Financial Invariants
1. **Deposit Top-Up Invariant:** Adding funds to `DigitalDepositAccount` is an internal capital relocation ($\text{Cash} \rightarrow \text{Digital Deposit}$) and **must never** be recorded as sales revenue or gross profit.
2. **Digital Profit Recognition:** Realized immediately upon successful dispatch as the spread:
   $$\text{Digital Gross Profit} = \text{Selling Price} - \text{Distributor Cost Price}$$
3. **Down Payment Recognition:** Down payments collected on service orders represent unearned revenue / customer deposits until service fulfillment.
4. **Compensating Reversal Invariant:** Failed digital transactions or voided sales must produce traceable compensating mutation records (`REFUND` / `REVERSAL`), never destructive balance overwrites.
5. **Debt Reduction Invariant:** Customer debt payments decrease `CustomerDebt.remainingAmount` and record cash inflows without double-counting historical sales revenue.
6. **No Double-Entry General Ledger:** Full double-entry journal accounting, balance sheets, and chart of accounts remain strictly out of scope.

---

## 7. Multi-Branch Local-First Architecture

Yukelola operates under a **Local-First, Offline-First, and Branch-Isolated** architecture.

```
                         ┌─────────────────────────────────┐
                         │   Branch Local Network (LAN)    │
                         │      (NO INTERNET REQUIRED)     │
                         └───────────────┬─────────────────┘
                                         │
                 ┌───────────────────────┼───────────────────────┐
                 ▼                       ▼                       ▼
      ┌─────────────────────┐ ┌─────────────────────┐ ┌─────────────────────┐
      │  Branch Host Node   │ │ Secondary Cashier 1 │ │ Secondary Cashier 2 │
      │  (Authoritative DB) │ │ (POS Client Tablet) │ │ (POS Client Phone)  │
      └─────────────────────┘ └─────────────────────┘ └─────────────────────┘
```

### Branch Isolation Rules
1. **Branch Data Sovereignty:** Every branch maintains an independent local SQLite/Room database hosted on the primary in-store terminal.
2. **Strict Aggregate Isolation:** Stock, CashRegisters, CustomerDebts, SupplierDebts, DigitalDeposits, and ServiceOrders are 100% isolated to the local branch. Cross-branch direct transactional mutation is prohibited.
3. **Attribution Standard:** Every transactional record must log:
   $$\mathbf{Attribution} = \{\mathbf{businessId},\ \mathbf{branchId},\ \mathbf{userId},\ \mathbf{deviceId},\ \mathbf{cashierSessionId},\ \mathbf{createdAt}\}$$
4. **Master Catalog vs Branch Overrides:** Global product definitions are managed at the Business level; local stock, reorder thresholds, availability, and pricing overrides are managed at the Branch level.
5. **Cross-Branch Reporting:** Consolidated reporting is an offline aggregation and export concern on the Owner console (via Google Drive or local files), not a real-time shared database.

---

## 8. Backup, Recovery & Google Services Policy

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         BACKUP & SAFETY CONTRACT                            │
├────────────────────────────────┬────────────────────────────────────────────┤
│ 1. Operational Database        │ Authoritative Local SQLite / Room DB       │
│ 2. Primary Disaster Backup     │ Encrypted Binary Snapshots (.ykbak)        │
│ 3. Cloud Storage Target        │ Google Drive (App Data / User Storage)     │
│ 4. Local Storage Target        │ SD Card / USB OTG Storage                  │
│ 5. Google Sheets Role          │ OPTIONAL ONE-WAY ANALYTICAL EXPORT ONLY    │
└────────────────────────────────┴────────────────────────────────────────────┘
```

### Safety Rules
1. **Google Sheets Restriction:** Google Sheets is **STRICTLY PROHIBITED** from serving as an operational transaction database or bidirectional sync engine due to fatal risks of data loss, lack of ACID guarantees, schema truncation, and concurrency collisions.
2. **Deterministic Backup:** The authoritative disaster recovery mechanism is an encrypted SQLite binary snapshot (`.ykbak`) backed up to Google Drive or local storage at shift close.

---

## 9. License Architecture & Offline Validation

1. **Licensing Hierarchy:** $\mathbf{License} \longrightarrow \mathbf{Business} \longrightarrow \mathbf{Branches} \longrightarrow \mathbf{Users\ \&\ Devices}$.
2. **Offline Verification:** Validated locally via cryptographically signed tokens.
3. **Data Sovereign Guarantee:** License expiration or network outage **never locks, corrupts, or deletes** local business databases. Historical data and export features remain accessible.

---

## 10. Separation of Concerns: Domain vs. Infrastructure

To maintain clean architectural boundaries, technical implementation details are strictly decoupled from the pure domain model:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      DOMAIN CONTRACT (LOCKED IN v1.2.0)                     │
├─────────────────────────────────────────────────────────────────────────────┤
│ • Entities: Business, Branch, BusinessProfile, Product, Sale, ServiceOrder  │
│ • Aggregates: CashRegister, DigitalDepositAccount, CustomerDebt, Session    │
│ • Invariants: Mathematical formulas, lifecycles, attribution, isolation     │
│ • Transaction Modes: RETAIL_TRANSACTION, DIGITAL_TX, SERVICE_ORDER_TX       │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │ Implemented by
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                 INFRASTRUCTURE & ARCHITECTURE SPECIFICATIONS                │
├─────────────────────────────────────────────────────────────────────────────┤
│ • Room DAOs, SQLite tables, WAL mode, foreign keys, migrations              │
│ • Local Network: mDNS / NSD discovery, local REST/RPC transport             │
│ • Security: AES-256-GCM backup encryption, PIN hashing                      │
│ • Cloud Adapters: Google Drive REST API, Google Sheets export formatting    │
│ • Hardware Drivers: ESC/POS Bluetooth/USB printer, Barcode wedge scanner   │
│ • External PPOB: Third-party provider HTTP adapters and webhook listeners   │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 11. Explicit Out-of-Scope Items

The following remain strictly **OUT OF SCOPE** for the Yukelola core domain:
- Complex multi-branch double-entry journal accounting and balance sheets.
- Multi-warehouse bin management, batch numbering, and expiry date tracking (FEFO/FIFO).
- Multi-tier composite manufacturing bills of materials (BOM).
- Employee payroll and commission calculation engines.
- Direct external PPOB provider SDKs or credentials inside core domain modules.
- Bidirectional database synchronization via spreadsheets.

---

## 12. Version History & Changelog

| Version | Date | Gate | Summary of Key Changes |
|---|---|---|---|
| **1.0.0** | 2026-09-24 | YK-DOMAIN-01 | Initial canonical product & domain contract baseline. |
| **1.1.0** | 2026-09-24 | YK-DOMAIN-01A| Reconciled Business Model vs Capability vs Transaction Mode; added Digital Deposit ledger. |
| **1.2.0** | 2026-09-24 | YK-DOMAIN-05 | **Major Canonical Consolidation:**<br>- Integrated multi-branch hierarchy (`Business` $1:N$ `Branch`).<br>- Scoped `BusinessProfile`, `BusinessModel`, and `Capabilities` to Branch.<br>- Added `RETAIL_HEALTH` (Apotek + Toko Obat) & `WARUNG + OBAT` domain logic.<br>- Added unified `ServiceOrder` aggregate (Laundry, Workshop, Percetakan).<br>- Formalized 3 Transaction Modes (`RETAIL`, `DIGITAL`, `SERVICE_ORDER`).<br>- Added `User`, `Role`, `Device`, and `CashierSession` attribution.<br>- Added Global Master Catalog with `BranchProductOverride`.<br>- Formalized Local-First Branch Isolation and Safe Backup Policy (Google Sheets as export only). |
