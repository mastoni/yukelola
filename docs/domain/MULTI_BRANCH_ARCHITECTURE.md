# Multi-Branch Local-First Architecture

> **Document Status:** CANONICAL REFERENCE  
> **Target Scope:** Core Multi-Branch Topology, Branch Isolation, Operational Autonomy, and Local-First Guarantees in Yukelola.

---

## 1. Architectural Philosophy: Local-First & Branch-Isolated

Yukelola is engineered from the ground up as a **Local-First, Offline-First, and Branch-Isolated** business operating system. 

```
                               ┌─────────────────────────────┐
                               │          BUSINESS           │
                               │      (Tenant Identity)      │
                               └──────────────┬──────────────┘
                                              │
                    ┌─────────────────────────┼─────────────────────────┐
                    │                         │                         │
                    ▼                         ▼                         ▼
         ┌─────────────────────┐   ┌─────────────────────┐   ┌─────────────────────┐
         │      BRANCH A       │   │      BRANCH B       │   │      BRANCH C       │
         │  (Operational Unit) │   │  (Operational Unit) │   │  (Operational Unit) │
         ├─────────────────────┤   ├─────────────────────┤   ├─────────────────────┤
         │ • Local Database A  │   │ • Local Database B  │   │ • Local Database C  │
         │ • Branch Cash Drawer│   │ • Branch Cash Drawer│   │ • Branch Cash Drawer│
         │ • Branch Inventory  │   │ • Branch Inventory  │   │ • Branch Inventory  │
         │ • Local LAN Node    │   │ • Local LAN Node    │   │ • Local LAN Node    │
         └─────────────────────┘   └─────────────────────┘   └─────────────────────┘
```

### Core Invariants
1. **Local Source of Truth:** Each branch owns an independent, authoritative local database (`Branch Local DB`) hosted on its primary local node.
2. **Zero Mandatory Cloud Dependencies:** POS sales, inventory mutations, service order management, and cash drawers **must never** depend on an active internet connection or a central cloud database.
3. **Strict Branch Isolation:** Financial balances, physical stock counts, cash registers, digital deposits, and active service queues belonging to Branch A are strictly isolated from Branch B.
4. **No Complex Multi-Master Cloud Sync:** The system deliberately avoids fragile multi-master cloud database synchronization that leads to transactional split-brain, corrupted inventory counts, or race conditions.

---

## 2. Multi-Branch Hierarchy

```
Business (Enterprise Root)
   ├── License Entitlement (Max Branches, Max Terminals, Capabilities)
   ├── Global Master Catalog (Product Templates, Categories, Base SKU)
   │
   └── Branch [1..N] (Physical Store / Outlet)
         ├── BusinessProfile (Store Name, Address, Receipt Header)
         ├── BusinessModel (e.g. RETAIL_WARUNG, RETAIL_HEALTH, DIGITAL_KIOSK)
         ├── Capabilities (Enabled feature modules for this specific branch)
         ├── Local Database (SQLite / Room on Branch Host)
         ├── Inventory & Stock Ledgers (Branch-specific on-hand stock & pricing)
         ├── CashRegister & Cashier Sessions (Branch cash drawers & shift records)
         ├── DigitalDepositAccount (Branch-specific PPOB balance)
         ├── ServiceOrder Queue (Branch-specific job orders)
         └── Authorized Terminals [1..M] (Owner, Manager, Cashier Devices)
```

---

## 3. Operational Guarantees

| Dimension | Guarantee | Architectural Implementation |
|---|---|---|
| **Data Locality** | 100% Local Execution | All transactions write directly to the local SQLite database on the Branch Host. |
| **Offline Resilience** | Zero Downtime | Complete functionality without internet; transactions proceed at full LAN speed. |
| **Data Privacy** | Merchant Sovereignty | Merchant transaction records reside locally on their own hardware; no cloud data leakage. |
| **Branch Autonomy** | Failure Isolation | If Branch A's network or hardware fails, Branch B and C continue operating with zero disruption. |
| **Auditability** | Complete Attribution | Every transactional record logs `businessId`, `branchId`, `userId`, `deviceId`, and `cashierSessionId`. |
