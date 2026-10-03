package br.com.arch.toolkit.sample.design

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import br.com.arch.toolkit.sample.core.model.AppLanguage

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ENGLISH }

enum class AppText(val english: String, val portuguese: String) {
    GITHUB("GitHub", "GitHub"),
    REPOSITORIES("Repositories", "Repositórios"),
    GITHUB_INTRO(
        "Explore projects and see the Toolkit in action.",
        "Explore projetos e veja o Toolkit em ação."
    ),
    TOOLKIT_INTRO(
        "Run an operation, inspect its result and explore the code.",
        "Execute uma operação, veja o resultado e explore o código."
    ),
    DESIGN_INTRO(
        "One visual language shared by every screen.",
        "Uma linguagem visual compartilhada entre todas as telas."
    ),
    SETTINGS_INTRO(
        "Make your workspace comfortable to read.",
        "Ajuste o ambiente para uma leitura confortável."
    ),
    APPEARANCE("Appearance", "Aparência"),
    SHOW_CODE("View executed code", "Ver código executado"),
    HIDE_CODE("Hide executed code", "Ocultar código executado"),
    SHOW_HISTORY("View recent repositories", "Ver repositórios recentes"),
    HIDE_HISTORY("Hide recent repositories", "Ocultar repositórios recentes"),
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
    SPLINTER_ONESHOT(
        "OneShot emits loading snapshots, then success or failure. Run again to retry. Cancel returns to Ready.",
        "OneShot emite snapshots de carregamento e termina com sucesso ou falha. Execute de novo para tentar novamente. Cancelar volta a Pronto."
    ),
    SPLINTER_POLLING(
        "Polling repeats a local task until step 3. Cancel stops the loop; run again to restart.",
        "Polling repete uma tarefa local até a etapa 3. Cancelar interrompe o loop; execute de novo para reiniciar."
    ),
    RUN_TASK("Run task", "Executar tarefa"),
    SIMULATE_FAILURE("Simulate failure", "Simular falha"),
    START_POLLING("Start polling", "Iniciar polling"),
    CANCEL_TASK("Cancel", "Cancelar"),
    READY("Ready", "Pronto"),
    SUCCEEDED("Success", "Sucesso"),
    FAILED("Failure", "Falha"),
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
    ECOSYSTEM("Arch ecosystem", "Ecossistema Arch"),
    ECOSYSTEM_INTRO(
        "Find the library for your task. Each repository can be used independently.",
        "Encontre a biblioteca para sua tarefa. Cada repositório pode ser usado de forma independente."
    ),
    TOOLKIT_REPOSITORY_DESCRIPTION(
        "The ecosystem hub: examples, shared guides and Splinter for asynchronous operations.",
        "O ponto de encontro do ecossistema: exemplos, guias compartilhados e Splinter para operações assíncronas."
    ),
    TOOLKIT_REPOSITORY_USAGE(
        "Use Splinter for requests with polling, cache or execution policies.",
        "Use Splinter em requisições com polling, cache ou políticas de execução."
    ),
    ANDROID_REPOSITORY_DESCRIPTION(
        "Android helpers for lifecycle, context, views and lists.",
        "Utilitários Android para ciclo de vida, contexto, views e listas."
    ),
    ANDROID_REPOSITORY_USAGE(
        "Use it for Android-specific integration in your app.",
        "Use nas integrações específicas de Android do seu app."
    ),
    OBSERVER_REPOSITORY_DESCRIPTION(
        "Represents loading, data and errors, with Flow and Compose observation.",
        "Representa carregamento, dados e erros, com observação em Flow e Compose."
    ),
    OBSERVER_REPOSITORY_USAGE(
        "Use it to observe operation results and restore UI state.",
        "Use para observar resultados de operações e restaurar o estado da UI."
    ),
    LUMBER_REPOSITORY_DESCRIPTION(
        "Logging with tags and custom output destinations across platforms.",
        "Logs com tags e destinos de saída personalizados entre plataformas."
    ),
    LUMBER_REPOSITORY_USAGE(
        "Use it for diagnostics; add an Oak to send logs to your own destination.",
        "Use no diagnóstico; adicione um Oak para enviar logs ao seu próprio destino."
    ),
    STORAGE_REPOSITORY_DESCRIPTION(
        "Typed preferences with observable values, in memory or persisted with DataStore.",
        "Preferências tipadas com valores observáveis, em memória ou persistidos com DataStore."
    ),
    STORAGE_REPOSITORY_USAGE(
        "Use it for settings and small values. Web supports the memory provider.",
        "Use para configurações e pequenos valores. Na web, use o provider de memória."
    ),
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
