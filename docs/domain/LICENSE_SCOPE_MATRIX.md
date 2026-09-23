# License Scope & Offline Validation Architecture

> **Document Status:** CANONICAL REFERENCE  
> **Target Scope:** Software licensing hierarchy, tier entitlements, offline cryptographic validation, and grace period policies.

---

## 1. Licensing Hierarchy & Entitlement Scoping

Yukelola’s licensing model aligns directly with the multi-branch enterprise topology:

```
                            ┌────────────────────────┐
                            │        LICENSE         │
                            ├────────────────────────┤
                            │ - tier: Enum           │
                            │ - maxBranches: Int     │
                            │ - maxDevicesPerBranch  │
                            │ - validUntil: Date     │
                            │ - capabilities: Set    │
                            └───────────┬────────────┘
                                        │
                                        ▼
                            ┌────────────────────────┐
                            │        BUSINESS        │
                            └───────────┬────────────┘
                                        │ 1..*
                                        ▼
                            ┌────────────────────────┐
                            │        BRANCHES        │
                            └───────────┬────────────┘
                                        │ 1..*
                                        ▼
                            ┌────────────────────────┐
                            │    USERS & DEVICES     │
                            └────────────────────────┘
```

### Licensing Entitlements Matrix

| License Tier | Max Branches | Max Terminals / Branch | Enabled Capabilities |
|---|---|---|---|
| **COMMUNITY (Free)** | 1 Branch | 1 Device | Basic Retail POS, Cash, Debt, Standard Inventory |
| **STARTER** | 1 Branch | Up to 2 Devices | Full Retail/Service POS, Digital Services, Multi-unit Inventory |
| **MULTI-BRANCH PRO** | Up to 5 Branches | Up to 5 Devices / Branch | Full Capabilities, Multi-Branch Overrides, Consolidated Export |
| **ENTERPRISE** | Custom | Unlimited | Custom Integration, Priority Support |

---

## 2. Offline License Validation & Grace Period Architecture

To prevent abrupt operational shutdowns in retail environments during network outages, Yukelola enforces **Cryptographically Signed Offline License Tokens**:

```
                          ┌─────────────────────────────┐
                          │   Yukelola License Server   │ (Cloud)
                          └──────────────┬──────────────┘
                                         │ Issues Asymmetric Signed Token (ECDSA / Ed25519)
                                         ▼
                          ┌─────────────────────────────┐
                          │     Local License Cache     │ (Stored on Android Device / Host)
                          │   - Payload: Business, Tier │
                          │   - Signature: Verified     │
                          │   - Expiry: Target Date     │
                          └──────────────┬──────────────┘
                                         │
                 ┌───────────────────────┴───────────────────────┐
                 │                                               │
                 ▼                                               ▼
     [Token Valid (Offline)]                           [Token Expired / Offline]
                 │                                               │
                 ▼                                               ▼
      Normal 100% POS Access                         30-Day Soft Grace Period
                                                                 │
                                                                 ▼
                                                  (Banner Warning, Never Locks Data)
```

### Offline Invariants
1. **Asymmetric Local Verification:** License validity is verified locally using an embedded public key. No internet ping is required for day-to-day POS operations.
2. **30-Day Soft Grace Period:** If a subscription expires while the device is offline, the POS enters a **Grace Period** where checkout remains 100% operational, displaying a non-intrusive renewal reminder.
3. **Data Sovereign Guarantee:** An expired license **never locks or deletes** local business databases. Export, report generation, and historical data retrieval remain permanently accessible.
