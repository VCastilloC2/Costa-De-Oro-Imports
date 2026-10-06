package com.application.persistence.entity.usuario;

import com.application.persistence.entity.comentario.Comentario;
import com.application.persistence.entity.compra.Compra;
import com.application.persistence.entity.empresa.Empresa;
import com.application.persistence.entity.factura.FacturaProveedor;
import com.application.persistence.entity.pqrs.Peticion;
import com.application.persistence.entity.producto.Producto;
import com.application.persistence.entity.rol.Rol;
import com.application.persistence.entity.usuario.enums.EIdentificacion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "usuario", uniqueConstraints = {
        @UniqueConstraint(columnNames = "telefono", name = "uk_usuario_telefono"),
        @UniqueConstraint(columnNames = "correo", name = "uk_usuario_correo")
})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(name = "tipo_identificacion")
    @Enumerated(EnumType.STRING)
    private EIdentificacion tipoIdentificacion;
    @Column(name = "numero_identificacion", length = 15)
    private String numeroIdentificacion;
    private String imagen;
    @Column(length = 175)
    private String nombres;
    @Column(length = 175)
    private String apellidos;
    @Column(length = 20)
    private String telefono;
    @Column(length = 100, nullable = false)
    private String correo;
    @Column(length = 100)
    private String password;
    @Column(length = 100)
    private String direccion;

    @Column(name = "is_enabled")
    @Builder.Default
    private boolean isEnabled = true;

    @Column(name = "account_non_expired")
    @Builder.Default
    private boolean accountNonExpired = true;

    @Column(name = "account_non_locked")
    @Builder.Default
    private boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired")
    @Builder.Default
    private boolean credentialsNonExpired = true;

    // Cardinalidad con la tabla rol (relación unidireccional)
    @ManyToOne
    @JoinColumn(name = "rol_id", referencedColumnName = "rol_id", foreignKey = @ForeignKey(name = "fk_usuario_rol"))
    @JsonIgnore
    private Rol rol;

    // Cardinalidad con la tabla empresas (relación unidireccional)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "empresa_id", referencedColumnName = "empresa_id", foreignKey = @ForeignKey(name = "fk_usuario_empresa"))
    @JsonIgnore
    private Empresa empresa;

    // Cardinalidad con la tabla compra (relación bidireccional)
    @Builder.Default
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Compra> compras = new HashSet<>();

    // Cardinalidad con la tabla producto (relación bidireccional)
    @Builder.Default
    @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Producto> productos = new HashSet<>();

    // Cardinalidad con la tabla factura proveedor (relación bidireccional)
    @Builder.Default
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<FacturaProveedor> facturas = new HashSet<>();

    // Cardinalidad con la tabla comentarios (relación bidireccional)
    @Builder.Default
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Comentario> comentarios = new HashSet<>();

    // Cardinalidad con la tabla peticiones (relación bidireccional)
    @Builder.Default
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Peticion> peticiones = new HashSet<>();

    // Agregar usuario a compra y viceversa (bidireccional)
    public void addCompra(Compra compra) {
        compra.setUsuario(this);
        this.compras.add(compra);
    }

    // Eliminar usuario de compra y viceversa (bidireccional)
    public void deleteCompra(Compra compra) {
        compra.setUsuario(null);
        this.compras.remove(compra);
    }

    // Agregar proveedor a producto y viceversa (bidireccional)
    public void addProducto(Producto producto) {
        producto.setProveedor(this);
        this.productos.add(producto);
    }

    // Eliminar proveedor de producto y viceversa (bidireccional)
    public void deleteProducto(Producto producto) {
        producto.setProveedor(null);
        this.productos.remove(producto);
    }

    // Agregar proveedor a factura y viceversa (bidireccional)
    public void addFactura(FacturaProveedor factura) {
        factura.setUsuario(this);
        this.facturas.add(factura);
    }

    // Eliminar proveedor de factura y viceversa (bidireccional)
    public void deleteFactura(FacturaProveedor factura) {
        factura.setUsuario(null);
        this.facturas.remove(factura);
    }

    // Agregar usuario a petición y viceversa (bidireccional)
    public void addPeticion(Peticion peticion) {
        peticion.setUsuario(this);
        this.peticiones.add(peticion);
    }

    // Eliminar usuario de petición y viceversa (bidireccional)
    public void deletePeticion(Peticion peticion) {
        peticion.setUsuario(null);
        this.peticiones.remove(peticion);
    }
}