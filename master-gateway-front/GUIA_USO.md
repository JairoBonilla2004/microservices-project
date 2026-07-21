# Guía de Uso — Panel de Administración Master Gateway

## Índice

1. [Arquitectura conceptual](#1-arquitectura-conceptual)
2. [Módulos (`/modules`)](#2-módulos-modules)
3. [Menús (`/menus`)](#3-menús-menus)
4. [Service Registry (`/services`)](#4-service-registry-services)
5. [Glosario](#5-glosario)

---

## 1. Arquitectura conceptual

El sistema de autorización está organizado en tres niveles jerárquicos:

```
MÓDULOS                  (agrupación funcional de alto nivel)
    │
    └── MENÚS             (ítems de navegación en el sidebar)
            │
            └── RUTAS      (URLs del frontend a las que se navega)


SERVICE REGISTRY         (microservicios backend registrados,
                          aparte — no tiene relación con módulos/menús)
```

El flujo completo es:

```
1. Creas un MÓDULO                    → "Ventas"
2. Creas MENÚS dentro de ese módulo   → "Productos" (/productos), "Pedidos" (/pedidos)
3. Asignas el MÓDULO a un ROL         → rol "Vendedor" puede ver módulo Ventas
4. Asignas los MENÚS a ese ROL        → rol "Vendedor" ve esos ítems en el sidebar
5. Asignas PERMISOS al ROL            → rol "Vendedor" puede crear productos, etc.
```

---

## 2. Módulos (`/modules`)

### 2.1. ¿Qué es un módulo?

Un **módulo** es una agrupación funcional de alto nivel. Representa un área del sistema.

**Ejemplos del mundo real:**

| Sistema | Módulos |
|---------|---------|
| ERP | Ventas, Compras, Inventario, Contabilidad, RRHH |
| Hospital | Admisión, Consulta Externa, Farmacia, Laboratorio |
| Banco | Cuentas, Préstamos, Tarjetas, Seguros |

Cada módulo:
- Tiene un **nombre** y una **descripción**
- Puede tener un **icono** (identificador visual)
- Tiene un **orden** (posición en la que aparece)
- Se asigna a **roles** para que esos roles tengan acceso al módulo

### 2.2. Campos del formulario

| Campo | Tipo | Requerido | ¿Qué es? |
|-------|------|-----------|----------|
| **Nombre** | Texto | Sí | Nombre visible del módulo. Ej: "Gestión de Usuarios" |
| **Descripción** | Texto largo | Sí | Explica qué funcionalidades agrupa este módulo |
| **Icono** | Texto | No | Nombre del ícono (opcional, se usa en futuras versiones) |
| **Orden** | Número | Sí | Posición del módulo en la lista (menor número = más arriba) |

### 2.3. Ejemplos de módulos

| Nombre | Descripción | Orden |
|--------|-------------|-------|
| Gestión de Usuarios | Administración de cuentas, roles y permisos | 1 |
| Ventas | Procesos de venta, cotizaciones y facturación | 2 |
| Inventario | Control de stock, entradas y salidas | 3 |
| Reportes | Generación de reportes y estadísticas | 4 |

### 2.4. Acciones disponibles en el listado

| Acción | ¿Cuándo aparece? | ¿Qué hace? |
|--------|------------------|------------|
| **Crear módulo** | Si tienes permiso `MODULES_CREATE` | Abre el formulario de creación |
| **Editar** | Si el módulo está ACTIVO y tienes `MODULES_UPDATE` | Permite cambiar nombre, descripción, icono, orden |
| **Desactivar** | Si el módulo está ACTIVO y tienes `MODULES_DELETE` | Marca el módulo como INACTIVO (borrado lógico) |
| **Reactivar** | Si el módulo está INACTIVO y tienes `MODULES_DELETE` | Vuelve a activar el módulo |

### 2.5. Asignar módulo a un rol

En la parte superior del listado hay un panel "Asignar módulo a rol":

1. **Módulo**: selecciona el módulo que quieres asignar
2. **Rol**: selecciona el rol que recibirá acceso al módulo
3. **Asignar**: confirma la asignación

**¿Qué efecto tiene?** Los usuarios con ese rol podrán ver los menús de ese módulo en su sidebar (siempre que los menús también estén asignados al rol).

---

## 3. Menús (`/menus`)

### 3.1. ¿Qué es un menú?

Un **menú** (o ítem de menú) es un elemento de navegación en el sidebar. Los menús se organizan en una estructura de **árbol jerárquico**:

```
Módulo Ventas (asignado al rol)
├── Productos           → /productos        (nodo hoja: tiene URL)
│   ├── Categorías      → /productos/categorias
│   └── Marcas          → /productos/marcas
├── Pedidos             → /pedidos          (nodo hoja)
└── Configuración                          (nodo intermedio: sin URL)
    ├── Métodos de pago → /config/pagos
    └── Impuestos       → /config/impuestos
```

**Reglas del árbol:**

- **Nodo raíz**: No tiene padre (`parentId` vacío). Es el nivel superior dentro del módulo.
- **Nodo intermedio**: Tiene hijos pero **no tiene URL**. Solo agrupa.
- **Nodo hoja**: **Tiene URL** y puede no tener hijos. Es navegable.
- Un nodo no puede tener URL y tener hijos al mismo tiempo (si tiene URL, se considera hoja).

### 3.2. Campos del formulario

| Campo | Tipo | Requerido | ¿Qué es? |
|-------|------|-----------|----------|
| **Nombre del ítem** | Texto | Sí | Texto visible en el sidebar. Ej: "Productos" |
| **URL de ruta** | Texto | No | Ruta del frontend. Ej: `/productos`. Solo nodos hoja. |
| **Módulo** | Select | Sí | A qué módulo pertenece este menú |
| **Nodo padre** | Select | No | Si se deja vacío, es nodo raíz del módulo. Si se elige un padre, será hijo de ese nodo |
| **Orden** | Número | Sí | Posición del ítem dentro de su nivel. Menor número = arriba |

### 3.3. Ejemplos de menús

Para un módulo "Ventas":

| Nombre | URL | Módulo | Padre | Orden | ¿Qué hace? |
|--------|-----|--------|-------|-------|------------|
| Productos | *(vacío)* | Ventas | *(raíz)* | 1 | Nodo contenedor, agrupa sub-ítems |
| Listado | /productos | Ventas | Productos | 1 | Muestra el listado de productos |
| Categorías | /productos/categorias | Ventas | Productos | 2 | Administración de categorías |
| Pedidos | /pedidos | Ventas | *(raíz)* | 2 | Nodo hoja, navega a listado de pedidos |
| Configuración | *(vacío)* | Ventas | *(raíz)* | 3 | Nodo contenedor |
| Impuestos | /config/impuestos | Ventas | Configuración | 1 | Administración de impuestos |

El sidebar resultante se vería así:
```
▶ Ventas
    ▶ Productos
        Listado
        Categorías
    Pedidos
    ▶ Configuración
        Impuestos
```

### 3.4. Acciones en el árbol

| Acción | ¿Qué hace? |
|--------|------------|
| **Crear ítem** | Abre formulario para crear un nuevo nodo de menú |
| **Editar** | Cambia nombre, URL, orden del nodo (no se puede cambiar módulo ni padre después de creado) |
| **Eliminar** | Borra el nodo del sistema (no solo del rol) |
| **Quitar del rol** | Desasigna el nodo del rol actual. El nodo sigue existiendo, pero los usuarios de ese rol ya no lo ven |

### 3.5. Ver árbol por rol

Selecciona un rol en el dropdown "Ver árbol del rol" para ver qué menús tiene asignados ese rol. Esto te permite:

1. Verificar qué ve cada rol en el sidebar
2. Quitar menús específicos de un rol (botón "Quitar del rol" en cada nodo)

### 3.6. Asignar menú a un rol

En el panel superior "Asignar ítem de menú a rol":

1. **Rol**: selecciona el rol destino
2. **Nodo de menú**: selecciona qué nodo asignar (una vez elegido el rol)
3. **Asignar**: confirma la asignación

**¿Qué efecto tiene?** El nodo y todos sus hijos aparecerán en el sidebar de los usuarios con ese rol.

---

## 4. Service Registry (`/services`)

### 4.1. ¿Qué es el Service Registry?

El **Service Registry** es un registro de microservicios backend. Cada entrada representa un microservicio independiente que se comunica con el Gateway.

**No tiene relación con módulos/menús.** Módulos y menús controlan la navegación del frontend. El Service Registry controla la comunicación entre el Gateway y los microservicios backend.

```
                    ┌──────────────┐
                    │   Gateway     │
                    │  (este panel) │
                    └──────┬───────┘
                           │
            ┌──────────────┼──────────────┐
            ▼              ▼              ▼
      ┌──────────┐  ┌──────────┐  ┌──────────┐
      │ ventas-ms│  │inv-ms   │  │users-ms  │
      │:8081     │  │:8082     │  │:8083     │
      └──────────┘  └──────────┘  └──────────┘
```

### 4.2. Campos del formulario

| Campo | Tipo | Requerido | ¿Qué es? |
|-------|------|-----------|----------|
| **Código del servicio** | Texto | Sí | Identificador único del microservicio. Ej: `ventas-ms`. No se puede cambiar después. |
| **Nombre del servicio** | Texto | Sí | Nombre descriptivo. Ej: "Módulo de Ventas" |
| **URL Base** | Texto | Sí | Dirección donde corre el microservicio. Ej: `http://ventas-ms:8081` |
| **Modo de validación JWT** | Select | Sí | Cómo el microservicio valida los tokens JWT |

### 4.3. Modos de validación JWT

| Modo | Descripción | ¿Cuándo usarlo? |
|------|-------------|-----------------|
| **Ninguno (NONE)** | El microservicio no valida tokens JWT | Solo para microservicios internos de confianza, o que no necesitan autenticación |
| **Delegado (DELEGATE)** | El microservicio consulta al Gateway en CADA request para validar el token | Máxima seguridad. El MS no necesita saber nada de criptografía. **Recomendado** |
| **Local (LOCAL)** | El microservicio obtiene la clave pública del Gateway al iniciar y valida JWT localmente | Mínima latencia. El MS necesita tener lógica de validación JWT. Requiere `jwt.mode=asymmetric` en el Gateway |

### 4.4. Ejemplos de registro

| Código | Nombre | URL | Validación | ¿Qué hace? |
|--------|--------|-----|------------|------------|
| `ventas-ms` | Módulo de Ventas | `http://ventas-ms:8081` | DELEGATE | Servicio de ventas, consulta al Gateway en cada request |
| `inventario-ms` | Inventario | `http://inventario:8082` | DELEGATE | Servicio de inventario |
| `reportes-ms` | Generador de Reportes | `http://reportes:8083` | NONE | Servicio interno sin autenticación |
| `pagos-ms` | Pasarela de Pagos | `http://pagos:8084` | LOCAL | Servicio con alta carga, valida tokens localmente |

### 4.5. Acciones en el listado

| Acción | ¿Qué hace? |
|--------|------------|
| **Registrar servicio** | Abre formulario para registrar un nuevo microservicio |
| **Editar** | Permite cambiar nombre, URL base (no se puede cambiar código ni modo de validación) |
| **Desactivar** | Marca el servicio como inactivo (borrado lógico). El Gateway rechazará validaciones para este servicio |

---

## 5. Glosario

| Término | Significado |
|---------|-------------|
| **Módulo** | Agrupación funcional de alto nivel (ej: Ventas, RRHH). Se asigna a roles. |
| **Menú** | Ítem de navegación en el sidebar. Se organiza en árbol jerárquico dentro de un módulo. |
| **Nodo raíz** | Menú sin padre, nivel superior dentro de un módulo. |
| **Nodo hoja** | Menú con URL (navegable). |
| **Nodo intermedio** | Menú sin URL, solo agrupa hijos. |
| **Orden** | Número que define la posición de un módulo o menú. Menor = más arriba. |
| **Rol** | Conjunto de permisos, módulos y menús asignados a un usuario. |
| **Sidebar** | Barra lateral izquierda con los menús de navegación. |
| **Service Registry** | Registro de microservicios backend que se comunican con el Gateway. |
| **DELEGATE** | Modo de validación donde el MS consulta al Gateway en cada request. |
| **LOCAL** | Modo de validación donde el MS valida JWT localmente con la clave pública del Gateway. |
| **Gateway** | Punto de entrada único (Master Gateway) que orquesta la autenticación, autorización y enrutamiento. |
