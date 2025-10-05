#!/bin/bash

OUTPUT="todo_el_codigo.txt"

> "$OUTPUT"

for file in *.java; do
    [ -e "$file" ] || continue

    echo "========== INICIO: $file ==========" >> "$OUTPUT"
    cat "$file" >> "$OUTPUT"
    echo -e "\n========== FIN: $file ==========\n" >> "$OUTPUT"
done

echo "Listo! Todo quedó en $OUTPUT"

