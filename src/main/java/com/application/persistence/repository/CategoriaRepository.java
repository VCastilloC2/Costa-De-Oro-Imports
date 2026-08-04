package com.application.persistence.repository;

import com.application.persistence.entity.categoria.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByActivoTrue();

    @Query("select count(p) from Categoria c join c.productos p where c.id = :categoriaId")
    long countProductosByCategoriaId(@Param("categoriaId") Long categoriaId);

    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}