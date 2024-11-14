package com.alura.literatura.service;

import com.alura.literatura.dto.AutorDTO;
import com.alura.literatura.dto.LibroDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class ConsumoAPI {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    String URL = "https://gutendex.com/books/?search=";

    public ConsumoAPI(ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
    }

    public List<LibroDTO> searchBooksByTitle(String titulo) throws Exception {
        String nombreDelLibro = URLEncoder.encode(titulo, "UTF-8");
        String url = URL + nombreDelLibro;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode resultsNode = rootNode.path("results");

            List<LibroDTO> libros = new ArrayList<>();
            for (JsonNode node : resultsNode) {
                LibroDTO libro = new LibroDTO();

                // Mapea el título
                libro.setTitulo(node.path("title").asText());

                // Mapea autores
                JsonNode authorsNode = node.path("authors");

                List<AutorDTO> autores = new ArrayList<>();
                for (JsonNode authorNode : authorsNode) {
                    AutorDTO autor = new AutorDTO();

                    // Mapea el nombre del autor
                    autor.setNombre(authorNode.path("name").asText());

                    // Mapea el año de nacimiento
                    autor.setCumpleanios(authorNode.path("birth_year").isNull() ? null : authorNode.path("birth_year").asInt());

                    // Mapea el año de fallecimiento
                    autor.setFechaFallecimiento(authorNode.path("death_year").isNull() ? null : authorNode.path("death_year").asInt());

                    // Agrega el autor a la lista
                    autores.add(autor);
                }
                libro.setAutor(autores);

                // Mapea idiomas
                JsonNode languagesNode = node.path("languages");
                List<String> idiomas = new ArrayList<>();
                for (JsonNode languageNode : languagesNode) {
                    idiomas.add(languageNode.asText());
                }
                libro.setIdioma(idiomas);

                // Mapea cantidad de descargas
                libro.setCantidadDescargas(node.path("download_count").asInt(0));

                // Agrega el libro a la lista
                libros.add(libro);
            }

            return libros;
        } else {
            throw new RuntimeException("Error al consultar la API de Gutendex: " + response.statusCode());
        }

    }
}