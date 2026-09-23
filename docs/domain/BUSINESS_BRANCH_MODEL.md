# Business & Branch Domain Model

> **Document Status:** CANONICAL REFERENCE  
> **Target Scope:** Domain hierarchy, Business vs. Branch entity separation, Business Model assignment, and Capability scoping.

---

## 1. Domain Hierarchy: Business vs. Branch

In real-world UMKM operations, an enterprise entity (`Business`) often owns multiple physical locations (`Branch`), each with distinct operational identities, physical inventory, and local staff.

```
                      ┌────────────────────────────┐
                      │          Business          │
                      ├────────────────────────────┤
                      │ - id: UUID                 │
                      │ - legalName: String        │
                      │ - ownerUserId: UUID        │
                      │ - createdAt: Timestamp     │
                      └─────────────┬──────────────┘
                                    │ 1..*
                                    ▼
                      ┌────────────────────────────┐
                      │           Branch           │
                      ├────────────────────────────┤
                      │ - id: UUID                 │
                      │ - businessId: UUID         │
                      │ - code: String (e.g. CAB01)│
                      │ - name: String             │
                      │ - businessProfile: Profile │
                      │ - businessModel: Model     │
                      │ - enabledCapabilities: Set │
                      │ - isActive: Boolean        │
                      └────────────────────────────┘
```

---

## 2. Location of Business Model & Business Profile

### Decision: Branch-Level Business Model Assignment
$$\mathbf{Business} \longrightarrow \mathbf{Branch} \longrightarrow \{\mathbf{BusinessProfile},\ \mathbf{BusinessModel},\ \mathbf{Capabilities}\}$$

### Rationale
A single business entity may operate heterogeneous retail and service branches. For example:
- **`Business PT Sukses Mandiri`**:
  - **`Branch 1 (Pusat Pasar)`**: Operates as `RETAIL_WARUNG` (Groceries, Fast POS).
  - **`Branch 2 (Klinik & Apotek)`**: Operates as `RETAIL_HEALTH` (Pharmaceutical multi-unit retail).
  - **`Branch 3 (Kios Stasiun)`**: Operates as `DIGITAL_KIOSK` (PPOB, Phone accessories).

If `BusinessModel` were locked at the top-level `Business` root, the merchant would be forced to create separate software subscriptions, separate licenses, and separate master company accounts for different operational formats.

By assigning `BusinessModel` and `BusinessProfile` at the **Branch** level:
1. Each branch presents the appropriate UX dashboard, navigational priorities, and transactional flows suited to its local operation.
2. The owner manages all branches under a unified business account.
3. Master product definitions can still be shared where applicable.

---

## 3. Capability Scoping Matrix

Capabilities must be scoped to their correct functional domain layer:

| Capability / Feature | Scope | Rationale |
|---|---|---|
| **License Subscription Tier** | **Business-Level** | Governs the total allowed branches, cashier terminals, and enterprise-wide entitlements. |
| **Global Master Catalog** | **Business-Level** | Standardizes SKUs, barcodes, and base categories across all enterprise branches. |
| **`BusinessModel` Assignment** | **Branch-Level** | Dictates the operational identity and workflow of a specific branch. |
| **`DIGITAL_SERVICE` (PPOB)** | **Branch-Level** | Enabled per branch depending on whether that branch has an active agent deposit and operator. |
| **`FUEL` (Bensin Eceran)** | **Branch-Level** | Enabled only at branches with physical fuel distribution infrastructure. |
| **`SERVICE_WORKSHOP` Queue** | **Branch-Level** | Active only at repair bay locations. |
| **`CUSTOMER_DEBT` (Kasbon)** | **Branch-Level** | Governed by branch credit policies and local customer relationships. |
| **User Role & Permissions** | **User-Level** | A user carries roles (`OWNER`, `MANAGER`, `CASHIER`) determining allowed operations. |
| **Peripheral Device Binding** | **Device-Level** | Configures physical hardware (Bluetooth printer MAC address, USB barcode scanner) for a specific terminal. |
