# Yukelola Product & Domain Contract

> **Contract Version:** 1.0.0  
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
- **Robust Domain Core:** Business rules and invariants are encapsulated in pure Kotlin domain models (`:core:domain`), independent of UI frameworks, database engines, or external services.

---

## 2. Domain Glossary

All domain code, interfaces, repository contracts, and database schemas **must** adhere strictly to the English business terminology defined below. Indonesian terms are reserved exclusively for UI presentation and localization strings.

| Canonical Domain Term | Meaning & Purpose | UI / Localized Synonym (ID) |
|---|---|---|
| **Business** | Aggregate root representing the tenant or enterprise entity. | Usaha / Bisnis |
| **BusinessProfile** | Metadata and operational attributes of a business (name, contact, receipt header). | Profil Usaha |
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
| **CashRegister** | Cash drawer / register session tracking inflows, outflows, and balance. | Buku Kas / Kasir |
| **CashMutation** | Non-sale / non-purchase cash adjustment (cash in, cash out, expense). | Mutasi Kas / Pengeluaran |
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
                    └─────────────────────────┘
                                 │
     ┌────────────────┬──────────┴─────────┬────────────────┐
     │ 1:*            │ 1:*                 │ 1:*            │ 1:*
┌────▼──────┐   ┌─────▼───────┐      ┌─────▼──────┐   ┌─────▼──────┐
│  Category │   │   Product   │      │  Customer  │   │  Supplier  │
└───────────┘   └─────┬───────┘      └─────┬──────┘   └─────┬──────┘
                      │                    │                │
                      │ 1:* (SaleItem)     │ 0..1           │ 0..1
                ┌─────▼────────────────────▼─────┐    ┌─────▼──────────┐
                │              Sale              │    │    Purchase    │
                ├────────────────────────────────┤    ├────────────────┤
                │ - SaleItems                    │    │ - PurchaseItems│
                │ - Payments                     │    │ - Payments     │
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
2. **BusinessProfile**: Profile entity (`businessId`, `name`, `businessType`, `phone`, `address`, `receiptFooter`, `currency`, `timezone`).
3. **Category**: Organizational taxonomy (`id`, `businessId`, `name`, `color`, `icon`, `sortOrder`).
4. **Product**: Sellable catalog item (`id`, `businessId`, `categoryId`, `sku`, `name`, `productType`, `unit`, `purchasePrice`, `sellingPrice`, `stock`, `minStock`, `trackStock`, `isActive`).
5. **Customer**: Buyer profile (`id`, `businessId`, `name`, `phone`, `email`, `address`, `debtBalance`, `notes`, `isActive`).
6. **Supplier**: Vendor profile (`id`, `businessId`, `name`, `contactPerson`, `phone`, `address`, `debtBalance`, `isActive`).
7. **Sale**: Sale transaction aggregate (`id`, `businessId`, `saleNumber`, `customerId`, `subtotal`, `discountAmount`, `taxAmount`, `totalAmount`, `paidAmount`, `paymentStatus`, `fulfillmentStatus`, `notes`, `createdAt`).
8. **SaleItem**: Immutable snapshot line (`id`, `saleId`, `productId`, `productName`, `unit`, `unitPrice`, `costPrice`, `quantity`, `discountAmount`, `subtotal`).
9. **Purchase**: Inventory order aggregate (`id`, `businessId`, `purchaseNumber`, `supplierId`, `totalAmount`, `paidAmount`, `paymentStatus`, `createdAt`).
10. **PurchaseItem**: Line item (`id`, `purchaseId`, `productId`, `productName`, `unitCost`, `quantity`, `subtotal`).
11. **Payment**: Payment execution (`id`, `businessId`, `transactionType` [SALE/PURCHASE/DEBT], `referenceId`, `paymentMethod`, `amount`, `createdAt`).
12. **CustomerDebt**: Receivable tracking (`id`, `businessId`, `customerId`, `saleId`, `originalAmount`, `remainingAmount`, `status`, `dueDate`, `createdAt`).
13. **SupplierDebt**: Payable tracking (`id`, `businessId`, `supplierId`, `purchaseId`, `originalAmount`, `remainingAmount`, `status`, `dueDate`, `createdAt`).
14. **DebtPayment**: Debt settlement record (`id`, `businessId`, `debtType` [CUSTOMER/SUPPLIER], `debtId`, `amount`, `paymentMethod`, `notes`, `createdAt`).
15. **CashRegister**: Cash balance manager (`id`, `businessId`, `name`, `currentBalance`, `updatedAt`).
16. **CashMutation**: Inflow/outflow record (`id`, `businessId`, `registerId`, `mutationType` [INFLOW/OUTFLOW], `category`, `amount`, `source`, `referenceId`, `notes`, `createdAt`).
17. **StockAdjustment**: Inventory reconciliation record (`id`, `businessId`, `productId`, `adjustmentType` [ADD/SUBTRACT/SET], `quantity`, `reason`, `createdAt`).

---

## 4. Entity Responsibilities & Boundaries

- **Business & Profile:** Encapsulate enterprise identity and presentation rules. They do not hold transaction line items directly.
- **Product:** Manages catalog data, pricing, and active status. Physical stock count is kept consistent via transaction mutations.
- **Sale & Purchase:** Serve as immutable historic commercial records. Once completed, line items reflect the exact prices and names at the moment of execution.
- **Debts (Customer / Supplier):** Explicitly decouple unpaid credit obligations from completed transaction records. A credit sale is completed from a sales perspective, and the outstanding balance is tracked in the CustomerDebt ledger.
- **CashRegister:** Records actual monetary movements. It does not calculate accounting profits, only physical cash flows and drawer reconciliations.

---

## 5. Relationships & Cardinality

| Source Entity | Cardinality | Target Entity | Relationship Semantics |
|---|---|---|---|
| `Business` | `1 : 1` | `BusinessProfile` | Strict ownership, created on onboarding. |
| `Business` | `1 : *` | `Category` | Categorization taxonomy owned by business. |
| `Business` | `1 : *` | `Product` | Products belong to one business tenant. |
| `Business` | `1 : *` | `Customer` | Customer list scoped per business. |
| `Business` | `1 : *` | `Supplier` | Supplier contacts scoped per business. |
| `Business` | `1 : *` | `Sale` | Sales history scoped per business. |
| `Sale` | `1 : *` | `SaleItem` | Cascade lifecycle; items belong exclusively to parent Sale. |
| `Sale` | `0..1 : 1` | `Customer` | Optional; guest sales have null customer. |
| `Sale` | `1 : *` | `Payment` | One sale can have multiple payments (split/installments). |
| `Sale` | `1 : 0..1` | `CustomerDebt` | Created if `paidAmount < totalAmount` and Customer is present. |
| `Purchase` | `1 : *` | `PurchaseItem` | Items belong exclusively to parent Purchase. |
| `Purchase` | `0..1 : 1` | `Supplier` | Optional or specified vendor. |
| `Purchase` | `1 : 0..1` | `SupplierDebt` | Created if `paidAmount < totalAmount` on credit purchase. |
| `Customer` | `1 : *` | `CustomerDebt` | Aggregate receivable balance for a customer. |
| `Supplier` | `1 : *` | `SupplierDebt` | Aggregate payable balance for a supplier. |
| `CashRegister`| `1 : *` | `CashMutation` | Historical audit log of cash movements. |

---

## 6. Ownership & Aggregate Boundaries

1. **Catalog Aggregate:** Root = `Product`. Contains pricing, stock limits, and measurement units.
2. **Sales Aggregate:** Root = `Sale`. Contains `SaleItem` list, discount specifications, and attached `Payment` records.
3. **Purchase Aggregate:** Root = `Purchase`. Contains `PurchaseItem` list and initial purchase payments.
4. **Party Aggregates:** `Customer` and `Supplier` manage contact details and aggregate credit/payable balances.
5. **Cash Aggregate:** `CashRegister` manages cash on hand and serializes `CashMutation` records.

---

## 7. Lifecycle States

### Sale Lifecycle
```
[DRAFT] ──(Confirm & Pay)──► [COMPLETED] ──(Void/Cancel)──► [CANCELLED]
```
- `DRAFT`: Active cart / pending order.
- `COMPLETED`: Inventory decremented, cash recorded, customer debt recorded if unpaid balance exists.
- `CANCELLED`: Only permitted under strict void/reversal rules; reverts stock and cash effects.

### Payment Status (Sale & Purchase)
- `PAID`: `paidAmount == totalAmount`
- `PARTIAL`: `0 < paidAmount < totalAmount`
- `UNPAID`: `paidAmount == 0`

### Debt Lifecycle (Customer & Supplier)
```
[UNPAID] ──(Partial Payment)──► [PARTIALLY_PAID] ──(Full Settlement)──► [SETTLED]
```

---

## 8. Product Types & Fulfillment Semantics

| Product Type | Inventory Tracking | Fulfillment Behavior | Example Items |
|---|---|---|---|
| **PHYSICAL** | Tracked (`stock` decrements on sale, increments on purchase). Reorder alert on `stock <= minStock`. | Physical handover of goods across counter. | Beras, Minyak Goreng, Snack, Rokok, Bensin Eceran, Minuman Dingin. |
| **SERVICE** | Untracked (`trackStock = false`). Stock quantity is not decremented. | Service execution / labor provided. | Jasa Jahit, Potong Rambut, Servis Motor, Cuci Mobil. |
| **DIGITAL** | Untracked physical inventory. Unit price and purchase cost tracked. | Digital token, electronic top-up, serial code, voucher. | Pulsa Telkomsel, Token Listrik PLN, Topup DANA/GoPay, Voucher Game. |

---

## 9. Business Type & Contextual Adaptation

Business Type is a **contextual configuration profile**, NOT a rigid hard-coded silo. A business owner selecting `WARUNG` is not prohibited from selling services or digital products.

### Supported Business Context Profiles
1. `RETAIL_WARUNG`: General grocery, retail items, snacks, beverages.
2. `FOOD_BEVERAGE_CAFE`: Small cafes, food stalls, angkringan (quick ordering, kitchen notes).
3. `SERVICE_WORKSHOP`: Repair shops, barbershops, tailors, laundromats.
4. `DIGITAL_KIOSK`: Mobile credit, bill payment counter, digital vouchers.
5. `GENERAL_STORE`: Mixed multi-category small businesses.

### Adaptation Influence Matrix
- **Terminology:** Adapts UI copy (e.g., "Menu" vs "Barang" vs "Jasa").
- **Default Categories:** Pre-populates recommended categories upon onboarding.
- **Quick POS Layout:** Configures grid versus list quick-action tiles for fast checkout.
- **Contextual Fields:** Toggles optional receipt fields (e.g., table number, license plate, serial number).

---

## 10. Sale Transaction Rules & Invariants

1. **Item Price Snapshotting:** `SaleItem` must store the exact `unitPrice` and `costPrice` at transaction time. Subsequent changes to `Product.sellingPrice` must NEVER alter historical sale items.
2. **Subtotal & Total Formula:**
   $$\text{SaleItem.subtotal} = (\text{quantity} \times \text{unitPrice}) - \text{itemDiscount}$$
   $$\text{Sale.subtotal} = \sum \text{SaleItem.subtotal}$$
   $$\text{Sale.totalAmount} = \max(0, \text{Sale.subtotal} - \text{Sale.discountAmount} + \text{Sale.taxAmount})$$
3. **Cash & Stock Settlement:**
   - Completed Sale with `PaymentMethod.CASH` $\rightarrow$ Increments `CashRegister` balance by cash paid.
   - Completed Sale with `ProductType.PHYSICAL` $\rightarrow$ Decrements `Product.stock` by sold quantity.
4. **Credit Sale Validation:**
   - If `paidAmount < totalAmount`, `Sale.customerId` **must not be null**. Anonymous guest credit sales are strictly forbidden by domain invariants.
   - A `CustomerDebt` record is created for the remaining balance ($\text{totalAmount} - \text{paidAmount}$).

---

## 11. Purchase Transaction Rules & Invariants

1. **Cost Price Tracking:** `PurchaseItem.unitCost` represents the inventory acquisition cost.
2. **Inventory Invariant:**
   - Completed Purchase with `ProductType.PHYSICAL` $\rightarrow$ Increments `Product.stock` by purchased quantity.
3. **Financial Invariant:**
   - Completed Purchase paid in cash $\rightarrow$ Decrements `CashRegister` balance.
   - Completed Purchase on credit $\rightarrow$ Requires valid `supplierId`, creates `SupplierDebt` for unpaid balance.

---

## 12. Customer Debt (Piutang) Rules

1. **Origin:** Created exclusively from a credit `Sale` with an assigned `Customer`.
2. **Settlement Invariant:**
   - Debt payment received $\rightarrow$ Decreases `CustomerDebt.remainingAmount`, decreases `Customer.debtBalance`.
   - If paid in cash $\rightarrow$ Creates `CashMutation.INFLOW` and increments `CashRegister`.
3. **Zero Balance Constraint:** `remainingAmount` cannot drop below zero. Overpayments must be handled as change or customer credit advance.

---

## 13. Supplier Debt (Hutang) Rules

1. **Origin:** Created from a credit `Purchase` with an assigned `Supplier`.
2. **Settlement Invariant:**
   - Debt payment made $\rightarrow$ Decreases `SupplierDebt.remainingAmount`, decreases `Supplier.debtBalance`.
   - If paid from cash drawer $\rightarrow$ Creates `CashMutation.OUTFLOW` and decrements `CashRegister`.

---

## 14. Cash & Register Rules

1. **Single Register Principle:** For standard UMKM operations, one active `CashRegister` is maintained per device/business.
2. **Auditability:** Cash balance must always equal:
   $$\text{Current Balance} = \text{Opening Balance} + \sum \text{Cash Inflows} - \sum \text{Cash Outflows}$$
3. **Mutation Sources:**
   - `INFLOW`: Cash Sale, Customer Debt Repayment, Manual Cash In (Modal Awal / Setoran).
   - `OUTFLOW`: Cash Purchase, Supplier Debt Repayment, Operational Expense (Biaya Operasional), Manual Cash Out (Prive / Tarik Tunai).

---

## 15. Stock & Inventory Rules

1. **Stock Ownership:** Only `PHYSICAL` products maintain inventory quantity.
2. **Negative Stock Policy:** Default domain configuration prevents negative stock unless the business profile explicitly enables `allowNegativeStock = true` (to accommodate fast-paced warung environments where physical count entry lags behind sales).
3. **Stock Adjustment:** All manual stock adjustments must capture an explicit `reason` (`STOCK_OPNAME`, `DAMAGED`, `EXPIRED`, `LOST`, `INTERNAL_USE`).

---

## 16. Report & Projection Boundary

- **Projections Only:** Reports (Sales summary, profit/loss, daily cash book, top products, customer debt balance) are **pure projections** derived from completed transactions.
- **No Direct Mutation:** Report views must never be edited directly; corrections must occur via transaction reversals or adjustments.
- **Estimated Gross Profit Formula:**
  $$\text{Gross Profit} = \sum (\text{SaleItem.subtotal} - (\text{SaleItem.quantity} \times \text{SaleItem.costPrice})) - \text{Sale.discountAmount}$$

---

## 17. License Boundary

- **Separation of Concerns:** The licensing domain (`:core:license`) is completely decoupled from business transaction mechanics.
- **Entitlements:** License evaluates features such as `maxProducts`, `cloudSyncEnabled`, `advancedReportsEnabled`, `multiDeviceEnabled`.
- **Core Persistence Independence:** Expiration of a license must **never** lock or destroy local business data or prevent read access to historic transactions.

---

## 18. Offline-First Architecture Contract

1. **Local-First Authority:** Every transaction (Sale, Purchase, Payment, Debt) must succeed and commit locally to Room Database without network availability.
2. **Canonical Identifiers:** All primary keys for synchronized entities use standard UUID (v4) or collision-resistant monotonic string IDs generated on the local client.
3. **Optimistic Mutation:** State changes are applied locally immediately and marked with sync metadata (`syncStatus`: `PENDING`, `SYNCED`, `CONFLICT`).

---

## 19. Future PPOB & Digital Provider Boundary

PPOB (Payment Point Online Bank) and digital aggregators represent external third-party fulfillment services. The core domain strictly defines the boundary:

```
┌────────────────────────────────────────────────────────┐
│                   Yukelola Core Domain                 │
│  - ProductType: DIGITAL                                │
│  - Sale / SaleItem (Generic digital product sale)      │
│  - FulfillmentStatus: PENDING | SUCCESS | FAILED       │
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

## 20. Domain Invariants Summary

1. **Sale Completeness:** A `Sale` with status `COMPLETED` must have at least one `SaleItem`.
2. **Anonymous Credit Prohibition:** Unpaid or partially paid sales without an assigned `Customer` are rejected.
3. **Non-Negative Monetary Amounts:** Unit prices, totals, payments, and discounts must be $\ge 0$.
4. **Idempotent Payments:** Sum of payments attached to a transaction cannot exceed total amount unless recording change/kembalian.
5. **Historical Immutability:** Completed sales and purchases are immutable; modifications must use cancellation/void workflow.

---

## 21. Explicit Out-of-Scope Items

The following are explicitly **out of scope** for the Yukelola core domain:
- Complex multi-branch double-entry journal accounting.
- Warehouse bin management, batch numbering, and expiry tracking.
- Multi-tier composite manufacturing bills of materials (BOM).
- Payroll and employee shift commissions.
- Direct provider PPOB API client integration in the local domain module.
