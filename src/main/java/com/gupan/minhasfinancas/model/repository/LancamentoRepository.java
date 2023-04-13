package com.gupan.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gupan.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
