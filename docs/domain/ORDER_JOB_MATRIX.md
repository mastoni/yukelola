# Order & Job Matrix (Unified Service Order Abstraction)

> **Document Status:** CANONICAL REFERENCE  
> **Target Scope:** Specifications for the unified `ServiceOrder` abstraction serving asynchronous service businesses (Laundry, Workshop, Printing, and Custom Services).

---

## 1. Unified `ServiceOrder` Abstraction

Rather than creating fragmented, one-off domain models for each service vertical (e.g., `LaundryOrder`, `WorkshopOrder`, `PrintOrder`), Yukelola establishes a **Unified `ServiceOrder` Aggregate** that adapts through contextual metadata attachments:

```
                         ┌─────────────────────────────┐
                         │        ServiceOrder         │
                         ├─────────────────────────────┤
                         │ - id: UUID                  │
                         │ - businessId: UUID          │
                         │ - orderNumber: String       │
                         │ - customerId: UUID?         │
                         │ - orderType: Enum           │
                         │ - fulfillmentStatus: Enum   │
                         │ - totalAmount: Long         │
                         │ - downPaymentAmount: Long   │
                         │ - remainingBalance: Long    │
                         │ - estimatedCompletionDate   │
                         │ - createdAt: Timestamp      │
                         └──────────────┬──────────────┘
                                        │
        ┌───────────────────────────────┼───────────────────────────────┐
        │ 1:*                           │ 0..1                          │ 1:*
        ▼                               ▼                               ▼
┌──────────────────┐           ┌──────────────────┐           ┌──────────────────┐
│ ServiceOrderItem │           │ ContextMetadata  │           │     Payment      │
│ - itemId         │           │ (Vehicle / Specs)│           │ - DP / Final     │
│ - service/partId │           └──────────────────┘           └──────────────────┘
│ - quantity       │
│ - unitPrice      │
│ - subtotal       │
└──────────────────┘
```

---

## 2. Universal Service Order Lifecycle

```
[RECEIVED] ──(Work Started)──► [IN_PROGRESS] ──(Job Finished)──► [READY] ──(Pickup & Pay)──► [COMPLETED]
     │                                │                             │
     └────────────────────────────────┴─────────────────────────────┴──(Cancel)──► [CANCELLED]
```

- **`RECEIVED`:** Customer item/vehicle/file checked in. Down payment collected and recorded in `CashRegister`.
- **`IN_PROGRESS`:** Work actively underway (washing, mechanical repair, printing).
- **`READY`:** Work finished. Ready notification triggered (e.g. WhatsApp pickup text).
- **`COMPLETED`:** Customer picks up item, remaining balance settled in full.
- **`CANCELLED`:** Work aborted; down payment refund handled and material/spare parts stock reverted.

---

## 3. Specialization Adaptations by Business Type

| Business Vertical | `orderType` | Line Items (`ServiceOrderItem`) | Contextual Metadata (`ContextMetadata`) | Down Payment Rules | Pickup & Completion Criteria |
|---|---|---|---|---|---|
| **LAUNDRY** | `LAUNDRY` | - Cuci Komplit (Kg)<br>- Cuci Kering (Kg)<br>- Setrika (Kg)<br>- Bed Cover (Pcs) | - Weight (Kg)<br>- Perfume Choice<br>- Express/Regular Flag<br>- Special Care Notes | Optional DP (Can be Rp 0 / Bayar Nanti, or Full upfront) | Customer receives clean clothes, pays remaining balance. |
| **SERVICE_WORKSHOP** | `WORKSHOP` | - Service Ringan / Berat<br>- Ganti Oli (Labor + Part)<br>- Spare Part Items (Busi, Kampas) | - Vehicle Plate Number<br>- Vehicle Brand/Model<br>- Customer Complaint Notes<br>- Mechanic Name | Optional DP (Parts cost upfront, labor upon finish) | Vehicle test-driven & handed over; invoice finalized. |
| **PERCETAKAN** | `PRINTING` | - Cetak Spanduk Flexi<br>- Cetak Brosur A4<br>- Kartu Nama Box | - File Name / Cloud Link<br>- Dimensions ($L \times W$ meters)<br>- Paper Type / Grammage<br>- Finishing (Laminasi, Mata Ayam) | Typical $\ge 50\%$ Down Payment required before print | Quality inspected; goods packed & collected. |
| **BULK FOTOCOPY** | `PHOTOCOPY`| - Fotocopy A4 / F4 (Pages)<br>- Jilid Lakban / Spiral | - Total Master Pages<br>- Copies Count<br>- Paper Size & Color | Optional for large volume batches | Document verified & collected. |

---

## 4. Key Architectural Benefits

1. **Zero Code Duplication:** A single set of UseCases (`CreateServiceOrderUseCase`, `UpdateOrderStatusUseCase`, `CollectOrderPaymentUseCase`) powers all three industries.
2. **Unified Queue Management:** Feature UI (`:feature:pos` / `:feature:orders`) renders an adaptive Kanban / Queue board driven by standard `fulfillmentStatus` states.
3. **Traceable Down Payments:** Solves the classic UMKM challenge of partial down payments, customer debt tracking, and pickup verification without complex double-entry accounting.
