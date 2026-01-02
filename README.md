# Dirty Code - Backend

Este projeto é o backend para o jogo "Dirty Code", desenvolvido com Spring Boot. Ele gerencia autenticação, usuários e integrações com Firebase e Google Cloud.

## 🗄️ Bancos de Dados

O projeto utiliza dois tipos principais de armazenamento:

### 1. Banco de Dados Relacional (SQL)

O sistema suporta dois perfis de banco de dados, configurados via perfis do Spring:

#### A. H2 Database (Desenvolvimento Local)
- **Arquivo**: `application.yml` (Perfil padrão)
- **Tipo**: Banco de dados em memória (modo PostgreSQL).
- **Utilização**: Ideal para desenvolvimento rápido e testes locais.
- **Console**: Acessível em `/dirty-code/h2-console`.
- **Configuração**:
  - **URL**: `jdbc:h2:mem:dirtycode`
  - **Username**: `sa`
  - **Password**: (vazio)

#### B. PostgreSQL (QA/Produção)
- **Arquivo**: `application-qa.yml` (Ativado com `-Dspring.profiles.active=qa`)
- **Tipo**: Banco de dados relacional persistente.
- **Utilização**: Ambiente de homologação e testes integrados.
- **Configuração padrão**:
  - **URL**: `jdbc:postgresql://localhost:5432/dirtycode`
  - **Username**: `root`
  - **Password**: `root`

> **Nota**: As migrações de schema para ambos os bancos são gerenciadas automaticamente pelo Flyway (diretório `src/main/resources/db/migration`).

### 2. Firebase (NoSQL/Auth)
- **Utilização**: Gerenciamento de autenticação e tokens.
- **Integração**: Utiliza o Firebase Admin SDK para validar tokens e criar tokens customizados.

---

## 🚀 Endpoints

A URL base para todos os endpoints é: `http://localhost:8080/dirty-code`

### 🔑 Autenticação (Públicos)

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/v1/gmail/auth-page` | Redireciona para a página de login do Google. |
| `GET` | `/v1/gmail/call-back` | Callback do Google OAuth2. Recebe o parâmetro `code`. |
| `POST` | `/auth/token/{uid}` | Gera um token customizado do Firebase para um UID específico. |

---

## 🛠️ Como Utilizar

### 1. Autenticação
A maioria dos endpoints requer um token de autenticação do Firebase no cabeçalho da requisição:

```http
Authorization: Bearer <seu_firebase_token>
```

Para obter um token em desenvolvimento:
1. Acesse `/v1/gmail/auth-page`.
2. Após o login, você receberá um código que será processado pelo `/v1/gmail/call-back`.

### 2. Cabeçalhos (Headers)
Para requisições `POST` e `PUT`, certifique-se de enviar o cabeçalho:
```http
Content-Type: application/json
```

### 3. Requisitos
- **Java 25**
- Variáveis de ambiente configuradas (ver `application.yml` para as chaves do Firebase e GCP necessárias).

---

## 🏗️ Estrutura de Pastas Principal
- `controller/`: Camada de exposição da API.
- `service/`: Regras de negócio.
- `repository/`: Acesso aos dados (JPA).
- `dto/`: Objetos de transferência de dados.
- `config/`: Configurações de segurança e beans do sistema.
