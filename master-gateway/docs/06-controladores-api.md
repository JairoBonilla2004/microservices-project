# Fase 06 — Controladores REST API

## Propósito

Exponer los casos de uso de la capa de aplicación como endpoints RESTful, siguiendo una arquitectura hexagonal donde los controladores actúan como adaptadores de entrada (inbound adapters).

## Estructura

Todos los controladores residen en `infrastructure/adapter/in/web/` dentro de su contexto correspondiente:

```
contexts/
├── auth/
│   └── infrastructure/adapter/in/web/
│       ├── AuthController.java
│       └── InternalTokenValidationController.java
├── identity/
│   └── infrastructure/adapter/in/web/
│       ├── UserController.java
│       └── RoleController.java
├── module/
│   └── infrastructure/adapter/in/web/
│       └── ModuleController.java
├── menu/
│   └── infrastructure/adapter/in/web/
│       └── MenuController.java
└── service_registry/
    └── infrastructure/adapter/in/web/
        └── ServiceRegistryController.java
```

## Controllers

### `AuthController`

| Método | Endpoint | Use Case | Request | Response | Código |
|--------|----------|----------|---------|----------|--------|
| POST | `/api/auth/login` | `LoginUseCase` | `LoginRequest` | `LoginResponse` | 200 |
| POST | `/api/auth/select-role` | `SelectRoleUseCase` | `SelectRoleRequest` | `SelectRoleResponse` | 200 |
| POST | `/api/auth/refresh-token` | `RefreshTokenUseCase` | `RefreshTokenRequest` | `RefreshTokenResponse` | 200 |
| POST | `/api/auth/logout` | `LogoutUseCase` | `RefreshTokenRequest` | - | 204 |

### `InternalTokenValidationController`

| Método | Endpoint | Use Case | Request | Response | Código |
|--------|----------|----------|---------|----------|--------|
| POST | `/api/internals/validate-token` | `ValidateTokenUseCase` | `{ "token": "..." }` | `TokenValidationResponse` | 200 |

### `UserController`

| Método | Endpoint | Use Case | Request | Response | Código |
|--------|----------|----------|---------|----------|--------|
| GET | `/api/users` | `ListUsersUseCase` | - | `List<UserResponse>` | 200 |
| GET | `/api/users/{id}` | `GetUserUseCase` | - | `UserResponse` | 200 |
| POST | `/api/users` | `CreateUserUseCase` | `CreateUserRequest` | `CreateUserResponse` | 201 |
| PUT | `/api/users/{id}` | `UpdateUserUseCase` | `UpdateUserRequest` | `UpdateUserResponse` | 200 |
| DELETE | `/api/users/{id}` | `DeactivateUserUseCase` | - | - | 204 |
| GET | `/api/users/{id}/roles` | `GetUserRolesUseCase` | - | `List<RoleResponse>` | 200 |
| POST | `/api/users/{id}/roles` | `AssignRoleUseCase` | `AssignRoleRequest` | - | 201 |
| DELETE | `/api/users/{id}/roles/{roleId}` | `RevokeRoleUseCase` | - | - | 204 |

### `RoleController`

| Método | Endpoint | Use Case | Request | Response | Código |
|--------|----------|----------|---------|----------|--------|
| GET | `/api/roles` | `ListRolesUseCase` | - | `List<RoleResponse>` | 200 |
| POST | `/api/roles` | `CreateRoleUseCase` | `CreateRoleRequest` | `CreateRoleResponse` | 201 |
| PUT | `/api/roles/{id}` | `UpdateRoleUseCase` | `UpdateRoleRequest` | `RoleResponse` | 200 |
| DELETE | `/api/roles/{id}` | `DeactivateRoleUseCase` | - | - | 204 |
| GET | `/api/roles/{id}/users` | `GetRoleUsersUseCase` | - | `List<UserResponse>` | 200 |
| POST | `/api/roles/{id}/users` | `AssignRoleUseCase` | `AssignRoleRequest` | - | 201 |
| DELETE | `/api/roles/{id}/users/{userId}` | `RevokeRoleUseCase` | - | - | 204 |

### `ModuleController`

| Método | Endpoint | Use Case | Request | Response | Código |
|--------|----------|----------|---------|----------|--------|
| GET | `/api/modules` | `ListModulesUseCase` | - | `List<ModuleResponse>` | 200 |
| POST | `/api/modules` | `CreateModuleUseCase` | `CreateModuleRequest` | `CreateModuleResponse` | 201 |
| PUT | `/api/modules/{id}` | `UpdateModuleUseCase` | `UpdateModuleRequest` | `ModuleResponse` | 200 |
| DELETE | `/api/modules/{id}` | `DeactivateModuleUseCase` | - | - | 204 |
| POST | `/api/modules/roles/{roleId}/modules` | `AssignModuleToRoleUseCase` | `AssignModuleToRoleRequest` | - | 201 |
| DELETE | `/api/modules/roles/{roleId}/modules/{moduleId}` | `RemoveModuleFromRoleUseCase` | - | - | 204 |

### `MenuController`

| Método | Endpoint | Use Case | Request | Response | Código |
|--------|----------|----------|---------|----------|--------|
| GET | `/api/menus/tree?roleId=` | `GetMenuTreeUseCase` | - | `List<MenuNodeResponse>` | 200 |
| POST | `/api/menus` | `CreateMenuItemUseCase` | `CreateMenuItemRequest` | `MenuItemResponse` | 201 |
| PUT | `/api/menus/{id}` | `UpdateMenuItemUseCase` | `UpdateMenuItemRequest` | `MenuItemResponse` | 200 |
| DELETE | `/api/menus/{id}` | `DeactivateMenuItemUseCase` | - | - | 204 |
| PATCH | `/api/menus/{id}/move` | `MoveMenuItemUseCase` | `MoveMenuItemRequest` | - | 200 |
| POST | `/api/menus/roles/{roleId}/menus` | `AssignMenuToRoleUseCase` | `AssignMenuToRoleRequest` | - | 201 |
| DELETE | `/api/menus/roles/{roleId}/menus/{menuId}` | `RemoveMenuFromRoleUseCase` | - | - | 204 |

### `ServiceRegistryController`

| Método | Endpoint | Use Case | Request | Response | Código |
|--------|----------|----------|---------|----------|--------|
| POST | `/api/service-registry` | `RegisterServiceUseCase` | `RegisterServiceRequest` | `RegisterServiceResponse` | 201 |
| GET | `/api/service-registry` | `ListRegisteredServicesUseCase` | - | `List<ServiceResponse>` | 200 |
| PUT | `/api/service-registry/{code}` | `UpdateServiceUseCase` | `UpdateServiceRequest` | `ServiceResponse` | 200 |
| DELETE | `/api/service-registry/{code}` | `DeactivateServiceUseCase` | - | - | 204 |

## Seguridad

Configurada en `SecurityConfig.filterChain()`:

| Endpoint | Acceso |
|----------|--------|
| `POST /api/auth/login` | Público |
| `POST /api/auth/refresh-token` | Público |
| `POST /api/internals/validate-token` | Público |
| `/actuator/health` | Público |
| `/api-docs/**`, `/swagger-ui/**` | Público |
| Cualquier otro `/**` | Requiere autenticación JWT |

## Patrón de implementación

Cada controlador:
1. Es anotado con `@RestController` y `@RequestMapping` a nivel de clase.
2. Inyecta los casos de uso (puertos de entrada) mediante constructor.
3. Los métodos son cortos (1-3 líneas), delegando toda la lógica al caso de uso.
4. Usa `@Valid @RequestBody` para validación de entrada (manejada por `GlobalExceptionHandler`).
5. Retorna `ResponseEntity<T>` con el código HTTP apropiado.

## Excepciones

Manejadas por `GlobalExceptionHandler` (`@RestControllerAdvice`):

| Excepción | HTTP | Descripción |
|-----------|------|-------------|
| `NotFoundException` | 404 | Recurso no encontrado |
| `DuplicateException` | 409 | Conflicto por duplicado |
| `RateLimitExceededException` | 429 | Límite de tasa excedido |
| `AuthenticationException` | 401 | Credenciales inválidas |
| `DomainException` (FORBIDDEN) | 403 | Acceso denegado |
| `DomainException` (CYCLE_DETECTED) | 400 | Ciclo detectado en árbol |
| `MethodArgumentNotValidException` | 400 | Error de validación de campos |
| `Exception` (genérica) | 500 | Error interno del servidor |
