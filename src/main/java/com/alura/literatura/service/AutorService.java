package com.alura.literatura.service;


import com.alura.literatura.Repository.IAutorRepository;
import com.alura.literatura.model.Autor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutorService {
    @Autowired
    private IAutorRepository autorRepository;

    public List<Autor> getAllAutores() {
        return autorRepository.findAllWithBooks();
    }

    public List<Autor> getAutoresVivosEnElAnio(int anio) {
        return autorRepository.findAutoresVivos(anio);
    }

}