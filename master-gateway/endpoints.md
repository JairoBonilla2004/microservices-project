# Master Gateway API — Documentación Completa de Endpoints

## 1. Arquitectura General y Flujo de Autenticación

### 1.1 Hexagonal Architecture (Ports & Adapters)

Cada endpoint sigue el patrón **hexagonal**: Controller → UseCase (inbound port) → Service (implementación) → Port (outbound) → Adapter (JPA, JWT, etc.).

```text
[ HTTP Request ]
       |
[ Controller ]         ← Infrastructure (inbound adapter / web)
       |
[ UseCase (interface) ] ← Application (inbound port)
       |
[ Service (impl) ]     ← Application (core)
       |
[ Port (interface) ]   ← Domain (outbound port)
       |
[ Adapter (impl) ]     ← Infrastructure (outbound adapter: JPA, JWT, BCrypt, etc.)
```

### 1.2 Autenticación en 3 Pasos

```
POST /api/auth/login   →  TEMP_TOKEN (5 min)
       ↓
POST /api/auth/select-role  →  ACCESS_TOKEN (15 min) + REFRESH_TOKEN (7 días)
       ↓
[ Todas las llamadas protegidas llevan ACCESS_TOKEN en header Authorization: Bearer ... ]
```

### 1.3 Tipos de Token JWT

| Token | Expiración | Propósito |
|-------|-----------|-----------|
| `TEMP_TOKEN` | 5 minutos | Autenticación inicial, permite seleccionar rol |
| `ACCESS_TOKEN` | 15 minutos | Autoriza llamadas a endpoints protegidos |
| `REFRESH_TOKEN` | 7 días | Obtener nuevo ACCESS_TOKEN sin re-login |

#### Claims incluidos en ACCESS_TOKEN

```json
{
  "jti": "uuid-unico",
  "sub": "userId-uuid",
  "type": "ACCESS_TOKEN",
  "roleName": "ADMIN",
  "role": "roleId-uuid",
  "permissions": "USERS_CREATE,USERS_READ,USERS_UPDATE,...",
  "iat": 1710000000,
  "exp": 1710000900,
  "iss": "master-gateway"
}
```

### 1.4 Modos de Firma JWT

El gateway soporta **2 modos** de firma/validación de tokens:

#### Modo Simétrico (HMAC-SHA256) — POR DEFECTO

- Usa `jwt.secret` del `application.yml` como clave HMAC
- Un solo servicio → no necesita compartir clave pública
- Implementado en: `SymmetricJwtIssuerAdapter`, `SymmetricJwtValidatorAdapter`
- Config: `jwt.mode=DIRECT` (omitiendo la propiedad, se usa simétrico)

#### Modo Asimétrico (RSA-2048)

- Genera un par de llaves RSA-2048 en memoria al iniciar
- Firma con `PrivateKey`, valida con `PublicKey`
- Útil cuando otros microservicios necesitan validar tokens sin compartir secreto
- Cada servicio registrado puede aportar su propia `publicKey` en el Service Registry
- Implementado en: `AsymmetricJwtIssuerAdapter`, `AsymmetricJwtValidatorAdapter`
- Config: `jwt.mode=asymmetric`
- Activado vía `@ConditionalOnProperty(name = "jwt.mode", havingValue = "asymmetric")`

#### Validación Interna entre Microservicios

`POST /api/internals/validate-token` usa `TokenIssuerFactory` para seleccionar el validador:

- Si `serviceCode` es `null` → validador por defecto (simétrico)
- Si `serviceCode` está presente → busca el servicio registrado y usa su `validationMode`
  - `NONE` o `DIRECT` → validador simétrico (misma clave secreta)
  - `ASYMMETRIC` → crea `AsymmetricJwtValidatorAdapter` con la `publicKey` del servicio

### 1.5 Rate Limiting

| Endpoint | Límite | Ventana | Clave |
|----------|--------|---------|-------|
| `POST /api/auth/login` | 5 intentos | 1 minuto | IP |
| `POST /api/auth/register` | 5 solicitudes | 1 minuto | IP |

Implementado con Caffeine cache y AspectJ (`@Around`). Ambos endpoints retornan `429 Too Many Requests` con headers `Retry-After`.

### 1.6 Soft Delete (Borrado Lógico)

Todos los `DELETE` endpoints realizan **borrado lógico**: cambian `estado` de `ACTIVO` a `INACTIVO`.

- `SoftDeleteRepository` sobrescribe `deleteById()`, `delete()`, `deleteAll()` para hacer `UPDATE estado='INACTIVO'`
- Las consultas de listado (`findAll`, `findAllActive`, etc.) filtran por `estado='ACTIVO'`
- Los registros INACTIVO persisten en BD con integridad referencial intacta

### 1.7 Sistema de Autorización

Cada `requirePermission()` en los servicios consulta las authorities del JWT extraídas por `JwtAuthenticationFilter`.

- El JWT contiene `permissions` como string separado por comas en un claim
- El filtro Spring Security convierte cada permiso en `GrantedAuthority` con prefijo `PERMISSION_`
- `SpringSecurityAuthorizationAdapter.requirePermission(Permission)` verifica las authorities del `SecurityContext`
- `requireOwnershipOrPermission(userId, permission)` permite acceso si el usuario es el dueño del recurso **o** tiene el permiso
- No existe `@PreAuthorize` en los controllers — toda la autorización es programática y centralizada

### 1.8 25 Permisos del Sistema

```text
USERS_CREATE | USERS_READ | USERS_UPDATE | USERS_DELETE
USERS_ASSIGN_ROLE | USERS_REVOKE_ROLE
ROLES_CREATE | ROLES_READ | ROLES_UPDATE | ROLES_DELETE | ROLES_ASSIGN_USERS
MODULES_CREATE | MODULES_READ | MODULES_UPDATE | MODULES_DELETE | MODULES_ASSIGN
MENUS_CREATE | MENUS_READ | MENUS_UPDATE | MENUS_DELETE | MENUS_ASSIGN
SERVICES_CREATE | SERVICES_READ | SERVICES_UPDATE | SERVICES_DELETE
```

### 1.9 Formato de Respuesta de Error

```json
{
  "codigo": 400,
  "mensaje": "Mensaje descriptivo del error",
  "timestamp": "2026-07-15T19:49:28.030295",
  "errores": {
    "campo1": "Error de validación del campo 1",
    "campo2": "Error de validación del campo 2"
  }
}
```

| HTTP | Condición |
|------|-----------|
| 400 | Validation errors, invalid input |
| 401 | Invalid/expired JWT, bad credentials |
| 403 | Missing required permission |
| 404 | Resource not found |
| 409 | Duplicate entry (username/email) |
| 429 | Rate limit exceeded |
| 500 | Unexpected internal error (sin stack trace) |

---

## 2. AUTH — Autenticación (6 Endpoints)

---

### 2.1 `POST /api/auth/login`

Inicia sesión del usuario. Verifica credenciales contra BD, devuelve token temporal y roles disponibles.

#### Protección
- **Sin autenticación** (público)
- Rate limit: 5/min por IP (LoginRateLimitingAspect)

#### Request Body
```json
{
  "username": "boss_admin",
  "password": "Admin1234"
}
```

#### Response 200 OK
```json
{
  "tempToken": "eyJhbGciOiJIUzI1NiJ9...",
  "roles": [
    {
      "roleId": "92ad2210-9fc9-4397-a3a3-983a1729a285",
      "nombre": "ADMIN"
    }
  ]
}
```

#### Response 401 — Credenciales inválidas (mismo mensaje para usuario inexistente y contraseña incorrecta — OWASP)
```json
{
  "codigo": 401,
  "mensaje": "Credenciales inválidas",
  "timestamp": "2026-07-15T19:49:28.030295"
}
```

#### Flow interno
1. Busca usuario por `username` en BD → si no existe → `AuthenticationException("Credenciales inválidas")`
2. Verifica `user.isActive()` → si inactivo → `AuthenticationException("Credenciales inválidas")`
3. Verifica password con BCrypt → si no coincide → `AuthenticationException("Credenciales inválidas")`
4. Obtiene asignaciones usuario-rol activas → si ninguna → `AuthenticationException("El usuario no tiene roles activos")`
5. Emite `TEMP_TOKEN` (5 min) vía `TokenIssuerPort.issueTempToken(userId)`
6. Retorna tempToken + lista de roles (id, nombre)

---

### 2.2 `POST /api/auth/register`

Registra un nuevo usuario en el sistema.

#### Protección
- **Sin autenticación** (público)
- Rate limit: 5/min por IP (RegisterRateLimitingAspect)
- Valida que password == confirmPassword
- Valida username único, email único

#### Request Body
```json
{
  "username": "nuevo_usuario",
  "email": "user@email.com",
  "password": "MiPassword123",
  "confirmPassword": "MiPassword123",
  "nombreCompleto": "Nombre Completo"
}
```

#### Response 201 Created
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "username": "nuevo_usuario",
  "email": "user@email.com",
  "nombreCompleto": "Nombre Completo"
}
```

#### Response 400 — Contraseñas no coinciden
```json
{
  "codigo": 400,
  "mensaje": "Las contraseñas no coinciden",
  "timestamp": "2026-07-15T19:49:28.030295"
}
```

#### Response 409 — Username o email duplicado
```json
{
  "codigo": 409,
  "mensaje": "El nombre de usuario 'nuevo_usuario' ya existe",
  "timestamp": "2026-07-15T19:49:28.030295"
}
```

#### Flow interno
1. Valida que `password == confirmPassword` → si no → `InvalidInputException`
2. Crea usuario via `CreateUserUseCase`:
   - Verifica `username` único y `email` único en BD
   - Hashea password con BCrypt (cost factor 12)
   - Persiste en tabla `users` con estado `ACTIVO`
3. Retorna datos del usuario creado (NUNCA la contraseña)

---

### 2.3 `POST /api/auth/select-role`

Selecciona el rol con el que el usuario accederá al sistema. Invalida el tempToken y emite accessToken + refreshToken.

#### Protección
- Requiere `TEMP_TOKEN` válido (no expirado, no usado previamente)

#### Request Body
```json
{
  "tempToken": "eyJhbGciOiJIUzI1NiJ9...",
  "roleId": "92ad2210-9fc9-4397-a3a3-983a1729a285"
}
```

#### Response 200 OK
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 3600
}
```

#### Response 401 — Token temporal inválido o expirado
```json
{
  "codigo": 401,
  "mensaje": "Token temporal inválido o expirado",
  "timestamp": "2026-07-15T19:49:28.030295"
}
```

#### Response 403 — Usuario no tiene ese rol
```json
{
  "codigo": 403,
  "mensaje": "El usuario no tiene el rol solicitado",
  "timestamp": "2026-07-15T19:49:28.030295"
}
```

#### Flow interno
1. Valida `TEMP_TOKEN` via `TokenValidationPort.validateTempToken()` → si inválido/expirado/usado → 401
2. Obtiene `userId` del token
3. Verifica asignación usuario-rol activa para `userId + roleId` → si no → 403
4. Obtiene el rol y resuelve sus permisos desde BD (`DatabasePermissionResolver.resolvePermissions(roleName)`)
5. Emite `ACCESS_TOKEN` (15 min) con claims: userId, roleId, roleName, permissions (string separado por comas)
6. Emite `REFRESH_TOKEN` (7 días) con claims: userId, roleId, roleName
7. Guarda refreshToken en tabla `refresh_tokens` (para revocación)
8. **Invalida** el tempToken (no puede reutilizarse)
9. Retorna accessToken + refreshToken + expiresIn (segundos)

---

### 2.4 `POST /api/auth/refresh-token`

Obtiene un nuevo par accessToken + refreshToken usando un refreshToken válido.

#### Protección
- Requiere `REFRESH_TOKEN` válido (no expirado, no revocado)
- El refreshToken anterior es **revocado** (no se puede reutilizar)

#### Request Body
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### Response 200 OK
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 3600
}
```

#### Flow interno
1. Busca refreshToken en tabla `refresh_tokens`
2. Si no existe → `AuthenticationException("Token de refresco inválido")`
3. Si `!token.isValid()` (revocado o expirado) → `AuthenticationException("Token de refresco expirado o revocado")`
4. **Revoca** el viejo refreshToken (no puede reutilizarse)
5. Obtiene el rol y resuelve permisos
6. Emite nuevos accessToken + refreshToken
7. Guarda el nuevo refreshToken en BD
8. Retorna nuevo par

---

### 2.5 `POST /api/auth/logout`

Cierra la sesión del usuario. Revoca el refreshToken en BD e invalida el accessToken en memoria.

#### Protección
- Requiere `ACCESS_TOKEN` (header Authorization)
- Body: refreshToken a revocar

#### Request Body
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### Response 204 No Content
*(Sin cuerpo)*

#### Flow interno
1. Extrae accessToken del header `Authorization`
2. Si accessToken presente → `tokenValidationPort.revokeAccessToken(accessToken)` (se agrega a set en memoria `revokedAccessTokens`)
3. Busca refreshToken en BD → si existe → `token.revoke()` + persist
4. Retorna 204

---

### 2.6 `POST /api/internals/validate-token`

Endpoint público para que otros microservicios validen tokens JWT emitidos por el gateway.

#### Protección
- **Sin autenticación** (público para comunicación entre microservicios)

#### Request Body
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "serviceCode": "my-microservice"
}
```

- `serviceCode` es **opcional**: si se omite o va `null`, se usa el validador por defecto (simétrico)
- Si `serviceCode` está presente, busca el servicio registrado y selecciona el validador según `validationMode`:
  - `NONE`/`DIRECT` → validador simétrico (misma clave HMAC)
  - `ASYMMETRIC` → validador RSA con la `publicKey` del servicio

#### Response 200 OK — Token Válido
```json
{
  "valid": true,
  "userId": "b0d46c6d-c218-4900-b685-47c476a8e51b",
  "roleId": "92ad2210-9fc9-4397-a3a3-983a1729a285",
  "tokenType": "ACCESS_TOKEN",
  "message": null
}
```

#### Response 401 — Token Inválido
```json
{
  "valid": false,
  "userId": null,
  "roleId": null,
  "tokenType": null,
  "message": "JWT expired at 2026-07-15T20:00:00Z"
}
```

#### Flow interno
1. Si `serviceCode` está presente → busca servicio registrado en BD
2. Si el servicio no existe → `IllegalArgumentException("Servicio no registrado: code")`
3. Selecciona validador según el modo de validación del servicio
4. Valida el token: parsea JWT, verifica firma, verifica expiración, verifica tipo `ACCESS_TOKEN`
5. Si válido → `TokenValidationResponse(valid=true, userId, roleId, tokenType, null)`
6. Si excepción → `TokenValidationResponse(valid=false, null, null, null, mensajeError)`

---

## 3. USERS — Gestión de Usuarios (8 Endpoints)

Todos los endpoints de Users requieren `ACCESS_TOKEN` en header `Authorization: Bearer <token>`.

| Permiso | Endpoints |
|---------|-----------|
| `USERS_CREATE` | `POST /api/users` |
| `USERS_READ` | `GET /api/users`, `GET /api/users/{id}` |
| `USERS_UPDATE` | `PUT /api/users/{id}` (o propio usuario sin permiso) |
| `USERS_DELETE` | `DELETE /api/users/{id}` |
| `ROLES_ASSIGN_USERS` | `POST /api/users/{id}/roles`, `DELETE /api/users/{id}/roles/{roleId}` |

---

### 3.1 `GET /api/users`

Lista todos los usuarios activos.

#### Request
```
GET /api/users
Authorization: Bearer <ACCESS_TOKEN>
```

#### Response 200 OK
```json
[
  {
    "id": "b0d46c6d-c218-4900-b685-47c476a8e51b",
    "username": "boss_admin",
    "email": "admin@sistema.com",
    "nombreCompleto": "Administrador del Sistema",
    "estado": "ACTIVO",
    "fechaCreacion": "2026-07-15T19:49:28.030295",
    "fechaActualizacion": "2026-07-15T19:49:28.030295"
  }
]
```

#### Authorization
Requiere `USERS_READ`

#### Flow interno
1. `authorizationPort.requirePermission(Permission.USERS_READ)`
2. `userRepositoryPort.findAllActive()`
3. Mapea cada User a UserResponse
4. Retorna lista

---

### 3.2 `GET /api/users/{id}`

Obtiene un usuario específico por ID.

#### Request
```
GET /api/users/b0d46c6d-c218-4900-b685-47c476a8e51b
Authorization: Bearer <ACCESS_TOKEN>
```

#### Response 200 OK
```json
{
  "id": "b0d46c6d-c218-4900-b685-47c476a8e51b",
  "username": "boss_admin",
  "email": "admin@sistema.com",
  "nombreCompleto": "Administrador del Sistema",
  "estado": "ACTIVO",
  "fechaCreacion": "2026-07-15T19:49:28.030295",
  "fechaActualizacion": "2026-07-15T19:49:28.030295"
}
```

#### Response 404 — Usuario no encontrado
```json
{
  "codigo": 404,
  "mensaje": "Usuario no encontrado: b0d46c6d-...",
  "timestamp": "2026-07-15T19:49:28.030295"
}
```

#### Authorization
Requiere `USERS_READ`

#### Flow interno
1. `userRepositoryPort.findById(id)` → si no existe → 404
2. Mapea a UserResponse
3. Retorna

---

### 3.3 `POST /api/users`

Crea un usuario (vía administrador). Valida username y email únicos.

#### Request
```json
{
  "username": "nuevo_user",
  "email": "user@correo.com",
  "password": "Segura123",
  "nombreCompleto": "Nombre Completo"
}
```

#### Response 201 Created
```json
{
  "id": "uuid-del-usuario",
  "username": "nuevo_user",
  "email": "user@correo.com",
  "nombreCompleto": "Nombre Completo",
  "fechaCreacion": "2026-07-15T19:49:28.030295"
}
```

#### Response 409 — Duplicado
```json
{
  "codigo": 409,
  "mensaje": "El nombre de usuario 'nuevo_user' ya existe",
  "timestamp": "2026-07-15T19:49:28.030295"
}
```

#### Authorization
Requiere `USERS_CREATE`

#### Flow interno
1. `authorizationPort.requirePermission(Permission.USERS_CREATE)`
2. Verifica username y email únicos → si no → `DuplicateException(409)`
3. Hashea password con BCrypt (cost 12)
4. Crea `new User(username, email, passwordHash, nombreCompleto)`
5. Persiste en BD
6. Retorna 201 con datos del usuario

---

### 3.4 `PUT /api/users/{id}`

Actualiza datos del usuario (email, nombre, contraseña). Requiere contraseña actual para cambiar contraseña.

#### Request
```json
{
  "email": "nuevo@correo.com",
  "nombreCompleto": "Nuevo Nombre",
  "currentPassword": "MiPasswordActual123",
  "newPassword": "MiNuevaPassword456"
}
```

Todos los campos son opcionales. Si no se envía `newPassword`, no se valida `currentPassword`.

#### Response 200 OK
```json
{
  "id": "b0d46c6d-...",
  "username": "boss_admin",
  "email": "nuevo@correo.com",
  "nombreCompleto": "Nuevo Nombre",
  "estado": "ACTIVO",
  "fechaActualizacion": "2026-07-15T20:00:00.000000"
}
```

#### Response 400 — Contraseña actual incorrecta (al cambiar contraseña)
```json
{
  "codigo": 400,
  "mensaje": "La contraseña actual no es correcta",
  "timestamp": "2026-07-15T19:49:28.030295"
}
```

#### Authorization
`requireOwnershipOrPermission(id, USERS_UPDATE)`:

- Si el usuario autenticado es el mismo del recurso → permitido
- Si no → requiere `USERS_UPDATE`

#### Flow interno
1. Busca usuario por ID → si no existe → 404
2. `authorizationPort.requireOwnershipOrPermission(id, Permission.USERS_UPDATE)`
3. Si `newPassword` presente:
   - `currentPassword` es obligatorio → si no → 400
   - Verifica currentPassword contra BCrypt → si no coincide → 400
   - Hashea newPassword y actualiza
4. Si `email` presente → actualiza
5. Si `nombreCompleto` presente → actualiza
6. Persiste cambios
7. Retorna datos actualizados

---

### 3.5 `DELETE /api/users/{id}`

Desactiva un usuario (borrado lógico: estado → INACTIVO).

#### Request
```
DELETE /api/users/b0d46c6d-...
Authorization: Bearer <ACCESS_TOKEN>
```

#### Response 204 No Content

#### Authorization
Requiere `USERS_DELETE`

#### Flow interno
1. `authorizationPort.requirePermission(Permission.USERS_DELETE)`
2. Busca usuario → si no existe → 404
3. `user.deactivate()` (cambia estado a INACTIVO)
4. Persiste
5. Retorna 204

---

### 3.6 `GET /api/users/{id}/roles`

Obtiene los roles asignados a un usuario.

#### Request
```
GET /api/users/b0d46c6d-.../roles
Authorization: Bearer <ACCESS_TOKEN>
```

#### Response 200 OK
```json
[
  {
    "id": "92ad2210-9fc9-4397-a3a3-983a1729a285",
    "nombre": "ADMIN",
    "descripcion": "Rol de administrador con todos los permisos del sistema",
    "estado": "ACTIVO",
    "fechaCreacion": "2026-07-15T19:49:28.030295"
  }
]
```

#### Response 404 — Usuario no existe
```json
{
  "codigo": 404,
  "mensaje": "Usuario no encontrado: ...",
  "timestamp": "2026-07-15T19:49:28.030295"
}
```

#### Authorization
Requiere `USERS_READ`

#### Flow interno
1. Verifica que el usuario existe → si no → 404
2. Obtiene asignaciones activas `userId`
3. Por cada asignación, busca el rol por `roleId`
4. Retorna lista de `RoleResponse`

---

### 3.7 `POST /api/users/{id}/roles`

Asigna un rol a un usuario.

#### Request
```json
{
  "roleId": "uuid-del-rol"
}
```

#### Response 201 Created

#### Response 409 — Ya asignado
```json
{
  "codigo": 409,
  "mensaje": "El usuario ya tiene asignado el rol especificado",
  "timestamp": "2026-07-15T19:49:28.030295"
}
```

#### Authorization
Requiere `ROLES_ASSIGN_USERS`

#### Flow interno
1. `authorizationPort.requirePermission(Permission.ROLES_ASSIGN_USERS)`
2. Verifica usuario existe → si no → 404
3. Verifica rol existe → si no → 404
4. Verifica asignación previa → si existe activa → 409
5. Crea `UserRoleAssignment(userId, roleId, "system")`
6. Persiste
7. Retorna 201

---

### 3.8 `DELETE /api/users/{id}/roles/{roleId}`

Revoca un rol de un usuario (delete lógico).

#### Request
```
DELETE /api/users/b0d46c6d-.../roles/92ad2210-...
Authorization: Bearer <ACCESS_TOKEN>
```

#### Response 204 No Content

#### Authorization
Requiere `ROLES_ASSIGN_USERS`

#### Flow interno
1. `authorizationPort.requirePermission(Permission.ROLES_ASSIGN_USERS)`
2. Busca asignación `userId + roleId` activa → si no existe → 404
3. `assignment.revoke()` (cambia estado a INACTIVO)
4. Persiste
5. Retorna 204

---

## 4. ROLES — Gestión de Roles (10 Endpoints)

| Permiso | Endpoints |
|---------|-----------|
| `ROLES_CREATE` | `POST /api/roles` |
| `ROLES_READ` | `GET /api/roles`, `GET /api/roles/{id}/users`, `GET /api/roles/{id}/permissions` |
| `ROLES_UPDATE` | `PUT /api/roles/{id}` |
| `ROLES_DELETE` | `DELETE /api/roles/{id}` |
| `ROLES_ASSIGN_USERS` | `POST /api/roles/{id}/users`, `DELETE /api/roles/{id}/users/{userId}` |

---

### 4.1 `GET /api/roles`

Lista todos los roles activos.

#### Response 200 OK
```json
[
  {
    "id": "92ad2210-...",
    "nombre": "ADMIN",
    "descripcion": "Rol de administrador con todos los permisos del sistema",
    "estado": "ACTIVO",
    "fechaCreacion": "2026-07-15T19:49:28.030295"
  }
]
```

#### Authorization
Requiere `ROLES_READ`

---

### 4.2 `POST /api/roles`

Crea un nuevo rol.

#### Request
```json
{
  "nombre": "AUDITOR",
  "descripcion": "Rol de auditoría"
}
```

#### Response 201 Created
```json
{
  "id": "uuid-del-rol",
  "nombre": "AUDITOR",
  "descripcion": "Rol de auditoría",
  "fechaCreacion": "2026-07-15T19:49:28.030295"
}
```

#### Response 409 — Nombre duplicado
```json
{
  "codigo": 409,
  "mensaje": "El nombre 'AUDITOR' ya está en uso",
  "timestamp": "..."
}
```

#### Authorization
Requiere `ROLES_CREATE`

---

### 4.3 `PUT /api/roles/{id}`

Actualiza nombre y/o descripción de un rol.

#### Request
```json
{
  "nombre": "SUPER_ADMIN",
  "descripcion": "Nueva descripción"
}
```

#### Response 200 OK
```json
{
  "id": "92ad2210-...",
  "nombre": "SUPER_ADMIN",
  "descripcion": "Nueva descripción",
  "estado": "ACTIVO",
  "fechaCreacion": "2026-07-15T19:49:28.030295"
}
```

#### Authorization
Requiere `ROLES_UPDATE`

---

### 4.4 `DELETE /api/roles/{id}`

Desactiva un rol (estado → INACTIVO).

#### Response 204 No Content

#### Authorization
Requiere `ROLES_DELETE`

---

### 4.5 `GET /api/roles/{id}/users`

Lista los usuarios que tienen asignado un rol específico.

#### Response 200 OK
```json
[
  {
    "id": "b0d46c6d-...",
    "username": "boss_admin",
    "email": "admin@sistema.com",
    "nombreCompleto": "Administrador del Sistema",
    "estado": "ACTIVO",
    "fechaCreacion": "...",
    "fechaActualizacion": "..."
  }
]
```

#### Authorization
Requiere `ROLES_READ`

---

### 4.6 `POST /api/roles/{id}/users`

Asigna un usuario a un rol (mismo que `POST /api/users/{id}/roles` pero por rol).

#### Request
```json
{
  "roleId": "92ad2210-...",
  "userId": "b0d46c6d-..."
}
```

Nota: El `roleId` se envía tanto en path como en body. El path tiene prioridad.

#### Response 201 Created

#### Authorization
Requiere `ROLES_ASSIGN_USERS`

---

### 4.7 `DELETE /api/roles/{id}/users/{userId}`

Revoca un usuario de un rol.

#### Response 204 No Content

#### Authorization
Requiere `ROLES_ASSIGN_USERS`

---

### 4.8 `GET /api/roles/{id}/permissions`

Obtiene los permisos asignados a un rol.

#### Response 200 OK
```json
["USERS_CREATE", "USERS_READ", "USERS_UPDATE", "USERS_DELETE"]
```

#### Authorization
Requiere `ROLES_READ`

---

### 4.9 `POST /api/roles/{id}/permissions`

Asigna un permiso a un rol.

#### Request
```json
{
  "permission": "USERS_READ"
}
```

#### Response 201 Created

#### Response 409 — Permiso ya asignado
```json
{
  "codigo": 409,
  "mensaje": "El permiso USERS_READ ya está asignado a este rol",
  "timestamp": "..."
}
```

#### Authorization
Requiere `ROLES_UPDATE`

---

### 4.10 `DELETE /api/roles/{id}/permissions/{permission}`

Remueve un permiso de un rol.

#### Request
```
DELETE /api/roles/92ad2210-.../permissions/USERS_READ
```

#### Response 204 No Content

#### Authorization
Requiere `ROLES_UPDATE`

---

## 5. MODULES — Gestión de Módulos (7 Endpoints)

| Permiso | Endpoints |
|---------|-----------|
| `MODULES_CREATE` | `POST /api/modules` |
| `MODULES_READ` | `GET /api/modules`, `GET /api/modules/{id}` |
| `MODULES_UPDATE` | `PUT /api/modules/{id}` |
| `MODULES_DELETE` | `DELETE /api/modules/{id}` |
| `MODULES_ASSIGN` | `POST /api/modules/roles/{roleId}/modules`, `DELETE /api/modules/roles/{roleId}/modules/{moduleId}` |

---

### 5.1 `GET /api/modules`

Lista todos los módulos activos.

#### Response 200 OK
```json
[
  {
    "id": "uuid",
    "nombre": "Seguridad",
    "descripcion": "Módulo de seguridad",
    "icono": "shield",
    "orden": 1,
    "estado": "ACTIVO",
    "fechaCreacion": "..."
  }
]
```

#### Authorization
Requiere `MODULES_READ`

---

### 5.2 `GET /api/modules/{id}`

Obtiene un módulo por ID.

#### Response 200 OK
```json
{
  "id": "uuid",
  "nombre": "Seguridad",
  "descripcion": "Módulo de seguridad",
  "icono": "shield",
  "orden": 1,
  "estado": "ACTIVO",
  "fechaCreacion": "..."
}
```

#### Response 404
```json
{
  "codigo": 404,
  "mensaje": "Recurso no encontrado",
  "timestamp": "..."
}
```

#### Authorization
Requiere `MODULES_READ`

---

### 5.3 `POST /api/modules`

Crea un nuevo módulo.

#### Request
```json
{
  "nombre": "Facturación",
  "descripcion": "Módulo de facturación",
  "icono": "receipt",
  "orden": 5
}
```

#### Response 201 Created
```json
{
  "id": "uuid",
  "nombre": "Facturación",
  "descripcion": "Módulo de facturación",
  "icono": "receipt",
  "orden": 5,
  "fechaCreacion": "..."
}
```

#### Authorization
Requiere `MODULES_CREATE`

---

### 5.4 `PUT /api/modules/{id}`

Actualiza un módulo.

#### Request
```json
{
  "nombre": "Facturación Electrónica",
  "descripcion": "Módulo de facturación actualizado",
  "icono": "file-invoice",
  "orden": 3
}
```

#### Response 200 OK
*(Misma estructura que GET /api/modules/{id})*

#### Authorization
Requiere `MODULES_UPDATE`

---

### 5.5 `DELETE /api/modules/{id}`

Desactiva un módulo (estado → INACTIVO).

#### Response 204 No Content

#### Authorization
Requiere `MODULES_DELETE`

---

### 5.6 `POST /api/modules/roles/{roleId}/modules`

Asigna un módulo a un rol (los menús del módulo aparecerán en el árbol del rol).

#### Request
```json
{
  "moduleId": "uuid-del-modulo"
}
```

#### Response 201 Created

#### Authorization
Requiere `MODULES_ASSIGN`

---

### 5.7 `DELETE /api/modules/roles/{roleId}/modules/{moduleId}`

Remueve un módulo de un rol (delete lógico).

#### Response 204 No Content

#### Authorization
Requiere `MODULES_ASSIGN`

---

## 6. MENUS — Gestión del Árbol de Menús (7 Endpoints)

| Permiso | Endpoints |
|---------|-----------|
| `MENUS_CREATE` | `POST /api/menus` |
| `MENUS_READ` | `GET /api/menus/tree?roleId={roleId}` |
| `MENUS_UPDATE` | `PUT /api/menus/{id}`, `PATCH /api/menus/{id}/move` |
| `MENUS_DELETE` | `DELETE /api/menus/{id}` |
| `MENUS_ASSIGN` | `POST /api/menus/roles/{roleId}/menus`, `DELETE /api/menus/roles/{roleId}/menus/{menuId}` |

---

### 6.1 `GET /api/menus/tree?roleId={roleId}`

Obtiene el árbol completo de menús para un rol específico.

#### Request
```
GET /api/menus/tree?roleId=92ad2210-...
Authorization: Bearer <ACCESS_TOKEN>
```

#### Response 200 OK
```json
[
  {
    "id": "uuid",
    "nombre": "Dashboard",
    "url": "/dashboard",
    "moduleId": "uuid-modulo",
    "parentId": null,
    "orden": 1,
    "children": [
      {
        "id": "uuid",
        "nombre": "Reportes",
        "url": "/dashboard/reportes",
        "moduleId": "uuid-modulo",
        "parentId": "uuid-padre",
        "orden": 1,
        "children": []
      }
    ]
  }
]
```

#### Authorization
Requiere `MENUS_READ`

#### Flow interno (GetMenuTreeService)
1. Obtiene `menuNodeIds` directamente asignados al rol via `roleMenuAssignmentRepositoryPort.findMenuNodeIdsByRoleId(roleId)`
2. Si hay asignaciones directas → construye árbol recursivamente desde esos nodos
3. Si no hay asignaciones directas → obtiene módulos del rol y construye desde nodos raíz de esos módulos
4. Para cada nodo, obtiene hijos recursivamente y construye estructura anidada `children`

---

### 6.2 `POST /api/menus`

Crea un nuevo ítem de menú.

#### Request
```json
{
  "nombre": "Usuarios",
  "url": "/seguridad/usuarios",
  "moduleId": "uuid-del-modulo",
  "parentId": null,
  "orden": 1
}
```

- `parentId` opcional: si es `null`, el menú es raíz; si tiene valor, es hijo de ese padre
- `moduleId` obligatorio: módulo al que pertenece

#### Response 201 Created
```json
{
  "id": "uuid",
  "nombre": "Usuarios",
  "url": "/seguridad/usuarios",
  "moduleId": "uuid-modulo",
  "parentId": null,
  "orden": 1,
  "estado": "ACTIVO"
}
```

#### Authorization
Requiere `MENUS_CREATE`

---

### 6.3 `PUT /api/menus/{id}`

Actualiza un ítem de menú (nombre, url, orden). No permite cambiar `moduleId` ni `parentId`.

#### Request
```json
{
  "nombre": "Gestión de Usuarios",
  "url": "/seguridad/gestion-usuarios",
  "orden": 2
}
```

#### Response 200 OK
```json
{
  "id": "uuid",
  "nombre": "Gestión de Usuarios",
  "url": "/seguridad/gestion-usuarios",
  "moduleId": "uuid-modulo",
  "parentId": null,
  "orden": 2,
  "estado": "ACTIVO"
}
```

#### Authorization
Requiere `MENUS_UPDATE`

---

### 6.4 `DELETE /api/menus/{id}`

Desactiva un ítem de menú (estado → INACTIVO). Los hijos pueden quedar huérfanos (con `parent_id` apuntando a un nodo INACTIVO).

#### Response 204 No Content

#### Authorization
Requiere `MENUS_DELETE`

---

### 6.5 `PATCH /api/menus/{id}/move`

Mueve un ítem de menú a un nuevo padre. Incluye detección de ciclos.

#### Request
```json
{
  "newParentId": "uuid-del-nuevo-padre"
}
```

Para mover a raíz (sin padre):
```json
{}
```
O `{"newParentId": null}`.

#### Response 200 OK
*(Sin cuerpo)*

#### Response 409 — Ciclo detectado
```json
{
  "codigo": 409,
  "mensaje": "El movimiento crearía un ciclo en el árbol de menú",
  "timestamp": "..."
}
```

#### Response 409 — Auto-referencia
```json
{
  "codigo": 409,
  "mensaje": "Un nodo no puede ser padre de sí mismo",
  "timestamp": "..."
}
```

#### Authorization
Requiere `MENUS_UPDATE`

#### Flow interno (MoveMenuItemService)
1. `authorizationPort.requirePermission(Permission.MENUS_UPDATE)`
2. Busca el nodo a mover → si no existe → 404
3. Si `newParentId != null`:
   - Busca el nodo padre → si no existe → 404
   - Verifica que nodoId != newParentId → si igual → `CycleException`
   - `cycleDetectionPort.wouldCreateCycle(nodeId, newParentId)`: ejecuta CTE recursiva que busca ancestros del `newParentId` y verifica que `nodeId` no esté entre ellos
   - Si ciclo detectado → `CycleException`
4. `node.moveTo(newParentId)` (actualiza `parentId`)
5. Persiste

---

### 6.6 `POST /api/menus/roles/{roleId}/menus`

Asigna un ítem de menú directamente a un rol.

#### Request
```json
{
  "menuNodeId": "uuid-del-menu"
}
```

#### Response 201 Created

#### Authorization
Requiere `MENUS_ASSIGN`

---

### 6.7 `DELETE /api/menus/roles/{roleId}/menus/{menuId}`

Remueve un menú de un rol (delete lógico).

#### Response 204 No Content

#### Authorization
Requiere `MENUS_ASSIGN`

---

## 7. SERVICE-REGISTRY — Registro de Microservicios (4 Endpoints)

| Permiso | Endpoints |
|---------|-----------|
| `SERVICES_CREATE` | `POST /api/service-registry` |
| `SERVICES_READ` | `GET /api/service-registry` |
| `SERVICES_UPDATE` | `PUT /api/service-registry/{code}` |
| `SERVICES_DELETE` | `DELETE /api/service-registry/{code}` |

---

### 7.1 `GET /api/service-registry`

Lista todos los servicios registrados activos.

#### Response 200 OK
```json
[
  {
    "id": "uuid",
    "serviceCode": "my-service",
    "nombre": "Mi Microservicio",
    "baseUrl": "http://localhost:8081",
    "validationMode": "ASYMMETRIC",
    "publicKey": "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0B...",
    "estado": "ACTIVO",
    "fechaCreacion": "...",
    "fechaActualizacion": "..."
  }
]
```

#### Authorization
Requiere `SERVICES_READ`

---

### 7.2 `POST /api/service-registry`

Registra un nuevo microservicio.

#### Request
```json
{
  "serviceCode": "my-service",
  "nombre": "Mi Microservicio",
  "baseUrl": "http://localhost:8081",
  "validationMode": "ASYMMETRIC",
  "publicKey": "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0B..."
}
```

- `serviceCode` único, usado como identificador en URL
- `validationMode`: `NONE`, `DIRECT`, o `ASYMMETRIC`
- `publicKey`: obligatorio solo si `validationMode = ASYMMETRIC`

#### Response 201 Created
```json
{
  "id": "uuid",
  "serviceCode": "my-service",
  "nombre": "Mi Microservicio",
  "baseUrl": "http://localhost:8081",
  "validationMode": "ASYMMETRIC",
  "fechaCreacion": "..."
}
```

#### Authorization
Requiere `SERVICES_CREATE`

---

### 7.3 `PUT /api/service-registry/{code}`

Actualiza un servicio registrado. Usa `serviceCode` (no UUID) como identificador en la URL.

#### Request
```
PUT /api/service-registry/my-service
```

```json
{
  "nombre": "Mi Servicio V2",
  "baseUrl": "http://localhost:9090",
  "publicKey": "-----BEGIN PUBLIC KEY-----\n..."
}
```

#### Response 200 OK
```json
{
  "id": "uuid",
  "serviceCode": "my-service",
  "nombre": "Mi Servicio V2",
  "baseUrl": "http://localhost:9090",
  "validationMode": "ASYMMETRIC",
  "publicKey": "-----BEGIN PUBLIC KEY-----\n...",
  "estado": "ACTIVO",
  "fechaCreacion": "...",
  "fechaActualizacion": "..."
}
```

#### Authorization
Requiere `SERVICES_UPDATE`

---

### 7.4 `DELETE /api/service-registry/{code}`

Desactiva un servicio registrado. Usa `serviceCode` en la URL.

#### Request
```
DELETE /api/service-registry/my-service
```

#### Response 204 No Content

#### Authorization
Requiere `SERVICES_DELETE`

---

## 8. Anexo: Configuración de Referencia (application.yml)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/master_gateway
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        jdbc.batch_size: 25
        order_inserts: true
        order_updates: true
    open-in-view: false

jwt:
  temp-token-expiration: 5m      # TEMP_TOKEN expires in 5 minutes
  access-token-expiration: 15m    # ACCESS_TOKEN expires in 15 minutes
  refresh-token-expiration: 7d    # REFRESH_TOKEN expires in 7 days
  secret: ${JWT_SECRET:dev-secret-key-at-least-32-characters}

rate-limiting:
  login:
    max-attempts: 5
    window-duration: 1m
  register:
    max-attempts: 5
    window-duration: 1m

password:
  policy:
    hash-algorithm: bcrypt
    cost-factor: 12
    min-length: 8

server:
  port: 8080
```

---

## 9. Anexo: Resumen de Códigos de Estado HTTP

| Código | Significado | Uso |
|--------|-------------|-----|
| 200 | OK | GET, PUT, PATCH, POST (login, select-role, refresh-token) |
| 201 | Created | POST (create user, role, module, menu, service) |
| 204 | No Content | DELETE, PUT (update service), POST (logout) |
| 400 | Bad Request | Validation errors, invalid input |
| 401 | Unauthorized | Invalid credentials, invalid/expired token |
| 403 | Forbidden | Missing permission |
| 404 | Not Found | Resource not found |
| 405 | Method Not Allowed | Wrong HTTP method |
| 409 | Conflict | Duplicate entry, cycle detected |
| 415 | Unsupported Media Type | Wrong Content-Type |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Internal Server Error | Unexpected error |

---

## 10. Anexo: DataInitializer (Seed por Defecto)

Al iniciar la aplicación por primera vez (BD vacía), `DataInitializer` crea:

1. **Rol ADMIN** — rol de administrador con todos los 25 permisos
2. **25 permisos** — todas las combinaciones CRUD + assign/revoke para cada dominio
3. **Usuario `boss_admin`** — username: `boss_admin`, password: `Admin1234`
   - Asignado al rol ADMIN
   - Email: `admin@sistema.com`
   - Nombre: `Administrador del Sistema`

No existe ningún otro seed. El sistema arranca desde cero con estos datos mínimos.
