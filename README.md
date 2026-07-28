# Guía de Estrategia de Ramas y Pipeline CI/CD

---

## IMPORTANTE — Convención de Nombres de Ramas

El pipeline CI/CD está configurado para reaccionar automáticamente según el **nombre y destino** de la rama.  
Solo las ramas que sigan esta convención activarán los workflows correspondientes:

| Prefijo de Rama | ¿Dispara pipeline? | ¿Cuál? |
|-----------------|-------------------|--------|
| `feature/*` | Sí | Feature Pipeline (push) |
| `bugfix/*` | Sí | Feature Pipeline (push) |
| `hotfix/*` | Sí | Feature Pipeline (push) |
| `dev` | Sí | Deploy Pipeline (push) |
| `main` | ❌ No dispara por push | Solo recibe PRs desde `dev` |
| Cualquier otra | ❌ Sin pipeline automático | No recomendada |

> **Regla de oro:**  
> `feature/*`, `bugfix/*` o `hotfix/*` → PR a `dev` → merge a `dev` → PR de `dev` a `main`

---

## 1. Convención de Ramas

### 1.1 Ramas de Feature (`feature/*`)

Se crean a partir de `dev` para desarrollar nuevas funcionalidades.

```
feature/registro-usuarios
feature/menu-dinamico
```

### 1.2 Ramas de Bugfix (`bugfix/*`)

Para corrección de errores sobre código existente en `dev`.

```
bugfix/corregir-orden-menu
bugfix/duplicados-en-arbol
```

### 1.3 Ramas de Hotfix (`hotfix/*`)

Para correcciones urgentes que deben llegar rápido a `dev` e idealmente a `main`.

```
hotfix/parche-seguridad-jwt
```

### 1.4 Rama `dev`

Rama de integración continua. Todos los PRs de `feature/*`, `bugfix/*` y `hotfix/*` se mergean aquí.  
El push a `dev` dispara el **Deploy Pipeline** completo (build, tests, SonarCloud, SAST ML, Docker, GitOps).

### 1.5 Rama `main`

Rama de producción. Solo recibe PRs desde `dev`.  
El pipeline **Integration Pipeline** valida que el PR provenga de `dev` y no de otra rama.

---

## 2. Flujo de Trabajo (GitFlow Adaptado)

```
                    feature/xyz
                  /            \
dev  ------------·--------------·--- (integración continua + deploy)
                  \            /
                   bugfix/abc

  (PR desde dev)
         |
         v
main  --·--------------------------- (producción)
```

### Ciclo típico:

1. Crear rama desde `dev`: `git checkout -b feature/mi-funcionalidad dev`
2. Desarrollar y hacer push. Se dispara **Feature Pipeline** (build & test rápido).
3. Abrir Pull Request hacia `dev`. Se dispara **Integration Pipeline** (build + SonarCloud + SAST ML).
4. Tras aprobación y merge a `dev`, se dispara **Deploy Pipeline** (build + SonarCloud + SAST ML + Docker + GitOps).
5. Para llevar a `main`: abrir PR desde `dev` hacia `main`. El Integration Pipeline valida el origen.
6. Tras merge a `main`, no hay deploy automático (requiere intervención manual o pipeline adicional).

---

## 3. Pipelines del Sistema

### 3.1 Feature Pipeline

**Archivo:** `.github/workflows/feature-pipeline.yml`  
**Disparador:** Push a `feature/*`, `bugfix/*` o `hotfix/*`  
**Propósito:** Feedback rápido al desarrollador (build + tests).  
**Jobs:**
- `build-and-test` — compila backend (Maven + Java 21) y frontend (npm + Node 22)

![Feature Pipeline](imagenes/feature-pipeline.png)
*Ancho sugerido: 800px — Captura del workflow corriendo en GitHub Actions*

### 3.2 Integration Pipeline

**Archivo:** `.github/workflows/integration-pipeline.yml`  
**Disparador:** PR hacia `dev` o `main` (eventos: `opened`, `synchronize`, `reopened`, `ready_for_review`)  
**Propósito:** Gate obligatorio para mergear.  
**Jobs:**
1. `validate-branch-source` — valida que PR a `main` solo venga de `dev`
2. `build-and-test` — build + tests (se salta si el PR es Draft)
3. `sonarcloud-scan` — análisis de calidad con SonarCloud
4. `sast-ml-scan` — escaneo de seguridad con ML (detecta vulnerabilidades en Java)
5. `telegram-notify` — notifica resultado por Telegram

![Integration Pipeline — Jobs](imagenes/integration-pipeline-jobs.png)
*Ancho sugerido: 800px — Vista de los jobs del Integration Pipeline*

![Integration Pipeline — SAST ML](imagenes/integration-pipeline-sast.png)
*Ancho sugerido: 800px — Resultado del escaneo SAST con ML*

### 3.3 Deploy Pipeline

**Archivo:** `.github/workflows/deploy-pipeline.yml`  
**Disparador:** Push a `dev`  
**Propósito:** Despliegue real al ambiente de desarrollo.  
**Jobs:**
1. `build-and-test` — revalidación del merge commit
2. `sonarcloud-scan` — calidad + cobertura
3. `sast-ml-scan` — escaneo de seguridad
4. `docker-build-push` — build y push de imágenes a Docker Hub
5. `gitops-deploy` — actualiza el repositorio GitOps con los nuevos tags
6. `telegram-notify` — notificación final del deploy

![Deploy Pipeline](imagenes/deploy-pipeline.png)
*Ancho sugerido: 800px — Flujo completo del Deploy Pipeline*

![Notificación Telegram](imagenes/telegram-notificacion.png)
*Ancho sugerido: 600px — Ejemplo de notificación en Telegram*

---

## 4. Secretos y Variables Requeridas en GitHub

Para que los pipelines funcionen correctamente, deben configurarse estos secretos en `Settings → Secrets and variables → Actions` del repositorio:

### Secrets (`Settings → Secrets`)

| Secreto | Propósito |
|---------|-----------|
| `SONAR_TOKEN` | Token de autenticación para SonarCloud |
| `TELEGRAM_BOT_TOKEN` | Token del bot de Telegram para notificaciones |
| `TELEGRAM_CHAT_ID` | ID del chat/grupo de Telegram |
| `DOCKERHUB_USERNAME` | Usuario de Docker Hub |
| `DOCKERHUB_TOKEN` | Token de acceso de Docker Hub |
| `GITOPS_GITHUB_TOKEN` | Token con acceso al repositorio GitOps |

### Variables (`Settings → Variables`)

| Variable | Propósito | Default |
|----------|-----------|---------|
| `SONAR_ORG` | Organización de SonarCloud | `jairobonilla2004` |

---

## 5. Buenas Prácticas

1. **Nombres descriptivos**: Usa nombres en minúscula con guiones (`feature/login-social`, no `feature/abc`).
2. **PRs pequeños**: Cada PR debe resolver una sola unidad lógica. Entre 100–400 líneas es ideal.
3. **No mergees tu propio PR**: Solicita revisión de otro miembro del equipo.
4. **Draft PRs**: Si el código está en progreso, abre el PR como Draft para evitar ejecución del Integration Pipeline.
5. **Commit messages**: Usa convención semántica (`feat:`, `fix:`, `refactor:`, `test:`, `docs:`).

---

## 6. Evidencias de Funcionamiento

### 6.1 Feature Pipeline — Build Exitoso

![Feature Pipeline OK](imagenes/feature-pipeline-ok.png)
*Ancho sugerido: 800px*

### 6.2 Integration Pipeline — Validación de Origen de Rama

![Validación Branch Source](imagenes/validate-branch-source.png)
*Ancho sugerido: 800px*

### 6.3 Escaneo SAST ML — Reporte de Seguridad

![SAST ML Report](imagenes/sast-ml-report.png)
*Ancho sugerido: 800px*

### 6.4 Despliegue GitOps — Actualización de Manifiesto

![GitOps Deploy](imagenes/gitops-deploy.png)
*Ancho sugerido: 800px*

---

## 7. Resolución de Problemas Comunes

| Problema | Causa | Solución |
|----------|-------|----------|
| El pipeline no se dispara | La rama no sigue la convención de nombres | Renombrar la rama a `feature/*`, `bugfix/*` o `hotfix/*` |
| PR a main rechazado | El PR no viene desde `dev` | Cambiar el origen del PR a `dev` |
| SAST ML falla | El modelo no está entrenado o falta `modelo/model_artifacts/` | Ejecutar `python modelo/train_model.py` para generar los artefactos |
| SonarCloud falla | `SONAR_TOKEN` no configurado o expirado | Verificar secrets en GitHub |
| Docker Build falla | `DOCKERHUB_TOKEN` inválido | Regenerar token en Docker Hub y actualizar el secreto |
| Deploy no actualiza | `GITOPS_GITHUB_TOKEN` sin permisos de escritura | Verificar que el token tenga acceso al repo GitOps |

---

*Documentación generada para el proyecto **Master Gateway** — Arquitectura de Microservicios*
*Autores: Jairo Bonilla, Reishel Tipán, Julio Viche*
