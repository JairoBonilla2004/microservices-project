package ec.edu.espe.master_gateway.contexts.module.infrastructure.mapper;

import ec.edu.espe.master_gateway.contexts.module.domain.model.Module;
import ec.edu.espe.master_gateway.contexts.module.domain.model.RoleModuleAssignment;
import ec.edu.espe.master_gateway.contexts.module.infrastructure.adapter.out.persistence.ModuleJpaEntity;
import ec.edu.espe.master_gateway.contexts.module.infrastructure.adapter.out.persistence.RoleModuleAssignmentJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper de MapStruct para convertir entre entidades JPA y objetos de dominio
 * del contexto de módulos.
 *
 * <p>Convierte {@link ModuleJpaEntity} &harr; {@link Module} y
 * {@link RoleModuleAssignmentJpaEntity} &harr; {@link RoleModuleAssignment},
 * garantizando que no haya campos sin mapear mediante
 * {@code unmappedTargetPolicy = ReportingPolicy.ERROR}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ModuleMapper {

    ModuleJpaEntity toJpaEntity(Module domain);

    Module toDomainEntity(ModuleJpaEntity entity);

    RoleModuleAssignmentJpaEntity toJpaEntity(RoleModuleAssignment domain);

    RoleModuleAssignment toDomainEntity(RoleModuleAssignmentJpaEntity entity);
}
