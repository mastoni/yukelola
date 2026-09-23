# Operational Workflow Matrix

> **Document Status:** CANONICAL REFERENCE  
> **Target Scope:** Detailed end-to-end operational workflows for each business type supported in Yukelola.

---

## 1. Workflow Typology Overview

Yukelola business operations map into four canonical workflow archetypes:

1. **Instant POS Workflow:** Direct catalog pick/scan $\rightarrow$ instant payment $\rightarrow$ physical goods handover $\rightarrow$ receipt print.
2. **Digital Fulfillment Workflow:** Destination identifier entry $\rightarrow$ Inquiry validation $\rightarrow$ customer confirmation $\rightarrow$ digital deposit debit $\rightarrow$ electronic dispatch.
3. **Asynchronous Service / Job Order Workflow:** Customer intake & spec recording $\rightarrow$ down payment collection $\rightarrow$ job queue processing $\rightarrow$ ready notification $\rightarrow$ customer pickup & final balance settlement.
4. **Dining / Table Service Workflow:** Table/guest assignment $\rightarrow$ menu line entry $\rightarrow$ kitchen prep $\rightarrow$ post-meal / upfront payment settlement.

---

## 2. Detailed Workflows by Business Type

### A. RETAIL_WARUNG & ATK (Stationery)
```text
[Scan / Select Product] ──► [Active Cart] ──► [Payment Selection] ──► [Settlement] ──► [Print / WhatsApp Receipt]
                                                      │
                                                      ├── Cash (Drawer Inflow)
                                                      ├── QRIS / Transfer
                                                      └── Customer Debt (Kasbon)
```
- **Operational Pace:** Sub-second checkout, rapid quantity manipulation.
- **Stock Effect:** Real-time stock decrement on completed transaction.

---

### B. DIGITAL_KIOSK / KONTER (Digital Services)
```text
[Input Phone / IDPEL] ──► [Auto-Detect Operator] ──► [Optional Inquiry] ──► [Select Denomination]
                                                                                   │
[Final State] ◄── [Electronic Dispatch] ◄── [Debit Digital Deposit] ◄── [Customer Pays]
       │
       ├── SUCCESS: Transaction finalized
       └── FAILED: Deterministic Refund / Reversal to Digital Deposit
```
- **Operational Pace:** Real-time electronic verification.
- **Deposit Invariant:** Deposit balance checked before dispatch; never touches physical stock count.

---

### C. LAUNDRY (Kiloan & Satuan)
```text
[Customer Drop-Off] ──► [Weigh / Count Items] ──► [Select Service (Wash/Dry/Iron/Express)]
                                                              │
[Pickup & Settlement] ◄── [Status: READY] ◄── [Status: IN_PROGRESS] ◄── [Create ServiceOrder + Down Payment]
        │
        ├── Customer pays remaining balance
        ├── Order marked COMPLETED
        └── Receipt / Collection Ticket printed
```
- **Operational Pace:** Asynchronous multi-day turnaround.
- **Financial Invariant:** Down payment records immediate cash inflow; remaining balance tracked until pickup.

---

### D. SERVICE_WORKSHOP / BENGKEL (Repair & Parts)
```text
[Vehicle Check-In] ──► [Record Plate & Complaints] ──► [Inspection & Diagnosis]
                                                               │
[Vehicle Handover] ◄── [Final Settlement] ◄── [Add Spare Parts & Labor] ◄── [Customer Approval]
                                                       │
                                                       └── Parts Stock Decremented
```
- **Operational Pace:** Multi-hour / multi-day job order.
- **Stock Invariant:** Spare parts are deducted from physical inventory upon work order confirmation/completion.

---

### E. PERCETAKAN / PRINTING & FOTOCOPY (Custom Jobs)
```text
[Customer File / Specs] ──► [Define Specs (Size, Color, Paper, Finishing)] ──► [Calculate Cost & Price]
                                                                                      │
[Delivery / Pickup] ◄── [Final Settlement] ◄── [Production & Quality Check] ◄── [Collect Down Payment]
```
- **Bulk Photocopy:** Uses fast counter keypad (Quantity $\times$ Rate) for instant jobs, or `ServiceOrder` for large document print batches.

---

### F. APOTEK / PHARMACY (Standard OTC Retail)
```text
[Search Medicine / Barcode] ──► [Select Packaging Unit (Strip / Box / Botol)] ──► [Record Dosage Instructions]
                                                                                             │
[Print Receipt / Drug Label] ◄────────────────────── [Instant Checkout Settlement] ◄─────────┘
```
- **Stock Invariant:** Standard physical unit decrement. Batch numbers and expiry tracking remain out of scope for core domain baseline.

---

### G. FOOD_BEVERAGE_CAFE (Dine-in / Takeaway)
```text
[Select Menu Items] ──► [Add Modifier / Kitchen Notes] ──► [Assign Table / Order Name]
                                                                  │
[Dine-in Service] ◄── [Print Kitchen Slip] ◄── [Upfront Pay OR Open Bill] ──► [Close Bill on Exit]
```

---

## 3. Mixed Multi-Capability Scenarios

1. **Warung + Pulsa + Bensin:**
   - Cashier uses standard retail POS for snacks/groceries.
   - Cashier taps "Layanan Digital" tab for mobile credit without polluting the grocery cart.
   - Fuel (Bensin Eceran) is selected as a physical item with liter measurement.
2. **Bengkel + Penjualan Sparepart Langsung:**
   - Walk-in spare part buyer: Standard `RETAIL_TRANSACTION` (Instant checkout).
   - Motor service customer: `SERVICE_ORDER_TRANSACTION` (Service order with vehicle plate + mechanic labor + attached spare parts).
3. **Percetakan + Fotocopy + ATK:**
   - ATK & instant photocopy: Standard `RETAIL_TRANSACTION`.
   - Custom banner/invitation printing: `SERVICE_ORDER_TRANSACTION`.
