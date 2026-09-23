# Yukelola Product & Domain Contract

> **Contract Version:** 1.1.0
> **Status:** LOCKED / CANONICAL SOURCE OF TRUTH
> **Canonical Identity:** `id.yukelola`
> **Target Scope:** Android Modular Monolith Core Domain, Room Database Entities, Repository Contracts, UseCases, Future Backend Services.

---

## 1. Product Vision & Boundary

Yukelola is a streamlined, transaction-oriented business management application designed for Indonesian micro, small, and medium enterprises (UMKM).

### Core Philosophy
- **Simple & Practical:** Fast, intuitive, and accessible to business owners with varying levels of financial and technical literacy.
- **Mobile-First & Offline-First:** Core business operations (sales, purchases, cash drawer management, debts, inventory) operate reliably without an active internet connection.
- **Transaction-Centric:** Driven by real-world transactions rather than complex double-entry accounting ledgers or full-scale enterprise ERP systems.
- **Robust Domain Core:** Business rules, capabilities, and invariants are encapsulated in pure Kotlin domain models (`:core:domain`), independent of UI frameworks, database engines, or external services.

---

## 2. Domain Glossary

All domain code, interfaces, repository contracts, and database schemas **must** adhere strictly to the English business terminology defined below. Indonesian terms are reserved exclusively for UI presentation and localization strings.

| Canonical Domain Term | Meaning & Purpose | UI / Localized Synonym (ID) |
|---|---|---|
| **Business** | Aggregate root representing the tenant or enterprise entity. | Usaha / Bisnis |
| **BusinessProfile** | Metadata and operational attributes of a business (name, contact, receipt header). | Profil Usaha |
| **BusinessModel** | Primary operational identity and business logic profile of the enterprise. | Model Bisnis / Jenis Usaha |
| **Capability** | Specific functional business module enabled for a business profile. | Fitur / Kemampuan Usaha |
| **Product** | Any sellable unit of goods, services, or digital items. | Produk / Barang |
| **ProductType** | Classification of goods fulfillment behavior (`PHYSICAL`, `SERVICE`, `DIGITAL`). | Jenis Produk |
| **Category** | Organizational grouping for products. | Kategori |
| **Unit** | Measurement unit for physical goods (e.g., PCS, KG, PACK, LITER). | Satuan |
| **Stock** | Available on-hand quantity for physical goods. | Stok / Persediaan |
| **StockAdjustment** | Manual inventory count correction (opname, damage, shrinkage). | Penyesuaian Stok |
| **Sale** | Record of a customer transaction, selling products for monetary compensation. | Penjualan / Transaksi |
| **SaleItem** | Line item in a sale referencing product, quantity, unit price, and discounts. | Item Penjualan |
| **Purchase** | Record of inventory acquisition from a supplier. | Pembelian / Kulakan |
| **PurchaseItem** | Line item in a purchase record with quantity and cost price. | Item Pembelian |
| **Payment** | Financial settlement event applied to a Sale, Purchase, or Debt. | Pembayaran |
| **Discount** | Monetary or percentage reduction applied to an item or entire transaction. | Diskon / Potongan |
| **Customer** | External entity purchasing goods or services, with potential debt balance. | Pelanggan |
| **Supplier** | External vendor supplying goods or materials, with potential payable balance. | Pemasok / Supplier |
| **CustomerDebt** | Outstanding receivable owed by a customer to the business (Piutang). | Piutang Pelanggan / Kasbon |
| **SupplierDebt** | Outstanding payable owed by the business to a supplier (Hutang). | Hutang Pemasok |
| **DebtPayment** | Financial event reducing an outstanding customer or supplier debt. | Pembayaran Hutang / Piutang |
| **CashRegister** | Cash drawer / register session tracking physical cash inflows, outflows, and balance. | Buku Kas / Kasir |
| **CashMutation** | Non-sale / non-purchase cash adjustment (cash in, cash out, expense). | Mutasi Kas / Pengeluaran |
| **DigitalDepositAccount** | Dedicated agent deposit balance used exclusively for digital product fulfillment. | Saldo Deposit Digital |
| **DigitalDepositMutation** | Traceable historical movement of digital agent deposit funds. | Mutasi Deposit Digital |
| **DigitalTransaction** | Dedicated transaction record for electronic vouchers, tokens, and bill payments. | Transaksi Digital |
| **Inquiry** | Read-only validation and bill retrieval check prior to digital settlement. | Cek Tagihan / Cek Nomor |
| **License** | Software entitlement model granting application features and validity. | Lisensi Aplikasi |

---

## 3. Entity Catalog

```
                    ┌─────────────────────────┐
                    │        Business         │
                    └────────────┬────────────┘
                                 │ 1:1
                    ┌────────────▼────────────┐
                    │     BusinessProfile     │
                    │  (BusinessModel, etc.)  │
                    └────────────┬────────────┘
                                 │
     ┌────────────────┬──────────┴─────────┬────────────────┬────────────────────────┐
     │ 1:*            │ 1:*                 │ 1:*            │ 1:*                    │ 1:1
┌────▼──────┐   ┌─────▼───────┐      ┌─────▼──────┐   ┌─────▼──────┐      ┌───────────▼───────────┐
│  Category │   │   Product   │      │  Customer  │   │  Supplier  │      │ DigitalDepositAccount │
└───────────┘   └─────┬───────┘      └─────┬──────┘   └─────┬──────┘      └───────────┬───────────┘
                      │                    │                │                         │ 1:*
                      │ 1:* (SaleItem)     │ 0..1           │ 0..1                    │
                ┌─────▼────────────────────▼─────┐    ┌─────▼──────────┐              ▼
                │              Sale              │    │    Purchase    │    ┌───────────────────┐
                ├────────────────────────────────┤    ├────────────────┤    │   DigitalDeposit  │
                │ - SaleItems                    │    │ - PurchaseItems│    │      Mutation     │
                │ - Payments                     │    │ - Payments     │    └───────────────────┘
                │ - CustomerDebt (if credit)     │    │ - SupplierDebt │
                └──────────────┬─────────────────┘    └─────┬──────────┘
                               │                            │
                               └───────────┬────────────────┘
                                           │ Impacts
                               ┌───────────▼────────────┐
                               │      CashRegister      │
                               │  - Inflow / Outflow    │
                               └────────────────────────┘
```

### Entity Definitions & Identifiers
1. **Business**: Aggregate Root. Identifies the primary business context (`id`, `createdAt`, `isActive`).
2. **BusinessProfile**: Profile entity (`businessId`, `name`, `businessModel`, `enabledCapabilities`, `phone`, `address`, `receiptFooter`, `currency`, `timezone`).
3. **Category**: Organizational taxonomy (`id`, `businessId`, `name`, `color`, `icon`, `sortOrder`).
4. **Product**: Sellable catalog item (`id`, `businessId`, `categoryId`, `sku`, `name`, `productType`, `unit`, `purchasePrice`, `sellingPrice`, `stock`, `minStock`, `trackStock`, `isActive`).
5. **Customer**: Buyer profile (`id`, `businessId`, `name`, `phone`, `email`, `address`, `debtBalance`, `notes`, `isActive`).
6. **Supplier**: Vendor profile (`id`, `businessId`, `name`, `contactPerson`, `phone`, `address`, `debtBalance`, `isActive`).
7. **Sale**: Sale transaction aggregate (`id`, `businessId`, `saleNumber`, `transactionMode`, `customerId`, `subtotal`, `discountAmount`, `taxAmount`, `totalAmount`, `paidAmount`, `paymentStatus`, `fulfillmentStatus`, `notes`, `createdAt`).
8. **SaleItem**: Immutable snapshot line (`id`, `saleId`, `productId`, `productName`, `unit`, `unitPrice`, `costPrice`, `quantity`, `discountAmount`, `subtotal`).
9. **Purchase**: Inventory order aggregate (`id`, `businessId`, `purchaseNumber`, `supplierId`, `totalAmount`, `paidAmount`, `paymentStatus`, `createdAt`).
10. **PurchaseItem**: Line item (`id`, `purchaseId`, `productId`, `productName`, `unitCost`, `quantity`, `subtotal`).
11. **Payment**: Payment execution (`id`, `businessId`, `transactionType` [SALE/PURCHASE/DEBT/DIGITAL], `referenceId`, `paymentMethod`, `amount`, `createdAt`).
12. **CustomerDebt**: Receivable tracking (`id`, `businessId`, `customerId`, `saleId`, `originalAmount`, `remainingAmount`, `status`, `dueDate`, `createdAt`).
13. **SupplierDebt**: Payable tracking (`id`, `businessId`, `supplierId`, `purchaseId`, `originalAmount`, `remainingAmount`, `status`, `dueDate`, `createdAt`).
14. **DebtPayment**: Debt settlement record (`id`, `businessId`, `debtType` [CUSTOMER/SUPPLIER], `debtId`, `amount`, `paymentMethod`, `notes`, `createdAt`).
15. **CashRegister**: Physical cash drawer manager (`id`, `businessId`, `name`, `currentBalance`, `updatedAt`).
16. **CashMutation**: Cash inflow/outflow record (`id`, `businessId`, `registerId`, `mutationType` [INFLOW/OUTFLOW], `category`, `amount`, `source`, `referenceId`, `notes`, `createdAt`).
17. **StockAdjustment**: Inventory reconciliation record (`id`, `businessId`, `productId`, `adjustmentType` [ADD/SUBTRACT/SET], `quantity`, `reason`, `createdAt`).
18. **DigitalDepositAccount**: Dedicated digital agent balance (`id`, `businessId`, `currentBalance`, `currency`, `updatedAt`).
19. **DigitalDepositMutation**: Traceable deposit history (`id`, `businessId`, `accountId`, `mutationType` [TOP_UP/DIGITAL_SALE/REFUND/REVERSAL/ADJUSTMENT], `amount`, `balanceBefore`, `balanceAfter`, `referenceId`, `notes`, `createdAt`).
20. **DigitalTransaction**: Electronic transaction record (`id`, `businessId`, `saleId`, `targetNumber`, `productCode`, `denomination`, `costPrice`, `sellingPrice`, `depositMutationId`, `fulfillmentStatus` [INITIATED/PENDING/SUCCESS/FAILED/REVERSED], `providerReference`, `failureReason`, `createdAt`, `updatedAt`).

---

## 4. Entity Responsibilities & Boundaries

- **Business & Profile:** Encapsulate enterprise identity, primary business model, and enabled capabilities. They govern which modules and transaction modes are active.
- **Product:** Manages catalog data, pricing, and active status. Physical stock count is kept consistent via transaction mutations.
- **Sale & Purchase:** Serve as immutable historic commercial records. Once completed, line items reflect the exact prices and names at the moment of execution.
- **Debts (Customer / Supplier):** Explicitly decouple unpaid credit obligations from completed transaction records. A credit sale is completed from a sales perspective, and the outstanding balance is tracked in the CustomerDebt ledger.
- **CashRegister:** Records physical drawer movements. It does not calculate accounting profits, only physical cash flows and drawer reconciliations.
- **DigitalDepositAccount:** Manages electronic agent fulfillment funds. Strictly isolated from physical cash drawer funds.
- **DigitalTransaction:** Encapsulates the multi-step lifecycle, target destination, and fulfillment states of digital products.

---

## 5. Relationships & Cardinality

| Source Entity | Cardinality | Target Entity | Relationship Semantics |
|---|---|---|---|
| `Business` | `1 : 1` | `BusinessProfile` | Strict ownership, created on onboarding. |
| `Business` | `1 : 1` | `DigitalDepositAccount` | Optional/Created when `DIGITAL_DEPOSIT` capability is enabled. |
| `Business` | `1 : *` | `Category` | Categorization taxonomy owned by business. |
| `Business` | `1 : *` | `Product` | Products belong to one business tenant. |
| `Business` | `1 : *` | `Customer` | Customer list scoped per business. |
| `Business` | `1 : *` | `Supplier` | Supplier contacts scoped per business. |
| `Business` | `1 : *` | `Sale` | Sales history scoped per business. |
| `Sale` | `1 : *` | `SaleItem` | Cascade lifecycle; items belong exclusively to parent Sale. |
| `Sale` | `0..1 : 1` | `Customer` | Optional; guest sales have null customer. |
| `Sale` | `1 : *` | `Payment` | One sale can have multiple payments (split/installments). |
| `Sale` | `1 : 0..1` | `CustomerDebt` | Created if `paidAmount < totalAmount` and Customer is present. |
| `Sale` | `1 : 0..1` | `DigitalTransaction` | 1:1 linkage when transactionMode = `DIGITAL_TRANSACTION`. |
| `Purchase` | `1 : *` | `PurchaseItem` | Items belong exclusively to parent Purchase. |
| `Purchase` | `0..1 : 1` | `Supplier` | Optional or specified vendor. |
| `Purchase` | `1 : 0..1` | `SupplierDebt` | Created if `paidAmount < totalAmount` on credit purchase. |
| `Customer` | `1 : *` | `CustomerDebt` | Aggregate receivable balance for a customer. |
| `Supplier` | `1 : *` | `SupplierDebt` | Aggregate payable balance for a supplier. |
| `CashRegister`| `1 : *` | `CashMutation` | Historical audit log of cash movements. |
| `DigitalDepositAccount` | `1 : *` | `DigitalDepositMutation` | Historical audit log of deposit movements. |

---

## 6. Ownership & Aggregate Boundaries

1. **Catalog Aggregate:** Root = `Product`. Contains pricing, stock limits, and measurement units.
2. **Sales Aggregate:** Root = `Sale`. Contains `SaleItem` list, discount specifications, attached `Payment` records, and optional `DigitalTransaction` linkage.
3. **Purchase Aggregate:** Root = `Purchase`. Contains `PurchaseItem` list and initial purchase payments.
4. **Party Aggregates:** `Customer` and `Supplier` manage contact details and aggregate credit/payable balances.
5. **Cash Aggregate:** `CashRegister` manages cash on hand and serializes `CashMutation` records.
6. **Digital Deposit Aggregate:** `DigitalDepositAccount` manages agent balance and serializes `DigitalDepositMutation` records.

---

## 7. Lifecycle States

### Sale Lifecycle
```
[DRAFT] ──(Confirm & Pay)──► [COMPLETED] ──(Void/Cancel)──► [CANCELLED]
```
- `DRAFT`: Active cart / pending order.
- `COMPLETED`: Inventory decremented, cash recorded, customer debt recorded if unpaid balance exists.
- `CANCELLED`: Only permitted under strict void/reversal rules; reverts stock and cash effects.

### Digital Transaction Lifecycle
```
[INITIATED] ──(Submit to Provider)──► [PENDING] ──(Provider Success)──► [SUCCESS]
                                         │
                                         └──(Provider Fail)──► [FAILED] ──(Auto-Refund)──► [REVERSED]
```
- `INITIATED`: Target verified, deposit debited/reserved, awaiting fulfillment dispatch.
- `PENDING`: Dispatched to network/provider; awaiting terminal status.
- `SUCCESS`: Confirmed by provider; deposit debit finalized, sale complete.
- `FAILED`: Provider returned failure or timeout expired.
- `REVERSED`: Deposit restored to `DigitalDepositAccount` via explicit `REVERSAL` / `REFUND` mutation.

### Inquiry Lifecycle (Bill / Number Check)
```
[INITIATED] ──(Query Provider/Cache)──► [SUCCESS] ──(TTL Expired)──► [EXPIRED]
                   │
                   └──(Query Error)──► [FAILED]
```
- **Inquiry Invariant:** An Inquiry MUST NEVER debit cash, debit digital deposit, create a completed sale, or trigger provider fulfillment. It is strictly read-only validation data.

### Debt Lifecycle (Customer & Supplier)
```
[UNPAID] ──(Partial Payment)──► [PARTIALLY_PAID] ──(Full Settlement)──► [SETTLED]
```

---

## 8. Product Types & Fulfillment Semantics

Product Type defines **how a product is fulfilled physically or electronically**.

| Product Type | Inventory Tracking | Fulfillment Behavior | Example Items |
|---|---|---|---|
| **PHYSICAL** | Tracked (`stock` decrements on sale, increments on purchase). Reorder alert on `stock <= minStock`. | Physical handover of goods across counter. | Beras, Minyak Goreng, Snack, Rokok, Bensin Eceran, Minuman Dingin. |
| **SERVICE** | Untracked (`trackStock = false`). Stock quantity is not decremented. | Service execution / labor provided. | Jasa Jahit, Potong Rambut, Servis Motor, Cuci Mobil. |
| **DIGITAL** | Untracked physical inventory. Unit price, purchase cost, and digital deposit tracked. | Digital token, electronic top-up, serial code, voucher. | Pulsa Telkomsel, Token Listrik PLN, Topup DANA/GoPay, Voucher Game. |

---

## 9. Business Models (Primary Business Profiles)

Business Model defines the **primary operational identity and business logic profile** of the enterprise.

$$\text{Business Model} \ne \text{Product Type} \ne \text{Capability} \ne \text{Transaction Mode}$$

1. **`RETAIL_WARUNG` (Retail-First):**
   - *Primary Model:* `RETAIL`
   - *Operational Focus:* Fast over-the-counter sales of physical goods, inventory tracking, restocking, customer kasbon / debts.
   - *Secondary Extensions:* Can enable optional capabilities (e.g., `DIGITAL_SERVICE`, `FUEL`) without turning the business into a Konter.
2. **`DIGITAL_KIOSK` / `KONTER` (Digital-First):**
   - *Primary Model:* `DIGITAL_SERVICE`
   - *Operational Focus:* Fast mobile credit, data packages, electricity tokens, e-wallet top-ups, digital deposit management.
   - *Secondary Extensions:* Can optionally sell physical accessories (cases, chargers, cables) via secondary retail capability.
3. **`FOOD_BEVERAGE_CAFE` (F&B-First):**
   - *Primary Model:* `FOOD_BEVERAGE`
   - *Operational Focus:* Menu-oriented items, fast dine-in/takeaway ordering, optional table/seat notes, kitchen prep grouping.
4. **`SERVICE_WORKSHOP` (Service-First):**
   - *Primary Model:* `SERVICE`
   - *Operational Focus:* Labor/service fees, repair orders, customer asset/vehicle notes, physical spare parts sales.
5. **`GENERAL_STORE` (Mixed Retail):**
   - *Primary Model:* `MIXED_RETAIL`
   - *Operational Focus:* Multi-category general trade without a single mandatory primary workflow.

---

## 10. Capability Model

Capabilities represent **functional modules** that can be enabled or disabled for a business profile.

| Capability | Description & Business Value | Required Entities / Dependencies |
|---|---|---|
| `RETAIL` | Standard barcode scanning, cart management, and retail receipt generation. | `Product` (PHYSICAL), `SaleItem` |
| `INVENTORY` | Tracking stock levels, low-stock alerts, and manual adjustments. | `Product.stock`, `StockAdjustment` |
| `PURCHASE` | Recording supplier stock replenishment and cost prices. | `Purchase`, `PurchaseItem`, `Supplier` |
| `DIGITAL_SERVICE` | Dedicated UI and workflow for selling digital goods and bill payments. | `DigitalTransaction`, `Product` (DIGITAL) |
| `DIGITAL_DEPOSIT` | Dedicated balance tracking and mutation history for digital agent funds. | `DigitalDepositAccount`, `DigitalDepositMutation` |
| `PPOB` | Automated connectivity to external digital product aggregators/providers. | Future provider adapter layer |
| `FUEL` | Specialized unit pricing for retail fuel (Bensin Eceran / Pertalite). | `Product` (PHYSICAL, unit: LITER) |
| `FOOD_BEVERAGE` | Kitchen notes, dine-in/takeaway tagging, menu grouping. | `Sale.notes`, `Category` |
| `SERVICE` | Work order recording, labor fee tracking, service duration/status. | `Product` (SERVICE) |
| `CUSTOMER_DEBT` | Customer credit book (Catatan Kasbon Pelanggan). | `Customer`, `CustomerDebt`, `DebtPayment` |
| `SUPPLIER_DEBT` | Supplier payable book (Catatan Hutang Kulakan). | `Supplier`, `SupplierDebt`, `DebtPayment` |
| `CASH` | Cash drawer management, daily opening/closing balance, expense tracking. | `CashRegister`, `CashMutation` |
| `REPORTING` | Historical transaction summaries, gross profit projections, cash book. | Projection read-models |

---

## 11. Business Type → Logic Contract

Selecting a Business Type configures the **default business logic profile and capability matrix**, not merely superficial labels.

### Logic Profiles by Business Type
- **WARUNG (Standard):**
  - *Primary Transaction Mode:* `RETAIL_TRANSACTION`
  - *Default Capabilities:* `RETAIL`, `INVENTORY`, `PURCHASE`, `CASH`, `CUSTOMER_DEBT`, `SUPPLIER_DEBT`, `REPORTING`.
- **WARUNG + DIGITAL SERVICE:**
  - *Primary Transaction Mode:* `RETAIL_TRANSACTION` (Home/POS defaults to retail goods).
  - *Secondary Transaction Mode:* `DIGITAL_TRANSACTION` (Dedicated "Layanan Digital" tab/section).
  - *Enabled Capabilities:* Standard Warung + `DIGITAL_SERVICE` + `DIGITAL_DEPOSIT`.
  - *Logic Rule:* The business remains a Warung; retail POS and digital transactions remain separate operational experiences.
- **WARUNG + BENSIN:**
  - *Primary Transaction Mode:* `RETAIL_TRANSACTION`.
  - *Enabled Capabilities:* Standard Warung + `FUEL`.
  - *Logic Rule:* Fuel is treated as a physical inventory item with decimal/liter measurements, not a distinct business model.
- **KONTER (Digital Kiosk):**
  - *Primary Transaction Mode:* `DIGITAL_TRANSACTION` (Home/POS opens directly to digital phone/token entry).
  - *Secondary Transaction Mode:* `RETAIL_TRANSACTION` (Optional "Aksesoris / Barang Fisik" section).
  - *Enabled Capabilities:* `DIGITAL_SERVICE`, `DIGITAL_DEPOSIT`, `CASH`, `CUSTOMER_DEBT`, `REPORTING`, optional `RETAIL`.

---

## 12. Business Type → UX Context Contract

The UI is a presentation of underlying domain capabilities. Selecting a Business Type influences:

1. **Default Terminology:** Adapts localized copy (e.g., "Menu" in Cafe vs "Barang" in Warung vs "Layanan" in Konter).
2. **Dashboard & Navigation Hierarchy:**
   - In `RETAIL_WARUNG`: Primary tab = Retail POS. Digital services appear under a secondary "Layanan Digital" hub.
   - In `DIGITAL_KIOSK`: Primary tab = Digital Transaction keypad & quick provider buttons. Physical items appear under a secondary catalog.
3. **Retail vs Digital UX Separation:**
   - Digital products (pulsa, token, e-wallet) **must never** be dumped into the standard physical product grid.
   - Digital sales require destination number input, operator auto-detection, denomination picker, and inquiry confirmation.
4. **Digital Deposit Visibility:** The digital deposit widget and balance banner only render when `DIGITAL_DEPOSIT` capability is active.
5. **Contextual Form Fields:** Dynamic display of specialized fields (e.g., Table Number for Cafe, Vehicle/Police Plate for Workshop).

---

## 13. Retail Transaction Contract

### Flow
```
Product Catalog / Barcode Scan
  └──► Active Cart (SaleItems)
         └──► Discount / Customer Selection
                └──► Checkout (Payment: Cash / Transfer / Debt)
                       ├──► Stock Decrement (for PHYSICAL items)
                       ├──► Cash Register Inflow (for CASH payments)
                       └──► Customer Debt Creation (if unpaid balance)
```

### Financial & Stock Effects
- `Product.stock` decreases by sold quantity.
- `CashRegister` increases by cash paid.
- `CustomerDebt` increases if `paidAmount < totalAmount`.
- `DigitalDepositAccount` is **UNTOUCHED**.

---

## 14. Digital Transaction Contract

### Flow
```
Digital Services Hub
  └──► Select Service Category (Pulsa / Token PLN / E-Wallet)
         └──► Enter Target Number (Phone / Meter No / Account)
                └──► [Optional] Inquiry Validation Check
                       └──► Select Denomination / Product
                              └──► Customer Payment Settlement
                                     └──► Reserve / Debit Digital Deposit
                                            └──► Provider Fulfillment Dispatch
                                                   ├──► SUCCESS: Finalize transaction
                                                   └──► FAILED: Trigger deterministic Reversal / Refund
```

### Financial & Deposit Effects
- `Customer Payment:` Increases `CashRegister` by selling price (if paid in cash).
- `Digital Fulfillment:` Decreases `DigitalDepositAccount` by product `costPrice`.
- `Gross Margin:` Immediate profit realization = $\text{Selling Price} - \text{Cost Price}$.
- `Product.stock` is **UNTOUCHED** (digital goods have no physical warehouse count).

---

## 15. Digital Deposit (Deposit Digital)

The digital agent deposit represents working capital held with digital distributors/aggregators to fulfill electronic transactions.

### Domain Entities
- **`DigitalDepositAccount`:** Single aggregate per business holding `currentBalance`.
- **`DigitalDepositMutation`:** Immutable log of all balance changes.

### Supported Mutation Types
1. `TOP_UP`: Adding working capital to the digital deposit balance.
2. `DIGITAL_SALE`: Debiting deposit to fulfill a completed digital product sale.
3. `REFUND`: Returning debited deposit to account following a failed transaction.
4. `REVERSAL`: Administrative correction of an errant transaction.
5. `ADJUSTMENT`: Manual balance correction with recorded justification.

### Business Use Cases (Conceptual)
- `GetDigitalDepositBalance(businessId)`
- `TopUpDigitalDeposit(businessId, amount, paymentSource)`
- `GetDigitalDepositMutations(businessId, dateRange)`
- `ReserveDigitalDeposit(businessId, transactionId, amount)`
- `CompleteDigitalTransaction(businessId, transactionId)`
- `FailDigitalTransaction(businessId, transactionId, reason)`
- `ReverseDigitalTransaction(businessId, transactionId)`

---

## 16. Cash vs. Digital Deposit Separation

Physical cash and digital agent deposits represent **distinct, non-interchangeable financial assets**. They MUST NOT be combined into a single balance.

| Property | CashRegister | DigitalDepositAccount |
|---|---|---|
| **Physical Reality** | Physical bank notes & coins in the drawer. | Electronic balance held with digital provider/aggregator. |
| **Primary Inflow** | Customer cash payments, debt settlements, owner capital in. | Deposit Top-Up transfers. |
| **Primary Outflow** | Cash purchases, operational expenses, owner cash drawings. | Digital product fulfillments (pulsa, token, e-wallet). |
| **Impact of Digital Sale** | **Increases** by customer selling price (cash paid). | **Decreases** by distributor cost price (debited balance). |
| **Impact of Deposit Top-Up** | **Decreases** by top-up amount (cash used to buy deposit). | **Increases** by top-up amount (credited balance). |

### Traceable Example Flow
1. **Initial State:** Cash = Rp 500.000 | Deposit = Rp 200.000
2. **Action: Top Up Deposit Rp 100.000 using cash:**
   - Cash: $\text{Rp } 500.000 - \text{Rp } 100.000 = \mathbf{Rp\ 400.000}$ (recorded as `CashMutation.OUTFLOW`)
   - Deposit: $\text{Rp } 200.000 + \text{Rp } 100.000 = \mathbf{Rp\ 300.000}$ (recorded as `DigitalDepositMutation.TOP_UP`)
3. **Action: Sell Token PLN Rp 20.000 (Cost = Rp 20.200, Sell = Rp 23.000, Customer pays Cash):**
   - Customer pays cash: Cash increases by Rp 23.000 $\rightarrow \mathbf{Rp\ 423.000}$ (`Sale.paidAmount`)
   - Provider debits deposit: Deposit decreases by Rp 20.200 $\rightarrow \mathbf{Rp\ 279.800}$ (`DigitalDepositMutation.DIGITAL_SALE`)
   - Net profit earned = $\text{Rp } 23.000 - \text{Rp } 20.200 = \mathbf{Rp\ 2.800}$.
4. **Final State:** Cash = Rp 423.000 | Deposit = Rp 279.800.

---

## 17. Sale Transaction Rules & Invariants

1. **Item Price Snapshotting:** `SaleItem` must store the exact `unitPrice` and `costPrice` at transaction time. Subsequent changes to `Product.sellingPrice` must NEVER alter historical sale items.
2. **Subtotal & Total Formula:**
   $$\text{SaleItem.subtotal} = (\text{quantity} \times \text{unitPrice}) - \text{itemDiscount}$$
   $$\text{Sale.subtotal} = \sum \text{SaleItem.subtotal}$$
   $$\text{Sale.totalAmount} = \max(0, \text{Sale.subtotal} - \text{Sale.discountAmount} + \text{Sale.taxAmount})$$
3. **Credit Sale Validation:**
   - If `paidAmount < totalAmount`, `Sale.customerId` **must not be null**. Anonymous guest credit sales are strictly forbidden by domain invariants.
   - A `CustomerDebt` record is created for the remaining balance ($\text{totalAmount} - \text{paidAmount}$).

---

## 18. Purchase Transaction Rules & Invariants

1. **Cost Price Tracking:** `PurchaseItem.unitCost` represents the inventory acquisition cost.
2. **Inventory Invariant:**
   - Completed Purchase with `ProductType.PHYSICAL` $\rightarrow$ Increments `Product.stock` by purchased quantity.
3. **Financial Invariant:**
   - Completed Purchase paid in cash $\rightarrow$ Decrements `CashRegister` balance.
   - Completed Purchase on credit $\rightarrow$ Requires valid `supplierId`, creates `SupplierDebt` for unpaid balance.

---

## 19. Debt Rules (Customer & Supplier)

### Customer Debt (Piutang)
1. **Origin:** Created exclusively from a credit `Sale` with an assigned `Customer`.
2. **Settlement Invariant:**
   - Debt payment received $\rightarrow$ Decreases `CustomerDebt.remainingAmount`, decreases `Customer.debtBalance`.
   - If paid in cash $\rightarrow$ Creates `CashMutation.INFLOW` and increments `CashRegister`.
3. **Zero Balance Constraint:** `remainingAmount` cannot drop below zero.

### Supplier Debt (Hutang)
1. **Origin:** Created from a credit `Purchase` with an assigned `Supplier`.
2. **Settlement Invariant:**
   - Debt payment made $\rightarrow$ Decreases `SupplierDebt.remainingAmount`, decreases `Supplier.debtBalance`.
   - If paid from cash drawer $\rightarrow$ Creates `CashMutation.OUTFLOW` and decrements `CashRegister`.

---

## 20. Stock & Inventory Rules

1. **Stock Ownership:** Only `PHYSICAL` products maintain inventory quantity. `SERVICE` and `DIGITAL` products bypass inventory tracking.
2. **Negative Stock Policy:** Default domain configuration prevents negative stock unless the business profile explicitly enables `allowNegativeStock = true`.
3. **Stock Adjustment:** All manual stock adjustments must capture an explicit `reason` (`STOCK_OPNAME`, `DAMAGED`, `EXPIRED`, `LOST`, `INTERNAL_USE`).

---

## 21. Report & Projection Boundary

- **Projections Only:** Reports (Sales summary, profit/loss, daily cash book, top products, customer debt balance) are **pure projections** derived from completed transactions.
- **Strict Distinction:** Reports must separate:
  - Retail Sales vs. Digital Sales.
  - Physical Cash Movements vs. Digital Deposit Movements.
- **Deposit Top-Up is NOT Revenue/Profit:** Top-up transactions are internal capital transfers (Cash $\rightarrow$ Deposit) and must never be counted as sales revenue or business profit.
- **Estimated Gross Profit Formula:**
  $$\text{Gross Profit} = \sum (\text{SaleItem.subtotal} - (\text{SaleItem.quantity} \times \text{SaleItem.costPrice})) - \text{Sale.discountAmount}$$

---

## 22. License Boundary

- **Separation of Concerns:** The licensing domain (`:core:license`) is completely decoupled from business transaction mechanics.
- **Entitlements:** License evaluates feature flags such as `maxProducts`, `cloudSyncEnabled`, `advancedReportsEnabled`, `multiDeviceEnabled`.
- **Core Persistence Independence:** Expiration of a license must **never** lock or destroy local business data or prevent read access to historic transactions.

---

## 23. Offline-First Architecture Contract

1. **Local-First Authority:** Every transaction (Sale, Purchase, Payment, Debt) must succeed and commit locally to Room Database without network availability.
2. **Canonical Identifiers:** All primary keys for synchronized entities use standard UUID (v4) or collision-resistant monotonic string IDs generated on the local client.
3. **Optimistic Mutation:** State changes are applied locally immediately and marked with sync metadata (`syncStatus`: `PENDING`, `SYNCED`, `CONFLICT`).

---

## 24. Future PPOB & Digital Provider Boundary

PPOB (Payment Point Online Bank) and digital aggregators represent external third-party fulfillment services. The core domain strictly defines the boundary:

```
┌────────────────────────────────────────────────────────┐
│                   Yukelola Core Domain                 │
│  - ProductType: DIGITAL                                │
│  - DigitalTransaction & DigitalDepositAccount          │
│  - Inquiry Capability & Confirmation Model             │
│  - FulfillmentStatus: INITIATED|PENDING|SUCCESS|FAILED │
└───────────────────────────┬────────────────────────────┘
                            │ (Future Adapter Boundary)
                            ▼
┌────────────────────────────────────────────────────────┐
│              Future PPOB Transaction Adapter           │
│  - Provider API Adapters (Digiflazz, MobilePulsa, etc.)│
│  - Provider Balance & Idempotency Key Management       │
│  - Callback / Webhook Listeners & Reconciliation        │
│  - Retry & Timeout Engines                             │
└────────────────────────────────────────────────────────┘
```

*Note: No provider credentials, HTTP clients, or provider SDKs are included in the core domain.*

---

## 25. Domain Invariants Summary

1. **Sale Completeness:** A `Sale` with status `COMPLETED` must have at least one `SaleItem`.
2. **Anonymous Credit Prohibition:** Unpaid or partially paid sales without an assigned `Customer` are rejected.
3. **Non-Negative Monetary Amounts:** Unit prices, totals, payments, and discounts must be $\ge 0$.
4. **Idempotent Payments:** Sum of payments attached to a transaction cannot exceed total amount unless recording change/kembalian.
5. **Deposit Solvency:** Digital transaction cannot dispatch if $\text{DigitalDepositAccount.currentBalance} < \text{Product.costPrice}$.
6. **Deterministic Failure Reversal:** Failed digital transactions must produce a compensating `DigitalDepositMutation.REFUND` or `REVERSAL` to preserve auditability.
7. **Inquiry Isolation:** Inquiries must not mutate cash, deposit, or sales records.
8. **Historical Immutability:** Completed sales and purchases are immutable; modifications must use cancellation/void workflow.

---

## 26. Explicit Out-of-Scope Items

The following are explicitly **out of scope** for the Yukelola core domain:
- Complex multi-branch double-entry journal accounting.
- Warehouse bin management, batch numbering, and expiry tracking.
- Multi-tier composite manufacturing bills of materials (BOM).
- Payroll and employee shift commissions.
- Direct provider PPOB API client integration in the local domain module.
