package com.alura.literatura.Repository;

import com.alura.literatura.model.Autor;
import com.alura.literatura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ILibroRepository extends JpaRepository<Libro, Long> {
    List<Libro> findByIdioma(String idioma);
    boolean existsByTituloAndAutor(String titulo, Autor autor);
    Libro findByTitulo(String titulo);
    List<Libro> findByTituloAndAutor(String titulo, Autor autor);
}