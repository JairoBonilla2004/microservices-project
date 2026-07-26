package ec.edu.espe.master_gateway.contexts.identity.infrastructure.mapper;

import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.RolePermissionAssignment;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.UserRoleAssignment;
import ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence.RoleJpaEntity;
import ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence.RolePermissionAssignmentJpaEntity;
import ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence.UserJpaEntity;
import ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence.UserRoleAssignmentJpaEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct para la conversión entre entidades JPA y modelos
 * de dominio del contexto de identidad.
 *
 * <p>Utiliza el componente de Spring para la inyección de dependencias
 * y reporta error ante cualquier propiedad de destino no mapeada,
 * garantizando la integridad de las conversiones.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface IdentityMapper {

    UserJpaEntity toJpaEntity(User domain);

    default User toDomainEntity(UserJpaEntity entity) {
        if (entity == null) return null;
        var user = new User(entity.getUsername(), entity.getEmail(),
                entity.getPasswordHash(), entity.getNombreCompleto());
        if (entity.getId() != null) {
            user.markAsPersisted(entity.getId(), entity.getFechaCreacion(),
                    entity.getFechaActualizacion(), entity.getCreadoPor(),
                    entity.getActualizadoPor());
        }
        user.setEstado(entity.getEstado());
        return user;
    }

    RoleJpaEntity toJpaEntity(Role domain);

    default Role toDomainEntity(RoleJpaEntity entity) {
        if (entity == null) return null;
        var role = new Role(entity.getNombre(), entity.getDescripcion());
        if (entity.getId() != null) {
            role.markAsPersisted(entity.getId(), entity.getFechaCreacion(),
                    entity.getFechaActualizacion(), entity.getCreadoPor(),
                    entity.getActualizadoPor());
        }
        role.setEstado(entity.getEstado());
        return role;
    }

    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "creadoPor", ignore = true)
    @Mapping(target = "actualizadoPor", ignore = true)
    UserRoleAssignmentJpaEntity toJpaEntity(UserRoleAssignment domain);

    UserRoleAssignment toDomainEntity(UserRoleAssignmentJpaEntity entity);

    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "creadoPor", ignore = true)
    @Mapping(target = "actualizadoPor", ignore = true)
    RolePermissionAssignmentJpaEntity toJpaEntity(RolePermissionAssignment domain);

    RolePermissionAssignment toDomainEntity(RolePermissionAssignmentJpaEntity entity);
}
