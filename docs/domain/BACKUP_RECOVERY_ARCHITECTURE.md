# Backup & Disaster Recovery Architecture

> **Document Status:** CANONICAL REFERENCE  
> **Target Scope:** Backup target evaluation (Google Drive vs. Google Sheets), database snapshot format, encryption, retention policies, and disaster recovery procedures.

---

## 1. Evaluation of Backup Targets: Google Drive vs. Google Sheets

A thorough architectural audit was conducted to evaluate whether Google Sheets or Google Drive is technically suitable as an operational backup and recovery destination:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       BACKUP TARGET TECHNICAL AUDIT                         │
├────────────────────────────────┬────────────────────────────────────────────┤
│ EVALUATION CRITERIA            │ GOOGLE SHEETS        │ GOOGLE DRIVE (BLOB) │
├────────────────────────────────┼──────────────────────┼─────────────────────┤
│ 1. ACID Transaction Guarantee  │ FAILED (No ACID)     │ PASSED (SQLite ACID)│
│ 2. Schema Fidelity & Types     │ FAILED (Text/String) │ PASSED (Full Binary)│
│ 3. Concurrency & Rate Limits   │ FAILED (100 req/min) │ PASSED (Chunked API)│
│ 4. Deterministic 1-Click Restore│ FAILED (Complex map) │ PASSED (Exact Clone)│
│ 5. Performance at Scale (50k trx)│ FAILED (Sheet lag)  │ PASSED (Compressed) │
│ 6. End-to-End Encryption       │ FAILED (Plain text)  │ PASSED (AES-256-GCM)│
└────────────────────────────────┴──────────────────────┴─────────────────────┘
```

### Architectural Verdict & Hard Safety Rule
1. **Google Sheets is UNSUITABLE as a Database Backup Target:**
   Using Google Sheets as a database backup/sync destination introduces fatal data corruption risks (cell truncation, type coercion, non-atomic multi-table writes, lack of foreign key enforcement, and API throttling). **Attempting bidirectional database synchronization via Google Sheets is strictly prohibited.**
2. **Role of Google Sheets:** Google Sheets is designated **strictly as an optional, one-way analytical export** (e.g., Export Monthly Sales to Google Sheets for accountant review).
3. **Official Backup Mechanism:** **Encrypted Room SQLite Binary Snapshots (`.ykbak` / AES-256-GCM encrypted database files)** uploaded to **Google Drive (App Data Folder / Private Drive)** or saved to local external SD/USB storage.

---

## 2. Backup Pipeline & Encryption Standard

```
┌─────────────────────────┐
│ Active SQLite / Room DB │
└────────────┬────────────┘
             │ 1. Atomic SQLite Online Backup / Vacuum into Snapshot
             ▼
┌─────────────────────────┐
│ Raw Database Snapshot   │
└────────────┬────────────┘
             │ 2. AES-256-GCM Encryption (Owner Master Key / Passphrase)
             ▼
┌─────────────────────────┐
│ Encrypted Package (.ykbak) ──► Checksum (SHA-256) Verification
└────────────┬────────────┘
             │
     ┌───────┴───────────────────────────────┐
     ▼                                       ▼
┌─────────────────────────┐         ┌─────────────────────────┐
│ Google Drive Backup     │         │ Local Storage / SD Card │
│ (Private App Data / User│         │ (Offline Safekeeping)   │
└─────────────────────────┘         └─────────────────────────┘
```

### Backup Triggers & Retention Policy
- **Automatic Shift Close Backup:** Triggered whenever a Manager or Cashier closes the daily cash register / shift.
- **Daily Scheduled Background Backup:** Executes at midnight or off-peak hours when connected to Wi-Fi.
- **Manual 1-Click Backup:** Instantly accessible to Owner/Manager from settings.
- **Retention Strategy:** Rolling grandfather-father-son retention (7 daily snapshots, 4 weekly snapshots, 12 monthly archives).

---

## 3. Disaster Recovery Scenarios

| Disaster Scenario | Recovery Procedure | Target RPO / RTO |
|---|---|---|
| **Scenario 1: Branch Host Tablet Damaged / Stolen** | 1. Activate new Android tablet.<br>2. Log in as `OWNER`.<br>3. Download latest encrypted `.ykbak` from Google Drive.<br>4. Enter Owner Passphrase $\rightarrow$ Restore complete Room DB.<br>5. Branch resumes operations. | **RPO:** $\le 24$ hours (or last shift close)<br>**RTO:** $\le 10$ minutes |
| **Scenario 2: Database File Corruption** | Automated integrity check on startup detects corruption $\rightarrow$ Offers 1-click restore from latest verified local snapshot. | **RPO:** Latest shift close<br>**RTO:** $\le 2$ minutes |
| **Scenario 3: Secondary Cashier Terminal Disconnected** | Reconnect to Branch Wi-Fi; reconnect to Branch Host; zero data loss (all data is on Host). | **RPO:** Zero (0)<br>**RTO:** Immediate |
| **Scenario 4: Accidental App Uninstallation** | Reinstall APK $\rightarrow$ Select "Restore from Google Drive / Local File" during onboarding setup. | **RPO:** Last backup<br>**RTO:** $\le 5$ minutes |
