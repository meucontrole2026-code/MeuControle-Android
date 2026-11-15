💰 MeuControle - Gestão Financeira Inteligente

O **MeuControle** é uma aplicação nativa para Android desenvolvida em Java, focada em ajudar os utilizadores a gerir as suas finanças pessoais de forma simples, segura e automatizada. O projeto apresenta uma identidade visual moderna baseada no **Azul Royal (`#1E63F5`)** e integra serviços em nuvem e inteligência artificial.

---

## 🚀 Tecnologias e Arquitetura

O projeto foi construído seguindo boas práticas de desenvolvimento mobile:
* **Linguagem:** Java (Android SDK)
* **Interface (UI):** Material Design Components, XML Layouts e ConstraintLayout (Tema *Royal Blue*)
* **Autenticação:** Firebase Authentication (Registo, Login e Gestão de Sessão)
* **Base de Dados Local:** Room Database (para persistência de transações e extratos)
* **Inteligência Artificial & OCR:** Google ML Kit Text Recognition (para leitura automática de talões e recibos)
* **Gráficos e Relatórios:** MPAndroidChart (Gráficos de pizza e análises visuais de despesas)

---

## 📱 Funcionalidades Principais

1. **Autenticação Segura:**
   * Validação de e-mails e senhas fortes com regras em Regex.
   * Botão de alternância para visibilidade de senha (ícone de olho) e integração direta com o Firebase Auth.
2. **Ecrã Principal & Extrato:**
   * Saudação personalizada com o nome obtido do perfil do utilizador.
   * Listagem dinâmica de receitas e despesas.
3. **Painel de Análises:**
   * Gráficos interativos em pizza para categorização de gastos.
4. **Leitor Inteligente (OCR):**
   * Captura de faturas através da câmara com extração automática de valores por ML Kit.

---

## 🛠️ Como Executar o Projeto Localmente

1. Clona o repositório para a tua máquina:
   ```bash
   git clone [https://github.com/teu-utilizador/MeuControle.git](https://github.com/teu-utilizador/MeuControle.git)
