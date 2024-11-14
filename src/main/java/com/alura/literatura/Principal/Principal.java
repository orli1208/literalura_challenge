package com.alura.literatura.Principal;

import com.alura.literatura.model.Autor;
import com.alura.literatura.model.Libro;
import com.alura.literatura.service.AutorService;
import com.alura.literatura.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Principal {
    @Autowired
    private LibroService libroService;

    @Autowired
    private AutorService autorService;

    private Scanner teclado = new Scanner(System.in);

    public void start() {
        mostrarMenu();
    }

        private void mostrarMenu () {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    ------------------------------------------
                    Elija la opción a través de su número:
                    1 - Buscar libro por título
                    2 - listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos  en un determinado año
                    5 - Listar libros por idioma
                    
                    0 - Salir
                    """;

                System.out.println(menu);

                try {
                    opcion = teclado.nextInt();
                    teclado.nextLine();
                } catch (InputMismatchException e) {
                    System.out.println("Por favor, ingrese un número válido.");
                    System.out.println();
                    teclado.nextLine();
                    continue;
                }

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnAnio();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println();
                    System.out.println("Saliendo del sistema.");
                    System.out.println();
                    break;
                default:
                    System.out.println();
                    System.out.println("Opción no válida. Intente de nuevo.");
                    System.out.println();
            }
        }
    }

    private void buscarLibroPorTitulo() {

        System.out.print("Ingrese el título del libro: ");
        String titulo = teclado.nextLine();

        try {
            Libro libro = libroService.searchBooksByTitle(titulo);

            if (libro == null) {
                System.out.println("Libro no encontrado o ya existente en base de datos...");
            } else {
                mostrarDetallesLibro(libro);
                libroService.saveBook(libro);
                System.out.println();
                System.out.println("Libro guardado en la base de datos.");
                System.out.println();
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void listarLibrosRegistrados() {
        System.out.println();
        System.out.println("------ Libro guardados en la base de datos ------");
        System.out.println();

        List<Libro> books = libroService.getAllBooks();
        books.forEach(this::mostrarDetallesLibro);
    }

    private void listarAutoresRegistrados() {
        System.out.println();
        System.out.println("------ Autores guardados en la base de datos ------");
        System.out.println();

        List<Autor> authors = autorService.getAllAutores();
        authors.forEach(this::mostrarDetallesAutor);
    }

    private void listarAutoresVivosEnAnio() {
        System.out.println();
        System.out.print("Ingrese el año (4 dígitos): ");
        String anioInput = teclado.nextLine();

        int anio;
        try {
            anio = Integer.parseInt(anioInput);
            if (anio < -9999 || anio > 9999) {
                System.out.println("Por favor, ingrese un año válido de 4 dígitos.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un año válido.");
            System.out.println();
            return;
        }

        System.out.println();
        System.out.println("------ Autores vivos en el año " + anio + " ------");
        System.out.println();

        List<Autor> autores = autorService.getAutoresVivosEnElAnio(anio);
        if (autores.isEmpty()) {
            System.out.println("No hay autores vivos registrados en el año " + anio + ".");
            System.out.println();
        } else {
            autores.forEach(this::mostrarDetallesAutor);
        }
    }


    private void listarLibrosPorIdioma() {
        List<String> idiomas = libroService.getAvailableLanguages();

        if (idiomas.isEmpty()) {
            System.out.println();
            System.out.println("No hay libros registrados en la base de datos.");
            System.out.println();
            return;
        }

        System.out.println();
        System.out.println("Idiomas disponibles:");
        for (int i = 0; i < idiomas.size(); i++) {
            System.out.println((i + 1) + ". " + idiomas.get(i));
        }

        System.out.println();
        System.out.print("Seleccione un idioma (ingrese el número correspondiente): ");
        if (teclado.hasNextInt()) {
            int choice = teclado.nextInt();
            teclado.nextLine();

            if (choice < 1 || choice > idiomas.size()) {
                System.out.println();
                System.out.println("Selección no válida.");
                System.out.println();
                return;
            }

            String selectedLanguage = idiomas.get(choice - 1);
            List<Libro> books = libroService.getBooksByLanguage(selectedLanguage);

            if (books.isEmpty()) {
                System.out.println();
                System.out.println("No hay libros registrados en el idioma seleccionado.");
                System.out.println();
            } else {
                System.out.println();
                System.out.println("------ Libros en el idioma " + selectedLanguage + " ------");
                System.out.println();
                books.forEach(this::mostrarDetallesLibro);
            }
        } else {
            System.out.println();
            System.out.println("Error: Ingrese un número válido.");
            teclado.nextLine();
        }
    }

    private void mostrarDetallesLibro(Libro libro) {
        System.out.println();
        System.out.println("------ Libro ------");
        System.out.println("Título: " + libro.getTitulo());
        System.out.println("Autor: " + libro.getAutor().getNombre());
        System.out.println("Idioma: " + libro.getIdioma());
        System.out.println("Número de Descargas: " + libro.getCantidadDescargas());
        System.out.println("--------------------");
        System.out.println();
    }

    private void mostrarDetallesAutor(Autor autor) {
        System.out.println();
        System.out.println("------ Autor ------");
        System.out.println("Nombre del autor: " + autor.getNombre());
        System.out.println("Año de nacimiento: " + autor.getCumpleanios());
        System.out.println("Año de fallecimiento: " + autor.getFechaFallecimiento());
        System.out.println("Libros: ");
        for (Libro libro : autor.getLibros()){
            System.out.println("- " + libro.getTitulo());
        }
        System.out.println("--------------------");
        System.out.println();
    }
}
