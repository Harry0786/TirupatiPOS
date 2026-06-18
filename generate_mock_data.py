import uuid
import time
import random

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

with open("mock_data.sql", "w", encoding="utf-8") as f:
    f.write("-- Mock Data for TEPOS\n\n")
    
    current_time = int(time.time() * 1000)
    
    for c_idx, c_name in enumerate(companies):
        c_id = str(uuid.uuid4())
        f.write(f"INSERT INTO public.companies (id, name, created_at, updated_at) VALUES ('{c_id}', '{c_name}', {current_time}, {current_time});\n")
        
        # generate 100 products
        products_written = 0
        while products_written < 100:
            for cat in categories:
                for variant in colors_or_types:
                    if products_written >= 100:
                        break
                    
                    p_id = str(uuid.uuid4())
                    cat_name, unit, min_p, max_p = cat
                    item_name = f"{c_name} {cat_name} {variant}"
                    item_code = f"{c_name[:3].upper()}-{cat_name[:3].upper()}-{variant[:3].upper()}-{products_written+1:03d}"
                    
                    # random pricing within range
                    p_rate = random.randint(min_p, max_p)
                    s_rate = int(p_rate * random.uniform(1.15, 1.40))
                    stock = random.randint(10, 500)
                    
                    f.write(f"INSERT INTO public.products (id, company_id, item_code, item_name, unit, purchase_rate, selling_rate, stock_quantity, created_at, updated_at) ")
                    f.write(f"VALUES ('{p_id}', '{c_id}', '{item_code}', '{item_name}', '{unit}', {p_rate}, {s_rate}, {stock}, {current_time}, {current_time});\n")
                    
                    products_written += 1
                if products_written >= 100:
                    break
        f.write("\n")

print("Generated mock_data.sql")
