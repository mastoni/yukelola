# Cross-Branch Reporting & Consolidation Architecture

> **Document Status:** CANONICAL REFERENCE  
> **Target Scope:** Multi-branch reporting mechanisms in the absence of a central cloud database, data aggregation workflows, and operational trade-offs.

---

## 1. Architectural Philosophy: Privacy-First Offline Consolidation

Because Yukelola does not require or enforce an expensive real-time cloud transaction database, multi-branch reporting operates via **Local Snapshot Consolidation & Report Aggregation**:

```
 ┌──────────────────────┐         ┌──────────────────────┐         ┌──────────────────────┐
 │   Branch A Host DB   │         │   Branch B Host DB   │         │   Branch C Host DB   │
 └──────────┬───────────┘         └──────────┬───────────┘         └──────────┬───────────┘
            │ Shift Backup Snapshot          │ Shift Backup Snapshot          │ Shift Backup Snapshot
            ▼                                ▼                                ▼
 ┌────────────────────────────────────────────────────────────────────────────────────────┐
 │                      Owner Console / Device (Aggregator Engine)                        │
 ├────────────────────────────────────────────────────────────────────────────────────────┤
 │ • Imports Encrypted Branch Snapshots via Google Drive or Local File                    │
 │ • Reads Attribution Metadata (branchId, timestamps, sales, expenses)                  │
 │ • Generates Consolidated P&L, Inventory Valuation, and Revenue Comparison Reports     │
 └────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Consolidation Workflows

1. **Automated Cloud Drive Aggregation (When Internet Available):**
   - Each branch automatically deposits its encrypted shift close snapshot (`.ykbak`) into the Owner's private Google Drive folder.
   - The Owner’s Android App reads the branch snapshots in read-only mode and compiles a consolidated multi-branch dashboard.
2. **Offline Manual File Import (When Internet Unavailable):**
   - Branch managers export daily summary files or encrypted `.ykbak` snapshots via USB OTG, SD card, or local Wi-Fi Direct to the Owner device.
   - The Owner app ingests the files and produces consolidated reports locally.

---

## 3. Explicit Trade-offs & Operational Boundaries

| Capability | Supported in Local-First Architecture | Cloud-First Real-Time Alternative (Out of Scope) |
|---|---|---|
| **Data Privacy & Ownership** | **100% Merchant Owned & Encrypted** | Stored on third-party cloud servers |
| **Server Infrastructure Costs** | **Zero Recurring Database Server Costs** | High recurring cloud DB & egress fees |
| **Offline Reliability** | **100% Guaranteed Uninterrupted** | Prone to cloud outages & internet lag |
| **Reporting Latency** | **Shift-based / Periodic** (Near real-time on backup upload) | Sub-second real-time streaming |
| **Cross-Branch Stock Transfer**| **Batch Dispatch / Receive Workflow** | Real-time global inventory locking |
