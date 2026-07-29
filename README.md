# Universidad de las Fuerzas Armadas ESPE

**Proyecto de Integración Curricular — Arquitectura de Microservicios**

**Autores:** Jairo Bonilla, Julio Viche, Reishel Tipán

---

## ⚠️ Importante

Los pipelines de CI/CD estan configurados para ejecutarse automaticamente segun el nombre y el destino de la rama. No cualquier rama creada con un nombre arbitrario disparara un pipeline. Si un desarrollador crea una rama como `git checkout -b rama-inventada` y hace push, el workflow simplemente no se ejecutara.

Solo las ramas que siguen la convencion explicada en la seccion de **Convencion de Ramas** activaran los pipelines correspondientes. Esta restriccion evita ejecuciones innecesarias y garantiza que solo el codigo que sigue el flujo definido pase por los controles de calidad y seguridad.

---

## Contenido

1. [Descripcion del Proyecto](#1-descripcion-del-proyecto)
2. [Arquitectura](#2-arquitectura)
3. [Stack Tecnologico](#3-stack-tecnologico)
4. [Estructura del Proyecto](#4-estructura-del-proyecto)
5. [Convencion de Ramas](#5-convencion-de-ramas)
6. [Pipelines CI/CD](#6-pipelines-cicd)
7. [Como Ejecutar](#7-como-ejecutar)
8. [Variables de Entorno](#8-variables-de-entorno)
9. [Seguridad](#9-seguridad)

---

## 1. Descripcion del Proyecto

**Master Gateway** es un API Gateway que actua como puerta de entrada unica para un ecosistema de microservicios. Centraliza la autenticacion, la autorizacion y el enrutamiento, y proporciona un panel administrativo web desde el cual se gestionan todos los componentes del sistema.

Cada microservicio hijo se registra dinamicamente en el Service Registry integrado en el gateway. Pueden ser desarrollados en cualquier tecnologia, desplegarse de forma independiente y escalarse segun sus propias necesidades. La comunicacion entre el gateway y los microservicios se realiza mediante tokens JWT.

El proyecto se compone de dos artefactos principales:

- **Backend:** Java 21 con Spring Boot. Implementa la logica de negocio, la autenticacion JWT en dos fases, 25 permisos granulares y un arbol de menus dinamico configurable por rol.
- **Frontend:** React 19 con TypeScript. Consume la API y ofrece una interfaz grafica de administracion para gestionar usuarios, roles, modulos, menus y servicios.

Adicionalmente, el sistema incluye un pipeline de seguridad basado en machine learning que analiza el codigo modificado en cada Pull Request para detectar vulnerabilidades antes de la integracion, junto con un flujo completo de CI/CD mediante GitHub Actions y ArgoCD para el despliegue automatizado.

### Lo que se puede gestionar desde el panel

- **Usuarios** — Creacion, edicion, eliminacion y asignacion de roles
- **Roles y Permisos** — 25 permisos granulares (CREAR, LEER, ACTUALIZAR, ELIMINAR, ASIGNAR por dominio)
- **Modulos** — Agrupacion logica de funcionalidades del sistema
- **Menus Dinamicos** — Arbol de navegacion configurable por rol. Cada nodo tiene una ruta interna y opcionalmente una URL externa para renderizar en iframe
- **Service Registry** — Registro de microservicios hijos con URL base, modo de validacion JWT y clave publica

---

## 2. Arquitectura

### Diagrama de Arquitectura

El sistema sigue una arquitectura de microservicios donde el Master Gateway es el punto de entrada unico. Los microservicios hijos se conectan al gateway, que les provee autenticacion y autorizacion centralizada.

```
  Usuario/Navegador
         |
         v
  +---------------------------------------------+
  |           Master Gateway                     |
  |  +---------------------------------------+  |
  |  |   Frontend (React + TypeScript)       |  |
  |  |   Servido por Nginx                   |  |
  |  +------------------+--------------------+  |
  |                     | HTTP/JSON             |
  |  +------------------v--------------------+  |
  |  |   Backend (Spring Boot + Java 21)     |  |
  |  |                                       |  |
  |  |   Auth     Users    Roles   Modules   |  |
  |  |   Menus    Service  Permisos          |  |
  |  |   Registry                            |  |
  |  +------------------+--------------------+  |
  |                     | JDBC                  |
  |  +------------------v--------------------+  |
  |  |   PostgreSQL 17                       |  |
  |  +---------------------------------------+  |
  +---------------------------------------------+
         |                              |
         | JWT (simetrico o asimetrico) | iframe o llamada API
         v                              v
  +------------------+      +----------------------+
  | Microservicio A  |      |  Microservicio B     |
  | (Java, Python,   | ...  |  (cualquier stack)   |
  |  Node, etc.)     |      |                      |
  +------------------+      +----------------------+
```

**Despliegue:** Las imagenes Docker se publican en Docker Hub y ArgoCD sincroniza automaticamente los manifiestos del repositorio GitOps para desplegar los cambios en el cluster.

*(Insertar aqui imagen del diagrama de arquitectura)*

### Arquitectura Hexagonal (Ports & Adapters)

El backend implementa el patron de arquitectura hexagonal. El nucleo del negocio no sabe nada sobre la base de datos, ni sobre HTTP, ni sobre JWT. Esas son decisiones de infraestructura que se conectan a traves de interfaces llamadas puertos.

| Capa | Que contiene | Depende de |
|------|-------------|------------|
| **Dominio** | Entidades de negocio (Usuario, Rol, MenuNode) e interfaces de salida | Nada. Es puro Java |
| **Aplicacion** | Casos de uso (servicios que orquestan la logica) | Solo del dominio |
| **Infraestructura** | Controladores REST, repositorios JPA, emision JWT, BCrypt | De las interfaces del dominio |

**Ejemplo concreto:** Cuando alguien llama a `POST /api/users`, el flujo es:

1. El **controlador REST** recibe la peticion HTTP
2. Llama al **caso de uso** `CreateUserUseCase` (interfaz en aplicacion)
3. El **servicio** `CreateUserService` implementa la logica: verifica permisos, valida datos unicos, hashea la contrasena
4. Para persistir, usa el **puerto de salida** `UserRepositoryPort` (interface en dominio)
5. El **adaptador** `UserRepositoryAdapter` (en infraestructura) ejecuta la consulta JPA

**Beneficio:** cada capa se prueba de forma aislada. Los servicios se prueban con mocks de los puertos, sin base de datos ni HTTP.

---

## 3. Stack Tecnologico

| Componente | Tecnologia |
|-----------|-----------|
| Backend | Java 21, Spring Boot 3, Spring Security, Spring Data JPA, Hibernate |
| Base de Datos | PostgreSQL 17, HikariCP |
| Autenticacion | JWT (HMAC-SHA256 y RSA-2048), BCrypt, Argon2 |
| Frontend | React 19, TypeScript, Vite, Tailwind CSS, React Router |
| Proxy | Nginx |
| Contenedores | Docker, Docker Compose |
| CI/CD | GitHub Actions, Docker Hub, ArgoCD, GitOps |
| Calidad de Codigo | SonarCloud |
| Seguridad con ML | Python, scikit-learn, dataset CVEfixes |
| Notificaciones | Telegram Bot |

---

## 4. Estructura del Proyecto

```
microservices-project/
│
├── master-gateway/                   Backend Spring Boot
│   ├── src/main/java/.../
│   │   ├── contexts/                 Modulos del negocio
│   │   │   ├── auth/                 Autenticacion
│   │   │   ├── identity/             Usuarios y roles
│   │   │   ├── menu/                 Arbol de menus
│   │   │   ├── module/               Modulos
│   │   │   └── service-registry/     Registro de microservicios
│   │   └── shared/                   Codigo compartido
│   ├── Dockerfile
│   ├── pom.xml
│   └── endpoints.md                  Documentacion detallada de la API
│
├── master-gateway-front/             Frontend React
│   ├── src/
│   │   ├── pages/                    Pantallas del panel
│   │   ├── components/               Componentes reutilizables
│   │   ├── navigation/               Rutas y navegacion
│   │   ├── api/                      Clientes HTTP
│   │   └── context/                  Contextos de React
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
├── ci/                               Pipeline de seguridad con ML
│   ├── security_pipeline.py          Orquestador
│   ├── diff_extractor.py             Extrae fragmentos del diff
│   ├── feature_extractor.py          Calcula caracteristicas
│   ├── security_gate.py              Clasificador ML
│   ├── notify.py                     Notificaciones Telegram
│   └── tests.py                      Pruebas del pipeline
│
├── modelo/                           Modelo de ML
│   ├── train_model.py                Entrenamiento
│   ├── extract_juliet.py             Procesamiento del dataset
│   └── model_artifacts/              Artefactos del modelo entrenado
│
├── .github/workflows/                Pipelines CI/CD
│   ├── feature-pipeline.yml
│   ├── integration-pipeline.yml
│   └── deploy-pipeline.yml
│
├── docker-compose.yml                Orquestacion de contenedores
└── .env.example                      Plantilla de variables de entorno
```

---

## 5. Convencion de Ramas

Para que los pipelines se ejecuten correctamente, las ramas deben seguir esta convencion de nombres. Cualquier rama que no cumpla con estos prefijos no disparara ningun workflow.

| Prefijo | Se ejecuta pipeline | Uso |
|---------|-------------------|-----|
| `feature/*` | ✅ Si, en cada push | Nuevas funcionalidades |
| `bugfix/*` | ✅ Si, en cada push | Correccion de errores |
| `hotfix/*` | ✅ Si, en cada push | Parches urgentes |
| `dev` | ✅ Si, en cada push | Integracion continua + deploy automatico |
| `main` | ❌ No por push | Solo recibe PRs desde dev |
| Cualquier otra | ❌ Sin pipeline | No dispara nada |

**Regla de oro:** `feature/*` o `bugfix/*` o `hotfix/*` → PR a `dev` → merge a `dev` → PR de `dev` a `main`

### Flujo paso a paso

1. El desarrollador crea una rama desde `dev`: `git checkout -b feature/mi-funcionalidad dev`
2. Desarrolla y hace push → se dispara el **Feature Pipeline** (build y tests rapidos)
3. Abre un Pull Request hacia `dev` → se dispara el **Integration Pipeline** (SonarCloud + escaneo ML + Telegram)
4. Si el PR esta en Draft, el pipeline se salta build y test para ahorrar recursos
5. Tras aprobacion y merge a `dev` → se dispara el **Deploy Pipeline** completo
6. Para llevar a `main`: abrir PR desde `dev` hacia `main`. El pipeline valida que el origen sea `dev`
7. Tras merge a `main`, no hay deploy automatico

---

## 6. Pipelines CI/CD

### Feature Pipeline

**Archivo:** `.github/workflows/feature-pipeline.yml`

Se ejecuta en cada push a ramas `feature/*`, `bugfix/*` o `hotfix/*`. Proporciona retroalimentacion rapida al desarrollador: saber en minutos si el codigo compila y las pruebas pasan, antes de abrir el Pull Request.

**Jobs:**
- `build-and-test` — Compila backend (Maven + Java 21) y construye frontend (Node 22 + npm). Corre en paralelo.

**Evidencia:**
*(Insertar aqui captura del Feature Pipeline ejecutandose en GitHub Actions)*

---

### Integration Pipeline

**Archivo:** `.github/workflows/integration-pipeline.yml`

Se ejecuta en Pull Requests hacia `dev` o `main`. Es el gate obligatorio para mergear. No despliega nada, solo valida.

**Jobs:**
1. `validate-branch-source` — Verifica que los PRs a `main` solo vengan de `dev`. Si alguien intenta un PR desde `feature/xyz` a `main`, falla con un mensaje claro.
2. `build-and-test` — Compila y ejecuta pruebas. Se salta si el PR es Draft.
3. `sonarcloud-scan` — Analiza calidad con SonarCloud. Verifica el Quality Gate.
4. `sast-ml-scan` — Pipeline de seguridad con ML: extrae fragmentos del diff, calcula caracteristicas y clasifica con modelo entrenado en CVEfixes.
5. `telegram-notify` — Notifica el resultado de cada fase por Telegram.

**Evidencias:**
*(Insertar aqui captura del Integration Pipeline mostrando los jobs completados)*
*(Insertar aqui captura del escaneo SAST ML)*

---

### Deploy Pipeline

**Archivo:** `.github/workflows/deploy-pipeline.yml`

Se ejecuta en push a `dev` (despues de un merge aprobado). Despliega el codigo al ambiente de desarrollo.

**Jobs:**
1. `build-and-test` — Revalida el merge commit
2. `sonarcloud-scan` — Calidad + cobertura
3. `sast-ml-scan` — Escaneo de seguridad
4. `docker-build-push` — Construye imagenes Docker y las publica en Docker Hub con el SHA del commit como tag
5. `gitops-deploy` — Actualiza los archivos `patch-image.yaml` en el repositorio GitOps. ArgoCD detecta el cambio y sincroniza automaticamente el nuevo manifiesto en el cluster
6. `telegram-notify` — Notifica el resultado completo

**Evidencias:**
*(Insertar aqui captura del Deploy Pipeline completo)*
*(Insertar aqui captura de la actualizacion en ArgoCD)*
*(Insertar aqui captura de la notificacion de Telegram)*

---

