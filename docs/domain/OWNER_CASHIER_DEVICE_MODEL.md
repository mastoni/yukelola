# Owner, Manager, Cashier & Device Domain Model

> **Document Status:** CANONICAL REFERENCE  
> **Target Scope:** User roles, authority matrix, device lifecycle, terminal activation, and session binding.

---

## 1. Actor Typology & Role Hierarchy

Yukelola establishes three fundamental roles governing administrative and operational capabilities:

```
                            ┌────────────────────────┐
                            │         OWNER          │ (Business-Wide Authority)
                            └───────────┬────────────┘
                                        │
                                        ▼
                            ┌────────────────────────┐
                            │        MANAGER         │ (Branch-Wide Authority)
                            └───────────┬────────────┘
                                        │
                                        ▼
                            ┌────────────────────────┐
                            │        CASHIER         │ (Shift/Transaction Authority)
                            └────────────────────────┘
```

### Role Authority Matrix

| Operational Capability | `OWNER` | `MANAGER` | `CASHIER` |
|---|---|---|---|
| **License & Subscription Management** | **Full** | None | None |
| **Create / Deactivate Branches** | **Full** | None | None |
| **User & Staff Account Provisioning** | **Full** | Branch Staff Only | None |
| **Global Product Catalog Management** | **Full** | Branch Overrides | Read-Only |
| **Branch Inventory Stock Adjustments (Opname)** | **Full** | **Full** | None |
| **Supplier Purchasing & Supplier Payables** | **Full** | **Full** | None |
| **Cash Drawer Reconciliation & Shift Close** | **Full** | **Full** | Own Shift Only |
| **Execute POS Sales & Digital Transactions** | **Full** | **Full** | **Full** |
| **Cancel Completed Transactions / Issue Refunds** | **Full** | **Full** (with audit) | Supervisor Approval |
| **Customer Credit / Debt Approval (Kasbon)** | **Full** | **Full** | Policy-bounded |
| **Financial P&L and Profit Reports** | **Full (All Branches)** | **Branch Only** | None (End-of-shift only) |

---

## 2. Device Model & Terminal Lifecycle

A `Device` represents a physical Android phone, tablet, or POS terminal operating within a branch.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                               DEVICE LIFECYCLE                              │
├─────────────────┬──────────────────┬─────────────────┬──────────────────────┤
│ 1. REGISTRATION │ 2. ACTIVATION    │ 3. SESSION OPEN │ 4. REPLACEMENT       │
├─────────────────┼──────────────────┼─────────────────┼──────────────────────┤
│ App installed;  │ Owner/Manager    │ Cashier logs in │ Device replaced;     │
│ UUID generated  │ pairs device to  │ with PIN; starts│ unbind from branch;  │
│ locally         │ Branch with OTP  │ CashierSession  │ zero license penalty │
└─────────────────┴──────────────────┴─────────────────┴──────────────────────┘
```

### Device Entity Structure
- `id`: UUID (Locally generated installation fingerprint)
- `businessId`: UUID
- `branchId`: UUID (Branch to which device is currently assigned)
- `deviceName`: String (e.g., "Tablet Kasir Utama", "HP Kasir 2")
- `deviceType`: Enum (`MAIN_HOST_TABLET`, `SECONDARY_CASHIER_TERMINAL`, `MANAGER_PHONE`, `OWNER_PHONE`)
- `pairedAt`: Timestamp
- `isActive`: Boolean

### Flexible Hardware Replacement Guarantee
Yukelola strictly prohibits permanent, non-recoverable hardware fingerprint locking. If a cashier tablet is broken, lost, or upgraded:
1. The `OWNER` or `MANAGER` can deactivate the old device from the branch dashboard.
2. A new device is paired via a secure 6-digit Branch Activation Code.
3. No external support ticket or licensing penalty is incurred.
