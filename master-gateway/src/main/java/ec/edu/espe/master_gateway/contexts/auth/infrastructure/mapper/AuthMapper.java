package ec.edu.espe.master_gateway.contexts.auth.infrastructure.mapper;

import ec.edu.espe.master_gateway.contexts.auth.domain.model.RefreshToken;
import ec.edu.espe.master_gateway.contexts.auth.domain.model.RevokedToken;
import ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.persistence.RefreshTokenJpaEntity;
import ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.persistence.RevokedTokenJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface AuthMapper {

    RefreshTokenJpaEntity toJpaEntity(RefreshToken domain);

    RefreshToken toDomainEntity(RefreshTokenJpaEntity entity);

    RevokedTokenJpaEntity toJpaEntity(RevokedToken domain);

    RevokedToken toDomainEntity(RevokedTokenJpaEntity entity);
}
