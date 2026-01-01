# Dirty Code Backend

Aplicação Spring Boot para gerenciamento de exemplos, configurada para suportar múltiplos ambientes de banco de dados.

## Como Executar a Aplicação

### 1. Usando H2 (Banco em Memória - Padrão)
Este é o modo padrão, ideal para desenvolvimento rápido e testes. Não requer instalação de banco de dados externo.

**Comando:**
```powershell
./gradlew bootRun
```

**Links Úteis:**
*   **API (Listar Exemplos):** [http://localhost:8080/examples](http://localhost:8080/examples)
*   **Console do H2:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
    *   **JDBC URL:** `jdbc:h2:mem:dirtycode`
    *   **Usuário:** `sa`
    *   **Senha:** (vazio)

---

### 2. Usando PostgreSQL (Profile QA)
Utilizado para simular o ambiente de produção ou QA com um banco de dados persistente.

**Pré-requisitos:**
*   PostgreSQL instalado e rodando localmente.
*   Banco de dados chamado `dirtycode` criado.

**Comando:**
```powershell
./gradlew bootRun -Dspring.profiles.active=qa
```

**Links Úteis:**
*   **API (Listar Exemplos):** [http://localhost:8080/examples](http://localhost:8080/examples)

---

## Principais Endpoints (CRUD)

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/examples` | Lista todos os registros |
| `GET` | `/examples/{id}` | Busca um registro por ID |
| `POST` | `/examples` | Cria um novo registro |
| `PUT` | `/examples/{id}` | Atualiza um registro existente |
| `DELETE` | `/examples/{id}` | Remove um registro |

### Exemplo de criação (CURL):
```bash
curl -X POST http://localhost:8080/examples \
     -H "Content-Type: application/json" \
     -d '{"name": "Exemplo", "description": "Descrição via README"}'
```

## Tecnologias Utilizadas
*   Java 25
*   Spring Boot
*   Spring Data JPA
*   Flyway (Migrações de Banco)
*   H2 / PostgreSQL
*   Lombok
*   Firebase Admin SDK (Autenticação)

---

## Autenticação (Firebase)

A aplicação utiliza o Firebase para autenticação. Os endpoints (exceto `/h2-console`) exigem um **ID Token** válido enviado no cabeçalho `Authorization`.

**Restrição de Domínio:** Atualmente, o sistema aceita apenas autenticação de contas com domínio **@gmail.com**. Tentativas de acesso com outros domínios resultarão em `401 Unauthorized`.

### Como testar um endpoint protegido:

1.  **Obter o ID Token (ou Custom Token):**
    *   **ID Token:** Obtido no frontend após login Google.
    *   **Custom Token:** Pode ser gerado pelo backend (útil quando o backend é o autenticador):
        ```bash
        curl -X POST http://localhost:8080/auth/token/<UID_DO_USUARIO>
        ```
        O `customToken` retornado deve ser usado no frontend com `signInWithCustomToken()`.

2.  **Chamada via CURL:**

```bash
curl -X GET http://localhost:8080/auth/me \
     -H "Authorization: Bearer <SEU_FIREBASE_ID_TOKEN>"
```

> **Como obter a API_KEY:**
> 1. Vá para o [Console do Firebase](https://console.firebase.google.com/).
> 2. Selecione o projeto **DirtyCode The Game**.
> 3. Clique no ícone de engrenagem (Configurações do projeto) no menu lateral esquerdo.
> 4. Na aba **Geral**, você encontrará a **Chave de API da Web**.

> **Nota:** Se você deseja simular um login via REST API para obter um token (necessário `API_KEY` do Firebase):
> ```bash
> curl -X POST "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=[API_KEY]" \
>      -H "Content-Type: application/json" \
>      -d '{"email":"test@example.com","password":"password","returnSecureToken":true}'
> ```