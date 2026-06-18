-- ============================================================
-- TIRUPATI POS - Sales Module Database Schema
-- Run this in Supabase SQL Editor
-- ============================================================

-- Enable UUID extension
create extension if not exists "uuid-ossp";

-- ============================================================
-- TABLE: products
-- Matches LocalProduct Room entity (table: local_products in Room)
-- ============================================================
create table if not exists public.products (
    id              text primary key,
    item_code       text not null,
    item_name       text not null,
    unit            text not null default 'Pcs',
    selling_price   numeric(10,2) not null default 0,
    gst_percent     numeric(5,2) not null default 0,
    created_at      bigint not null default (extract(epoch from now()) * 1000)::bigint,
    updated_at      bigint not null default (extract(epoch from now()) * 1000)::bigint
);

-- ============================================================
-- TABLE: estimates
-- Matches LocalEstimate Room entity
-- ============================================================
create table if not exists public.estimates (
    id              text primary key,
    estimate_number text not null,
    customer_name   text not null default '',
    date            text not null,
    time            text not null,
    status          text not null default 'DRAFT',
    subtotal        numeric(10,2) not null default 0,
    item_discount   numeric(10,2) not null default 0,
    bill_discount   numeric(10,2) not null default 0,
    gst_total       numeric(10,2) not null default 0,
    grand_total     numeric(10,2) not null default 0,
    created_at      bigint not null default (extract(epoch from now()) * 1000)::bigint,
    updated_at      bigint not null default (extract(epoch from now()) * 1000)::bigint
);

-- ============================================================
-- TABLE: estimate_items
-- Matches LocalEstimateItem Room entity
-- ============================================================
create table if not exists public.estimate_items (
    id               text primary key,
    estimate_id      text not null references public.estimates(id) on delete cascade,
    sr_no            integer not null,
    item_code        text not null,
    item_name        text not null,
    quantity         integer not null default 1,
    unit             text not null default 'Pcs',
    rate             numeric(10,2) not null default 0,
    discount_percent numeric(5,2) not null default 0,
    discount_amount  numeric(10,2) not null default 0,
    gst_percent      numeric(5,2) not null default 0,
    amount           numeric(10,2) not null default 0
);

-- ============================================================
-- TABLE: invoices
-- Matches LocalInvoice Room entity
-- ============================================================
create table if not exists public.invoices (
    id              text primary key,
    estimate_id     text not null references public.estimates(id),
    invoice_number  text not null,
    customer_name   text not null default '',
    date            text not null,
    time            text not null,
    status          text not null default 'PENDING',
    subtotal        numeric(10,2) not null default 0,
    item_discount   numeric(10,2) not null default 0,
    bill_discount   numeric(10,2) not null default 0,
    gst_total       numeric(10,2) not null default 0,
    grand_total     numeric(10,2) not null default 0,
    payment_method  text,
    created_at      bigint not null default (extract(epoch from now()) * 1000)::bigint,
    updated_at      bigint not null default (extract(epoch from now()) * 1000)::bigint
);

-- ============================================================
-- TABLE: invoice_items
-- Matches LocalInvoiceItem Room entity
-- ============================================================
create table if not exists public.invoice_items (
    id               text primary key,
    invoice_id       text not null references public.invoices(id) on delete cascade,
    sr_no            integer not null,
    item_code        text not null,
    item_name        text not null,
    quantity         integer not null default 1,
    unit             text not null default 'Pcs',
    rate             numeric(10,2) not null default 0,
    discount_percent numeric(5,2) not null default 0,
    discount_amount  numeric(10,2) not null default 0,
    gst_percent      numeric(5,2) not null default 0,
    amount           numeric(10,2) not null default 0
);

-- ============================================================
-- TABLE: pending_operations
-- Matches PendingOperation Room entity in core module
-- ============================================================
create table if not exists public.pending_operations (
    id              text primary key,
    operation_type  text not null,
    entity_type     text not null,
    entity_id       text not null,
    payload_json    text,
    timestamp       bigint not null,
    retry_count     integer not null default 0
);

-- ============================================================
-- ROW LEVEL SECURITY
-- Enable RLS but allow all for anon (since no auth in POS yet)
-- ============================================================
alter table public.products enable row level security;
alter table public.estimates enable row level security;
alter table public.estimate_items enable row level security;
alter table public.invoices enable row level security;
alter table public.invoice_items enable row level security;
alter table public.pending_operations enable row level security;

-- Allow full access for anon key (POS is offline-first, device local)
create policy "Allow all for anon" on public.products for all using (true) with check (true);
create policy "Allow all for anon" on public.estimates for all using (true) with check (true);
create policy "Allow all for anon" on public.estimate_items for all using (true) with check (true);
create policy "Allow all for anon" on public.invoices for all using (true) with check (true);
create policy "Allow all for anon" on public.invoice_items for all using (true) with check (true);
create policy "Allow all for anon" on public.pending_operations for all using (true) with check (true);

-- ============================================================
-- INDEXES for fast lookups
-- ============================================================
create index if not exists idx_estimates_status on public.estimates(status);
create index if not exists idx_estimates_created_at on public.estimates(created_at desc);
create index if not exists idx_estimate_items_estimate_id on public.estimate_items(estimate_id);
create index if not exists idx_invoices_estimate_id on public.invoices(estimate_id);
create index if not exists idx_invoice_items_invoice_id on public.invoice_items(invoice_id);
create index if not exists idx_pending_operations_entity_type on public.pending_operations(entity_type);
