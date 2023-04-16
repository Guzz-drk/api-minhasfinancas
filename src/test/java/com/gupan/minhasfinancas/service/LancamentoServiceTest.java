package com.gupan.minhasfinancas.service;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gupan.minhasfinancas.exception.RegraNegocioException;
import com.gupan.minhasfinancas.model.entity.Lancamento;
import com.gupan.minhasfinancas.model.entity.Usuario;
import com.gupan.minhasfinancas.model.enums.StatusLancamento;
import com.gupan.minhasfinancas.model.repository.LancamentoRepository;
import com.gupan.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.gupan.minhasfinancas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		//Cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//Ação
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		//Verificação
		assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		//Cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
		
		//Ação e Verificação
		catchThrowableOfType(() -> service.salvar(lancamentoASalvar) , RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		Mockito.doNothing().when(service).validar(lancamento);
		Mockito.when(repository.save(lancamento)).thenReturn(lancamento);
		
		//Ação
		service.atualizar(lancamento);
		
		//Verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamento);
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		//Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//Ação e Verificação
		catchThrowableOfType(() -> service.atualizar(lancamento) , NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamento);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		//Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		
		//Ação
		service.deletar(lancamento);
		
		//Verificação
		Mockito.verify(repository).delete(lancamento);
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		//Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//Ação
		catchThrowableOfType(() -> service.deletar(lancamento) , NullPointerException.class);
		
		//Verificação
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}
	
	@Test
	public void deveFiltrarLancamento() {
		//Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
	
		//Ação
		List<Lancamento> result = service.buscar(lancamento);
		
		//Verificação
		assertThat(result).isNotEmpty().hasSize(1).contains(lancamento);
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		//Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		//Ação
		service.atualizarStatus(lancamento, novoStatus);
		
		//Verificação
		assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	}
	
	@Test
	public void deveObterUmLancamentoPorId() {
		//Cenário
		Long id = 1L;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//Ação
		Optional<Lancamento> result = service.obterPorId(id);
		
		//Verificação
		assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		//Cenário
		Long id = 1L;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		//Ação
		Optional<Lancamento> result = service.obterPorId(id);
		
		//Verificação
		assertThat(result.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarErroAoValidarUmLancamento() {
		//Cenário
		
		//01 - Descrição inválida!
		//Lancamento lancamento = Lancamento.builder().descricao("").build();
		
		//02 - Mês Inválido!
		//Lancamento lancamento = Lancamento.builder().descricao("Contas a pagar").mes(0).build();
		//Lancamento lancamento = Lancamento.builder().descricao("Contas a pagar").mes(13).build();
		
		//03 - Ano Inválido!
		//Lancamento lancamento = Lancamento.builder().descricao("Contas a pagar").mes(1).ano(23111).build();
		//Lancamento lancamento = Lancamento.builder().descricao("Contas a pagar").mes(1).ano(null).build();
		//Lancamento lancamento = Lancamento.builder().descricao("Contas a pagar").mes(1).ano(23).build();
		
		//04 - Usuário Inválido!
		//Lancamento lancamento = Lancamento.builder().descricao("Contas a pagar").mes(1).ano(2023).usuario().build();
		
		//05 - Valor Inválido!
		//Lancamento lancamento = Lancamento.builder().descricao("Contas a pagar").mes(1).ano(2023).usuario(Usuario.builder().id(1L).build()).valor(null).build();
		
		
		//06 - Tipo Inválido!
		Lancamento lancamento = Lancamento.builder().descricao("Contas a pagar").mes(1).ano(2023).usuario(Usuario.builder().id(1L).build()).valor(BigDecimal.valueOf(20L)).tipo(null).build();
		//Ação
		Throwable erro =  catchThrowable(() -> service.validar(lancamento));
		
		//Verificação
		
		//verificação 01
		//assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
		
		//Verificação 02
		//assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
	
		//Verificação 03
		//assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		//Verificação 04
		//assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
	
		//Verificação 05 
		//assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
	
		//Verificação 06
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo de Lançamento.");
	}
}
