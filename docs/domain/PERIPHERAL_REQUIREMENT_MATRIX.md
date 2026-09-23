# Peripheral Requirement Matrix

> **Document Status:** CANONICAL REFERENCE  
> **Target Scope:** Hardware peripheral compatibility, connection protocols, and UX requirements across all business types.

---

## 1. Peripheral Categories & Protocols

1. **Thermal Receipt Printers:** 58mm and 80mm ESC/POS printers via Bluetooth SPP/BLE, USB OTG, or Network (Wi-Fi/LAN).
2. **Barcode / QR Scanners:** Camera scanner (MLKit/Zebra), USB HID scanner, Bluetooth Keyboard Wedge scanner.
3. **Kitchen / Ticket Printers:** Dedicated 80mm impact/thermal printers over Network (Wi-Fi/LAN) or Bluetooth.
4. **Label / Sticker Printers:** Adhesive tag printers for laundry bags, service vehicle tags, and product barcode stickers.

---

## 2. Peripheral Matrix by Business Model

| Business Model | Thermal Receipt Printer | Barcode / QR Scanner | Kitchen / Ticket Printer | Label / Sticker Printer | Primary Connection Preference |
|---|---|---|---|---|---|
| **RETAIL_WARUNG** | Essential (58mm/80mm) | Highly Recommended (Camera / BT) | Optional | Optional | Bluetooth / USB OTG |
| **DIGITAL_KIOSK / KONTER** | Essential (Token/Receipt slip) | Optional (for accessories) | None | None | Bluetooth |
| **FOOD_BEVERAGE_CAFE** | Essential (Customer receipt) | Optional (Table QR scan) | Essential (Kitchen order slip)| None | Bluetooth + Wi-Fi/LAN |
| **SERVICE_WORKSHOP** | Essential (Service invoice) | Optional (Parts barcode scan) | None | Recommended (Vehicle key tag) | Bluetooth / USB |
| **LAUNDRY** | Essential (Customer claim ticket)| Optional (Order QR check-in) | None | Highly Recommended (Laundry bag tag)| Bluetooth |
| **PERCETAKAN** | Essential (Order spec receipt) | Optional | None | Recommended (Job box sticker)| Bluetooth / USB |
| **FOTOCOPY** | Recommended (Simple receipt) | Optional | None | None | Bluetooth |
| **APOTEK / PHARMACY** | Essential (Receipt + Drug note) | Essential (Rapid 1D barcode scan)| None | Recommended (Dosage label)| USB OTG / Bluetooth |
| **ATK (Stationery)** | Essential (Itemized receipt) | Essential (High-speed barcode)| None | Optional (Price tags) | USB / Bluetooth HID |
| **GENERAL_STORE** | Essential | Essential | Optional | Optional | USB / Bluetooth |

---

## 3. Architecture & Domain Impact

1. **Decoupled Printer Layer (`:core:printer`):** Business logic and domain transactions **must never** depend directly on hardware SDKs. The domain produces an immutable `ReceiptData` snapshot; the `:core:printer` module formats and dispatches ESC/POS commands.
2. **Scanner Input Flexibility:** POS input fields support standard Android Soft Keyboard, USB/Bluetooth Hardware Keyboard Wedge (intercepting Enter key events), and Camera-based scanning transparently.
