# Local Database Architecture & Concurrency Control

> **Document Status:** CANONICAL REFERENCE  
> **Target Scope:** Local database topology evaluation, branch database ownership, multi-cashier concurrency control, and transactional attribution.

---

## 1. Evaluation of Local Database Topologies

To guarantee data integrity in a multi-cashier branch without cloud dependencies, we evaluated two architectural models:

```
MODEL A: Independent Isolated DB per Device      MODEL B: Unified Branch Local Node (Authoritative)
┌─────────────────────────────────────────┐     ┌─────────────────────────────────────────┐
│              BRANCH LAN                 │     │               BRANCH LAN                │
│ ┌─────────┐   ┌─────────┐   ┌─────────┐ │     │         ┌─────────────────────┐         │
│ │Device 1 │   │Device 2 │   │Device 3 │ │     │         │  Branch Host Node   │         │
│ │(LocalDB)│   │(LocalDB)│   │(LocalDB)│ │     │         │ (Authoritative DB)  │         │
│ └────┬────┘   └────┬────┘   └────┬────┘ │     │         └──────────┬──────────┘         │
│      └─────────────┼─────────────┘      │     │         ┌──────────┼──────────┐         │
│          Multi-Master Peer Sync         │     │         ▼          ▼          ▼         │
│         (HIGH CONFLICT & CORRUPTION)    │     │      Cashier 1  Cashier 2    Owner      │
└─────────────────────────────────────────┘     └─────────────────────────────────────────┘
```

### Comparative Analysis Matrix

| Evaluation Dimension | Model A: Independent DB per Device | Model B: Unified Branch Local Node (Selected) |
|---|---|---|
| **Inventory Stock Consistency** | **Catastrophic Failure Risk.** Two cashiers selling the last item concurrently result in negative or corrupt stock counts after asynchronous peer sync. | **100% ACID Guaranteed.** All inventory decrements execute sequentially against the authoritative branch database engine. |
| **Transaction Numbering** | High collision risk ($TRX\text{-}001$ generated on two devices simultaneously). Requires complex composite sequence offsets. | **Deterministic Sequential Numbering.** Sequence counters are managed directly by the authoritative branch database. |
| **Cash Drawer Integrity** | Fragmented across devices; difficult to reconcile shared cash drawers or supervisor cash pickups. | **Unified Branch Drawer + Cashier Session Ledgers.** Full traceability per cashier shift with real-time branch consolidation. |
| **ServiceOrder Queue** | State transition conflicts (e.g. Cashier 1 marks order `IN_PROGRESS` while Cashier 2 cancels it offline). | **Single Truth State Machine.** Immediate real-time status updates across all connected tablets. |
| **Data Recovery & Backup** | Fragmented; requires aggregating partial SQLite files from multiple mobile phones. | **Clean Single-File Backup.** Complete branch state resides in a single, well-defined SQLite database snapshot. |

### Architectural Decision: Adopt Model B (Unified Branch Local Node)
Yukelola adopts **Model B**:
- Each branch designates a **Primary Branch Terminal** (typically the main cash register tablet or a dedicated in-store Android node) that hosts the authoritative local SQLite/Room database.
- Secondary cashier terminals, supervisor phones, and owner tablets interact with the Branch Host over the local Wi-Fi network via lightweight local REST/RPC requests.
- **Single-Device Fallback:** For small single-terminal warungs/kiosks, the single device acts as both Host and Terminal with zero network overhead.

---

## 2. Multi-Cashier Concurrency & Race Condition Prevention

Inside a multi-terminal branch, the Branch Host enforces deterministic SQLite transactions with Write-Ahead Logging (`WAL` mode):

1. **Inventory Reservation Invariant:**
   ```sql
   -- Atomic stock decrement with optimistic condition
   UPDATE products 
   SET stock = stock - :quantity, updated_at = :timestamp 
   WHERE id = :productId AND stock >= :quantity;
   ```
   If the update returns 0 affected rows, the transaction is immediately rejected with `OUT_OF_STOCK`, preventing overselling.
2. **Deterministic Transaction Sequence Generator:**
   Transaction numbers are formatted as `TRX-{BRANCH_CODE}-{YYYYMMDD}-{SEQUENCE}` generated inside an atomic database transaction.
3. **Cashier Session Isolation:**
   Each cashier operates within their own `CashierSession`. Cash inflows from sales are tagged with the active `cashierSessionId`, allowing distinct cash drawer reconciliation per cashier shift.

---

## 3. Transaction Attribution Standard

To guarantee end-to-end accountability across multi-user and multi-terminal operations, **every persistent transactional aggregate** (`Sale`, `Purchase`, `ServiceOrder`, `DigitalTransaction`, `CashMutation`, `StockAdjustment`) must include complete attribution metadata:

$$\mathbf{Attribution} = \{\mathbf{businessId},\ \mathbf{branchId},\ \mathbf{userId},\ \mathbf{deviceId},\ \mathbf{cashierSessionId},\ \mathbf{createdAt}\}$$

| Attribute Field | Type | Purpose |
|---|---|---|
| `businessId` | `UUID` | Global tenant identity. |
| `branchId` | `UUID` | Physical branch outlet where the transaction occurred. |
| `userId` | `UUID` | Authenticated staff member/operator who executed the action. |
| `deviceId` | `UUID` | Hardware terminal on which the action was initiated. |
| `cashierSessionId` | `UUID?` | Active cash drawer shift session (for POS sales & cash mutations). |
| `createdAt` | `Timestamp` | Exact local device timestamp of transaction creation. |
