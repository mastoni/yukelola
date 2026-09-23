# Local Network Architecture & Zero-Cloud Offline Resilience

> **Document Status:** CANONICAL REFERENCE  
> **Target Scope:** In-store local network topology, zero-cloud communication protocols, mDNS service discovery, and network fault tolerance matrix.

---

## 1. In-Store Local Area Network Topology

To operate multiple cashier terminals seamlessly without internet access or external cloud infrastructure, the branch relies entirely on standard **Local Wi-Fi / Local Area Network (LAN)**:

```
                            ┌────────────────────────┐
                            │ Standard Wi-Fi Router  │ (or Phone Mobile Hotspot)
                            │   (NO INTERNET NEEDED) │
                            └───────────┬────────────┘
                                        │
             ┌──────────────────────────┼──────────────────────────┐
             │                          │                          │
             ▼                          ▼                          ▼
  ┌─────────────────────┐    ┌─────────────────────┐    ┌─────────────────────┐
  │  Branch Host Node   │    │ Secondary Cashier 1 │    │ Secondary Cashier 2 │
  │  (Tablet / POS Box) │    │   (Android Phone)   │    │  (Android Tablet)   │
  │  • Authoritative DB │    │   • POS Client      │    │   • POS Client      │
  │  • Local REST/RPC   │    │   • Local mDNS      │    │   • Local mDNS      │
  │  • mDNS Broadcaster │    │   • Pairing Token   │    │   • Pairing Token   │
  └─────────────────────┘    └─────────────────────┘    └─────────────────────┘
```

---

## 2. Device Discovery & Pairing Protocol

1. **Zero-Configuration Discovery (mDNS / NSD):**
   - The Branch Host advertises its presence on the local network via Android Network Service Discovery (NSD) using service type `_yukelola-branch._tcp`.
   - Secondary cashier devices scan the local network and discover the Host's local IP address automatically without manual IP configuration.
2. **Secure Local Pairing:**
   - On first connection, the secondary terminal requests pairing.
   - The Manager/Owner confirms pairing on the Host screen or enters a 6-digit Branch PIN.
   - The Host issues an encrypted local JWT token (`BranchSessionToken`) stored in Android EncryptedSharedPreferences.

---

## 3. Network Fault Tolerance & State Matrix

| Environmental Network State | POS Checkout Status | Inventory / Cash Status | Operational Capability |
|---|---|---|---|
| **A. Internet UP, Local LAN UP** | **100% Operational** | Fully Synchronized across Terminals | Full operations + Background Cloud Backup & License Check. |
| **B. Internet DOWN, Local LAN UP** | **100% Operational** | Fully Synchronized across Terminals | Full multi-cashier operations. Zero interruption to POS, receipts, or drawers. |
| **C. Internet DOWN, Local LAN DOWN (Single Terminal)** | **100% Operational** | Local DB on Device | Single standalone terminal operates completely offline without network. |
| **D. Branch Host Unreachable (Multi-Terminal)** | **Secondary Cashier Halted** | Protected | Secondary terminals pause checkout and alert cashier to reconnect to Host Wi-Fi. (Prevents split-brain). |
| **E. Owner Device Disconnected** | **100% Operational** | Unaffected | Cashier operations continue unaffected on the Branch Host. |
