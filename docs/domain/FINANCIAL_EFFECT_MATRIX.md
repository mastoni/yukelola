# Financial Effect Matrix

> **Document Status:** CANONICAL REFERENCE  
> **Target Scope:** Financial ledgers, monetary mutations, gross profit recognition, and balance invariants across all business models.

---

## 1. Core Financial Ledger Separation

Yukelola enforces strict separation between four distinct financial categories:

$$\mathbf{CashRegister} \ne \mathbf{DigitalDepositAccount} \ne \mathbf{CustomerDebt} \ne \mathbf{SupplierDebt}$$

$$\mathbf{Cash\ Balance} \ne \mathbf{Business\ Profit}$$
$$\mathbf{Deposit\ Top\text{-}Up} \ne \mathbf{Revenue}$$

---

## 2. Financial Effect Matrix by Business Model

| Business Model | Transaction Event | CashRegister Effect | DigitalDeposit Effect | Customer Debt Effect | Supplier Debt Effect | Revenue & Profit Recognition |
|---|---|---|---|---|---|---|
| **RETAIL_WARUNG** | Cash Retail Sale | $+\text{Paid Amount}$ | Untouched | None | None | $\text{Revenue} = \text{Total}$; $\text{Profit} = \text{Subtotal} - \text{COGS}$ |
| | Credit Sale (Kasbon) | $+0$ (or partial) | Untouched | $+\text{Unpaid Balance}$ | None | $\text{Revenue} = \text{Total}$; Cash realized upon debt repayment |
| | Stock Purchase (Cash) | $-\text{Purchase Cost}$ | Untouched | None | None | Expense / Asset transfer (Cash $\rightarrow$ Inventory) |
| | Stock Purchase (Credit) | $0$ | Untouched | None | $+\text{Payable Balance}$ | Payable recorded; Cash debited upon supplier payment |
| **DIGITAL_KIOSK / KONTER**| Deposit Top-Up (via Cash) | $-\text{TopUp Amount}$ | $+\text{TopUp Amount}$ | None | None | **Capital Transfer (Zero Revenue, Zero Profit)** |
| | Digital Product Sale (Cash)| $+\text{Selling Price}$ | $-\text{Cost Price}$ | None | None | $\text{Gross Profit} = \text{Selling Price} - \text{Cost Price}$ |
| | Digital Transaction Failed | $+0$ (or cash refund) | $+\text{Cost Price (Refund)}$| None | None | Reversal/Refund mutation; zero net profit |
| **LAUNDRY** | Order Intake + Down Payment | $+\text{Down Payment}$ | Untouched | Tracked on Order | None | Revenue realized on order; Cash increases by DP |
| | Order Pickup & Full Pay | $+\text{Remaining Balance}$| Untouched | Order Settled | None | Total Cash In = Total Order Price |
| **SERVICE_WORKSHOP** | Spare Part + Labor Order | $+\text{Payment Amount}$ | Untouched | Optional Debt | None | $\text{Labor Fee} + (\text{Parts Price} - \text{Parts Cost})$ |
| **PERCETAKAN** | Print Job + Down Payment | $+\text{Down Payment}$ | Untouched | Tracked on Order | None | Material Cost + Finishing Margin |
| **FOTOCOPY** | Per-Page Instant Sale | $+\text{Cash Collected}$| Untouched | None | None | $\text{Pages} \times (\text{Unit Price} - \text{Paper Cost})$ |
| **APOTEK / ATK** | Barcode Retail Sale | $+\text{Cash Collected}$| Untouched | Optional Debt | None | Standard Retail Gross Margin |

---

## 3. Financial Invariants & Integrity Rules

1. **Top-Up Isolation Invariant:** Adding funds to `DigitalDepositAccount` represents an internal asset relocation ($\text{Cash} \rightarrow \text{Digital Deposit}$) and **must never** be counted in sales revenue or gross profit charts.
2. **Gross Margin Invariant on Digital Sales:** For digital transactions, gross profit is recognized immediately as the spread:
   $$\text{Digital Gross Profit} = \text{Customer Selling Price} - \text{Distributor Cost Price}$$
3. **Compensating Reversal Invariant:** When a digital transaction fails, `DigitalDepositAccount` must receive a traceable `DigitalDepositMutation.REFUND` or `REVERSAL` corresponding exactly to the debited cost price.
4. **Non-Negative Drawer Balance Policy:** Cash drawer mutations must validate sufficient physical cash before executing cash-out expenses or cash supplier payments.
5. **Debt Reduction Invariant:** A debt payment received from a customer decreases `CustomerDebt.remainingAmount` and records a corresponding inflow in `CashRegister` without double-counting historical sales revenue.
