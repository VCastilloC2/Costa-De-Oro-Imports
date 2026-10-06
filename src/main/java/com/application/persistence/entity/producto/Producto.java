package com.application.persistence.entity.producto;

import com.application.persistence.entity.categoria.Categoria;
import com.application.persistence.entity.categoria.SubCategoria;
import com.application.persistence.entity.compra.DetalleVenta;
import com.application.persistence.entity.factura.DetalleFactura;
import com.application.persistence.entity.producto.enums.ETipo;
import com.application.persistence.entity.usuario.Usuario;
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
@Table(name = "producto", uniqueConstraints = {
        @UniqueConstraint(columnNames = "codigo_producto", name = "uk_producto_codigo")
})
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Long productoId;
    @Column(name = "codigo_producto")
    private String codigoProducto;
    private String imagen;
    private String nombre;
    private String marca;
    private String pais;
    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private ETipo eTipo;
    private double precio;
    @Column(name = "precio_regular")
    private double precioRegular;
    private int stock;
    @Column(length = 700)
    private String descripcion;
    private boolean activo;

    // Cardinalidad con la tabla categoria
    @ManyToOne
    @JoinColumn(name = "categoria_id",
            referencedColumnName = "categoria_id",
            foreignKey = @ForeignKey(name = "fk_producto_categoria")
    )
    @JsonIgnore
    private Categoria categoria;

    // Cardinalidad con la tabla sub-categoria
    @ManyToOne
    @JoinColumn(name = "subcategoria_id",
            referencedColumnName = "subcategoria_id",
            foreignKey = @ForeignKey(name = "fk_producto_subcategoria")
    )
    @JsonIgnore
    private SubCategoria subCategoria;

    // Cardinalidad con la tabla Usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id",
            referencedColumnName = "usuario_id",
            foreignKey = @ForeignKey(name = "fk_producto_proveedor")
    )
    @JsonIgnore
    private Usuario proveedor;

    // Cardinalidad con la tabla detálle ventas
    @Builder.Default
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<DetalleVenta> detalleVentas = new HashSet<>();

    // Cardinalidad con la tabla detálle facturas
    @Builder.Default
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private Set<DetalleFactura> detalleFacturas = new HashSet<>();

    // Agregar producto a detalle_venta y viceversa (bidireccional)
    public void addDetalleVenta(DetalleVenta detalleVenta) {
        detalleVenta.setProducto(this);
        this.detalleVentas.add(detalleVenta);
    }

    // Eliminar producto a detalle_venta y viceversa (bidireccional)
    public void deleteDetalleVenta(DetalleVenta detalleVenta) {
        detalleVenta.setProducto(null);
        this.detalleVentas.remove(detalleVenta);
    }

    // Agregar producto a detalle_factura y viceversa (bidireccional)
    public void addDetalleFactura(DetalleFactura detalleFactura) {
        detalleFactura.setProducto(this);
        this.detalleFacturas.add(detalleFactura);
    }

    // Eliminar producto a detalle_factura y viceversa (bidireccional)
    public void deleteDetalleFactura(DetalleFactura detalleFactura) {
        detalleFactura.setProducto(null);
        this.detalleFacturas.remove(detalleFactura);
    }

}