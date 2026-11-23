#!/usr/bin/env bash
# =============================================================================
# Prepara el repositorio de GitHub de la práctica y sube el código.
#
#   1. Personaliza el pom.xml y el README con su apellido y su organización de SonarCloud.
#   2. Crea el repositorio público lab-dht-bicam-<apellido> (sin subir nada todavía).
#   3. Crea las etiquetas, el Milestone "Entrega Final" y los Issues #1..#6.
#   4. Hace push: los commits contienen "Closes #N" y cierran cada Issue.
#
# Requisitos: git y GitHub CLI (gh) con la sesión iniciada (gh auth login).
#
# Uso:
#   ./scripts/setup-github.sh <apellido> <fecha-entrega AAAA-MM-DD> <organizacion-sonarcloud>
# Ejemplo:
#   ./scripts/setup-github.sh garcia 2026-12-15 garcia-github
# =============================================================================
set -euo pipefail

if [ $# -ne 3 ]; then
    sed -n '2,17p' "$0" | sed 's/^# \{0,1\}//'
    exit 2
fi

APELLIDO="$(echo "$1" | tr '[:upper:]' '[:lower:]')"
FECHA="$2"
SONAR_ORG="$3"
REPO="lab-dht-bicam-${APELLIDO}"

[[ "$APELLIDO" =~ ^[a-z0-9-]+$ ]] || { echo "El apellido solo puede contener letras sin tilde, números y guiones" >&2; exit 2; }
[[ "$FECHA" =~ ^[0-9]{4}-[0-9]{2}-[0-9]{2}$ ]] || { echo "La fecha debe tener el formato AAAA-MM-DD" >&2; exit 2; }
[[ "$SONAR_ORG" =~ ^[A-Za-z0-9_-]+$ ]] || { echo "Organización de SonarCloud no válida" >&2; exit 2; }

command -v gh >/dev/null || { echo "Instale GitHub CLI: https://cli.github.com" >&2; exit 1; }
gh auth status >/dev/null 2>&1 || { echo "Inicie sesión primero con: gh auth login" >&2; exit 1; }

cd "$(dirname "$0")/.."
OWNER="$(gh api user --jq .login)"
SONAR_KEY="${SONAR_ORG}_${REPO}"

echo ">> Usuario de GitHub: ${OWNER}"
echo ">> Repositorio:       ${OWNER}/${REPO}"
echo ">> Proyecto Sonar:    ${SONAR_KEY}"
read -r -p "¿Continuar? [s/N] " ok
[[ "$ok" =~ ^[sS]$ ]] || exit 0

# ---------------------------------------------------------------- 1. personalizar
sed -i.bak \
    -e "s|<sonar.organization>CAMBIAR-organizacion</sonar.organization>|<sonar.organization>${SONAR_ORG}</sonar.organization>|" \
    -e "s|<sonar.projectKey>CAMBIAR-organizacion_lab-dht-bicam-apellido</sonar.projectKey>|<sonar.projectKey>${SONAR_KEY}</sonar.projectKey>|" \
    -e "s|<sonar.projectName>lab-dht-bicam-apellido</sonar.projectName>|<sonar.projectName>${REPO}</sonar.projectName>|" \
    pom.xml
sed -i.bak -e "s|CAMBIAR-organizacion_lab-dht-bicam-apellido|${SONAR_KEY}|g" \
           -e "s|CAMBIAR-usuario/lab-dht-bicam-apellido|${OWNER}/${REPO}|g" README.md
rm -f pom.xml.bak README.md.bak
if ! git diff --quiet; then
    git add pom.xml README.md
    git commit -q -m "Configuración de los identificadores de GitHub y SonarCloud"
fi

# ---------------------------------------------------------------- 2. repositorio
if gh repo view "${OWNER}/${REPO}" >/dev/null 2>&1; then
    echo ">> El repositorio ya existe"
else
    gh repo create "${REPO}" --public --description "Práctica 5 LDH: BiCIAM con Maven, JUnit 5, SonarCloud, Doxygen y Docker"
fi
git remote remove origin 2>/dev/null || true
git remote add origin "https://github.com/${OWNER}/${REPO}.git"
R="${OWNER}/${REPO}"

# ---------------------------------------------------------------- 3. etiquetas, milestone, issues
for label in "configuración:1d76db" "documentación:0075ca" "pruebas:0e8a16" "calidad:fbca04" "seguridad:d93f0b" "despliegue:5319e7"; do
    gh label create "${label%%:*}" --color "${label##*:}" --repo "$R" --force >/dev/null
done

if ! gh api "repos/$R/milestones" --jq '.[].title' | grep -qx "Entrega Final"; then
    gh api "repos/$R/milestones" -f title="Entrega Final" -f state=open \
        -f description="Entrega de la Práctica 5: Gestión de Proyecto y Pruebas Unitarias" \
        -f due_on="${FECHA}T23:59:59Z" >/dev/null
fi

existing="$(gh issue list --repo "$R" --state all --limit 1 --json number --jq 'length')"
if [ "$existing" != "0" ]; then
    echo "El repositorio ya tiene issues: los números no coincidirían con los 'Closes #N' de los commits." >&2
    echo "Use un repositorio nuevo o cierre los issues a mano." >&2
    exit 1
fi

create_issue() {  # $1 número esperado, $2 título, $3 etiqueta, $4 cuerpo
    url="$(gh issue create --repo "$R" --title "$2" --label "$3" --milestone "Entrega Final" --body "$4")"
    [ "${url##*/}" = "$1" ] || { echo "Se esperaba el issue #$1 y se creó ${url}" >&2; exit 1; }
    echo "   #$1 $2"
}

echo ">> Creando issues"
create_issue 1 "Configuración de Plugins de Calidad (Maven)" "configuración" \
"Incluir en el pom.xml los plugins OWASP Dependency-Check, JaCoCo, PMD, Checkstyle y SpotBugs + FindSecBugs."
create_issue 2 "Documentación de Código (Doxygen)" "documentación" \
"Configurar el proyecto para generar la documentación de código con Doxygen. Generar el Doxyfile."
create_issue 3 "Desarrollo de Pruebas Unitarias" "pruebas" \
"Implementar pruebas unitarias para las clases principales usando JUnit 5:

- problem.definition: State, Problem, ObjetiveFunction
- problem.extension: FactoresPonderados, MultiObjetivoPuro, MetricasMultiobjetivo
- local_search.acceptation_type: Dominance y todos los criterios Accept*
- local_search.candidate_type: GreaterCandidate, SmallerCandidate, RandomCandidate, NotDominatedCandidate, CandidateValue
- local_search.complement: TabuSolutions, StopExecute, UpdateParameter
- evolutionary_algorithms.complement: selección, cruce, mutación, reemplazo, Univariate y ProbabilisticSampling
- factory_method: FactoryLoader y todas las factorías
- metaheurictics.strategy.Strategy y los generadores de metaheuristics.generators
- util.RandomUtil"
create_issue 4 "Análisis de Cobertura y Calidad (SonarCloud)" "calidad" \
"Configurar el proyecto para que los resultados de JaCoCo se suban a SonarCloud y solucionar los principales Code Smells/Bugs."
create_issue 5 "Análisis de Seguridad Estática (SAST)" "seguridad" \
"Ejecutar los plugins de seguridad y solucionar todas las vulnerabilidades detectadas en el código fuente de BiCIAM."
create_issue 6 "Despliegue en Contenedor (Docker)" "despliegue" \
"Crear el Dockerfile y el script necesario para construir y ejecutar la aplicación dentro de un contenedor Docker."

# ---------------------------------------------------------------- 4. push
echo ">> Subiendo el código"
git branch -M main
git push -u origin main

echo
echo "Listo. Pasos siguientes (ver README.md, sección «Puesta en marcha»):"
echo "  - Añadir los secretos SONAR_TOKEN y NVD_API_KEY en https://github.com/${R}/settings/secrets/actions"
echo "  - Activar GitHub Pages (Source: GitHub Actions) en https://github.com/${R}/settings/pages"
echo "  - Comprobar los issues cerrados:  https://github.com/${R}/issues?q=is%3Aissue"
