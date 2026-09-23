# Operational Workflow Matrix

> **Document Status:** CANONICAL REFERENCE (REVISION: RETAIL_HEALTH / APOTEK + TOKO OBAT)  
> **Target Scope:** Detailed end-to-end operational workflows for each business type supported in Yukelola.

---

## 1. Workflow Typology Overview

Yukelola business operations map into four canonical workflow archetypes:

1. **Instant POS Workflow (`RETAIL_TRANSACTION`):** Direct catalog pick / barcode scan $\rightarrow$ active cart $\rightarrow$ instant payment $\rightarrow$ physical goods handover $\rightarrow$ receipt print.
2. **Digital Fulfillment Workflow (`DIGITAL_TRANSACTION`):** Destination identifier entry $\rightarrow$ read-only inquiry validation $\rightarrow$ customer confirmation $\rightarrow$ digital deposit reservation & debit $\rightarrow$ electronic provider dispatch $\rightarrow$ terminal status.
3. **Asynchronous Service / Job Order Workflow (`SERVICE_ORDER_TRANSACTION`):** Customer & asset intake $\rightarrow$ spec recording $\rightarrow$ optional down payment $\rightarrow$ operational job queue $\rightarrow$ ready notification $\rightarrow$ customer pickup & final balance settlement.
4. **Dining / Table Service Workflow (`RETAIL_TRANSACTION` with table context):** Table/guest assignment $\rightarrow$ menu selection $\rightarrow$ kitchen slip print $\rightarrow$ upfront / post-meal settlement.

---

## 2. Detailed Workflows by Business Context

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

### B. DIGITAL_KIOSK / KONTER (Digital Services & PPOB)
```text
[Input Phone / IDPEL] ──► [Auto-Detect Operator] ──► [Read-Only Inquiry] ──► [Select Denomination]
                                                                                    │
[Final State] ◄── [Electronic Dispatch] ◄── [Debit Digital Deposit] ◄── [Customer Pays]
       │
       ├── SUCCESS: Transaction finalized & logged
       └── FAILED: Deterministic Refund / Reversal mutation to Digital Deposit
```
- **Operational Pace:** Real-time electronic verification.
- **Deposit Invariant:** Deposit balance checked before dispatch; never touches physical stock count. Inquiry is strictly read-only.

---

### C. RETAIL_HEALTH: APOTEK & TOKO OBAT
```text
[Search Medicine / Barcode] ──► [Select Packaging Unit] ──► [Optional Dosage / Rx Note] ──► [Active Cart]
                                       │                                                           │
                                       ├── Box                                                     ▼
                                       ├── Strip                                        [Instant Settlement]
                                       └── Tablet / Botol                                          │
                                                                                                   ▼
[Print Receipt / Medication Label] ◄──────────────────────────────────────────── [Multi-Unit Stock Decrement]
```
- **Unit Precision:** Cashier selects sale unit (e.g., customer buys 2 strips from a box of 10 strips); inventory stock decrements accurately based on unit conversion factors.
- **Apotek Profile Extra:** Optional capture of doctor name / prescription number in transaction metadata without blocking standard retail throughput.
- **Toko Obat Profile:** Direct over-the-counter sales of OTC remedies, vitamins, and first aid.
- **Contract Boundary:** Expiry monitoring and batch numbers remain non-blocking / out of core v1.x scope.

---

### D. WARUNG + OBAT (Auxiliary Medicine in Warung)
```text
[Scan Daily Grocery] ────┐
                         ├─► [Combined Cart] ──► [Immediate Cash Settlement] ──► [Stock Decrement]
[Select OTC Paracetamol] ─┘
```
- **Operational Pace:** Standard fast-pace warung checkout.
- **Context Distinction:** Medicine is treated as a standard physical retail category (`Kesehatan / Obat Warung`). Does NOT trigger prescription prompts, doctor metadata, or specialized pharmacy search interfaces.

---

### E. SERVICE_WORKSHOP / BENGKEL (Repair & Parts)
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

### F. LAUNDRY (Kiloan & Satuan)
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

### G. PERCETAKAN / PRINTING (Custom Jobs)
```text
[Customer File / Specs] ──► [Define Specs (Size, Color, Paper, Finishing)] ──► [Calculate Cost & Price]
                                                                                      │
[Delivery / Pickup] ◄── [Final Settlement] ◄── [Production & Quality Check] ◄── [Collect Down Payment]
```
- **Operational Pace:** Multi-stage production queue with proofing, printing, and finishing.

---

### H. FOTOCOPY (Rapid Counter & Bulk Jobs)
```text
[Page Count & Paper Size] ──► [Direct Keypad Rate Calc] ──► [Instant Checkout] (Standard Fast Copy)
                                        │
                                        └── Bulk / Binding Job ──► [Create ServiceOrder Queue]
```

---

### I. FOOD_BEVERAGE_CAFE (Dine-in & Takeaway)
```text
[Select Menu Items] ──► [Add Modifier / Kitchen Notes] ──► [Assign Table / Order Name]
                                                                  │
[Dine-in Service] ◄── [Print Kitchen Slip] ◄── [Upfront Pay OR Open Bill] ──► [Close Bill on Exit]
```

---

## 3. Mixed Multi-Capability Scenarios (10 Standard Scenarios)

1. **Warung + Pulsa:** Standard retail grocery checkout on primary tab; switching to "Layanan Digital" tab allows phone credit top-up without polluting the grocery cart.
2. **Warung + Obat:** OTC medicines are scanned as regular grocery items in standard `RETAIL_TRANSACTION`.
3. **Warung + Bensin:** Fuel (Bensin Eceran) is rung up as a standard physical product per liter.
4. **Warung + ATK:** Pens, notebooks, and envelopes are rung up in the standard retail cart.
5. **Konter + Accessories:** PPOB on primary digital tab; screen protectors and chargers rung up via secondary retail POS tab with immediate stock deduction.
6. **Printing + Fotocopy + ATK:** Counter stationery and instant photocopies use `RETAIL_TRANSACTION`; large custom banner printing creates a `ServiceOrder`.
7. **Fotocopy + Printing + Scan:** Instant copies and scanning rung up via fast counter POS; multi-book binding/printing tracked via `ServiceOrder`.
8. **Bengkel + Spare Parts:** Direct walk-in parts sales use `RETAIL_TRANSACTION`; full vehicle servicing uses `SERVICE_ORDER_TRANSACTION`.
9. **Cafe + Packaged Retail Products:** Coffee and snacks ordered alongside packaged retail beans/chips in a single unified F&B checkout.
10. **Apotek / Toko Obat + Non-Health Retail Items:** Prescription/OTC medicines rung up alongside mineral water, tissues, and snacks in a single `RETAIL_TRANSACTION` with multi-unit deduction.
