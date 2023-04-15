package com.gupan.minhasfinancas.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gupan.minhasfinancas.exception.RegraNegocioException;
import com.gupan.minhasfinancas.model.entity.Usuario;
import com.gupan.minhasfinancas.model.repository.UsuarioRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioServiceTest {
	
	private static final String EMAIL = "usuario@email.com";
	private static final String NOME = "usuario";
	
	@Autowired
	UsuarioService service;
	
	@Autowired
	UsuarioRepository repository;
	
	@Test
	public void deveValidarEmail() {
		Assertions.assertDoesNotThrow(() -> {
			//Cenário
			repository.deleteAll();
			
			//Ação
			service.validarEmail(EMAIL);
		});
	}
	
	@Test
	public void deveLancarErroAoValidarEmailQuandoTiverEmailCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			
			//Cenário
			Usuario usuario = Usuario.builder().nome(NOME).email(EMAIL).build();
			repository.save(usuario);
			
			//Ação
			service.validarEmail(EMAIL);
		});
	}

}
