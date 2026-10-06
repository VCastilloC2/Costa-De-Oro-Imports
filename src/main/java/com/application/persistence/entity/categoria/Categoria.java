package com.application.persistence.entity.categoria;

import com.application.persistence.entity.producto.Producto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "categoria", uniqueConstraints = {
                @UniqueConstraint(columnNames = "nombre", name = "uk_categoria_nombre")
})
public class Categoria {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "categoria_id", nullable = false)
        private Long categoriaId;
        private String imagen;
        @Column(length = 175, nullable = false)
        private String nombre;
        @Column(length = 500, nullable = false)
        private String descripcion;
        @Column(nullable = false)
        private boolean activo;

        // Cardinalidad con la tabla producto
        @Builder.Default
        @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
        @JsonIgnore
        private Set<Producto> productos = new HashSet<>();

        // Cardinalidad con la tabla categorías
        @Builder.Default
        @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
        @JsonIgnore
        private Set<SubCategoria> subCategorias = new HashSet<>();

        // Agregar categoria a producto y viceversa (bidireccional)
        public void addProducto(Producto producto) {
                producto.setCategoria(this);
                this.productos.add(producto);
        }

        // Eliminar categoria a producto y viceversa (bidireccional)
        public void deleteProducto(Producto producto) {
                producto.setCategoria(null);
                this.productos.remove(producto);
        }

        // Agregar categoria a sub-categoria y viceversa (bidireccional)
        public void addSubCategoria(SubCategoria subCategoria) {
                subCategoria.setCategoria(this);
                this.subCategorias.add(subCategoria);
        }

        // Eliminar categoria de sub-categoria y viceversa (bidireccional)
        public void deleteSubCategoria(SubCategoria subCategoria) {
                subCategoria.setCategoria(null);
                this.subCategorias.remove(subCategoria);
        }
}