package com.alura.literatura.dto;

import java.util.List;

public class LibroDTO {
    private String titulo;
    private List<AutorDTO> autor;
    private List<String> idioma;
    private int cantidadDescargas;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<AutorDTO> getAutor() {
        return autor;
    }

    public void setAutor(List<AutorDTO> autor) {
        this.autor = autor;
    }

    public List<String> getIdioma() {
        return idioma;
    }

    public void setIdioma(List<String> idioma) {
        this.idioma = idioma;
    }

    public int getCantidadDescargas() {
        return cantidadDescargas;
    }

    public void setCantidadDescargas(int cantidadDescargas) {
        this.cantidadDescargas = cantidadDescargas;
    }
}