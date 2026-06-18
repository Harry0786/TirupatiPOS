import uuid
import time
import random
import requests
import sqlite3
import os

SUPABASE_URL = "https://rawcqqejrycunvkqcffu.supabase.co"
SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJhd2NxcWVqcnljdW52a3FjZmZ1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODE3NTkzNDAsImV4cCI6MjA5NzMzNTM0MH0.JJdK95dNAhe9UTIp9u7oq4lUClcHcgzqU8cCK7a8d_I"

headers = {
    "apikey": SUPABASE_KEY,
    "Authorization": f"Bearer {SUPABASE_KEY}",
    "Content-Type": "application/json",
    "Prefer": "return=minimal"
}

companies = [
    "Havells", "Polycab", "Finolex", "Legrand", "Anchor", 
    "Schneider", "Syska", "Philips", "Bajaj", "Crompton", 
    "V-Guard", "Orient", "Usha", "Wipro", "L&T"
]

categories = [
    ("Wire 1.0 sq mm", "Coil", 800, 1050),
    ("Wire 1.5 sq mm", "Coil", 1200, 1500),
    ("Wire 2.5 sq mm", "Coil", 1800, 2200),
    ("Wire 4.0 sq mm", "Coil", 2500, 3100),
    ("Switch 6A", "Pcs", 25, 45),
    ("Switch 16A", "Pcs", 45, 75),
    ("Socket 6A", "Pcs", 30, 55),
    ("Socket 16A", "Pcs", 60, 95),
    ("MCB 10A SP", "Pcs", 120, 180),
    ("MCB 16A SP", "Pcs", 120, 180),
    ("MCB 32A DP", "Pcs", 350, 480),
    ("MCB 63A DP", "Pcs", 450, 600),
    ("LED Bulb 9W", "Pcs", 65, 110),
    ("LED Bulb 12W", "Pcs", 90, 150),
    ("Ceiling Fan 1200mm", "Pcs", 1400, 1900),
    ("Exhaust Fan 150mm", "Pcs", 800, 1100),
    ("PVC Pipe 25mm", "Bundle", 300, 450),
    ("PVC Pipe 20mm", "Bundle", 250, 380),
    ("Casing Capping 1 inch", "Bundle", 150, 250),
    ("Distribution Board 4 Way", "Pcs", 400, 650),
]

colors_or_types = ["Red", "Black", "Blue", "Green", "White", "Standard", "Premium", "Heavy Duty", "Gold", "Silver"]

current_time = int(time.time() * 1000)

# ------------------------------------------------------------
# PART 1: Generate Local SQLite Room Database (products_db)
# ------------------------------------------------------------
db_filename = "products_db"
if os.path.exists(db_filename):
    os.remove(db_filename)

print(f"Creating local SQLite database '{db_filename}' matching Room schema...")
conn = sqlite3.connect(db_filename)
cursor = conn.cursor()

# Create companies table (Room Schema)
cursor.execute("""
CREATE TABLE IF NOT EXISTS `companies` (
    `id` TEXT NOT NULL,
    `name` TEXT NOT NULL,
    `createdAt` INTEGER NOT NULL,
    `updatedAt` INTEGER NOT NULL,
    PRIMARY KEY(`id`)
);
""")
cursor.execute("CREATE UNIQUE INDEX IF NOT EXISTS `index_companies_name` ON `companies` (`name`);")

# Create products table (Room Schema)
cursor.execute("""
CREATE TABLE IF NOT EXISTS `products` (
    `id` TEXT NOT NULL,
    `companyId` TEXT NOT NULL,
    `itemCode` TEXT NOT NULL,
    `itemName` TEXT NOT NULL,
    `unit` TEXT NOT NULL,
    `purchaseRate` REAL NOT NULL,
    `sellingRate` REAL NOT NULL,
    `stockQuantity` REAL NOT NULL,
    `createdAt` INTEGER NOT NULL,
    `updatedAt` INTEGER NOT NULL,
    PRIMARY KEY(`id`)
);
""")
cursor.execute("CREATE UNIQUE INDEX IF NOT EXISTS `index_products_itemCode` ON `products` (`itemCode`);")
cursor.execute("CREATE INDEX IF NOT EXISTS `index_products_itemName` ON `products` (`itemName`);")
cursor.execute("CREATE INDEX IF NOT EXISTS `index_products_companyId` ON `products` (`companyId`);")

# Room system table
cursor.execute("""
CREATE TABLE IF NOT EXISTS room_master_table (
    id INTEGER PRIMARY KEY,
    identity_hash TEXT
);
""")
cursor.execute("INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES (42, '9be73859666bd9a3d132644265492d3f');")

# ------------------------------------------------------------
# PART 2: Seed Data to both Supabase and Local SQLite
# ------------------------------------------------------------
print("Clearing existing products and companies from Supabase...")
requests.delete(f"{SUPABASE_URL}/rest/v1/products?id=not.is.null", headers=headers)
requests.delete(f"{SUPABASE_URL}/rest/v1/companies?id=not.is.null", headers=headers)

for c_idx, c_name in enumerate(companies):
    c_id = str(uuid.uuid4())
    
    # 1. Insert Company into Supabase
    c_data_sb = {
        "id": c_id,
        "name": c_name,
        "created_at": current_time,
        "updated_at": current_time
    }
    requests.post(f"{SUPABASE_URL}/rest/v1/companies", headers=headers, json=[c_data_sb])
    
    # 2. Insert Company into Local Room SQLite
    cursor.execute(
        "INSERT INTO companies (id, name, createdAt, updatedAt) VALUES (?, ?, ?, ?)",
        (c_id, c_name, current_time, current_time)
    )
    
    products_sb = []
    products_written = 0
    for cat in categories:
        for variant in colors_or_types:
            if products_written >= 100:
                break
            
            p_id = str(uuid.uuid4())
            cat_name, unit, min_p, max_p = cat
            item_name = f"{c_name} {cat_name} {variant}"
            item_code = f"{c_name[:3].upper()}-{cat_name[:3].upper()}-{variant[:3].upper()}-{products_written+1:03d}"
            
            p_rate = random.randint(min_p, max_p)
            s_rate = int(p_rate * random.uniform(1.15, 1.40))
            stock = random.randint(10, 500)
            
            # Prepare Supabase payload (snake_case)
            products_sb.append({
                "id": p_id,
                "company_id": c_id,
                "item_code": item_code,
                "item_name": item_name,
                "unit": unit,
                "purchase_rate": p_rate,
                "selling_rate": s_rate,
                "stock_quantity": stock,
                "created_at": current_time,
                "updated_at": current_time
            })
            
            # Insert Product into Local Room SQLite (camelCase)
            cursor.execute(
                "INSERT INTO products (id, companyId, itemCode, itemName, unit, purchaseRate, sellingRate, stockQuantity, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                (p_id, c_id, item_code, item_name, unit, float(p_rate), float(s_rate), float(stock), current_time, current_time)
            )
            products_written += 1
        if products_written >= 100:
            break
            
    # Push batch to Supabase
    requests.post(f"{SUPABASE_URL}/rest/v1/products", headers=headers, json=products_sb)
    print(f"Seeded company '{c_name}' and its 100 products both locally and on Supabase.")

conn.commit()
conn.close()

print("\nSeeding Completed Successfully!")
print(f"1. Online Supabase database is updated.")
print(f"2. Local Room SQLite database file '{db_filename}' is generated in this directory.")
print("\nTo load the mock data onto your Android Emulator/Device, run:")
print(f"adb push {db_filename} /data/data/com.tirupati.pos/databases/products_db")
