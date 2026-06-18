-- ============================================================
-- TIRUPATI POS - Database Relationship & Sync Hardening (004_relationship_and_sync_hardening.sql)
-- Run this in the Supabase SQL Editor (https://supabase.com/dashboard)
-- ============================================================

-- 1. Apply Unique Constraints (safe drop and add)
ALTER TABLE public.companies DROP CONSTRAINT IF EXISTS unique_company_name;
ALTER TABLE public.companies ADD CONSTRAINT unique_company_name UNIQUE (name);

ALTER TABLE public.products DROP CONSTRAINT IF EXISTS unique_product_item_code;
ALTER TABLE public.products ADD CONSTRAINT unique_product_item_code UNIQUE (item_code);

-- 2. Apply Foreign Keys (safe drop and add)
ALTER TABLE public.products DROP CONSTRAINT IF EXISTS fk_products_company;
ALTER TABLE public.products ADD CONSTRAINT fk_products_company FOREIGN KEY (company_id) REFERENCES public.companies(id) ON DELETE CASCADE;

ALTER TABLE public.estimate_items DROP CONSTRAINT IF EXISTS fk_estimate_items_product;
ALTER TABLE public.estimate_items ADD CONSTRAINT fk_estimate_items_product FOREIGN KEY (product_id) REFERENCES public.products(id) ON DELETE SET NULL;

ALTER TABLE public.invoice_items DROP CONSTRAINT IF EXISTS fk_invoice_items_product;
ALTER TABLE public.invoice_items ADD CONSTRAINT fk_invoice_items_product FOREIGN KEY (product_id) REFERENCES public.products(id) ON DELETE SET NULL;

-- 3. Create Performance Indexes for Fast Lookups
CREATE INDEX IF NOT EXISTS idx_companies_name ON public.companies(name);
CREATE INDEX IF NOT EXISTS idx_products_company_id ON public.products(company_id);
CREATE INDEX IF NOT EXISTS idx_products_item_code ON public.products(item_code);
CREATE INDEX IF NOT EXISTS idx_products_item_name ON public.products(item_name);
CREATE INDEX IF NOT EXISTS idx_estimates_number ON public.estimates(estimate_number);
CREATE INDEX IF NOT EXISTS idx_estimates_customer ON public.estimates(customer_name);
CREATE INDEX IF NOT EXISTS idx_estimates_phone ON public.estimates(customer_phone);
CREATE INDEX IF NOT EXISTS idx_estimates_status ON public.estimates(status);
CREATE INDEX IF NOT EXISTS idx_invoices_number ON public.invoices(invoice_number);
CREATE INDEX IF NOT EXISTS idx_estimate_items_estimate_id ON public.estimate_items(estimate_id);
CREATE INDEX IF NOT EXISTS idx_estimate_items_product_id ON public.estimate_items(product_id);
CREATE INDEX IF NOT EXISTS idx_invoice_items_invoice_id ON public.invoice_items(invoice_id);
CREATE INDEX IF NOT EXISTS idx_invoice_items_product_id ON public.invoice_items(product_id);
