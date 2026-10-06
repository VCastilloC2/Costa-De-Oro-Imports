package com.application.persistence.entity.categoria;

import com.application.persistence.entity.producto.Producto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sub_categorias")
public class SubCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subcategoria_id")
    private Long subCategoriaId;
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "categoria_id",
            referencedColumnName = "categoria_id",
            foreignKey = @ForeignKey(name = "fk_sub-Categoria_categoria")
    )
    @JsonIgnore
    private Categoria categoria;

    @Builder.Default
    @OneToMany(mappedBy = "subCategoria", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Producto> productos = new HashSet<>();

    // Agregar subcategoria a producto y viceversa (bidireccional)
    public void addProducto(Producto producto) {
        producto.setSubCategoria(this);
        this.productos.add(producto);
    }

    // Eliminar subcategoria a producto y viceversa (bidireccional)
    public void deleteProducto(Producto producto) {
        producto.setSubCategoria(null);
        this.productos.remove(producto);
    }
}
