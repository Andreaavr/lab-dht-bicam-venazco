# Documentación de código

La documentación de la API se genera con [Doxygen](https://www.doxygen.nl/) a partir
de los comentarios Javadoc del código fuente, usando el fichero `Doxyfile` de la raíz.

- **En línea:** se publica automáticamente en GitHub Pages en cada push a `main`
  (workflow `.github/workflows/docs.yml`).
- **En local:**

  ```bash
  sudo apt install doxygen graphviz   # o: brew install doxygen graphviz
  doxygen Doxyfile
  xdg-open docs/html/index.html       # o: open docs/html/index.html
  ```

Los avisos de Doxygen (elementos sin documentar, parámetros sin descripción…) se
guardan en `docs/doxygen-warnings.log`.
