package com.richardDev.IAportfolio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Serviço de integração com a API Google Gemini.
 * Responsável por processar perguntas e gerar respostas baseadas em contexto.
 */
@Service
public class GeminiService {

    @Autowired
    private ContextoCacheService contextoCacheService;

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    @Value("${GEMINI_MODEL}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    public String chat(String mensagem) {
        try {
            long inicio = System.currentTimeMillis();

            String url = GEMINI_API_URL + model + ":generateContent?key=" + apiKey;

            String promptFinal = construirPromptOtimizado(mensagem);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();

            // Configurações otimizadas para VELOCIDADE
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.05);  // Mais baixo = mais determinístico
            generationConfig.put("topP", 0.95);         // Mais alto = mais rápido
            generationConfig.put("topK", 10);           // Limita opções (mais rápido)
            generationConfig.put("maxOutputTokens", 200); // REDUZIDO - mais rápido e conciso
            generationConfig.put("stopSequences", new String[]{"PERGUNTA:", "---"}); // Para quando terminar
            requestBody.put("generationConfig", generationConfig);

            // Adiciona sistema de safeguards
            List<Map<String, Object>> safetySettings = new ArrayList<>();
            requestBody.put("safetySettings", safetySettings);

            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> content = new HashMap<>();
            List<Map<String, String>> parts = new ArrayList<>();
            Map<String, String> part = new HashMap<>();

            part.put("text", promptFinal);
            parts.add(part);
            content.put("parts", parts);
            contents.add(content);
            requestBody.put("contents", contents);

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            String response = restTemplate.postForObject(url, entity, String.class);

            String resposta = extrairResposta(response).trim();

            long duracao = System.currentTimeMillis() - inicio;
            System.out.println("⏱️  Resposta gerada em " + duracao + "ms");

            return resposta;
        } catch (Exception e) {
            System.err.println("❌ Erro na IA: " + e.getMessage());
            return "Desculpe, houve um erro ao processar sua pergunta. Tente novamente.";
        }
    }

    private String construirPromptOtimizado(String mensagem) {
        StringBuilder prompt = new StringBuilder();

        // Obtém contexto do banco de dados via cache
        String contextoDB = contextoCacheService.obterContextoCompleto();

        String sobreMim = """
        VOCÊ É O ASSISTENTE DO RICHARD MORAES SOUZA. AQUI ESTÃO OS DADOS DELE:
        
        PERFIL GERAL:
        - Desenvolvedor Full Stack focado em React, Node.js e TypeScript.
        - Especialista em criar aplicações escaláveis com Express, SQL (PostgreSQL/MySQL) e Docker.
        - Localização: Itajaí, SC.
        
        HISTÓRIA:
        - Começou com o curso de Python do Gustavo Guanabara (YouTube).
        - Migrou para o ecossistema JavaScript por paixão pela criação de lógica e interfaces.
        
        PROJETOS DE DESTAQUE:
        1. Serginho EstetiCar: Site de estética automotiva. Fez do design ao deploy (Netlify). 
           - Resultado: 100/100 no Google Lighthouse (SEO e Performance).
        2. PersonIA: Plataforma Fullstack de personas de IA.
           - Tech Stack: React 19, Node.js, PostgreSQL, JWT, OpenAI API (GPT-4o-mini).
           - Diferencial: Camada social com seguidores, likes e favoritos.
        
        SKILLS TÉCNICAS:
        - Backend: Node.js (Especialidade), Java (Spring Boot), Express, TypeScript.
        - Frontend: React, Tailwind CSS, Bootstrap, ES6+.
        - Infra: Docker (estudando a fundo), Deploy, Integração com IAs.
        
        PONTOS FORTES E EVOLUÇÃO:
        - Forte em: Proatividade, aprendizado rápido e autonomia.
        - A melhorar: Perfeccionismo com UI (está usando Metodologias Ágeis para equilibrar).
        - Desafio superado: Gerenciar o tempo de resposta da OpenAI no PersonIA sem travar o Front.
        
        POR QUE CONTRATAR O RICHARD?
        - Transforma requisitos complexos em soluções funcionais de ponta a ponta.
        - Focado em performance e segurança.
        - Atualmente focando em Java/Spring Boot e Docker para criar APIs ainda mais robustas.
        """;

        prompt.append("Você é o assistente virtual do Richard Moraes Souza.\n");
        prompt.append("Sua missão é responder perguntas usando as fontes abaixo:\n\n");

        prompt.append("FONTE 1 - BANCO DE DADOS:\n");
        prompt.append(contextoDB).append("\n\n");

        prompt.append("\nFONTE 2 - CONHECIMENTO GERAL SOBRE O RICHARD:\n");
        prompt.append(sobreMim).append("\n\n");

        prompt.append("INSTRUÇÕES:\n");
        prompt.append("- Se perguntarem 'Por que te contratar?', use os argumentos do perfil do Richard.\n");
        prompt.append("- Se a informação não existir em nenhuma fonte, diga: 'O Richard ainda não me treinou com essa informação específica, mas você pode perguntar sobre os projetos dele como o PersonIA'.\n\n");
        prompt.append("- Responda no MESMO IDIOMA em que a pergunta foi feita (Português ou Inglês).\n");
        prompt.append("PERGUNTA DO USUÁRIO: ").append(mensagem).append("\n");
        prompt.append("RESPOSTA: ");

        return prompt.toString();
    }

    @SuppressWarnings("unchecked")
    private String extrairResposta(String response) {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);

            if (responseMap.containsKey("error")) {
                Map<String, Object> erro = (Map<String, Object>) responseMap.get("error");
                String mensagemErro = (String) erro.get("message");
                System.err.println("❌ Erro da API Gemini: " + mensagemErro);
                return "Erro ao processar: " + mensagemErro;
            }

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseMap.get("candidates");

            if (candidates == null || candidates.isEmpty()) {
                System.err.println("⚠️  Sem candidatos na resposta");
                return "Não foi possível gerar uma resposta.";
            }

            Map<String, Object> candidate = candidates.get(0);

            // Verifica se a resposta foi bloqueada por segurança
            if (candidate.containsKey("finishReason")) {
                String finishReason = (String) candidate.get("finishReason");
                if ("SAFETY".equals(finishReason)) {
                    System.err.println("⚠️  Resposta bloqueada por filtro de segurança");
                    return "A resposta foi bloqueada por filtros de segurança.";
                }
            }

            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");

            if (parts == null || parts.isEmpty()) {
                System.err.println("⚠️  Nenhuma parte na resposta");
                return "Resposta vazia.";
            }

            String respostaTexto = parts.get(0).get("text");

            // Limpa espaços extras e quebras de linha desnecessárias
            respostaTexto = respostaTexto
                    .trim()
                    .replaceAll("\\s+", " ") // Reduz múltiplos espaços a um
                    .replaceAll("(?m)^\\s+", ""); // Remove espaços no início de linhas

            return respostaTexto;
        } catch (Exception e) {
            System.err.println("❌ Erro ao extrair resposta: " + e.getMessage());
            return "Desculpe, não consegui processar a resposta. Tente novamente.";
        }
    }
}
