-- ============================================================
-- TIRUPATI POS - Estimate Alignment Migration (002_estimate_alignment.sql)
-- ============================================================

-- 1. Alter public.products: Remove gst_percent
ALTER TABLE public.products DROP COLUMN IF EXISTS gst_percent;

-- 2. Alter public.estimates: Add columns, rename discount, drop bill_discount
ALTER TABLE public.estimates ADD COLUMN IF NOT EXISTS customer_phone text default '';
ALTER TABLE public.estimates ADD COLUMN IF NOT EXISTS customer_address text default '';
ALTER TABLE public.estimates RENAME COLUMN item_discount TO discount_total;
ALTER TABLE public.estimates DROP COLUMN IF EXISTS bill_discount;

-- 3. Alter public.estimate_items: Add product_id, rename columns, add timestamps
ALTER TABLE public.estimate_items ADD COLUMN IF NOT EXISTS product_id text;
ALTER TABLE public.estimate_items RENAME COLUMN rate TO selling_rate;
ALTER TABLE public.estimate_items RENAME COLUMN amount TO line_total;
ALTER TABLE public.estimate_items ADD COLUMN IF NOT EXISTS created_at bigint not null default (extract(epoch from now()) * 1000)::bigint;
ALTER TABLE public.estimate_items ADD COLUMN IF NOT EXISTS updated_at bigint not null default (extract(epoch from now()) * 1000)::bigint;

-- 4. Alter public.invoices: Add columns, rename discount, drop bill_discount
ALTER TABLE public.invoices ADD COLUMN IF NOT EXISTS customer_phone text default '';
ALTER TABLE public.invoices ADD COLUMN IF NOT EXISTS customer_address text default '';
ALTER TABLE public.invoices RENAME COLUMN item_discount TO discount_total;
ALTER TABLE public.invoices DROP COLUMN IF EXISTS bill_discount;

-- 5. Alter public.invoice_items: Add product_id, rename columns, add timestamps
ALTER TABLE public.invoice_items ADD COLUMN IF NOT EXISTS product_id text;
ALTER TABLE public.invoice_items RENAME COLUMN rate TO selling_rate;
ALTER TABLE public.invoice_items RENAME COLUMN amount TO line_total;
ALTER TABLE public.invoice_items ADD COLUMN IF NOT EXISTS created_at bigint not null default (extract(epoch from now()) * 1000)::bigint;
ALTER TABLE public.invoice_items ADD COLUMN IF NOT EXISTS updated_at bigint not null default (extract(epoch from now()) * 1000)::bigint;
