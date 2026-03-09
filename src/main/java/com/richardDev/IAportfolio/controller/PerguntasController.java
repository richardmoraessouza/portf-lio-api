package com.richardDev.IAportfolio.controller;

import com.richardDev.IAportfolio.Entity.PerguntasEntity;
import com.richardDev.IAportfolio.repository.PerguntasRepository;
import com.richardDev.IAportfolio.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/perguntas")
@CrossOrigin(origins = "*")
public class PerguntasController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private PerguntasRepository repository;

    // perguntas rápida sobre mim
    @GetMapping("/pergunta-rapida/{id}")
    public ResponseEntity<PerguntasEntity> perguntaRapida(@PathVariable Long id) {
        return repository.findById(id)
                .map(pergunta -> ResponseEntity.ok(pergunta))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chatComContexto(@RequestBody Map<String, String> payload) {
        try {
            String mensagemUsuario = payload.get("mensagem");

            if (mensagemUsuario == null || mensagemUsuario.trim().isEmpty()) {
                Map<String, String> erro = new HashMap<>();
                erro.put("resposta", "Erro: Mensagem vazia. Por favor, faça uma pergunta.");
                return ResponseEntity.badRequest().body(erro);
            }

            System.out.println("❓ Pergunta recebida: " + mensagemUsuario);

            // Primeiro, tenta encontrar resposta rápida no banco de dados
            String respostaRapida = buscarRespostaRapida(mensagemUsuario);
            if (respostaRapida != null) {
                System.out.println("⚡ Resposta rápida encontrada no BD");
                Map<String, String> response = new HashMap<>();
                response.put("resposta", respostaRapida);
                return ResponseEntity.ok(response);
            }

            // Se não encontrou no BD, usa a IA
            String respostaIA = geminiService.chat(mensagemUsuario);

            Map<String, String> response = new HashMap<>();
            response.put("resposta", respostaIA);

            System.out.println("✅ Resposta enviada");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Erro no controller: " + e.getMessage());
            Map<String, String> erro = new HashMap<>();
            erro.put("resposta", "Erro interno do servidor. Tente novamente.");
            return ResponseEntity.internalServerError().body(erro);
        }
    }

    /**
     * Busca resposta rápida no banco de dados.
     * Primeiro tenta match exato, depois busca por termos similares.
     */
    private String buscarRespostaRapida(String pergunta) {
        try {
            // 1. Busca exata
            var entidadeExata = repository.findByPergunta(pergunta.trim());
            if (entidadeExata.isPresent()) {
                return entidadeExata.get().getResposta();
            }

            // 2. Busca por termos similares (se pergunta for curta, tenta encontrar)
            if (pergunta.length() < 100) { // Só para perguntas curtas
                String[] palavras = pergunta.toLowerCase().split("\\s+");
                for (String palavra : palavras) {
                    if (palavra.length() > 3) { // Palavras com mais de 3 letras
                        var entidadeSimilar = repository.findByPerguntaContainingIgnoreCase(palavra);
                        if (entidadeSimilar.isPresent()) {
                            return entidadeSimilar.get().getResposta();
                        }
                    }
                }
            }

            return null; // Não encontrou
        } catch (Exception e) {
            System.err.println("Erro ao buscar resposta rápida: " + e.getMessage());
            return null;
        }
    }
}
