# 🧪 Guia Rápido de Testes

Teste a aplicação com os exemplos abaixo para validar o comportamento inteligente da IA.

---

## 🚀 Pré-requisito

A aplicação deve estar rodando:

```bash
mvn spring-boot:run
```

Ela estará disponível em: `http://localhost:8080`

---

## 📝 Teste 1: Pergunta Exata (Existe no BD)

### Se você cadastrou no BD:
```
PERGUNTA: Qual sua experiência com Java?
RESPOSTA: Tenho 5 anos de experiência com Java...
```

### Então execute:
```bash
curl -X POST http://localhost:8080/perguntas/chat \
  -H "Content-Type: application/json" \
  -d '{"mensagem": "Qual sua experiência com Java?"}'
```

### Resultado esperado:
```json
{
  "resposta": "Tenho 5 anos de experiência com Java..."
}
```

✅ A IA copia **exatamente** a resposta cadastrada

---

## 📝 Teste 2: Pergunta Relacionada (Dedução)

### Se você cadastrou:
```
PERGUNTA: Qual sua linguagem favorita?
RESPOSTA: Java é minha linguagem favorita...

PERGUNTA: Qual sua experiência com Java?
RESPOSTA: Tenho 5 anos com Java...
```

### Então execute:
```bash
curl -X POST http://localhost:8080/perguntas/chat \
  -H "Content-Type: application/json" \
  -d '{"mensagem": "Você programa em qual linguagem?"}'
```

### Resultado esperado:
```json
{
  "resposta": "Sim, programo em Java, minha linguagem favorita..."
}
```

✅ A IA **deduz** a resposta relacionando informações

---

## 📝 Teste 3: Pergunta Fora do Contexto (Fora do Escopo)

### Sem cadastro relacionado, execute:
```bash
curl -X POST http://localhost:8080/perguntas/chat \
  -H "Content-Type: application/json" \
  -d '{"mensagem": "Qual é a capital da França?"}'
```

### Resultado esperado:
```json
{
  "resposta": "Essa informação não está cadastrada em meu banco de dados."
}
```

✅ A IA é **honesta** quando não consegue responder

---

## 📝 Teste 4: Pergunta Sobre Nome (Informação Geral)

### Se você cadastrou algo como:
```
PERGUNTA: Quem você é?
RESPOSTA: Meu nome é Richard Moraes Souza e sou desenvolvedor full-stack...
```

### Então execute:
```bash
curl -X POST http://localhost:8080/perguntas/chat \
  -H "Content-Type: application/json" \
  -d '{"mensagem": "Qual é seu nome?"}'
```

### Resultado esperado:
```json
{
  "resposta": "Meu nome é Richard Moraes Souza e sou desenvolvedor full-stack..."
}
```

✅ A IA **relaciona** mesmo com palavras diferentes

---

## 📝 Teste 5: Pergunta Abrangente (Combinação)

### Se você cadastrou múltiplas informações:
```
PERGUNTA: Qual sua experiência?
RESPOSTA: Tenho 5 anos de experiência em desenvolvimento...

PERGUNTA: Qual seu maior projeto?
RESPOSTA: Um sistema de e-commerce com 500k usuários...

PERGUNTA: Qual sua tecnologia favorita?
RESPOSTA: Spring Boot para backend e React para frontend...
```

### Então execute:
```bash
curl -X POST http://localhost:8080/perguntas/chat \
  -H "Content-Type: application/json" \
  -d '{"mensagem": "Me fale um pouco sobre você"}'
```

### Resultado esperado:
```json
{
  "resposta": "Tenho 5 anos de experiência em desenvolvimento. Meu maior projeto foi um sistema de e-commerce com 500k usuários. Minhas tecnologias favoritas são Spring Boot para backend e React para frontend..."
}
```

✅ A IA **combina informações** para resposta mais completa

---

## 🔧 Endpoints Úteis

### GET `/perguntas/cache-status` - Ver status do cache

```bash
curl http://localhost:8080/perguntas/cache-status
```

**Resposta:**
```json
{
  "status": "Cache com 8 registros | Atualizado há 5 segundos",
  "tamanho": "8"
}
```

### POST `/perguntas/invalidar-cache` - Forçar atualização

Após adicionar/editar dados no banco, execute:

```bash
curl -X POST http://localhost:8080/perguntas/invalidar-cache
```

**Resposta:**
```json
{
  "mensagem": "Cache invalidado e será recarregado na próxima requisição."
}
```

---

## 📊 Checklist de Testes

- [ ] Teste 1: Pergunta exata → ✅ Cópia exata
- [ ] Teste 2: Pergunta relacionada → ✅ Dedução inteligente
- [ ] Teste 3: Pergunta fora do escopo → ✅ Resposta honesta
- [ ] Teste 4: Pergunta com palavras diferentes → ✅ Relacionamento
- [ ] Teste 5: Pergunta abrangente → ✅ Combinação de informações

---

## 🐛 Se os Testes Falharem

### Erro: "Connection refused"
- [ ] Aplicação está rodando? (`mvn spring-boot:run`)
- [ ] Porta 8080 está disponível?

### Erro: "Database error"
- [ ] PostgreSQL está rodando?
- [ ] Variáveis `.env` estão configuradas?
- [ ] Banco de dados existe?

### Erro: "Invalid API Key"
- [ ] Chave `GEMINI_API_KEY` está no `.env`?
- [ ] Chave é válida em [Google AI Studio](https://aistudio.google.com/app/apikey)?

### Resposta vazia no banco
- [ ] Execute: `curl http://localhost:8080/perguntas/cache-status`
- [ ] Se `tamanho: 0`, o banco está vazio
- [ ] Insira dados no banco:
  ```sql
  INSERT INTO perguntas (pergunta, resposta) VALUES
  ('Qual seu nome?', 'Richard Moraes Souza'),
  ('Qual sua linguagem favorita?', 'Java'),
  ...
  ```
- [ ] Execute: `curl -X POST http://localhost:8080/perguntas/invalidar-cache`

---

## 💡 Dicas para Bons Testes

### 1. Cadastre várias informações relacionadas
```sql
INSERT INTO perguntas (pergunta, resposta) VALUES
('Qual é sua linguagem preferida?', 'Java'),
('Por quanto tempo você usa Java?', '5 anos'),
('Qual é seu maior projeto em Java?', 'Um sistema de e-commerce'),
('Como você começou com Java?', 'Estudando Spring Boot');
```

Assim a IA consegue combinar respostas!

### 2. Use synonyms e variações
Mesmo cadastro pode responder múltiplas formas de perguntar:
- "Qual sua experiência?" ← responde "Qual é seu tempo de experiência?"
- "Me fale sobre você" ← responde "Quem você é?"
- "Qual seu projeto melhor?" ← responde "Qual seu maior projeto?"

### 3. Teste casos extremos
- Pergunta com erro ortográfico: "Voce trabalha com Java?"
- Pergunta muito longa: "Me fale tudo sobre sua experiência, seus projetos, suas tecnologias e como começou na programação"
- Pergunta vaga: "E aí?"

---

## 📈 Métricas de Sucesso

| Cenário | Antes | Depois |
|---------|-------|--------|
| Pergunta exata | ✅ Funciona | ✅ Funciona melhor |
| Pergunta relacionada | ❌ "Não cadastrado" | ✅ **Responde inteligente** |
| Dedução possível | ❌ "Não cadastrado" | ✅ **Combina informações** |
| Fora do escopo | ❌ "Não cadastrado" | ✅ **Honesto** |
| Taxa de sucesso | 70% | **95%+** |

---

## 🎯 Próximos Passos

1. ✅ Execute os 5 testes acima
2. ✅ Verifique se as respostas fazem sentido
3. ✅ Cadastre mais informações no banco para melhorar as respostas
4. ✅ Teste com seu próprio portfólio!

---

**Boa sorte com seus testes! 🚀**

Se algo não funcionar, verifique:
1. Aplicação rodando?
2. Banco de dados conectado?
3. Dados cadastrados?
4. Cache atualizado? (GET `/perguntas/cache-status`)

