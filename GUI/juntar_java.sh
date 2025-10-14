#!/bin/bash
# Junta todos los .java bajo src/ (recursivo) en un único archivo de texto.
# Uso:
#   bash GUI/juntar_java.sh
#   bash GUI/juntar_java.sh "ruta/salida.txt"

set -euo pipefail

# Directorio del script y raíz (src/)
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd -P)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd -P)"   # esto debería ser .../src

# Salida por defecto: mantenemos el lugar que usabas
OUTPUT_DEFAULT="$ROOT_DIR/GUI/todo_el_codigo.txt"
OUTPUT="${1:-$OUTPUT_DEFAULT}"

# Crear carpeta destino si no existe
mkdir -p "$(dirname "$OUTPUT")"

# Encabezado
{
  echo "============================================================"
  echo "  TODO EL CÓDIGO - Proyecto bajo: $ROOT_DIR"
  echo "  Generado: $(date '+%Y-%m-%d %H:%M:%S')"
  echo "============================================================"
  echo
} > "$OUTPUT"

# Buscar .java dentro de src/, evitando carpetas de build/ocultas
# -print0 + sort -z para manejar rutas con espacios de forma segura
find "$ROOT_DIR" \
  \( -path "$ROOT_DIR/.git/*" -o -path "$ROOT_DIR/bin/*" -o -path "$ROOT_DIR/out/*" -o -path "$ROOT_DIR/target/*" \) -prune -o \
  -type f -name "*.java" -print0 \
| sort -z \
| while IFS= read -r -d '' FILE; do
    # Ruta relativa a src/
    REL="${FILE#$ROOT_DIR/}"

    {
      echo "========== INICIO: $REL =========="
      cat "$FILE"
      # Asegurar un salto de línea final por si el archivo no lo tiene
      printf "\n"
      echo "========== FIN: $REL =========="
      echo
    } >> "$OUTPUT"
  done

echo "Listo! Todo quedó en: $OUTPUT"
