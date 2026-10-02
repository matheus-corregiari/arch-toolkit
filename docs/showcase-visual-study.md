# Novo estudo visual do Showcase

Proposta de 02/10/2026, baseada no app em `ad6d408`. Este documento substitui a
direção de projeto visual para a próxima implementação; o app atual permanece
como referência de comportamento. A proposta ainda não foi aplicada ao Kotlin.

[Abrir o protótipo interativo](assets/showcase-next.html) · [Plano de refactor](showcase-refactor.md)

## Objetivo e identidade

Um laboratório de desenvolvimento claro, confortável e fácil de explorar.
Manter amarelo `#FFCE2E`, tinta escura, família tipográfica atual, quatro destinos,
idiomas e funcionalidades. Recomeçar a organização das telas pela tarefa do
usuário, em vez de adaptar o mesmo cartão a todos os conteúdos.

Princípios: uma ação principal por grupo; informação importante primeiro; espaço
de leitura previsível; controles Material/DS consistentes; feedback junto à ação.
Centralização **apenas horizontal**. Todas as páginas começam no topo e usam a
altura disponível. Não criar hero, carrossel ou tela inicial adicional.

## Leitura do visual atual

Inspeção de `assets/showcase-responsive.png` e dos componentes reais:

| Evidência | Consequência visual | Nova decisão |
| --- | --- | --- |
| `ScreenTitle` usa TopAppBar; cada grupo repete título/cartão | Cabeçalho e pequenos controles consomem bastante altura em landscape | Cabeçalho de página compacto, com título e contexto; não aninhar barras |
| `AppSection` dá a mesma superfície a demos, controles e catálogos | Elementos de importância diferente recebem peso parecido | Separar painel de tarefa, lista de conteúdo e informações auxiliares |
| Busca ocupa painel lateral e histórico uma LazyRow | Resultado fica separado do contexto de busca; nomes longos competem na lateral | Busca no cabeçalho do conteúdo; filtros/histórico como contexto secundário |
| Demo mostra ações, resultado e código completo ao mesmo tempo | Código longo domina a primeira leitura | Ordem: controles → resultado → fonte expansível |
| Settings usa uma grade de cartões curtos | Preferências relacionadas ficam espalhadas | Painel contínuo de preferências com divisores e grupos semânticos |
| `AppPage` combina largura percentual, mínimo da grade e teto global | Política de layout depende da necessidade de um tipo específico de filho | Separar largura de página, número de colunas e proporção de painéis |

São avaliações de hierarquia e propostas de design, não bugs ou resultados de
pesquisa com usuários. Não há medição de tempo de tarefa ou desempenho nesta etapa.

## Composição por destino

**GitHub:** título “Repositórios”, contexto curto, busca com ação e filtro de
linguagem. Resultado em uma lista com proprietário/nome, descrição, tópicos e
metadados nesta ordem. Histórico compacto em disclosure. Detalhe mantém a mesma
identidade, com retorno visível e ação “Abrir no GitHub”. Nenhum feed em duas
colunas: nomes e descrições precisam de uma sequência estável de leitura.

**Toolkit:** duas demos independentes quando houver largura; uma coluna em
janelas menores ou fonte ampliada. Cada demo tem descrição curta, entradas,
ação principal, ações secundárias e resultado com estado explícito. Fonte
recolhida por padrão, acessível por “Ver código”; conteúdo gerado continua vindo
da implementação executável. Catálogo do ecossistema abaixo das demos.

**Design:** catálogo organizado em fundamentos, controles e estados. Exemplos
mostram o componente real e seu papel; nomes legíveis substituem siglas isoladas.
Amarelo aparece como destaque, não como fundo de todos os blocos. Estados de
erro/sucesso são sempre acompanhados de texto e ícone.

**Configurações:** painel único, com grupos Aparência e Idioma. Tema e contraste
ficam próximos, com preview contextual e rótulos de seleção. Não depende apenas
de swatches. Mudança de idioma preserva foco, destino e formulário.

## Layout adaptável

As medidas abaixo são metas da proposta, não os valores já implementados.
A área disponível é medida **depois** de navegação e safe insets. A orientação
não determina sozinha o modo. Fonte ampliada participa do cálculo das colunas.

| Papel | Regra proposta |
| --- | --- |
| Página | `fillMaxWidth` dentro de teto por conteúdo; `fillMaxHeight`; alinhamento TopCenter |
| Margem lateral | 16 dp compacta, 24 dp média, 32 dp ampla; margens externas simétricas |
| Área de trabalho | até 1120 dp em GitHub/Toolkit/Design |
| Leitura/formulário | até 760 dp em detalhe/Configurações |
| Demos | uma ou duas colunas; mínimo 360 dp × max(1, fontScale), mais gap de 24 dp |
| Busca | largura do conteúdo; filtro pode quebrar linha; histórico recolhível |
| Altura curta | cabeçalho enxuto, controles quebram linha e conteúdo rola; teclado não cobre ação |
| Navegação | barra/rail/drawer adaptáveis já existentes; drawer apenas em janela realmente ampla |

Não aumentar artificialmente a largura da página para forçar duas colunas.
Configurações não ganha uma segunda coluna só porque há espaço sobrando.
Longos textos de detalhe podem ter medida de leitura menor dentro do painel.

```text
Compacta                Ampla: trabalho              Ampla: preferências
┌─────────────────┐    ┌─────┬───────────────────┐    ┌─────┬───────────────────┐
│ Título/contexto │    │ nav │  Título/contexto  │    │ nav │   Título/contexto │
│ Busca/controles │    │     │  Busca / filtros  │    │     │   Tema/contraste  │
│ Conteúdo        │    │     │  Lista ou 2 demos │    │     │   Idioma          │
│ Conteúdo ↓      │    │     │  Conteúdo ↓       │    │     │                   │
│ navegação       │    │     │                   │    │     │                   │
└─────────────────┘    └─────┴───────────────────┘    └─────┴───────────────────┘
```

## Tipografia, densidade e componentes

Preservar a família atual. Mapear papéis semânticos antes de alterar nomes antigos.

| Papel | Compacta | Média/ampla |
| --- | --- | --- |
| Título da página | 24/30 sp, semibold | 28/34 sp, semibold |
| Título de seção | 18/24 sp | 20/26 sp |
| Corpo/entrada | 16/24 sp | 16/24 sp |
| Metadados | 13/18 sp | 14/20 sp |
| Código | 13/20 sp, monoespaçado | 14/22 sp, monoespaçado |

Estes valores são ponto de partida para previews de fonte 1×/1,3×/2×. Evitar
escalar todos os textos pelo tamanho da tela: corpo estável, títulos com ajuste
moderado. Nunca substituir a escala do sistema. O protótipo usa fontes de sistema
por ser um estudo HTML; a implementação Compose mantém a família do app.

Base de espaço 4 dp; gaps usuais 8/16/24/32. Raio de painéis 16 dp e campos 12 dp.
Alvo interativo mínimo 48 dp; foco com indicador visível e seleção com semântica.
Botão amarelo só para a ação principal. Secundário com contorno; destrutivo com
rótulo claro e papel de erro. Evitar cartões dentro de cartões e sombras decorativas.

Contratos mínimos do DS: PageHeader, ContentFrame, Section, ActionButton,
TextField, ChoiceGroup, InlineFeedback, CodeDisclosure e RepositoryRow.
Reutilizar/adaptar os componentes atuais; os nomes aqui descrevem papéis, não
obrigam criar nove novas abstrações.

## Light/dark e três níveis de contraste

Redesenhar as superfícies mantendo o amarelo e a tinta da marca. Dark usa neutros
mais equilibrados; os níveis maiores fortalecem texto, bordas e seleção juntos.
Padrão é confortável; Médio dá mais definição; Alto maximiza a separação visual.
Os três precisam ser utilizáveis: Padrão não é uma versão inacessível.

| Modo | Fundo / superfície | Título / corpo | Controle / borda | Seleção / tinta | Corpo / superfície | Controle / superfície |
| --- | --- | --- | --- | --- | ---: | ---: |
| Light padrão | `#F6F6F3` / `#FFFFFF` | `#242424` / `#555550` | `#85857C` / `#DADAD2` | `#FFF0B3` / `#695000` | 7.50:1 | 3.72:1 |
| Light médio | `#F2F2ED` / `#FFFFFF` | `#181816` / `#3F3F3A` | `#66665D` / `#B2B2A7` | `#FFE482` / `#594200` | 10.59:1 | 5.80:1 |
| Light alto | `#FFFFFF` / `#FFFFFF` | `#121210` / `#242420` | `#48483F` / `#707066` | `#FFCE2E` / `#242424` | 15.58:1 | 9.23:1 |
| Dark padrão | `#151515` / `#222222` | `#F7F7F2` / `#C0C0B6` | `#909084` / `#55554C` | `#3C3315` / `#FFDF73` | 8.68:1 | 4.93:1 |
| Dark médio | `#101010` / `#1E1E1C` | `#FAFAF5` / `#D5D5C9` | `#ACAC9C` / `#777766` | `#493A0D` / `#FFE58E` | 11.28:1 | 7.26:1 |
| Dark alto | `#080808` / `#161614` | `#FFFFFF` / `#F1F1E6` | `#D0D0B8` / `#A8A890` | `#FFCE2E` / `#242424` | 15.93:1 | 11.56:1 |


Os números são calculados por luminância sRGB de cores opacas, arredondados para
duas casas. “Controle” é o contorno contra a superfície do campo; borda decorativa
de cartão pode ser mais suave e não identifica sozinha uma ação. Amarelo com
tinta `#242424` tem contraste 10.44:1. Link, erro e sucesso devem
ser revalidados contra fundo **e** superfície antes da migração dos 48 papéis
Material. Não declarar conformidade global a partir desta tabela.

Metas: texto normal ≥ 4,5:1; texto grande ≥ 3:1; componentes/foco necessários ≥ 3:1.
Alto mira ≥ 7:1 para corpo. Testar também hover, pressed, selected, disabled,
inputs com erro, foco e navegação. Status não depende só da cor. Rótulos em ambos
os idiomas; TalkBack/VoiceOver, teclado e aumento de fonte são gates humanos.

## Protótipo e limites

O [protótipo](assets/showcase-next.html) permite trocar quatro destinos, seis
paletas, tamanho da janela, fonte e estados de resultado. As operações são
simuladas; não chama GitHub nem persiste dados. “Buscar”, “Carregar mais”, demos
e disclosure servem para estudar a hierarquia. Não substitui testes do app nativo,
suas fontes, navegação, teclado, performance ou acessibilidade da plataforma.

## Aceitação da implementação futura

- Todas as páginas alinhadas ao topo; centro horizontal na área útil.
- 360×800, 800×360, 800×1280, 1280×800 e 1600×1000; resize contínuo sem perda de campos.
- Fonte 1×/1,3×/2×, nomes longos, EN/PT-BR e light/dark × três contrastes.
- Busca vazia, loading inicial, lista, erro inicial, loading/erro de paginação e retry.
- Demos: validação, operação pendente, sucesso/erro, clear, delete, fonte expandida.
- Foco/ordem de leitura, navegação back/deep link e persistência mantidos.
- Aprovar visualmente cada referência alterada; manter os thresholds dos screenshots.

## Referências

Direção de projeto baseada no contexto deste app e em
[layouts adaptáveis](https://developer.android.com/develop/ui/compose/layouts/adaptive/support-different-display-sizes).
Metas de legibilidade: [WCAG contraste de texto](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum.html)
e [contraste de componentes](https://www.w3.org/WAI/WCAG22/Understanding/non-text-contrast.html).
