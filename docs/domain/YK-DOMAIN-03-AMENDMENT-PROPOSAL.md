# YK-DOMAIN-03 — Controlled Contract Amendment Proposal

> **Proposal Target:** Product & Domain Contract v1.1.0  
> **Status:** PROPOSED AMENDMENT FOR NEXT GATE  
> **Governance:** Non-breaking additive extension. Preserves all locked v1.1.0 rules.

---

## 1. Background & Discovered Gap

In Product & Domain Contract v1.1.0, commercial transactions are primarily modeled via the `Sale` and `SaleItem` aggregate, which assumes **synchronous (instant) point-of-sale execution** (over-the-counter item selection $\rightarrow$ instant payment $\rightarrow$ instant completion).

However, deep domain analysis across UMKM business types in YK-DOMAIN-03 reveals that **Service Verticals** (`LAUNDRY`, `SERVICE_WORKSHOP`, `PERCETAKAN`) operate on an **asynchronous multi-step order lifecycle**:
- Work is initiated at $T_0$ with optional down payment (`DownPayment`).
- Work progresses through operational stages (`RECEIVED` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `READY`).
- Final settlement and delivery occur at $T_1$ upon customer pickup.

Forcing asynchronous multi-day service workflows directly into instant `Sale` records would either:
1. Prematurely finalize unperformed work and unearned remaining balance, or
2. Overload the `Sale` entity with temporary job queue states and custom service specifications.

---

## 2. Proposed Additive Amendments

### Amendment A: Formalize `ServiceOrder` Aggregate
Introduce `ServiceOrder` and `ServiceOrderItem` to manage asynchronous order lifecycles cleanly in `:core:domain` and `:core:database`:
- **Attributes:** `id`, `businessId`, `orderNumber`, `customerId`, `orderType` (`LAUNDRY`, `WORKSHOP`, `PRINTING`, `CUSTOM`), `fulfillmentStatus` (`RECEIVED`, `IN_PROGRESS`, `READY`, `COMPLETED`, `CANCELLED`), `totalAmount`, `downPaymentAmount`, `remainingBalance`, `estimatedCompletionDate`, `createdAt`, `updatedAt`.
- **Context Attachment:** Optional metadata string / JSON for vehicle plate, laundry weight/perfume, or print file specs.

### Amendment B: Add `SERVICE_ORDER_TRANSACTION` Mode
Expand Transaction Modes to formally recognize:
1. `RETAIL_TRANSACTION` (Instant over-the-counter physical goods checkout)
2. `DIGITAL_TRANSACTION` (Electronic vouchers, tokens, deposit debit, and inquiry)
3. `SERVICE_ORDER_TRANSACTION` (Asynchronous service queue, down payment, and pickup settlement)

### Amendment C: Settlement Bridge to `Sale`
Upon `ServiceOrder` reaching `COMPLETED` status, it records a finalized transaction entry in the sales ledger, ensuring that:
- Daily revenue reports and cash drawer balances seamlessly aggregate all completed transactions.
- Customer debt and payment records maintain 100% mathematical integrity across both retail and service operations.

---

## 3. Impact Assessment & Risk Analysis

| Evaluation Dimension | Assessment | Impact & Mitigation |
|---|---|---|
| **Breaking Changes** | **ZERO (0)** | Purely additive. Existing `Sale`, `Purchase`, `Payment`, `CashRegister`, and `DigitalDepositAccount` contracts remain completely unchanged. |
| **Business Models Affected** | `LAUNDRY`, `SERVICE_WORKSHOP`, `PERCETAKAN` | Enables full, native operational workflows for laundry, vehicle repair, and custom printing. |
| **Retail & Konter Impact** | **None** | `RETAIL_WARUNG` and `DIGITAL_KIOSK` continue using `RETAIL_TRANSACTION` and `DIGITAL_TRANSACTION` unchanged. |
| **Persistence / Room Impact** | Low / Additive | Adds `service_orders` and `service_order_items` tables in `:core:database` without altering existing schema plans. |
| **Migration Risk** | **None** | Clean greenfield implementation in upcoming database gates. |

---

## 4. Recommendation for Subsequent Gates

- **Recommendation:** Accept and merge this amendment proposal into the canonical domain contract during the domain implementation gate (YK-DOMAIN-IMPL / YK-DB-01) to provide complete first-class support for Laundry, Bengkel, and Percetakan.
