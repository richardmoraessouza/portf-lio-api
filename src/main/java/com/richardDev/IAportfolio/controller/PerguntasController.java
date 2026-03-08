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
}
