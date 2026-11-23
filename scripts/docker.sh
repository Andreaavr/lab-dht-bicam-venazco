#!/usr/bin/env bash
# Construye la imagen Docker de BiCIAM y ejecuta la aplicación dentro del contenedor.
#
# Uso:
#   ./scripts/docker.sh                          # construye y ejecuta con los valores por defecto
#   ./scripts/docker.sh build                    # solo construye la imagen
#   ./scripts/docker.sh run TabuSearch 100 2000  # solo ejecuta (algoritmo, bits, iteraciones)
#   ./scripts/docker.sh test                     # construye y prueba varios algoritmos
set -euo pipefail

IMAGE="${IMAGE:-biciam:1.0.0}"
cd "$(dirname "$0")/.."

build() {
    echo ">> Construyendo la imagen ${IMAGE}"
    docker build -t "${IMAGE}" .
}

run() {
    echo ">> Ejecutando ${IMAGE} $*"
    docker run --rm "${IMAGE}" "$@"
}

smoke_test() {
    build
    for algorithm in HillClimbing TabuSearch SimulatedAnnealing GeneticAlgorithm; do
        output="$(run "${algorithm}" 30 500)"
        echo "${output}"
        grep -q "Mejor valor" <<< "${output}" || { echo "FALLO: ${algorithm}" >&2; exit 1; }
    done
    if docker run --rm "${IMAGE}" NoExiste >/dev/null 2>&1; then
        echo "FALLO: un algoritmo no válido debería terminar con error" >&2; exit 1
    fi
    echo ">> La imagen funciona correctamente"
}

case "${1:-all}" in
    build) build ;;
    run)   shift; run "$@" ;;
    test)  smoke_test ;;
    all)   build; run ;;
    *)     echo "Uso: $0 [build|run [args...]|test]" >&2; exit 2 ;;
esac
