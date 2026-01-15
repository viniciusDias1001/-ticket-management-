# 🎫 Sistema de Gerenciamento de Tickets
**Teste Prático – Vaga Desenvolvedor Júnior | BBG Telecom**

---

## 📌 SOBRE O PROJETO

Este projeto foi desenvolvido como **teste prático para a vaga de Desenvolvedor Júnior na BBG Telecom**.

Trata-se de uma **API RESTful para gerenciamento de tickets de suporte**, construída com foco em **boas práticas**, **organização de código**, **regras de negócio claras** e **segurança**, utilizando tecnologias modernas do ecossistema Java.

O sistema permite:
- Cadastro e autenticação de usuários
- Criação e gerenciamento de tickets
- Controle de acesso por perfil
- Histórico completo das ações realizadas
- Execução local ou via Docker

O **front-end será desenvolvido em um repositório separado**, utilizando **Angular**, seguindo o conceito de **microserviços (front e back desacoplados)**.

---

## 🎯 OBJETIVO DO TESTE

Demonstrar conhecimentos em:
- Desenvolvimento Back-end com Java e Spring Boot
- Arquitetura REST
- Regras de negócio
- Segurança com JWT
- Versionamento de banco de dados
- Testes unitários
- Organização e clareza de código

---

## 🧠 REGRAS DE NEGÓCIO IMPLEMENTADAS

- Apenas **CLIENT** e **ADMIN** podem criar tickets
- Um **CLIENT** só pode visualizar e alterar seus próprios tickets
- Tickets com status **DONE** não podem ser alterados
- Apenas **TECH** ou **ADMIN** podem:
    - alterar status
    - atribuir tickets
- Apenas **ADMIN** pode excluir tickets
- Toda ação relevante gera um **histórico (audit trail)**

---

## 🏗️ ARQUITETURA

- Arquitetura em camadas:
    - Controller
    - Service
    - Repository
    - DTO / Mapper
- Spring Security com JWT (OAuth2 Resource Server)
- Histórico como sub-recurso REST
- Front-end desacoplado (microserviço separado)

---

## 📘 Documentação interativa da API (Swagger / OpenAPI)

A API possui documentação automatizada e interativa via Swagger UI.

Após subir o projeto, acesse:
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Para testar endpoints protegidos:
1. Faça login em `POST /auth/login`
2. Copie o `accessToken`
3. Clique em **Authorize** no Swagger e cole **apenas o token** (sem escrever `Bearer`)

## 🧪 TESTES

- JUnit 5 + Mockito
- Testes unitários focados em **regras de negócio**
- Cobertura de:
    - permissões por perfil
    - validações de status
    - criação de histórico
    - regras de acesso
- Projeto configurado para **Java 21**

---

### 🧪 Testes de Integração (E2E) com Testcontainers + PostgreSQL

Os testes de integração rodam com **PostgreSQL real** via **Testcontainers**, validando:
- Controllers + validações + exception handler
- Autenticação JWT
- Regras de permissão por perfil (CLIENT/TECH/ADMIN)
- Persistência real com Flyway + JPA

Rodar testes:
```bash
mvn test
```
Obs: é necessário ter o Docker instalado e em execução para o Testcontainers.

## ⚙️ CI com GitHub Actions (build automatizado)

A cada `push` ou `pull request`, o GitHub Actions executa automaticamente:
- `mvn test`

Isso garante que alterações no projeto não quebrem o build e mantém o código sempre validado.

## 🛠️ FERRAMENTAS UTILIZADAS

- IntelliJ IDEA
- Postman
- Docker / Docker Compose
- Maven

---

## ⚙️ TECNOLOGIAS UTILIZADAS

- Java 21
- Spring Boot 3.5.9
- Spring Security
- OAuth2 Resource Server
- JWT
- JPA / Hibernate
- Flyway (Migrations)
- PostgreSQL
- H2 Database (dev)

---

## 📋 PRÉ-REQUISITOS

- Java 21
- Maven 3.8+
- Docker e Docker Compose (opcional)

---
## ▶️ COMO RODAR O PROJETO

### 🔹 Opção 1 — Rodar com Docker (Recomendado)

Esta é a forma mais simples e próxima de um ambiente real de produção.

``` bash  
docker compose up --build
```
Após a execução:

- PostgreSQL será iniciado automaticamente
- Flyway executará as migrations
- A API ficará disponível em:

``` bash  
http://localhost:8080
```

### 🔹 Opção 2 — Rodar localmente (H2 Database)

1️⃣ Clone o repositório:
``` bash  
git clone https://github.com/viniciusDias1001/gerenciamento_de_tickets.git
```
2️⃣ Acesse a pasta do projeto:
``` bash  
cd gerenciamento_de_tickets
```
3️⃣ Instale as dependências e gere o build:
``` bash  
mvn clean install
```
4️⃣ Execute a aplicação:
``` bash  
mvn spring-boot:run
```
📍 Aplicação disponível em:
``` bash  
http://localhost:8080
```
📍 Console do H2:
``` bash  
http://localhost:8080/h2-console
```

## 🔐 USUÁRIOS DE TESTE (SEED)

O projeto já inicia com usuários pré-cadastrados via Flyway (migrations).

| Perfil | Email | Senha |
|------|------|------|
| ADMIN | admin@local.com | Admin@123 |
| REVIEWER | reviewer@bbgtelecom.com | Reviewer@123 |

---

## 📡 ENDPOINTS PRINCIPAIS

### 🔑 Autenticação
- POST `/auth/register`
- POST `/auth/login`

### 👤 Usuários
- GET `/users/myId`
- GET `/users?role=TECH`

### 🎫 Tickets
- POST `/tickets`
- GET `/tickets`
- GET `/tickets/{id}`
- PUT `/tickets/{id}`
- PATCH `/tickets/{id}/status`
- PATCH `/tickets/{id}/assign/{techId}`
- DELETE `/tickets/{id}` (ADMIN)

### 🕒 Histórico
- GET `/tickets/{id}/history`
---

## 🌐 FRONT-END (MICROSSERVIÇO SEPARADO)

O front-end será desenvolvido em **Angular**, em um repositório separado, consumindo esta API via JWT.

> Arquitetura baseada em microserviços, com front-end e back-end desacoplados.

**Repositório do Front-end (Angular):** _https://github.com/viniciusDias1001/gerenciamento_de_tickets-frontend_

---

## 📬 CONTATO

- LinkedIn: https://www.linkedin.com/in/pedro-vinicius-8472351b7/
- Email: pedrorochadias1001@gmail.com

---

### ✅ OBSERVAÇÃO FINAL

Este projeto foi desenvolvido como **teste prático para a vaga de Desenvolvedor Júnior na BBG Telecom**, com foco em:

- Boas práticas de desenvolvimento
- Organização de código
- Segurança
- Clareza das regras de negócio
- Manutenibilidade
