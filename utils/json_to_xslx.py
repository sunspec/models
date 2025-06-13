"""
Convert JSON model definitions to an Excel workbook.

Requirements:
- `sunspec2` package must be installed.
- `openpyxl` package must be installed for Excel file handling.
- `natsort` package must be installed for natural sorting of files.
"""

import os
import glob
import json
from sunspec2.xlsx import ModelWorkbook
from natsort import natsorted

# Path to your JSON files
JSON_DIR = os.path.join(os.path.dirname(__file__), "../json")
JSON_DIR = os.path.abspath(JSON_DIR)
XLSX_FILE = os.path.join(os.path.dirname(__file__), "../models_workbook.xlsx")
XLSX_FILE = os.path.abspath(XLSX_FILE)

wb = ModelWorkbook()

for json_file in natsorted(glob.glob(os.path.join(JSON_DIR, "*.json"))):
    if json_file.endswith("schema.json"):
        continue
    with open(json_file, "r", encoding="utf-8") as f:
        model_def = json.load(f)
    try:
        wb.to_xlsx(model_def)
    except Exception as e:
        mid = model_def.get("ID", os.path.basename(json_file))
        wb.create_error_sheet(mid, str(e))

wb.save(XLSX_FILE)
print(f"Saved Excel file to {XLSX_FILE}")
