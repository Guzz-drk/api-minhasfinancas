package com.gupan.minhasfinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gupan.minhasfinancas.model.entity.Lancamento;
import com.gupan.minhasfinancas.model.enums.StatusLancamento;
import com.gupan.minhasfinancas.model.enums.TipoLancamento;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {
	
	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveSalvarUmLancamento() {
		//Cenário
		Lancamento lancamento = criarLancamento();
		
		//Ação
		lancamento = repository.save(lancamento);
		
		//Verificação
		assertThat(lancamento.getId()).isNotNull();
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		//Cenário
		Lancamento lancamento = criarEPersistirLancamento();
		
		//Ação
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		repository.delete(lancamento);
		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
		
		//Verificação
		assertThat(lancamentoInexistente).isNull();
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//Cenário
		Lancamento lancamento = criarEPersistirLancamento();
		lancamento.setAno(2022);
		lancamento.setMes(2);
		lancamento.setDescricao("Atualizar");
		lancamento.setTipo(TipoLancamento.RECEITA);
		lancamento.setStatus(StatusLancamento.CANCELADO);
		lancamento.setValor(BigDecimal.valueOf(20));
		repository.save(lancamento);
		
		//Ação
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
		
		//Verificação
		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2022);
		assertThat(lancamentoAtualizado.getMes()).isEqualTo(2);
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Atualizar");
		assertThat(lancamentoAtualizado.getTipo()).isEqualTo(TipoLancamento.RECEITA);
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
		assertThat(lancamentoAtualizado.getValor()).isEqualTo(BigDecimal.valueOf(20));
	}
	
	@Test
	public void deveBuscarUmLancamentoPorId() {
		//Cenário
		Lancamento lancamento = criarEPersistirLancamento();

		//Ação
		Optional<Lancamento> result = repository.findById(lancamento.getId());
		
		//Verificação
		assertThat(result.isPresent()).isTrue();
	}
	
	public static Lancamento criarLancamento() {
		Lancamento lancamento = Lancamento.builder()
				.ano(2023)
				.mes(1)
				.descricao("Conta de Luz")
				.valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.DESPESA)
				.status(StatusLancamento.PENDENTE)
				.dataCadastro(LocalDate.now())
				.build();
		
		return lancamento;
	}
	
	private Lancamento criarEPersistirLancamento() {
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return lancamento;
	}

}
