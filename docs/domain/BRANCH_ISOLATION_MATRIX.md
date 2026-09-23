# Branch Isolation Matrix & Master Catalog Architecture

> **Document Status:** CANONICAL REFERENCE  
> **Target Scope:** Data isolation boundaries, branch data sovereignty, and Global Master Catalog vs. Branch Override architecture.

---

## 1. Domain Aggregate Isolation Matrix

Yukelola enforces strict isolation boundaries across all operational domain aggregates:

| Domain Aggregate | Isolation Boundary | Cross-Branch Visibility Rules |
|---|---|---|
| **Inventory & Stock Counts** | **Strictly Branch-Isolated** | Branch A stock is physically located at Branch A. Branch B cannot view or deduct Branch A's stock during checkout. |
| **CashRegister & Cash Drawers** | **Strictly Branch-Isolated** | Cash physical drawers belong exclusively to the specific branch and active cashier shift. |
| **CustomerDebt (Kasbon)** | **Branch-Isolated Ledger** | Debt ledger is managed by the branch where credit was extended. (Optional global customer profile lookup). |
| **SupplierDebt (Hutang Kulakan)** | **Strictly Branch-Isolated** | Payables are tied to the branch purchase invoice and local branch tempo schedule. |
| **DigitalDepositAccount** | **Strictly Branch-Isolated** | PPOB deposit balance belongs to the branch that funded the account. Branch A deposit cannot be spent by Branch B. |
| **ServiceOrder Queues** | **Strictly Branch-Isolated** | Laundry bags, repair vehicles, and print jobs exist physically at a specific branch workshop. |
| **Sales & Purchase Histories** | **Strictly Branch-Isolated** | Daily transaction logs remain on the local branch host. Consolidated views exist only via Owner reports. |

---

## 2. Global Master Catalog vs. Branch Product Overrides

To balance enterprise-wide standardization with local operational flexibility, Yukelola adopts a **Global Product Template with Branch Overrides** architecture:

```
                          ┌─────────────────────────────┐
                          │    Global Product Master    │
                          │      (Business Scope)       │
                          ├─────────────────────────────┤
                          │ - id: UUID                  │
                          │ - sku: String               │
                          │ - barcode: String           │
                          │ - name: String              │
                          │ - categoryId: UUID          │
                          │ - baseUnit: String          │
                          │ - defaultSellingPrice: Long │
                          │ - defaultCostPrice: Long    │
                          └──────────────┬──────────────┘
                                         │ 1..*
                                         ▼
                          ┌─────────────────────────────┐
                          │    Branch Product Ledger    │
                          │       (Branch Scope)        │
                          ├─────────────────────────────┤
                          │ - branchId: UUID            │
                          │ - productId: UUID           │
                          │ - stock: Double             │
                          │ - minStock: Double          │
                          │ - localSellingPrice: Long?  │ (Override default if set)
                          │ - localCostPrice: Long?     │ (Override default if set)
                          │ - isAvailable: Boolean      │ (Enable/disable for branch)
                          └─────────────────────────────┘
```

### Architectural Benefits
1. **Zero Duplicate Data Entry:** The business owner creates a product once in the Master Catalog (e.g., "Minyak Goreng 1L", Barcode `899123456789`).
2. **Local Pricing Autonomy:** Branch A (Urban Center) can sell at Rp 18,000, while Branch B (Rural) can sell at Rp 19,000 based on local transport costs.
3. **Independent Stock Tracking:** Stock counts, low-stock reorder thresholds, and inventory opname operations occur strictly within the local branch ledger.
