# 📚 NoteLab

Uma aplicação web moderna para gerenciar cadernos e anotações de aulas. Organize suas notas por matérias, crie anotações detalhadas com formatação rica e tenha acesso a seus estudos de qualquer lugar.

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-21+-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791)

## 🎯 Visão Geral

O Caderno Online é uma plataforma que permite aos estudantes:
-  Criar e gerenciar múltiplos cadernos organizados por matéria
-  Adicionar notas com editor de texto rico
-  Buscar notas em toda a plataforma
-  Categorizar notas com tags
-  Acessar notas em qualquer dispositivo
-  Conta segura com autenticação JWT

## 🔑 Funcionalidades Principais

### Autenticação e Segurança
- ✅ Registro de novo usuário com validação
- ✅ Login com email e senha
- ✅ Autenticação via JWT (JSON Web Token)
- ✅ Refresh token para sessão persistente
- ✅ Proteção contra força bruta
- ✅ Logout seguro

### Gerenciamento de Cadernos
- ✅ Criar cadernos por matéria
- ✅ Listar todos os cadernos do usuário
- ✅ Editar nome e descrição do caderno
- ✅ Deletar cadernos
- ✅ Visualizar detalhes e notas do caderno

### Gerenciamento de Notas
- ✅ Criar notas com editor de texto rico
- ✅ Editar notas existentes
- ✅ Deletar notas
- ✅ Listar notas com filtros e busca
- ✅ Auto-save a cada 30 segundos
- ✅ Adicionar tags/categorias às notas


## 🛠️ Stack Tecnológico
Backend: Java, Spring Boot, Spring Security, PostgreSQL, JWT

Frontend: HTML, CSS


## 📚 Documentação das APIs

### Rotas backend
``` 
**GET /api/cadernos/{cadernoId}/notas** - Listar notas de um caderno

**POST /api/cadernos/{cadernoId}/notas** - Criar nota

**GET /api/notas/{id}** - Obter detalhes da nota

**PUT /api/notas/{id}** - Editar nota

**DELETE /api/notas/{id}** - Deletar nota

```

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor:

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/adicionar-dark-mode`)
3. Commit suas mudanças (`git commit -m 'feat: adiciona modo escuro'`)
4. Push para a branch (`git push origin feature/adicionar-dark-mode`)
5. Abra um Pull Request com descrição das mudanças

## 📝 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 👨‍💻 Autores

**Murilo**
- GitHub: https://github.com/murilosantosb

**Kauana**
- GitHub: https://github.com/kauana-santos

## 📧 Suporte

Para suporte, entre em contato através de:
- Issues do GitHub

---

**Desenvolvido com ❤️ por equipe KM**

## Deploy gratuito no Render

Este projeto usa Spring Boot com Thymeleaf e pode ser publicado como um Render Web Service sem Docker.

### 1. Subir no GitHub

1. Confirme que as alteracoes estao commitadas:
   ```bash
   git status
   git add .
   git commit -m "Configura deploy no Render"
   git push origin main
   ```
2. No GitHub, verifique se a pasta `backend` contem `pom.xml`, `mvnw`, `mvnw.cmd` e `Procfile`.

### 2. Criar Web Service no Render

1. Acesse o Render e escolha **New +** > **Web Service**.
2. Conecte o repositorio do GitHub.
3. Configure:
   - **Root Directory:** `backend`
   - **Environment:** `Java`
   - **Build Command:** `./mvnw clean install`
   - **Start Command:** `java -jar target/backend-0.0.1-SNAPSHOT.jar`

### 3. Variaveis de ambiente

Configure no Render:

```text
DB_URL=jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:6543/postgres
DB_USERNAME=postgres.vhtxdirsjdkqzbaesuup
DB_PASSWORD=senha_do_banco
```

A variavel `PORT` e definida automaticamente pelo Render. Localmente, a aplicacao continua usando `8080`. Para executar fora do Render, defina `DB_PASSWORD` e, se necessario, sobrescreva `DB_URL` e `DB_USERNAME`.

O datasource esta configurado para exigir SSL e usar o pooler transacional do Supabase na porta `6543`.

### 4. Arquivo jar gerado

O Maven gera o jar executavel em:

```text
backend/target/backend-0.0.1-SNAPSHOT.jar
```
