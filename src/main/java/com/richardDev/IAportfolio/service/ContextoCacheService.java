package com.richardDev.IAportfolio.service;

import com.richardDev.IAportfolio.Entity.PerguntasEntity;
import com.richardDev.IAportfolio.repository.PerguntasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ContextoCacheService {

    @Autowired
    private PerguntasRepository repository;

    private String contextoCache = "";
    private long ultimaAtualizacao = 0;
    private static final long TEMPO_CACHE_MS = 5 * 60 * 1000; // 5 minutos


    public String obterContextoCompleto() {
        long agora = System.currentTimeMillis();


        if (contextoCache.isEmpty() || (agora - ultimaAtualizacao) > TEMPO_CACHE_MS) {
            atualizarCache();
        }

        return contextoCache;
    }

    /**
     * Força uma atualização imediata do cache.
     * Chame quando novo registro for adicionado/modificado.
     */
    public void invalidarCache() {
        contextoCache = "";
        ultimaAtualizacao = 0;
    }

    /**
     * Atualiza o cache a cada 5 minutos automaticamente (background).
     */
    @Scheduled(fixedRate = TEMPO_CACHE_MS)
    public void atualizarCacheAgendado() {
        atualizarCache();
    }

    /**
     * Atualiza o cache de contexto a partir do banco de dados.
     */
    private synchronized void atualizarCache() {
        try {
            List<PerguntasEntity> todasAsInfos = repository.findAll();

            if (todasAsInfos.isEmpty()) {
                contextoCache = "BASE DE DADOS VAZIA - Nenhuma informação cadastrada ainda.";
            } else {
                // Formata o contexto de forma legível para a IA
                contextoCache = todasAsInfos.stream()
                        .map((entity) -> String.format(
                                "PERGUNTA: %s\nRESPOSTA: %s",
                                entity.getPergunta(),
                                entity.getResposta()
                        ))
                        .collect(Collectors.joining("\n---\n"));
            }

            ultimaAtualizacao = System.currentTimeMillis();
            System.out.println("✅ Cache de contexto atualizado: " + todasAsInfos.size() + " registros");
        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar cache: " + e.getMessage());
            contextoCache = "ERRO: Não foi possível carregar o contexto do banco de dados.";
        }
    }

    /**
     * Retorna o tamanho do cache (número de registros).
     */
    public int getTamanhoCache() {
        return (int) contextoCache.split("---").length;
    }

    /**
     * Retorna informações sobre o estado do cache.
     */
    public String getStatusCache() {
        return String.format("Cache com %d registros | Atualizado há %d segundos",
                getTamanhoCache(),
                (System.currentTimeMillis() - ultimaAtualizacao) / 1000);
    }
}

