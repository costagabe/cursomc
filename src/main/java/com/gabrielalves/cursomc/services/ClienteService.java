package com.gabrielalves.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gabrielalves.cursomc.domain.Cidade;
import com.gabrielalves.cursomc.domain.Cliente;
import com.gabrielalves.cursomc.domain.Endereco;
import com.gabrielalves.cursomc.domain.enums.TipoCliente;
import com.gabrielalves.cursomc.dto.ClienteDTO;
import com.gabrielalves.cursomc.dto.ClienteNewDTO;
import com.gabrielalves.cursomc.repositories.ClienteRepository;
import com.gabrielalves.cursomc.repositories.EnderecoRepository;
import com.gabrielalves.cursomc.services.exceptions.DataIntegrityException;
import com.gabrielalves.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repo;
	
	@Autowired
	EnderecoRepository enderecoRepository;

	public Cliente find(Integer id) {
		Optional<Cliente> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado."));
	}

	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repo.save(obj);
		
		enderecoRepository.saveAll(obj.getEnderecos());
		
		return obj;
	}

	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateData(newObj, obj);
		return repo.save(newObj);
	}

	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());

	}

	public void delete(Integer id) {
		find(id);
		try {
			repo.deleteById(id);
		} catch (DataIntegrityViolationException ex) {
			throw new DataIntegrityException("Nâo é possível realizar esta ação.");
		}
	}

	public List<Cliente> findAll() {
		return repo.findAll();
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}

	public Cliente fromDTO(ClienteDTO dto) {
		return new Cliente(dto.getId(), dto.getNome(), dto.getEmail(), null, null);
	}

	public Cliente fromDTO(ClienteNewDTO dto) {
		String nome = dto.getNome();
		String email = dto.getEmail();
		String cpfOuCnpj = dto.getCpfOuCnpj();
		TipoCliente tipo = TipoCliente.toEnum(dto.getTipo());

		Cidade cid = new Cidade(dto.getCidadeId(), null, null);

		Cliente cli = new Cliente(null, nome, email, cpfOuCnpj, tipo);
		Endereco end = new Endereco(null, dto.getLogradouro(), dto.getNumero(), dto.getComplemento(), dto.getBairro(),
				dto.getCep(), cli, cid);

		cli.getEnderecos().add(end);
		cli.getTelefones().add(dto.getTelefone1());

		if (dto.getTelefone2() != null) {
			cli.getTelefones().add(dto.getTelefone2());
		}
		if (dto.getTelefone3() != null) {
			cli.getTelefones().add(dto.getTelefone3());
		}
		
		return cli;
	}
}
