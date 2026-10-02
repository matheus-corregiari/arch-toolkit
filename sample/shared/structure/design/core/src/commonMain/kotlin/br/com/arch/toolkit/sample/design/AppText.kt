package br.com.arch.toolkit.sample.design

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import br.com.arch.toolkit.sample.github.shared.structure.core.model.AppLanguage

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ENGLISH }

enum class AppText(val english: String, val portuguese: String) {
    GITHUB("GitHub", "GitHub"),
    TOOLKIT("Toolkit", "Toolkit"),
    DESIGN("Design", "Design"),
    SETTINGS("Settings", "Configurações"),
    SEARCH("Search repositories", "Buscar repositórios"),
    SEARCH_ACTION("Search", "Buscar"),
    LANGUAGE_FILTER("Programming language", "Linguagem de programação"),
    ALL_LANGUAGES("All", "Todas"),
    RETRY("Try again", "Tentar novamente"),
    LOAD_MORE("Load more", "Carregar mais"),
    EMPTY("No repositories found", "Nenhum repositório encontrado"),
    LOADING("Loading", "Carregando"),
    BACK("Back", "Voltar"),
    CONNECTION_ERROR(
        "Unable to connect. Please try again.",
        "Não foi possível conectar. Tente novamente."
    ),
    RATE_LIMIT_ERROR(
        "GitHub request limit reached. Try again later.",
        "Limite de requisições do GitHub atingido. Tente mais tarde."
    ),
    NOT_FOUND_ERROR("Repository not found.", "Repositório não encontrado."),
    RESPONSE_ERROR(
        "GitHub returned an unexpected response.",
        "O GitHub retornou uma resposta inesperada."
    ),
    OPEN_GITHUB("Open on GitHub", "Abrir no GitHub"),
    DESCRIPTION("Description", "Descrição"),
    NO_DESCRIPTION("No description provided", "Sem descrição"),
    STARS("Stars", "Estrelas"),
    FORKS("Forks", "Forks"),
    UPDATED("Updated", "Atualizado"),
    APP_LANGUAGE("App language", "Idioma do aplicativo"),
    THEME("Theme", "Tema"),
    CONTRAST("Contrast", "Contraste"),
    LIGHT("Light", "Claro"),
    DARK("Dark", "Escuro"),
    SYSTEM("System", "Sistema"),
    STANDARD("Standard", "Padrão"),
    MEDIUM("Medium", "Médio"),
    HIGH("High", "Alto"),
    LOG("Write log", "Gerar log"),
    CLEAR("Clear", "Limpar"),
    KEY("Key", "Chave"),
    VALUE("Value", "Valor"),
    SAVE("Save", "Salvar"),
    READ("Read", "Ler"),
    DELETE("Delete", "Excluir"),
    RESULT("Result", "Resultado"),
    SNIPPET("Executed code", "Código executado"),
    DOCUMENTATION("Documentation", "Documentação"),
    LUMBER_DESCRIPTION(
        "Write logs to a dedicated tree. The latest 100 entries stay visible.",
        "Escreva logs em uma árvore dedicada. As últimas 100 entradas ficam visíveis."
    ),
    STORAGE_DESCRIPTION(
        "Save, read and remove text. Saved values survive restarting the app.",
        "Salve, leia e remova texto. Os valores salvos persistem ao reiniciar o app."
    ),
    MISSING_VALUE("No saved value", "Nenhum valor salvo"),
    SAVED("Saved", "Salvo"),
    DELETED("Deleted", "Excluído"),
    VALID_KEY("Enter a non-empty key", "Informe uma chave não vazia"),
    TOKENS("Shared tokens", "Tokens compartilhados"),
    TYPOGRAPHY("Typography", "Tipografia"),
    COLORS("Colors", "Cores"),
    SPACING("Spacing", "Espaçamento"),
    WIDGETS("Widgets", "Componentes"),
    ENABLED("Enabled", "Habilitado"),
    DISABLED("Disabled", "Desabilitado"),
    SELECTED("Selected", "Selecionado"),
    ARCH_ANDROID_DESCRIPTION(
        "Android lifecycle, context and device helpers.",
        "Utilitários Android para ciclo de vida, contexto e dispositivos."
    ),
    EVENT_OBSERVER_DESCRIPTION(
        "Observe coroutine results with lifecycle and Compose adapters.",
        "Observe resultados de corrotinas com adaptadores de ciclo de vida e Compose."
    ),
    SPLINTER_DESCRIPTION(
        "Model asynchronous loading, data and failure states.",
        "Modele estados assíncronos de carregamento, dados e falhas."
    ),
    STORAGE_ERROR("Unable to access saved data.", "Não foi possível acessar os dados salvos."),
    RECENT("Recently viewed", "Vistos recentemente"),
    THEME_DESCRIPTION(
        "Choose a light, dark or system appearance.",
        "Escolha a aparência clara, escura ou do sistema."
    ),
    CONTRAST_DESCRIPTION(
        "Standard balances reading comfort. Medium strengthens definition. High emphasizes text and controls.",
        "Padrão equilibra o conforto de leitura. Médio reforça a definição. Alto destaca textos e controles."
    ),
    SEARCH_HINT(
        "Try another name or programming language.",
        "Tente outro nome ou linguagem de programação."
    ),
    ECOSYSTEM("Ecosystem", "Ecossistema"),
    BRAND("Brand", "Marca"),
    SURFACE("Surface", "Superfície"),
    TEXT("Text", "Texto"),
    FEEDBACK("Feedback", "Feedback"),
    NAVIGATION("Sections", "Seções"),
    EMPTY_LOGS("No logs yet", "Nenhum log gerado"),
    SETTINGS_TAB("Settings", "Ajustes");

    fun resolve(language: AppLanguage): String = when (language) {
        AppLanguage.ENGLISH -> english
        AppLanguage.PORTUGUESE_BRAZIL -> portuguese
    }
}

@Composable
fun text(value: AppText): String = value.resolve(LocalAppLanguage.current)
