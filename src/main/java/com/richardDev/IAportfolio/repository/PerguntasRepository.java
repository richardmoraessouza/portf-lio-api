package com.richardDev.IAportfolio.repository;

import com.richardDev.IAportfolio.Entity.PerguntasEntity;
import org.springframework.data.jpa.repository.JpaRepository; // ISSO AQUI É O QUE TE DÁ O FINDALL
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerguntasRepository extends JpaRepository<PerguntasEntity, Long> {

    // Busca exata por pergunta
    Optional<PerguntasEntity> findByPergunta(String pergunta);

    // Busca por similaridade (case insensitive)
    @Query("SELECT p FROM PerguntasEntity p WHERE LOWER(p.pergunta) LIKE LOWER(CONCAT('%', :termo, '%'))")
    Optional<PerguntasEntity> findByPerguntaContainingIgnoreCase(@Param("termo") String termo);
}