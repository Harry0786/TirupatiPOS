import uuid
import time
import random
import requests
import json

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

for c_idx, c_name in enumerate(companies):
    c_id = str(uuid.uuid4())
    
    # Insert company
    c_data = {
        "id": c_id,
        "name": c_name,
        "created_at": current_time,
        "updated_at": current_time
    }
    
    res = requests.post(f"{SUPABASE_URL}/rest/v1/companies", headers=headers, json=[c_data])
    if res.status_code >= 400:
        print(f"Error inserting company {c_name}: {res.text}")
        continue
    
    print(f"Inserted company: {c_name}")
    
    # Generate 100 products
    products_to_insert = []
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
            
            products_to_insert.append({
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
            products_written += 1
        if products_written >= 100:
            break
            
    # Batch insert 100 products
    res = requests.post(f"{SUPABASE_URL}/rest/v1/products", headers=headers, json=products_to_insert)
    if res.status_code >= 400:
        print(f"Error inserting products for {c_name}: {res.text}")
    else:
        print(f"Inserted 100 products for {c_name}")

print("Done inserting mock data into Supabase.")
