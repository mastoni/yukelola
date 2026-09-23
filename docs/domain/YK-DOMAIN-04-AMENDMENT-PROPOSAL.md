# YK-DOMAIN-04 — Controlled Contract Amendment Proposal

> **Proposal Target:** Product & Domain Contract v1.1.0 and YK-DOMAIN-03 Amendment Proposal  
> **Status:** PROPOSED AMENDMENT FOR NEXT GATE  
> **Governance:** Non-breaking additive extension. Preserves all locked v1.1.0 rules.

---

## 1. Background & Discovered Multi-Branch Gap

Product & Domain Contract v1.1.0 defines single-tenant `Business` and `BusinessProfile` aggregates. To support multi-branch operations, multi-cashier shifts, and local-first branch data isolation without forcing merchants to manage multiple disjoint applications, the domain contract requires additive architectural structures.

---

## 2. Proposed Additive Domain Extensions

### Amendment A: `Branch` Aggregate
- **Entity:** `Branch`
- **Fields:** `id: UUID`, `businessId: UUID`, `code: String`, `name: String`, `businessProfile: BusinessProfile`, `businessModel: BusinessModel`, `enabledCapabilities: Set<Capability>`, `isActive: Boolean`, `createdAt: Timestamp`.

### Amendment B: `Device` & `CashierSession` Entities
- **Entity:** `Device` (`id: UUID`, `businessId: UUID`, `branchId: UUID`, `deviceName: String`, `deviceType: DeviceType`, `isActive: Boolean`).
- **Entity:** `CashierSession` (`id: UUID`, `branchId: UUID`, `userId: UUID`, `deviceId: UUID`, `openingBalance: Long`, `closingBalance: Long?`, `openedAt: Timestamp`, `closedAt: Timestamp?`, `status: SessionStatus`).

### Amendment C: Transactional Attribution Extension
Add standard attribution fields (`branchId: UUID`, `deviceId: UUID`, `cashierSessionId: UUID?`) to:
- `Sale` & `SaleItem`
- `Purchase` & `PurchaseItem`
- `ServiceOrder` & `ServiceOrderItem`
- `DigitalTransaction`
- `CashMutation`
- `StockAdjustment`

### Amendment D: `BranchProductOverride`
- **Entity:** `BranchProductOverride` (`branchId: UUID`, `productId: UUID`, `localSellingPrice: Long?`, `localCostPrice: Long?`, `stock: Double`, `minStock: Double`, `isAvailable: Boolean`).

---

## 3. Domain vs. Infrastructure Classification Matrix

| Concept | Domain | Infrastructure | Licensing | Security | UX Context | Persistence (Room) |
|---|---|---|---|---|---|---|
| **`Business`** | **Yes (Root)** | No | Yes | Yes | Yes | `businesses` table |
| **`Branch`** | **Yes (Aggregate)**| No | Yes | Yes | Yes | `branches` table |
| **`BusinessModel`** | **Yes (Enum)** | No | No | No | **Yes (Layout)** | Stored in `branches` |
| **`User` & `Role`** | **Yes** | No | No | Yes | Yes | `users` table |
| **`Device`** | **Yes** | Yes (Hardware)| Yes (Seat) | Yes | No | `devices` table |
| **`CashierSession`** | **Yes** | No | No | No | Yes | `cashier_sessions` table |
| **`BranchProductOverride`**| **Yes** | No | No | No | Yes | `branch_products` table |
| **`Local Network (LAN)`**| No | **Yes (mDNS/NSD)**| No | Yes (Pairing)| No | None (Ephemeral) |
| **`Backup (.ykbak)`** | No | **Yes (AES-GCM)**| No | Yes (Key) | Yes (Trigger) | Flat binary files |
| **`Google Drive Backup`**| No | **Yes (Drive API)**| No | Yes (OAuth) | Yes (Status) | Cloud blob storage |
| **`Google Sheets Export`**| No | **Yes (Sheets API)**| No | No | Yes (Export) | Spreadsheet file |

---

## 4. Impact Assessment & Risk Analysis

| Evaluation Dimension | Assessment | Impact & Mitigation |
|---|---|---|
| **Breaking Changes** | **ZERO (0)** | Completely non-breaking. Default single-store deployments map to `branchId = defaultBranchId`. |
| **Backward Compatibility** | **100%** | Single-device warungs operate without network overhead or mandatory branch management UI. |
| **Room Database Impact** | Clean Additive | Introduces `branches`, `devices`, `cashier_sessions`, and `branch_products` without altering existing tables. |
| **Recommendation** | **Adopt in YK-DB-01 / YK-DOMAIN-IMPL** | Lock into upcoming domain implementation phase. |
