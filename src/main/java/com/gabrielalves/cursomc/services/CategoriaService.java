package com.gabrielalves.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gabrielalves.cursomc.domain.Categoria;
import com.gabrielalves.cursomc.repositories.CategoriaRepository;
import com.gabrielalves.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository repo;
	
	public Categoria buscar(Integer id) {
		Optional<Categoria> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado."));
	}
	
	public Categoria insert (Categoria categoria) {
		categoria.setId(null);
		return repo.save(categoria);
	}
}
