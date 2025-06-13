#!/bin/python3

"""
Script adapted from @bikeshedder

This script reads the XML files in the smdx directory, extracts notes for models, points, and symbols,
and updates the corresponding JSON files in the json directory with this information. The information is added
to the 'detail' field in the JSON structure.

Why 'detail' and not 'notes'?
As of SunSpec Device Information Model Specification v1.2, Detailed Description (detail) is Mandatory
for the CSV encodings but Notes (notes) are Optional. Technically, Notes are "used for draft models"
and "not included in approved models"

Detail also shows up in the XLSX representation of the models.
"""

import json
from os import listdir
from xml.etree import ElementTree as ET

for fn in listdir('smdx'):
    if not (fn.startswith('smdx_') and fn.endswith('.xml')):
        continue
    model_id = int(fn[5:-4], base=10)
    dom = ET.parse(f'smdx/{fn}').getroot()
    strings = dom.find('strings')

    # model notes
    model_string = strings.find('model')
    model_notes = model_string.findtext('notes')
    if model_notes:
        model_notes = model_notes.strip()

    # point notes
    points_notes = {}
    symbols_notes = {}
    for point in strings.findall('point'):
        point_id = point.get('id')
        point_notes = point.findtext('notes')
        if point_notes:
            point_notes = point_notes.strip()
        if point_notes:
            points_notes[point_id] = point_notes
        for symbol in point.findall('symbol'):
            symbol_id = symbol.get('id')
            symbol_notes = symbol.findtext('notes')
            if symbol_notes:
                symbol_notes = symbol_notes.strip()
            if symbol_notes:
                symbols_notes[(point_id, symbol_id)] = symbol_notes

    print(f'Processing model {model_id} with notes:')
    print(f'-- Model notes: {model_notes}')
    print(f'-- Point notes: {points_notes}')
    print(f'-- Symbols notes: {symbols_notes}')

    with open(f'json/model_{model_id}.json') as fh:
        jsondoc = json.load(fh)

    # Update the JSON file groups if there are notes
    if not (model_notes or points_notes):
        continue
    # If the group has no notes and there are model notes, add them to detail
    if not jsondoc['group'].get('detail', '') and not jsondoc['group'].get('notes', '') and model_notes:
        jsondoc['group']['detail'] = model_notes

    # Update the points and symbols with detail from the XML notes
    for point in jsondoc['group']['points']:
        point_name = point['name']
        point_notes = points_notes.get(point_name, '')

        # If the point has no notes/detail and there are notes in the XML, add them to detail
        if not point.get('notes', '') and not point.get('detail', '') and point_notes:
            point['detail'] = point_notes

        for symbol in point.get('symbols', []):
            symbol_name = symbol['name']
            symbol_notes = symbols_notes.get((point_name, symbol_name), '')

            # If the symbol has no notes/detail and there are notes in the XML, add them to detail
            if not symbol.get('notes', '') and symbol_notes:
                symbol['detail'] = symbol_notes

    with open(f'json/model_{model_id}.json', 'w') as fh:
        json.dump(jsondoc, fh, indent=4)
