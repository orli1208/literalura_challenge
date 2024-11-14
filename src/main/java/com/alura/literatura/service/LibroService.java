package com.alura.literatura.service;

import com.alura.literatura.Repository.IAutorRepository;
import com.alura.literatura.Repository.ILibroRepository;
import com.alura.literatura.dto.AutorDTO;
import com.alura.literatura.dto.LibroDTO;
import com.alura.literatura.model.Autor;
import com.alura.literatura.model.Libro;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibroService {
    @Autowired
    private ILibroRepository libroRepository;

    @Autowired
    private IAutorRepository authorRepository;

    @Autowired
    private ConsumoAPI consumoAPI;

    public Libro searchBooksByTitle(String titulo) {
        try {
            List<LibroDTO> libroDTOS = consumoAPI.searchBooksByTitle(titulo);
            if (!libroDTOS.isEmpty()) {
                LibroDTO libroDTO = libroDTOS.get(0);
                Libro libroExistente = encontrarLibro(libroDTO);
                if (libroExistente != null) {
                    System.out.println("El libro ya está registrado en la base de datos.");
                    return null;
                }

                System.out.println("Libro encontrado: " + libroDTO.getTitulo());
                System.out.println("Autores encontrados: " + libroDTO.getAutor().size());
                for (AutorDTO authorDTO : libroDTO.getAutor()) {
                    System.out.println("Autor: " + authorDTO.getNombre());
                    System.out.println("Año nacimiento: " + authorDTO.getCumpleanios());
                    System.out.println("Año muerte: " + authorDTO.getFechaFallecimiento());
                }
                return convertirALibro(libroDTO);
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error al buscar libros: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al buscar libros por título", e);
        }
    }


    private Libro encontrarLibro(LibroDTO libroDTO) {
        if (libroDTO.getAutor().isEmpty()) {
            return libroRepository.findByTitulo(libroDTO.getTitulo());
        }

        String authorName = libroDTO.getAutor().get(0).getNombre();
        Autor existingAuthor = authorRepository.findFirstByNombre(authorName);

        if (existingAuthor != null) {
            List<Libro> existingBooks = libroRepository.findByTituloAndAutor(libroDTO.getTitulo(), existingAuthor);
            if (!existingBooks.isEmpty()) {
                return existingBooks.get(0);
            }
        }
        return null;
    }

    @Transactional
    private Libro convertirALibro(LibroDTO libroDTO) {
        Libro libro = new Libro();
        libro.setTitulo(libroDTO.getTitulo().substring(0, Math.min(libroDTO.getTitulo().length(), 254)));

        if (!libroDTO.getIdioma().isEmpty()) {
            libro.setIdioma(libroDTO.getIdioma().get(0));
        } else {
            libro.setIdioma("Unknown");
        }

        libro.setCantidadDescargas(libroDTO.getCantidadDescargas());

        if (!libroDTO.getAutor().isEmpty()) {
            AutorDTO authorDTO = libroDTO.getAutor().get(0);
            System.out.println("\nProcesando autor:");
            System.out.println("  Nombre: " + authorDTO.getNombre());
            System.out.println("  Año nacimiento DTO: " + authorDTO.getCumpleanios());
            System.out.println("  Año muerte DTO: " + authorDTO.getFechaFallecimiento());

            Autor autor = authorRepository.findFirstByNombre(authorDTO.getNombre());

            if (autor == null) {
                System.out.println("  Creando nuevo autor");
                autor = new Autor();
                autor.setNombre(authorDTO.getNombre().substring(0, Math.min(authorDTO.getNombre().length(), 254)));
                autor.setCumpleanios(authorDTO.getCumpleanios());
                autor.setFechaFallecimiento(authorDTO.getFechaFallecimiento());
                autor = authorRepository.save(autor);
            } else {
                System.out.println("  Actualizando autor existente");
                autor.setCumpleanios(authorDTO.getCumpleanios());
                autor.setFechaFallecimiento(authorDTO.getFechaFallecimiento());
                autor = authorRepository.save(autor);
            }

            libro.setAutor(autor);
            if (autor.getLibros() == null) {
                autor.setLibros(new ArrayList<>());
            }
            autor.getLibros().add(libro);

        } else {
            System.out.println("\nNo se encontraron autores, creando autor desconocido");
            Autor autorDesconocido = authorRepository.findFirstByNombre("Autor desconocido");
            if (autorDesconocido == null) {
                autorDesconocido = new Autor();
                autorDesconocido.setNombre("Unknown Author");
                autorDesconocido.setLibros(new ArrayList<>());
                autorDesconocido = authorRepository.save(autorDesconocido);
            }
            libro.setAutor(autorDesconocido);
            autorDesconocido.getLibros().add(libro);
        }

        return libroRepository.save(libro);
    }

    @Transactional
    public void saveBook(Libro libro) {
        System.out.println("\n------ Guardando libro ------");
        if (libro.getAutor() != null) {
            System.out.println("Autor del libro a guardar:");
            System.out.println("Nombre: " + libro.getAutor().getNombre());
            System.out.println("Año nacimiento: " + libro.getAutor().getCumpleanios());
            System.out.println("Año muerte: " + libro.getAutor().getFechaFallecimiento());

            if (libro.getAutor().getLibros() == null) {
                libro.getAutor().setLibros(new ArrayList<>());
            }

            if (!libro.getAutor().getLibros().contains(libro)) {
                libro.getAutor().getLibros().add(libro);
            }

            libro.getAutor().setNombre(libro.getAutor().getNombre().substring(0, Math.min(libro.getAutor().getNombre().length(), 254)));
            Autor savedAuthor = authorRepository.save(libro.getAutor());
            libro.setAutor(savedAuthor);
        }

        libroRepository.save(libro);
    }

    public List<Libro> getAllBooks() {
        return libroRepository.findAll();
    }

    public List<Libro> getBooksByLanguage(String language) {
        return libroRepository.findByIdioma(language);
    }

    public List<String> getAvailableLanguages() {
        return libroRepository.findAll().stream()
                .map(Libro::getIdioma)
                .distinct()
                .collect(Collectors.toList());
    }
}