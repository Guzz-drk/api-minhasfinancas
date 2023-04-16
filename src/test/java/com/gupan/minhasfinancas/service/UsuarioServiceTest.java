package com.gupan.minhasfinancas.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gupan.minhasfinancas.exception.ErroAutenticacaoException;
import com.gupan.minhasfinancas.exception.RegraNegocioException;
import com.gupan.minhasfinancas.model.entity.Usuario;
import com.gupan.minhasfinancas.model.repository.UsuarioRepository;
import com.gupan.minhasfinancas.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	private static final String EMAIL = "usuario@email.com";
	private static final String NOME = "usuario";
	private static final String SENHA = "senha";
	private static final Long ID = 1L;

	@SpyBean
	UsuarioServiceImpl service;

	@MockBean
	UsuarioRepository repository;

	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		
		Assertions.assertDoesNotThrow(() -> {
			// Cenário
			Usuario usuario = Usuario.builder().id(ID).nome(NOME).email(EMAIL).senha(SENHA).build();
			Mockito.when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));

			// Ação
			Usuario result = service.autenticar(EMAIL, SENHA);

			// Verificação
			org.assertj.core.api.Assertions.assertThat(result).isNotNull();
		});
	}

	@Test
	public void deveSalvarUmUsuarioComSucesso() {
		
		// Cenário
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder().id(ID).nome(NOME).email(EMAIL).senha(SENHA).build();
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		// Ação
		Usuario result = service.salvarUsuario(new Usuario());
		
		// Verificação
		org.assertj.core.api.Assertions.assertThat(result).isNotNull();
		org.assertj.core.api.Assertions.assertThat(result.getId()).isEqualTo(ID);
		org.assertj.core.api.Assertions.assertThat(result.getNome()).isEqualTo(NOME);
		org.assertj.core.api.Assertions.assertThat(result.getEmail()).isEqualTo(EMAIL);
		org.assertj.core.api.Assertions.assertThat(result.getSenha()).isEqualTo(SENHA);
	}
	
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			//Cenário
			Usuario usuario = Usuario.builder().email(EMAIL).build();
			Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(EMAIL);
			
			//Ação
			service.salvarUsuario(usuario);
			
			//Verificação
			Mockito.verify(repository, Mockito.never()).save(usuario);
		});
	}

	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {

		// Cenário
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

		// Ação
		Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.autenticar(EMAIL, SENHA));

		// Verificação
		org.assertj.core.api.Assertions.assertThat(exception).isInstanceOf(ErroAutenticacaoException.class)
				.hasMessage("Usuário não encontrado para o e-mail informado.");

	}

	@Test
	public void deveLancarErroQuandoASenhaNaoForCorreta() {

		// Cenário
		Usuario usuario = Usuario.builder().id(ID).nome(NOME).email(EMAIL).senha(SENHA).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

		// Ação
		Throwable exceprion = org.assertj.core.api.Assertions
				.catchThrowable(() -> service.autenticar(EMAIL, "senhaTeste"));

		// Verificação
		org.assertj.core.api.Assertions.assertThat(exceprion).isInstanceOf(ErroAutenticacaoException.class)
				.hasMessage("Senha inválida.");

	}

	@Test
	public void deveValidarEmail() {
		Assertions.assertDoesNotThrow(() -> {
			// Cenário
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

			// Ação
			service.validarEmail(EMAIL);
		});
	}

	@Test
	public void deveLancarErroAoValidarEmailQuandoTiverEmailCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {

			// Cenário
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

			// Ação
			service.validarEmail(EMAIL);
		});
	}

}
