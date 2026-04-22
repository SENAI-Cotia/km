# 📚 NoteLab

Uma aplicação web moderna para gerenciar cadernos e anotações de aulas. Organize suas notas por matérias, crie anotações detalhadas com formatação rica e tenha acesso a seus estudos de qualquer lugar.

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-21+-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![React](https://img.shields.io/badge/React-19+-61DAFB?logo=react)
![MySQL](https://img.shields.io/badge/MYSQL-336791)

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

Frontend: React, React Router, Axios, Context API


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
