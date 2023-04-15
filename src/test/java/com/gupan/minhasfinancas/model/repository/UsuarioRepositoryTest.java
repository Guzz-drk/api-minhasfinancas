package com.gupan.minhasfinancas.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gupan.minhasfinancas.model.entity.Usuario;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {
	
	private static final String EMAIL = "usuario@email.com";
	private static final String NOME = "usuario";
	private static final String SENHA = "senha";

	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	public static Usuario criarUsuario() {
		//Cenário
		return Usuario.builder().nome(NOME).email(EMAIL).senha(SENHA).build();
	}
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		//Cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//Ação/Execução
		boolean result = repository.existsByEmail(EMAIL);
		
		//Verificação
		Assertions.assertThat(result).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastroComOEmail() {
		//Ação
		boolean result = repository.existsByEmail(EMAIL);
		
		//Verificação
		Assertions.assertThat(result).isFalse();
	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		//Cenário
		Usuario usuario = criarUsuario();
		
		//Ação
		Usuario usuarioSalvo = repository.save(usuario);
		
		//Verificação
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
		
	}
	
	@Test
	public void deveBuscarOUsuarioPorEmail() {
		//Cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//Ação
		Optional<Usuario> result = repository.findByEmail(EMAIL);
		
		//Verificação
		Assertions.assertThat(result.isPresent()).isTrue();
		
	}
	
	@Test
	public void deveRetornarVazioAoBuscarOUsuarioPorEmailQuandoNaoExisteNaBase() {
		//Cenário
		
		//Ação
		Optional<Usuario> result = repository.findByEmail(EMAIL);
		
		//Verificação
		Assertions.assertThat(result.isPresent()).isFalse();
		
	}
}
