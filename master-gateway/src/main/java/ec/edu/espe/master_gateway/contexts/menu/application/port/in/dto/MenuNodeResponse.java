package ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto;

import java.util.List;
import java.util.UUID;

/**
 * Respuesta que representa un nodo del árbol de menú con sus hijos.
 *
 * <p>Esta clase es recursiva: el campo {@code children} contiene una lista
 * de objetos {@code MenuNodeResponse}, cada uno de los cuales puede tener
 * sus propios hijos, formando así una estructura jerárquica completa.</p>
 *
 * @param id       Identificador único del nodo.
 * @param nombre   Nombre visible del nodo.
 * @param url      Ruta a la que apunta el nodo.
 * @param moduleId Identificador del módulo al que pertenece.
 * @param parentId Identificador del nodo padre ({@code null} si es raíz).
 * @param orden    Posición relativa del nodo dentro de su nivel.
 * @param children Lista de nodos hijos (estructura recursiva).
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record MenuNodeResponse(
    UUID id,
    String nombre,
    String url,
    UUID moduleId,
    UUID parentId,
    int orden,
    List<MenuNodeResponse> children
) {}
