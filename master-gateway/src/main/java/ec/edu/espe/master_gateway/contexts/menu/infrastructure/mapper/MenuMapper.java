package ec.edu.espe.master_gateway.contexts.menu.infrastructure.mapper;

import ec.edu.espe.master_gateway.contexts.menu.domain.model.MenuNode;
import ec.edu.espe.master_gateway.contexts.menu.domain.model.RoleMenuAssignment;
import ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.persistence.MenuNodeJpaEntity;
import ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.persistence.RoleMenuAssignmentJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct para la conversi&oacute;n entre las entidades
 * JPA y los objetos de dominio del contexto de men&uacute;.
 *
 * <p>Utiliza el componente Spring y reporta errores en caso de
 * propiedades no mapeadas para garantizar la integridad de las
 * conversiones.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MenuMapper {

    MenuNodeJpaEntity toJpaEntity(MenuNode domain);

    MenuNode toDomainEntity(MenuNodeJpaEntity entity);

    RoleMenuAssignmentJpaEntity toJpaEntity(RoleMenuAssignment domain);

    RoleMenuAssignment toDomainEntity(RoleMenuAssignmentJpaEntity entity);
}
