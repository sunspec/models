from __future__ import print_function

import os
import hashlib
import sys
import xml.etree.ElementTree as ET

PY2 = (2,) <= sys.version_info < (3,)

MANIFEST_ROOT = 'manifest'
MANIFEST_FILE = 'file'
MANIFEST_NAME = 'name'
MANIFEST_MD5 = 'md5'

MANIFEST_FILE_NAME = 'manifest.xml'
MANIFEST_MD5_EXT = '.md5'

manifest_files = ['CHANGELOG', 'smdx.xsd']

""" Simple XML pretty print support function

"""
def xml_indent(elem, level=0):
    i = "\n" + level*"  "
    if len(elem):
        if not elem.text or not elem.text.strip():
            elem.text = i + "  "
        if not elem.tail or not elem.tail.strip():
            elem.tail = i
        for elem in elem:
            xml_indent(elem, level+1)
        if not elem.tail or not elem.tail.strip():
            elem.tail = i
    else:
        if level and (not elem.tail or not elem.tail.strip()):
            elem.tail = i

class ManifestError(Exception):
    pass

class File(object):
    def __init__(self, name=None, md5=None):
        self.name = name
        self.md5 = md5

class Manifest(object):
    def __init__(self, path='.', filename=None):
        self.path = path
        self.files = {}
        self.filename = filename

        if filename:
            self.from_xml(filename=filename)

    def create(self):
        self.scan()
        filename = os.path.join(self.path, MANIFEST_FILE_NAME)
        self.to_xml_file(filename=filename)
        md5 = hashlib.md5(open(filename, 'rb').read()).hexdigest()
        f = open(filename + MANIFEST_MD5_EXT, 'w')
        f.write(md5)
        f.close()

    def md5(self):
        return hashlib.md5(self.to_xml_bytes()).hexdigest()

    def diff(self, m):
        diff_str = ''
        for f, md5 in self.files.items():
            m_md5 = m.files.get(f)
            if md5 != m_md5:
                diff_str += '%s: %s  %s\n' % (f, md5, m_md5)

        if len(self.files) != len(m.files):
            for f, md5 in m.files.items():
                if self.files.get(f) is None:
                    diff_str += '%s: "None"  %s\n' % (f, md5)
        return diff_str

    def scan(self):
        try:
            files = os.listdir(self.path)
            for f in files:
                name, ext = os.path.splitext(f)
                if f in manifest_files or (ext == '.xml' and f.startswith('smdx_')):
                    filename = os.path.join(self.path, f)
                    content = open(filename, 'rb').read()
                    # if content.find(b'\r\n') >= 0:
                    #     print('windows file ', self.path)
                    content = content.replace(b'\r\n', b'\n')
                    md5 = hashlib.md5(content).hexdigest()
                    self.files[f] = md5
        except Exception as e:
            raise ManifestError('Error scanning directory %s: %s' % (self.path, str(e)))

    def scan_strip(self):
        try:
            files = os.listdir(self.path)
            for f in files:
                name, ext = os.path.splitext(f)
                if f in manifest_files or (ext == '.xml' and f.startswith('smdx_')):
                    filename = os.path.join(self.path, f)
                    content = open(filename, 'rb').read()
                    if content.find(b'\r\n') >= 0:
                        content = content.replace(b'\r\n', b'\n')
                        fc = open(filename, 'w')
                        fc.write(content)
                        fc.close()
                    md5 = hashlib.md5(content).hexdigest()
                    self.files[f] = md5
        except Exception as e:
            raise ManifestError('Error scanning directory %s: %s' % (self.path, str(e)))

    def to_xml(self, parent=None, filename=None):
        if parent is not None:
            e = ET.SubElement(parent, MANIFEST_ROOT)
        else:
            e = ET.Element(MANIFEST_ROOT)

        for f in sorted(self.files.keys()):
            attr = {MANIFEST_NAME: f, MANIFEST_MD5: self.files.get(f)}
            ET.SubElement(e, MANIFEST_FILE, attrib=attr)

        return e

    def to_xml_bytes(self, pretty_print=True):
        e = self.to_xml()

        if pretty_print:
            xml_indent(e)

        return ET.tostring(e)

    def to_xml_file(self, filename=None, pretty_print=True, replace_existing=True):
        xml = self.to_xml_bytes(pretty_print)

        if filename is not None:
            if replace_existing is False and os.path.exists(filename):
                raise ManifestError('File %s already exists' % (filename))
            f = open(filename, 'wb')
            f.write(xml)
            f.close()
        else:
            if PY2:
                print(xml)
            else:
                print(xml.decode())

    def from_xml(self, element=None, filename=None):
        if element is None and filename is not None:
            element = ET.ElementTree(file=filename).getroot()

        if element is None:
            raise ManifestError('No xml document element')

        if element.tag != MANIFEST_ROOT:
            raise ManifestError('Unexpected test suite root element %s' % (element.tag))

        for e in element.findall('*'):
            if e.tag == MANIFEST_FILE:
                name = e.attrib.get(MANIFEST_NAME)
                md5 = e.attrib.get(MANIFEST_MD5)
                if name and md5:
                    self.files[name] = md5

if __name__ == "__main__":


    path = os.path.dirname(os.path.realpath(__file__))
    a = Manifest(path)
    a.scan()
    a.create()

    # b = Manifest(filename=os.path.join(path, 'manifest.xml'))
    
    # print a.diff(b)
    # print '----------------------------------------------------'
    # print a.to_xml_str()
    # print a.md5()
