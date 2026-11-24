#!/usr/bin/env bash
set -euo pipefail

if [[ ! -f .env ]]; then
    echo "Falta el archivo .env. Copia .env.example y configura los valores." >&2
    exit 1
fi

set -a
source .env
set +a

mvn spring-boot:run
