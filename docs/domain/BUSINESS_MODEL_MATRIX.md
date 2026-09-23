# Business Model Matrix

> **Document Status:** CANONICAL REFERENCE (REVISION: RETAIL_HEALTH / APOTEK + TOKO OBAT)  
> **Target Scope:** Yukelola Business Model Taxonomy, Operational Archetypes, Capability Mapping, and Cross-Domain Reconciliation.

---

## 1. Core Principle: Business Model vs. Capability

$$\mathbf{Business\ Model} \ne \mathbf{Capability} \ne \mathbf{Product\ Type} \ne \mathbf{Transaction\ Mode}$$

1. **Business Model:** Represents the **PRIMARY operational archetype** and business logic identity of the enterprise. It dictates the default navigational priorities, transaction layout, terminal workflows, and core operational queues.
2. **Capability:** Reusable **functional feature sets** enabled dynamically on top of any primary model (e.g., `DIGITAL_SERVICE`, `CUSTOMER_DEBT`, `INVENTORY`, `FUEL`).
3. **Product Type:** Inventory fulfillment characteristics (`PHYSICAL`, `SERVICE`, `DIGITAL`).
4. **Transaction Mode:** Operational checkout lifecycle (`RETAIL_TRANSACTION`, `DIGITAL_TRANSACTION`, `SERVICE_ORDER_TRANSACTION`).

> **Rule of Identity:** An enterprise does NOT transform its primary Business Model merely because it sells an auxiliary product or service. A warung that sells blister-pack medicine or pulsa remains `RETAIL_WARUNG` with auxiliary product categories and capabilities; it does NOT become `RETAIL_HEALTH` or `DIGITAL_KIOSK`.

---

## 2. RETAIL_HEALTH Domain Architecture: Apotek vs. Toko Obat

### 2.1 Unified Domain Model: `RETAIL_HEALTH`
`APOTEK` and `TOKO_OBAT` share the identical fundamental operational and financial core:
$$\mathbf{RETAIL\_HEALTH} \longrightarrow \begin{cases} \mathbf{APOTEK} & \text{(Profile: Standard OTC + Ethical / Prescription notation context)} \\ \mathbf{TOKO\_OBAT} & \text{(Profile: Standard OTC / General health merchandise)} \end{cases}$$

Both profiles operate via:
- Direct item/SKU retail checkout (`RETAIL_TRANSACTION`).
- Standard inventory tracking (Units: Tablet, Strip, Box, Botol, Tube, Sachet).
- Fast search/barcode scanning.
- Supplier purchase and stock receiving.
- Customer debt and cash drawer management.

### 2.2 Apotek vs. Toko Obat Comparison
| Aspect | TOKO_OBAT Profile | APOTEK Profile | Domain Classification |
|---|---|---|---|
| **Primary Model** | `RETAIL_HEALTH` | `RETAIL_HEALTH` | Shared Domain Core |
| **Product Scope** | OTC Medicines, Supplements, First-Aid | Full Health Portfolio (OTC + Ethical/Prescription) | Shared `Product` Entity with Category distinction |
| **Prescription Metadata** | Not applicable | Optional Prescription Note / Doctor Reference | Non-blocking Transaction Metadata |
| **Inventory Mechanics** | Multi-unit stock (Strip, Box) | Multi-unit stock (Strip, Box) | Shared Multi-Unit Concept |
| **Batch / Expiry** | Out of Scope (Core v1.x) | Out of Scope (Core v1.x) / Future capability | Preserved Contract Boundary |
| **Transaction Flow** | `RETAIL_TRANSACTION` | `RETAIL_TRANSACTION` | Identical Engine |
| **Navigational Priority** | POS, Inventory, Purchases | POS (with Rx tag), Inventory, Purchases | Profile-driven UX Context |

---

## 3. WARUNG + OBAT vs. RETAIL_HEALTH Comparison Matrix

| Aspect | WARUNG + OBAT | RETAIL_HEALTH (Apotek / Toko Obat) |
|---|---|---|
| **Primary Business Model** | `RETAIL_WARUNG` | `RETAIL_HEALTH` |
| **Product Context** | Groceries, snacks, daily staples + auxiliary OTC shelf (e.g. Paracetamol, Tolak Angin) | Dedicated health, therapeutic, wellness, pharmacological inventory |
| **Navigation Hierarchy** | Kasir (Fast Grid/Staples) $\rightarrow$ Produk $\rightarrow$ Hutang $\rightarrow$ Kas | Kasir (Barcode/Drug Search) $\rightarrow$ Obat/Stok $\rightarrow$ Pembelian/Supplier $\rightarrow$ Resep (Opt) |
| **Dashboard Priority** | Daily sales total, cash register, fast moving staples, pulsa widget | Daily sales, low stock alerts on essential medicines, supplier payables |
| **Product Management** | Simple single-unit or basic multi-pack retail item | Strict multi-unit conversion (e.g., 1 Box = 10 Strip = 100 Tablet), dosage form |
| **Stock Tracking** | General physical inventory tracking | Unit-precise inventory tracking with restock thresholds |
| **Purchase & Supplier** | Local general distributor / grosir | Authorized pharmaceutical distributors (PBF) / health distributors |
| **Customer Context** | Local neighborhood patrons, warung credit ledger (kasbon) | Walk-in patients, recurring chronic therapy customers, debt ledger |
| **Supplier Debt Context** | Standard informal supplier tempo | Structured invoice tempo with pharmaceutical distributors |
| **Transaction Mode** | `RETAIL_TRANSACTION` (optional `DIGITAL_TRANSACTION` tab) | `RETAIL_TRANSACTION` (high-speed search + barcode) |
| **Reporting Priority** | Daily cash flow, grocery margins, fast-selling staples | Medicine turnover velocity, inventory valuation, supplier payables |
| **Health-Specific Capability** | None (treated as standard physical product category) | Enabled (`HEALTH_RETAIL_PROFILE`, multi-unit packaging) |
| **Batch / Expiry Implication** | Out of scope; standard item count | Out of scope for core v1.x; future modular extension |
| **Future Upgrade Path** | Toggle `DIGITAL_SERVICE` or `FUEL` capabilities | Upgrade to advanced clinical/batch module when available |

---

## 4. Complete Business Model Matrix (10 Business Contexts)

| Business Context | Primary Business Model | Profile | Primary Objective | Primary Transaction | Secondary Transaction | Capabilities | Product Types | Services | Required Workflow | Required Domain Concepts | Financial Effects | Stock Effects | Customer Context | Supplier Context | Order/Job Requirement | Reporting | UX Context | Peripheral Needs | Offline Requirement |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **1. RETAIL_WARUNG** | `RETAIL_WARUNG` | `WARUNG_KELONTONG` | Fast local grocery & staple retail | `RETAIL_TRANSACTION` | `DIGITAL_TRANSACTION` | `RETAIL`, `INVENTORY`, `PURCHASE`, `CUSTOMER_DEBT`, `SUPPLIER_DEBT`, `CASH`, `DIGITAL_SERVICE`, `FUEL` | `PHYSICAL`, `DIGITAL` | Optional courier/local | Immediate Over-the-Counter POS | `Product`, `Category`, `Sale`, `SaleItem`, `CustomerDebt`, `CashRegister` | Cash inflow, Customer debt on kasbon | Immediate stock decrement on sale completion | Local neighborhood patrons | General wholesalers / grosir | None (Direct POS) | Daily revenue, profit estimation, kasbon balances | Quick grid POS, fast cash tendered buttons | Bluetooth thermal printer, Barcode scanner | Full 100% Offline with Local Sync |
| **2. DIGITAL_KIOSK / KONTER** | `DIGITAL_SERVICE` | `KONTER_PULSA` | Electronic voucher & PPOB distribution | `DIGITAL_TRANSACTION` | `RETAIL_TRANSACTION` | `DIGITAL_SERVICE`, `DIGITAL_DEPOSIT`, `RETAIL`, `INVENTORY`, `CUSTOMER_DEBT` | `DIGITAL`, `PHYSICAL` (Accessories) | None | Target Input $\rightarrow$ Inquiry $\rightarrow$ Deposit Debit $\rightarrow$ Fulfillment | `DigitalTransaction`, `DigitalDepositAccount`, `DigitalDepositMutation`, `Inquiry` | Cash inflow, Digital deposit balance debit | No stock effect for digital; immediate for physical accessories | Mobile subscribers, regular bill payers | PPOB Providers, Distributor Pulsa | None (Instant Operator Flow) | Digital transaction volume, margin analysis, deposit reconciliation | Keypad-first entry, pinned deposit balance, quick denomination chips | Thermal receipt printer | Offline queue for draft/inquiry; online for provider settlement |
| **3. FOOD_BEVERAGE_CAFE** | `FOOD_BEVERAGE` | `WARUNG_MAKAN_CAFE` | Dine-in & takeaway food/drink service | `RETAIL_TRANSACTION` | None | `RETAIL`, `INVENTORY`, `PURCHASE`, `CUSTOMER_DEBT`, `CASH` | `PHYSICAL` (Prepared / Packaged) | Custom preparation | Menu selection $\rightarrow$ Table/Dine-in note $\rightarrow$ Instant checkout / Tab | `Product` (Menu), `Category`, `Sale` (with table notes) | Cash/QRIS inflow, food expense | Immediate decrement of finished drinks/dishes or tracked items | Walk-in diners, table tabs | Ingredient suppliers / wet market | Optional Table Order note (no complex ERP) | Sales by category, peak hour volume | Visual menu grid, category tabs, dine-in/takeaway toggles | Thermal receipt & kitchen slip printer | Full 100% Offline |
| **4. SERVICE_WORKSHOP / BENGKEL** | `SERVICE_WORKSHOP` | `BENGKEL_MOTOR_MOBIL` | Vehicle maintenance, repair, and spare parts | `SERVICE_ORDER_TRANSACTION` | `RETAIL_TRANSACTION` | `SERVICE`, `RETAIL`, `INVENTORY`, `PURCHASE`, `CUSTOMER_DEBT`, `SUPPLIER_DEBT` | `SERVICE` (Labor), `PHYSICAL` (Spare parts) | Mechanical repair, tune-up, oil change | Intake $\rightarrow$ Inspection $\rightarrow$ Progress $\rightarrow$ Ready $\rightarrow$ Settlement | `ServiceOrder`, `ServiceOrderItem`, `VehicleContext`, `Product`, `Stock` | Labor revenue + spare part margin, down payment, final balance | Spare parts deducted upon job completion / status advance | Vehicle owner, license plate tracking | Spare part distributors | Required (`ServiceOrder` aggregate) | Service labor vs parts margin, technician throughput | Queue status board, vehicle plate intake sheet | Thermal receipt & Job ticket printer | Full 100% Offline |
| **5. LAUNDRY** | `SERVICE` | `LAUNDRY_KILOAN_SATUAN` | Asynchronous garment washing and care | `SERVICE_ORDER_TRANSACTION` | `RETAIL_TRANSACTION` | `SERVICE`, `RETAIL`, `CUSTOMER_DEBT`, `CASH` | `SERVICE` (Kiloan, Satuan), `PHYSICAL` (Detergent/Perfume) | Washing, ironing, dry cleaning | Drop-off/Weigh $\rightarrow$ Washing $\rightarrow$ Ready $\rightarrow$ Pickup & Settle | `ServiceOrder`, `LaundrySpecs` (Weight, Satuan, Perfume, Express), `DownPayment` | Down payment inflow, remaining balance upon pickup | Consumables untracked; retail detergent deducted if sold | Registered pickup customer, phone number | Detergent/chemical suppliers | Required (`ServiceOrder` aggregate) | Unclaimed orders, daily wash weight (Kg), cash intake | Weight intake keypad, visual status lane (Received/Ready/Picked Up) | Thermal tag printer, Bluetooth receipt printer | Full 100% Offline |
| **6. RETAIL_HEALTH (APOTEK / TOKO OBAT)** | `RETAIL_HEALTH` | `APOTEK` / `TOKO_OBAT` | Health, medicine, and wellness product retail | `RETAIL_TRANSACTION` | None | `RETAIL`, `INVENTORY`, `PURCHASE`, `CUSTOMER_DEBT`, `SUPPLIER_DEBT`, `CASH` | `PHYSICAL` (Medicine, Medical Supplies) | None | Search/Scan SKU $\rightarrow$ Select Unit $\rightarrow$ Settle $\rightarrow$ Print Receipt | `Product` (Medicine), `Category`, `Unit` (Strip/Box), `Sale`, `Supplier` | Cash/QRIS inflow, supplier payable | Multi-unit stock decrement upon sale completion | Patient / customer name, credit ledger | Pharmaceutical distributors (PBF) | None (Direct POS) | Medicine sales volume, inventory turnover, low-stock warnings | Fast SKU/Barcode search bar, multi-unit price picker, stock counter | Barcode scanner, thermal receipt printer | Full 100% Offline |
| **7. PERCETAKAN / PRINTING** | `SERVICE` | `PERCETAKAN_DIGITAL_OFFSET` | Custom digital/offset document printing | `SERVICE_ORDER_TRANSACTION` | `RETAIL_TRANSACTION` | `SERVICE`, `RETAIL`, `INVENTORY`, `PURCHASE`, `CUSTOMER_DEBT` | `SERVICE` (Print Job), `PHYSICAL` (Paper/Banner media) | Large format, banner, document printing | File intake $\rightarrow$ Spec input $\rightarrow$ Proof/Production $\rightarrow$ Ready $\rightarrow$ Pickup | `ServiceOrder`, `PrintSpecs` (File, Dims, Material, Finishing), `DownPayment` | Down payment, job expense, final collection | Raw material / banner media deducted if tracked | Print client, business account | Paper & ink suppliers | Required (`ServiceOrder` aggregate) | Jobs in production, overdue pickups, revenue by finish type | Spec intake builder, job timeline tracker | Thermal receipt & Job spec sheet printer | Full 100% Offline |
| **8. FOTOCOPY** | `RETAIL` | `FOTOCOPY_JILID` | Fast on-demand document copy and binding | `RETAIL_TRANSACTION` | `SERVICE_ORDER_TRANSACTION` (Bulk) | `RETAIL`, `SERVICE`, `INVENTORY`, `CUSTOMER_DEBT` | `SERVICE` (Per-page copy), `PHYSICAL` (Paper, Covers, ATK) | Copying, laminating, spiral binding | Counter per-page count $\rightarrow$ Quick calculation $\rightarrow$ Instant checkout | `Product` (Copy service per page, binding), `Sale`, `ServiceOrder` (Bulk) | Immediate cash inflow | Paper stock decremented if tracked | Walk-in students, office workers | Paper distributors | None for fast copy; `ServiceOrder` for bulk jobs | Daily page copy volume, paper consumption | Page $\times$ Price rapid counter, quick size chips (A4/F4) | Thermal receipt printer | Full 100% Offline |
| **9. ATK (Stationery)** | `RETAIL` | `TOKO_ATK` | Office and school stationery retail | `RETAIL_TRANSACTION` | None | `RETAIL`, `INVENTORY`, `PURCHASE`, `CUSTOMER_DEBT`, `SUPPLIER_DEBT` | `PHYSICAL` (Pens, Books, Paper, Supplies) | None | Barcode scan $\rightarrow$ Unit selection $\rightarrow$ Instant checkout | `Product` (Stationery), `Category`, `Barcode`, `Unit` (Pcs, Pack, Lusin) | Immediate cash/digital settlement | Multi-unit immediate stock decrement | Walk-in patrons, institutional buyers | Stationery wholesalers | None (Direct POS) | Product category turnover, dead stock analysis | Barcode-first POS, rapid quantity multipliers | USB/Bluetooth barcode scanner, receipt printer | Full 100% Offline |
| **10. GENERAL_STORE** | `RETAIL` | `TOKO_SERBAGUNA` | Multi-category commercial merchandise | `RETAIL_TRANSACTION` | `DIGITAL_TRANSACTION` (Opt) | Modular (Full Retail + Optional Services) | `PHYSICAL`, `DIGITAL` (Opt) | Optional | Scan/Select $\rightarrow$ Settle $\rightarrow$ Receipt | `Product`, `Category`, `Sale`, `Customer`, `Supplier` | Multi-category commercial revenue | Standard physical stock decrement | General retail customers | Multi-category suppliers | None (Direct POS) | Multi-category margin reports, cash reconciliation | Adaptive category navigation, generalized dashboard | Scanner, receipt printer | Full 100% Offline |

---

## 5. Domain Classification and Reusability Architecture

1. **Retail Archetype Consolidation:**
   - `RETAIL_WARUNG`, `RETAIL_HEALTH` (`APOTEK` / `TOKO_OBAT`), `ATK`, and `GENERAL_STORE` share the canonical `RETAIL_TRANSACTION` engine and core inventory entities.
   - Distinct operational identities are achieved through **Business Profiles** and contextual UX navigation without duplicating underlying database schemas.
2. **Asynchronous Service Archetype Consolidation:**
   - `LAUNDRY`, `SERVICE_WORKSHOP`, and `PERCETAKAN` share the unified `ServiceOrder` aggregate (`SERVICE_ORDER_TRANSACTION`).
   - Domain-specific details (Vehicle plates, Laundry weights, Print dimensions) are encapsulated in extensible `ContextMetadata`.
3. **Digital Service Isolation:**
   - `DIGITAL_SERVICE` transactions remain strictly segregated from physical retail baskets, governed by `DigitalDepositAccount` and read-only inquiry mechanics.
