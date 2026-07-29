from pathlib import Path
import re

base = Path('app/src/main/java/com/cornai')
if not base.exists():
    raise SystemExit('Base folder not found: ' + str(base))

patterns = [
    (r'MainActivity', 'Activity utama aplikasi, lokasi start UI dan navigasi utama.'),
    (r'CornAIModel', 'Logika pemuatan model TFLite dan inferensi klasifikasi penyakit.'),
    (r'DiseaseData', 'Data statis informasi penyakit dan rekomendasi perawatan.'),
    (r'ViewModel', 'ViewModel yang mengatur state UI dan interaksi dengan data.'),
    (r'Screen', 'Composable UI untuk satu halaman tampilan aplikasi.'),
    (r'Components', 'Komponen UI custom yang digunakan di banyak halaman.'),
    (r'Navigation', 'Definisi navigasi aplikasi dan route screen.'),
    (r'Theme', 'Pengaturan tema, warna, dan gaya tampilan UI.'),
    (r'Repository', 'Repository untuk mengelola akses data dan sumber yang digunakan.'),
    (r'Firebase', 'Layanan Firebase untuk autentikasi dan penyimpanan data.'),
    (r'Database', 'Database lokal untuk menyimpan data secara offline.'),
    (r'PreferencesManager', 'Menyimpan preferensi pengguna di penyimpanan lokal.'),
    (r'Result', 'Struct hasil operasi dengan status Loading, Success, atau Error.'),
    (r'ScanHistory', 'Model data histori scan hasil deteksi aplikasi.'),
    (r'UiState', 'Model state UI untuk menangani tampilan aplikasi.'),
    (r'User', 'Model data pengguna aplikasi.'),
]


def describe(path: Path) -> str:
    name = path.name
    for pattern, desc in patterns:
        if re.search(pattern, name, re.IGNORECASE):
            return desc
    if 'screens' in str(path.parts):
        return 'File layar UI untuk menampilkan konten aplikasi.'
    if 'components' in str(path.parts):
        return 'Komponen UI reusable untuk aplikasi.'
    if 'viewmodel' in str(path.parts):
        return 'ViewModel untuk mengatur logika tampilan.'
    if 'data' in str(path.parts) and 'model' in str(path.parts):
        return 'Model data untuk menyimpan data aplikasi.'
    if 'data' in str(path.parts) and 'remote' in str(path.parts):
        return 'Layanan remote untuk komunikasi jaringan atau Firebase.'
    if 'data' in str(path.parts) and 'local' in str(path.parts):
        return 'Manajemen penyimpanan lokal (database/preference).'
    return 'File Kotlin aplikasi CornAI.'

updated = 0
for path in sorted(base.rglob('*.kt')):
    text = path.read_text(encoding='utf-8')
    lines = text.splitlines()
    if not lines:
        continue
    idx = 0
    while idx < len(lines) and not lines[idx].strip():
        idx += 1
    if idx < len(lines) and lines[idx].strip().startswith('//'):
        # skip if a comment already exists before the package declaration
        continue
    if idx < len(lines) and lines[idx].strip().startswith('/*'):
        continue
    if idx < len(lines) and lines[idx].strip().startswith('package '):
        desc = describe(path)
        rel = path.relative_to(base.parent.parent.parent)
        header = f'// {desc}\n// File: {rel}\n'
        new_lines = lines[:idx] + [header] + lines[idx:]
        path.write_text('\n'.join(new_lines) + '\n', encoding='utf-8')
        updated += 1
print(f'Updated {updated} Kotlin files')
