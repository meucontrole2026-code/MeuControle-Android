# 💰 MeuControle - Gestão Financeira Inteligente

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
   * Botão de alternância para visibilidade de senha e integração direta com o Firebase Auth.
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

   Abre o Android Studio e seleciona Open an Existing Project.

Nota de Segurança: Como o ficheiro de configuração do Firebase (google-services.json) é ignorado pelo Git por motivos de segurança, deves colocar o teu próprio ficheiro google-services.json (descarregado da Consola do Firebase) dentro da pasta app/ do projeto.

Clica em Sync Project with Gradle Files e faz um Rebuild Project.

Executa num emulador ou dispositivo físico com Android 7.0 (API 24) ou superior.

### 👥 Equipa de Desenvolvimento & Autoria
Projeto desenvolvido no âmbito académico do curso de Análise e Desenvolvimento de Sistemas (ADS) da Universidade Cidade de São Paulo (UNICID)
