package ec.edu.espe.master_gateway.contexts.service_registry.infrastructure.mapper;

import ec.edu.espe.master_gateway.contexts.service_registry.domain.model.RegisteredService;
import ec.edu.espe.master_gateway.contexts.service_registry.infrastructure.adapter.out.persistence.RegisteredServiceJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper de MapStruct para convertir entre la entidad de dominio
 * {@link RegisteredService} y la entidad JPA {@link RegisteredServiceJpaEntity}.
 *
 * <p>Utiliza el componente de Spring como modelo de inyección y
 * reporta errores en compilación para todos los campos no mapeados
 * explícitamente, garantizando que ambos modelos se mantengan
 * sincronizados.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ServiceRegistryMapper {

    RegisteredServiceJpaEntity toJpaEntity(RegisteredService domain);

    RegisteredService toDomainEntity(RegisteredServiceJpaEntity entity);
}
