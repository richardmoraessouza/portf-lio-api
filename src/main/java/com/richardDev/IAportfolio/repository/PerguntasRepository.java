package com.richardDev.IAportfolio.repository;

import com.richardDev.IAportfolio.Entity.PerguntasEntity;
import org.springframework.data.jpa.repository.JpaRepository; // ISSO AQUI É O QUE TE DÁ O FINDALL
import org.springframework.stereotype.Repository;

@Repository
public interface PerguntasRepository extends JpaRepository<PerguntasEntity, Long> {
}