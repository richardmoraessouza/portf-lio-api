# 🤖 IA Portfolio - Backend

Um sistema inteligente de chat para portfólio de desenvolvedor, alimentado pela **API Google Gemini**. A aplicação responde perguntas sobre Richard Moraes Souza utilizando um banco de dados PostgreSQL com informações cadastradas.

## 📋 Visão Geral

Este backend implementa um sistema de perguntas e respostas que:
- Recebe perguntas via API REST
- Busca contexto relevante no banco de dados
- Processa a pergunta com a IA Gemini
- Retorna respostas baseadas **exclusivamente** nas informações cadastradas
- Evita alucinações com prompts rigorosos e context-aware

## 🛠️ Tecnologias

| Tecnologia | Versão | Função |
|-----------|--------|--------|
| **Java** | 21 | Linguagem principal |
| **Spring Boot** | 4.0.3 | Framework web |
| **Spring Data JPA** | - | Persistência de dados |
| **PostgreSQL** | - | Banco de dados relacional |
| **Google Gemini API** | 1.36.0 | Processamento de IA |
| **Lombok** | - | Redução de boilerplate |
| **Maven** | 3.6+ | Gerenciador de dependências |
| **Docker** | - | Containerização (opcional) |

## 📦 Pré-requisitos

### Obrigatórios
- **Java JDK 21** ou superior
- **Maven 3.6+**
- **PostgreSQL 12+** (ou outro banco relacional)
- **Chave de API do Google Gemini** (gratuita em [Google AI Studio](https://aistudio.google.com/app/apikey))

### Opcionais
- **Docker & Docker Compose** - para containerização
- **Git** - para controle de versão

## 🚀 Como Configurar e Executar

### 1️⃣ Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/IAportfolio.git
cd IAportfolio
```

### 2️⃣ Configurar Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
# Banco de Dados PostgreSQL
DB_URL=jdbc:postgresql://localhost:5432/iaportfolio
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha

# Google Gemini API
GEMINI_API_KEY=sua_chave_api_aqui
GEMINI_MODEL=gemini-1.5-flash
```

**Ou configure no `application.properties`:**

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/iaportfolio
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

google.api.key=sua_chave_api_aqui
google.api.model=gemini-1.5-flash
```

### 3️⃣ Instalar Dependências

```bash
mvn clean install
```

### 4️⃣ Executar a Aplicação

#### Opção A: Via Maven
```bash
mvn spring-boot:run
```

#### Opção B: Via arquivo JAR compilado
```bash
mvn clean package
java -jar target/IAportfolio-0.0.1-SNAPSHOT.jar
```

#### Opção C: Via Docker
```bash
docker-compose up --build
```

A aplicação estará disponível em: **http://localhost:8080**

## 📡 API - Endpoints

### POST `/perguntas/chat`

Realiza uma pergunta e recebe uma resposta gerada pela IA baseada no banco de dados.

#### Request
```json
{
  "mensagem": "Qual é sua experiência com Spring Boot?"
}
```

#### Response (200 OK)
```json
{
  "resposta": "Tenho 5 anos de experiência desenvolvendo aplicações backend com Spring Boot..."
}
```

#### Error Response (500 Internal Server Error)
```json
{
  "resposta": "Erro: [mensagem de erro]"
}
```

### Exemplos de Requisição

**cURL:**
```bash
curl -X POST http://localhost:8080/perguntas/chat \
  -H "Content-Type: application/json" \
  -d '{"mensagem": "Quais são seus projetos?"}'
```

**JavaScript/Fetch:**
```javascript
fetch('http://localhost:8080/perguntas/chat', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ mensagem: 'Qual é sua experiência com Java?' })
})
  .then(res => res.json())
  .then(data => console.log(data.resposta));
```

**Python/Requests:**
```python
import requests

response = requests.post(
  'http://localhost:8080/perguntas/chat',
  json={'mensagem': 'Qual é sua especialidade?'}
)
print(response.json()['resposta'])
```

## 🧠 Comportamento Inteligente da IA

A IA foi otimizada para responder de forma **inteligente e prestativa**:

1. ✅ **Lê TODO o contexto** do banco de dados
2. ✅ **Tenta responder** mesmo sem match exato de pergunta
3. ✅ **Relaciona informações** para gerar respostas melhores
4. ✅ **Só diz que não está cadastrado** quando realmente não consegue responder

### Exemplos de Resposta:

| Pergunta | Comportamento |
|----------|---------------|
| "Qual sua experiência com Java?" | Copia resposta exata do BD |
| "Como você é?" | Deduz resposta de várias informações |
| "Me fale sobre você" | Relaciona infos para resposta completa |
| "Qual é a capital do Brasil?" | Responde que não está cadastrado |

**Para mais detalhes, veja [PROMPT_INTELIGENTE.md](./PROMPT_INTELIGENTE.md)**

## 📁 Estrutura do Projeto

```
IAportfolio/
├── src/
│   ├── main/
│   │   ├── java/com/richardDev/IAportfolio/
│   │   │   ├── controller/
│   │   │   │   └── PerguntasController.java        # 🎯 Endpoints REST
│   │   │   ├── service/
│   │   │   │   └── GeminiService.java              # 🤖 Integração Gemini
│   │   │   ├── dto/
│   │   │   │   ├── ChatRequest.java                # Modelo de requisição
│   │   │   │   └── ChatResponse.java               # Modelo de resposta
│   │   │   ├── Entity/
│   │   │   │   └── PerguntasEntity.java            # 📊 Entidade JPA
│   │   │   ├── repository/
│   │   │   │   └── ProjetosRepository.java         # 💾 Acesso ao BD
│   │   │   └── IAportfolioApplication.java         # ▶️ Classe principal
│   │   └── resources/
│   │       └── application.properties              # ⚙️ Configurações
│   └── test/
│       └── java/...                                # 🧪 Testes unitários
├── docker-compose.yml                             # 🐳 Configuração Docker
├── Dockerfile                                     # 🐳 Build da imagem
├── pom.xml                                        # 📦 Dependências Maven
└── README.md                                      # 📖 Este arquivo
```

## 🔧 Configurações Importantes

### GeminiService

**Configurações do Modelo Gemini:**
| Propriedade | Valor | Função |
|------------|-------|--------|
| `temperature` | 0.1 | Baixa aleatóriedade (respostas determinísticas) |
| `topP` | 0.8 | Controle de diversidade |
| `maxOutputTokens` | 1500 | Limite de tamanho da resposta |
| `stopSequences` | `["USER:", "PERGUNTA:"]` | Evita continuação desnecessária |

### Prompt System

O sistema utiliza um prompt estruturado que:

1. ✅ Define o papel da IA (assistente do portfólio de Richard)
2. ✅ Carrega o contexto do banco de dados
3. ✅ Define regras rígidas (só responder com contexto fornecido)
4. ✅ Evita alucinações com instruções explícitas
5. ✅ Trata informações não encontradas graciosamente

**Prompt Template:**
```
Você é o sistema de busca do portfólio do desenvolvedor Richard Moraes Souza.
Sua única tarefa é responder à PERGUNTA usando APENAS o CONTEXTO fornecido.

CONTEXTO (Vindo do Banco de Dados):
[contexto do BD aqui]

REGRAS:
1. Se a resposta estiver no CONTEXTO, transcreva-a INTEGRALMENTE.
2. Se não encontrar, responda: "Ainda não cadastrei essa informação no meu banco de dados."
3. Não adicione saudações, comentários ou opiniões próprias.

PERGUNTA: [pergunta do usuário]
RESPOSTA:
```

## 🗄️ Banco de Dados

### Tabela: `perguntas_entity`

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | BIGINT (PK) | ID único |
| `pergunta` | VARCHAR | Pergunta cadastrada |
| `resposta` | TEXT | Resposta correspondente |
| `criado_em` | TIMESTAMP | Data de criação |
| `atualizado_em` | TIMESTAMP | Data de atualização |

### Exemplo de Inserção

```sql
INSERT INTO perguntas_entity (pergunta, resposta) VALUES
('Qual é sua experiência com Java?', 'Tenho 5 anos de experiência com Java...'),
('Quais são seus projetos principais?', 'Meus principais projetos incluem...'),
('Como entrar em contato?', 'Você pode me contatar através de meu email: ...'),
```

## 🧪 Testes

Execute os testes com:

```bash
mvn test
```

Para testes com cobertura:

```bash
mvn clean test jacoco:report
```

O relatório será gerado em: `target/site/jacoco/index.html`

## 🐛 Troubleshooting

### ❌ Erro: "API key not found"
**Solução:**
- Verifique se a variável `GEMINI_API_KEY` está definida
- Confirme o formato correto no `.env` ou `application.properties`
- Reinicie a aplicação após alterar

### ❌ Erro: "Connection refused" (PostgreSQL)
**Solução:**
- Verifique se PostgreSQL está rodando: `pg_isready`
- Confirme as credenciais em `application.properties`
- Verifique se o banco `iaportfolio` existe

### ❌ Resposta vazia ou genérica
**Solução:**
- Confirme que há dados no banco de dados
- Verifique os logs: `tail -f logs/app.log`
- Teste diretamente a API com `cURL` ou Postman

### ❌ Erro 401: "Invalid API Key"
**Solução:**
- Gere uma nova chave em [Google AI Studio](https://aistudio.google.com/app/apikey)
- Confirme que não há espaços em branco na chave
- Teste a chave diretamente no Google AI Studio

## 📝 Variáveis de Ambiente

| Variável | Obrigatória | Exemplo | Descrição |
|----------|-------------|---------|-----------|
| `DB_URL` | ✅ | `jdbc:postgresql://localhost:5432/iaportfolio` | URL de conexão PostgreSQL |
| `DB_USERNAME` | ✅ | `postgres` | Usuário do banco |
| `DB_PASSWORD` | ✅ | `senha123` | Senha do banco |
| `GEMINI_API_KEY` | ✅ | `abc123...xyz` | Chave da API Google Gemini |
| `GEMINI_MODEL` | ✅ | `gemini-1.5-flash` | Modelo Gemini a usar |

## ⚠️ Notas de Segurança

- 🔐 **NUNCA** commite arquivo `.env` ou `application.properties` com credenciais
- 🔐 Use variáveis de ambiente em produção
- 🔐 Implemente autenticação/autorização antes de deployar
- 🔐 Adicione `.env` e arquivos sensíveis ao `.gitignore`
- 🔐 Valide e sanitize todas as entradas de usuário
- 🔐 Use HTTPS em produção

### Arquivo .gitignore Recomendado
```
.env
*.properties
target/
.vscode/
.idea/
*.log
.DS_Store
```

## 🚀 Deploy

### Opção 1: Docker (Recomendado)
```bash
docker-compose up -d
```

### Opção 2: Azure / AWS
1. Configure as variáveis de ambiente no serviço
2. Faça build: `mvn clean package`
3. Deploy do arquivo JAR

### Opção 3: VPS/Servidor
```bash
ssh usuario@servidor
git clone https://github.com/seu-usuario/IAportfolio.git
cd IAportfolio
mvn clean package
nohup java -jar target/IAportfolio-*.jar > app.log 2>&1 &
```

## 📚 Documentação Adicional

- 📖 [SETUP_GEMINI.md](./SETUP_GEMINI.md) - Guia detalhado de configuração da API Gemini
- ⚠️ [SECURITY_WARNING.md](./SECURITY_WARNING.md) - Alertas de segurança
- ℹ️ [HELP.md](./HELP.md) - Ajuda geral

## 🔄 Fluxo da Aplicação

```
┌─────────────────┐
│  Cliente HTTP   │
└────────┬────────┘
         │ POST /perguntas/chat
         │ {"mensagem": "..."}
         ▼
┌─────────────────────────────┐
│ PerguntasController         │
└────────┬────────────────────┘
         │
         ├─ Extrai mensagem do request
         │
         ▼
┌──────────────────────────────┐
│ ProjetosRepository           │
│ (Spring Data JPA)            │
└────────┬─────────────────────┘
         │ findAll()
         ▼
┌──────────────────────────────┐
│ PostgreSQL Database          │
│ (perguntas_entity)           │
└────────┬─────────────────────┘
         │ Retorna todos os registros
         ▼
┌──────────────────────────────┐
│ GeminiService.chat()         │
│ (Constrói prompt)            │
└────────┬─────────────────────┘
         │
         ├─ Contexto BD + Pergunta
         ├─ Prompt template
         │
         ▼
┌──────────────────────────────┐
│ Google Gemini API            │
│ (/generateContent)           │
└────────┬─────────────────────┘
         │ HTTP POST
         ▼
┌──────────────────────────────┐
│ IA Gemini                    │
│ (Processa com IA)            │
└────────┬─────────────────────┘
         │ Retorna resposta
         ▼
┌──────────────────────────────┐
│ GeminiService                │
│ (Extrai resposta do JSON)    │
└────────┬─────────────────────┘
         │ String com resposta
         ▼
┌──────────────────────────────┐
│ PerguntasController          │
│ (Formata resposta HTTP)      │
└────────┬─────────────────────┘
         │ {"resposta": "..."}
         ▼
┌─────────────────┐
│  Cliente HTTP   │
└─────────────────┘
```

## 🤝 Contribuindo

Contribuições são bem-vindas! Siga esses passos:

1. **Fork** o projeto
2. Crie uma branch para sua feature: `git checkout -b feature/MinhaFeature`
3. **Commit** suas mudanças: `git commit -m 'Adiciona MinhaFeature'`
4. **Push** para a branch: `git push origin feature/MinhaFeature`
5. Abra um **Pull Request**

## 📄 Licença

Este projeto está licenciado sob a **MIT License**. Veja o arquivo `LICENSE` para detalhes completos.

## 👨‍💻 Autor

**Richard Moraes Souza**

- 📧 Email: seu-email@exemplo.com
- 💼 LinkedIn: [seu-linkedin](https://linkedin.com/in/seu-usuario)
- 🐙 GitHub: [seu-github](https://github.com/seu-usuario)
- 🌐 Portfolio: [seu-portfolio.com](https://seu-portfolio.com)

## 📞 Suporte

Para suporte e dúvidas:

- 💬 Abra uma [Issue](https://github.com/seu-usuario/IAportfolio/issues)
- 📧 Envie um email
- 💭 Crie uma [Discussion](https://github.com/seu-usuario/IAportfolio/discussions)

---

**Última atualização:** Março 2026
**Status:** ✅ Em desenvolvimento ativo
