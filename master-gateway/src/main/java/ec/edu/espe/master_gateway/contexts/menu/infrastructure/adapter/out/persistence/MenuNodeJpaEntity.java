package ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.contexts.module.infrastructure.adapter.out.persistence.ModuleJpaEntity;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.JpaAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "menus")
public class MenuNodeJpaEntity extends JpaAuditableEntity {

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(length = 500)
    private String url;

    @Column(name = "modulo_id", nullable = false)
    private UUID moduleId;

    @Column(name = "parent_id")
    private UUID parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id", referencedColumnName = "id",
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_menus_module"))
    private ModuleJpaEntity module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id",
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_menus_parent"))
    private MenuNodeJpaEntity parent;

    @Column(nullable = false)
    private int orden;

    public MenuNodeJpaEntity() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public UUID getModuleId() { return moduleId; }
    public void setModuleId(UUID moduleId) { this.moduleId = moduleId; }
    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }
    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
}
