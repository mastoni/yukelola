# Transaction Mode Matrix

> **Document Status:** CANONICAL REFERENCE (REVISION: RETAIL_HEALTH / APOTEK + TOKO OBAT)  
> **Target Scope:** Formal specification of transaction modes, settlement mechanics, and fulfillment rules in Yukelola.

---

## 1. Transaction Modes Overview

Yukelola defines three distinct transaction modes to govern validation, inventory changes, financial ledgers, and lifecycle states:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                             YUKELOLA TRANSACTION MODES                      │
├──────────────────────────┬───────────────────────────┬──────────────────────┤
│ 1. RETAIL_TRANSACTION    │ 2. DIGITAL_TRANSACTION    │ 3. SERVICE_ORDER_TX  │
├──────────────────────────┼───────────────────────────┼──────────────────────┤
│ • Instant Over-the-Counter│ • Electronic Fulfillment │ • Asynchronous Queue │
│ • Physical Stock Decrement│ • Digital Deposit Debit  │ • Down Payment + Rem │
│ • Immediate Cash/Credit  │ • Inquiry & Verification │ • Work Lifecycle     │
└──────────────────────────┴───────────────────────────┴──────────────────────┘
```

---

## 2. Detailed Transaction Mode Specification

| Attribute | `RETAIL_TRANSACTION` | `DIGITAL_TRANSACTION` | `SERVICE_ORDER_TRANSACTION` |
|---|---|---|---|
| **Primary Business Contexts** | `RETAIL_WARUNG`, `WARUNG + OBAT`, `ATK`, `RETAIL_HEALTH` (`APOTEK` / `TOKO_OBAT`), `GENERAL_STORE`, `F&B CAFE`, `FOTOCOPY` (instant) | `DIGITAL_KIOSK` / `KONTER`, `WARUNG + DIGITAL` | `LAUNDRY`, `SERVICE_WORKSHOP`, `PERCETAKAN`, `FOTOCOPY` (bulk) |
| **Operational Execution** | Synchronous (Instant) | Synchronous / Electronic Dispatch | Asynchronous (Multi-step queue) |
| **Catalog Input** | Barcode scan, catalog grid, quick items, multi-unit drug selector | Target number (Phone/IDPEL), denomination picker | Service specs (Weight/Unit/Job size), vehicle plate, file ref |
| **Fulfillment State** | `COMPLETED` immediately upon payment | `INITIATED` $\rightarrow$ `PENDING` $\rightarrow$ `SUCCESS` / `FAILED` | `RECEIVED` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `READY` $\rightarrow$ `COMPLETED` |
| **Physical Inventory Effect** | Decrements `Product.stock` for `PHYSICAL` goods | **None** (Digital products do not use warehouse stock) | Decrements stock for physical materials/spare parts consumed |
| **Cash Drawer Effect** | Increases `CashRegister` by cash paid | Increases `CashRegister` by selling price (if paid cash) | Increases `CashRegister` on down payment and final pickup payment |
| **Digital Deposit Effect** | **None** (Untouched) | Decreases `DigitalDepositAccount` by distributor `costPrice` | **None** (Untouched) |
| **Customer Credit Effect** | Creates `CustomerDebt` if `paidAmount < totalAmount` | Not recommended for anonymous digital transactions | Unpaid balance tracked as Order Receivable until pickup or debt transfer |
| **Cancellation / Failure** | Void / Reversal (restores stock, reverses cash) | Automatic `DigitalDepositMutation.REFUND` on provider failure | Order Cancellation (reverses parts stock, handles down payment refund) |
| **Inquiry Requirement** | None | Optional / Required for PLN Token & E-Wallet | None |

---

## 3. Justification for `SERVICE_ORDER_TRANSACTION`

The existing `RETAIL_TRANSACTION` models instant point-of-sale transactions where goods are handed over and payment is finalized on the spot. 

However, service businesses (Laundry, Workshop, Printing) inherently operate on an **asynchronous job lifecycle**:
1. Goods/Vehicles/Files are received at $T_0$.
2. Work is performed over hours/days ($T_1$).
3. Customer receives notification when ready ($T_2$).
4. Goods/Vehicles are picked up and remaining payment is settled at $T_3$.

Introducing `SERVICE_ORDER_TRANSACTION` as a first-class mode provides:
- Clean multi-state tracking (`RECEIVED` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `READY` $\rightarrow$ `COMPLETED`) without corrupting the instant `Sale` domain.
- Explicit down payment (`downPaymentAmount`) and remaining balance calculation (`remainingBalance`).
- Traceable assignment to specific customers and contextual tags (vehicle plate, laundry bag tag, job ticket).
