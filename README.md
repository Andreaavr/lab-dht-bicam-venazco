# BiCIAM

[![Build](https://github.com/Andreaavr/lab-dht-bicam-venazco/actions/workflows/build.yml/badge.svg)](https://github.com/Andreaavr/lab-dht-bicam-venazco/actions/workflows/build.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=andreaavr_lab-dht-bicam-venazco&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=andreaavr_lab-dht-bicam-venazco)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=andreaavr_lab-dht-bicam-venazco&metric=coverage)](https://sonarcloud.io/summary/new_code?id=andreaavr_lab-dht-bicam-venazco)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=andreaavr_lab-dht-bicam-venazco&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=andreaavr_lab-dht-bicam-venazco)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=andreaavr_lab-dht-bicam-venazco&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=andreaavr_lab-dht-bicam-venazco)
[![Security](https://sonarcloud.io/api/project_badges/measure?project=andreaavr_lab-dht-bicam-venazco&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=andreaavr_lab-dht-bicam-venazco)

Biblioteca de metaheurísticas en Java: búsqueda local (Hill Climbing, Tabu Search,
Simulated Annealing…), algoritmos evolutivos (GA, ES, EDA, PSO), portafolio de
generadores y algoritmos multiobjetivo.

Práctica 5 de *Laboratorio de Desarrollo y Herramientas*: gestión de proyecto y
pruebas unitarias con GitHub, Maven, JUnit 5, JaCoCo, PMD, Checkstyle, SpotBugs +
FindSecBugs, OWASP Dependency-Check, SonarCloud, Doxygen y Docker.

## Estructura

| Ruta | Contenido |
|------|-----------|
| `pom.xml` | Proyecto Maven con todos los plugins de calidad y seguridad |
| `src/main/java` | Código fuente de BiCIAM (+ `demo`: problema OneMax y aplicación de ejemplo; `util`: `RandomUtil`) |
| `src/test/java` | 127 pruebas unitarias con JUnit 5 |
| `config/` | Filtro de SpotBugs (categoría SECURITY) y supresiones de OWASP |
| `Doxyfile`, `docs/` | Configuración de Doxygen; la documentación se genera en `docs/html` |
| `Dockerfile`, `scripts/docker.sh` | Imagen multi-etapa y script de construcción/ejecución |
| `.github/workflows/` | CI (build, análisis, SonarCloud, Docker) y publicación de la documentación |
| `scripts/setup-github.sh` | Crea el repositorio, el Milestone y los Issues, y sube el código |

## Uso rápido

```bash
mvn verify                        # compila, prueba y ejecuta JaCoCo, PMD, Checkstyle, SpotBugs y OWASP
mvn verify -Ddependency-check.skip=true   # igual, sin OWASP (más rápido, no descarga el NVD)
java -jar target/biciam.jar TabuSearch 100 2000

./scripts/docker.sh               # construye la imagen y ejecuta la demo en el contenedor
./scripts/docker.sh run GeneticAlgorithm 64 3000
doxygen Doxyfile                  # documentación en docs/html/index.html
```

Algoritmos de la demo: `HillClimbing`, `HillClimbingRestart`, `TabuSearch`,
`SimulatedAnnealing`, `RandomSearch`, `LimitThreshold`, `GeneticAlgorithm`,
`EvolutionStrategies`.

## Informes

| Herramienta | Comando | Informe |
|-------------|---------|---------|
| JUnit 5 | `mvn test` | `target/surefire-reports/` |
| JaCoCo | `mvn test` | `target/site/jacoco/index.html` (la fase `verify` exige ≥ 80 % de líneas) |
| PMD + CPD | `mvn verify` | `target/pmd.xml`, `target/cpd.xml` |
| Checkstyle | `mvn verify` | `target/checkstyle-result.xml` |
| SpotBugs + FindSecBugs | `mvn verify` (solo SECURITY, falla si hay hallazgos) · `mvn compile spotbugs:spotbugs -Dspotbugs.filter=config/spotbugs/spotbugs-all-include.xml` (todo) | `target/spotbugsXml.xml` |
| OWASP Dependency-Check | `mvn verify` (falla con CVSS ≥ 7) | `target/dependency-check-report.html` |
| Informe HTML completo | `mvn site` | `target/site/index.html` |
| SonarCloud | CI en cada push | Badges de arriba |

## Resumen del trabajo realizado

**Seguridad (OWASP + FindSecBugs):**
- Eliminada la dependencia `jxl 2.6.12`, que arrastraba `log4j 1.2.14` (CVE-2019-17571,
  CVE-2021-4104, CVE-2022-23302/23305/23307; CVSS de hasta 9.8).
- `PREDICTABLE_RANDOM`: `Math.random()` y `java.util.Random` sustituidos por `SecureRandom` (`util.RandomUtil`).
- `INFORMATION_EXPOSURE_THROUGH_AN_ERROR_MESSAGE`: eliminados los `printStackTrace()`; se usa `java.util.logging`.
- `FactoryLoader`: la reflexión solo instancia clases de una lista blanca de paquetes de BiCIAM.

**Bugs corregidos (detectados por análisis y por las pruebas):** `State.getCopy()` compartía la
lista de código con el original (cada vecino modificaba el estado actual), `clone()` devolvía
`this`, el cruce de un punto siempre cortaba en 0, la máscara del cruce uniforme solo rellenaba la
posición 0, la lista tabú nunca filtraba (comparaba por identidad), `AcceptNotBadT` y
`AcceptNotBadU` estaban invertidos al minimizar, `SmallerCandidate` podía devolver `null`,
`LimitThreshold.getReferenceList()` devolvía `null`, `HillClimbingRestart` se identificaba como
`HillClimbing`, comparaciones de `Integer` con `==`, divisiones enteras y distancias que no se
reiniciaban en `MetricasMultiobjetivo`.

**Pruebas:** 127 pruebas JUnit 5. Cobertura de línea medida: ~91 % en las clases probadas
(80 % en todo el proyecto). Quedan fuera de la regla de JaCoCo `MultiGenerator`, PSO y EDA, que en
el código original no pueden ejecutarse de forma aislada.

## Puesta en marcha (paso a paso)

1. **Repositorio, Milestone e Issues.** Con [GitHub CLI](https://cli.github.com) instalado y la sesión iniciada (`gh auth login`):
   ```bash
   ./scripts/setup-github.sh <apellido> <fecha-entrega AAAA-MM-DD> <organización-sonarcloud>
   ```
   El script crea `lab-dht-bicam-<apellido>`, las etiquetas, el Milestone *Entrega Final* y los
   Issues #1–#6, y hace push. Los commits contienen `Closes #N`, así que los Issues quedan cerrados
   y enlazados a su commit.
2. **SonarCloud.** Entre en <https://sonarcloud.io> con GitHub → *Analyze new project* → elija el
   repositorio → *With GitHub Actions*. Desactive el *Automatic Analysis* (Administration →
   Analysis Method). Copie el token y créelo como secreto `SONAR_TOKEN` en
   *Settings → Secrets and variables → Actions* del repositorio.
3. **NVD (OWASP).** Pida una clave gratuita en <https://nvd.nist.gov/developers/request-an-api-key> y
   guárdela como secreto `NVD_API_KEY`. Sin clave, la descarga del NVD tarda mucho o falla.
4. **GitHub Pages.** *Settings → Pages → Source: GitHub Actions*. La documentación Doxygen se publicará
   en `https://<usuario>.github.io/lab-dht-bicam-<apellido>/`.
5. **Relanzar el CI** (*Actions → Build → Re-run*) cuando estén los secretos, y revisar el informe en
   SonarCloud. Ese informe forma parte de la entrega.
