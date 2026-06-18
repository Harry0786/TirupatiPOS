-- ============================================================
-- TIRUPATI POS - Database Alignment & Stabilization (003_database_alignment.sql)
-- ============================================================

-- 1. Add UNIQUE constraints to business numbers
ALTER TABLE public.estimates ADD CONSTRAINT unique_estimate_number UNIQUE (estimate_number);
ALTER TABLE public.invoices ADD CONSTRAINT unique_invoice_number UNIQUE (invoice_number);

-- 2. Create performance indexes for frequent lookups
CREATE INDEX IF NOT EXISTS idx_estimates_number ON public.estimates(estimate_number);
CREATE INDEX IF NOT EXISTS idx_estimates_customer ON public.estimates(customer_name);
CREATE INDEX IF NOT EXISTS idx_invoices_number ON public.invoices(invoice_number);
CREATE INDEX IF NOT EXISTS idx_products_code ON public.products(item_code);
CREATE INDEX IF NOT EXISTS idx_products_name ON public.products(item_name);

-- 3. Drop remote pending_operations table (keep it local Room only)
DROP TABLE IF EXISTS public.pending_operations;
