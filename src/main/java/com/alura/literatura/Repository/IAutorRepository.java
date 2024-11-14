package com.alura.literatura.Repository;

import com.alura.literatura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IAutorRepository extends JpaRepository<Autor, Long> {

    Autor findFirstByNombre(String nombre);

    @Query("SELECT a FROM Autor a LEFT JOIN FETCH a.libros WHERE a.cumpleanios <= :anio AND a.fechaFallecimiento >= :anio")
    List<Autor> findAutoresVivos(@Param("anio") int anio);

    @Query("SELECT DISTINCT a FROM Autor a LEFT JOIN FETCH a.libros")
    List<Autor> findAllWithBooks();
}