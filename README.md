# CineNet

Sistema de cinema em Java para a disciplina de Programação Orientada a Objetos (C06) do Inatel. O programa funciona no terminal. O perfil **Cliente** consulta a programação e compra ingressos. O perfil **Funcionário** também cadastra filmes, salas e sessões e consulta o total das vendas.

O atendimento é local. Não usa sockets, protocolos de rede nem threads na aplicação.

## Integrantes

| Nome | Matrícula | Curso | Responsabilidade |
|---|---|---|---|
| Lucas Mendoça de Almeida | 791 | GES | Gerenciamento do cinema |
| Gabriel Westin Generoso Pontes | 635 | GES | Bilheteria, persistência em arquivos e testes |
| Leandro Caminha Ramos Pereira | 9825 | GET | Atendimento, verificação de solicitações e integração |

O grupo é formado por três integrantes:

- **Lucas:** gerenciamento do cinema (`Cinema`, `Filme`, `Sala` e `Sessao`).
- **Gabriel:** bilheteria (`Bilheteria`, `Venda`, `Ingresso`, `IngressoInteira`, `IngressoMeia` e `AssentoOcupadoException`), persistência em arquivos (`ArquivoCinema` e `ArquivoTexto`) e testes.
- **Leandro:** atendimento e verificação de solicitações (`Atendimento`, `AtendimentoBase`, `MenuCliente`, `MenuFuncionario`, `ControladorAtendimento`, `ValidadorEntrada` e `MensagensSistema`), `Main` e a integração dos módulos com `ArquivoServidor`.

## Como compilar e executar

É necessário um **JDK 11 ou superior**. Execute os comandos na pasta que contém este README, pois o programa usa a pasta local `dados/`.

**Linux e macOS**

```bash
mkdir -p out
find src -name '*.java' | sort > out/fontes.txt
javac --release 11 -encoding UTF-8 -d out @out/fontes.txt
java -Dfile.encoding=UTF-8 -cp out br.inatel.projetocinema.main.Main
```

**Windows (PowerShell)**

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$fontes = @(Get-ChildItem src -Recurse -Filter *.java |
    ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' })
[System.IO.File]::WriteAllLines((Join-Path (Get-Location) 'out/fontes.txt'), $fontes, [System.Text.UTF8Encoding]::new($false))
javac --release 11 -encoding UTF-8 -d out '@out/fontes.txt'
java '-Dfile.encoding=UTF-8' -cp out br.inatel.projetocinema.main.Main
```

Em uma IDE, abra a pasta do projeto, configure `src` como raiz de código-fonte e rode `br.inatel.projetocinema.main.Main` com a pasta do projeto como diretório de execução.

## Roteiro de demonstração

1. Inicie o programa e escolha **Funcionário**.
2. Use a opção **2** para ver a programação ou as opções **5–7** para cadastrar filme, sala e sessão. O cadastro de sessão pede data e hora no formato `DD/MM/AAAA HH:mm`. A mesma sala não pode ter sessões com horários sobrepostos.
3. Use a opção **3**, escolha o código da sessão e um assento livre. A inteira custa **R$ 30,00** e a meia **R$ 15,00**. Tente comprar o mesmo assento na mesma sessão para demonstrar o tratamento da exceção.
4. Use a opção **4** para listar vendas e ver o total. Encerre e abra novamente para verificar que as vendas e os assentos ocupados foram recuperados.

O perfil Funcionário é uma opção de menu, sem autenticação. Não há pagamento real. A demonstração considera uma instância do programa por pasta de dados.

## Onde estão os conceitos de POO

| Conceito | Onde aparece |
|---|---|
| Classes, construtores e encapsulamento | `Filme`, `Sala`, `Sessao`, `Venda` e `Cinema`. Os objetos são inicializados pelos construtores, e os atributos não ficam expostos. |
| Classe abstrata, herança e sobrescrita | `Ingresso` exige `calcularPreco()`, implementado de forma diferente em `IngressoInteira` e `IngressoMeia`. |
| Polimorfismo | `Venda.getValor()` chama `calcularPreco()` por uma referência `Ingresso`. O controlador usa `Atendimento` com `MenuCliente` ou `MenuFuncionario`. |
| Agregação e composição | `ControladorAtendimento` recebe `Cinema`, `Bilheteria`, `Atendimento` e `ArquivoServidor` criados no `Main` (agregação). `ArquivoServidor` cria e guarda três `ArquivoCinema` (composição). |
| Interface e classe abstrata | `AtendimentoBase` implementa `Atendimento` e compartilha a leitura de entradas entre os menus. |
| Arrays e coleções | `Sessao` usa `boolean[]` para assentos. `Cinema` e `Bilheteria` usam `ArrayList`. A carga das vendas usa `HashSet` para detectar duplicatas. |
| Tratamento de erros | `AssentoOcupadoException` é lançada pela bilheteria e tratada no controlador. A inicialização bloqueia o atendimento se a leitura dos arquivos falhar. |
| Java NIO | `ArquivoCinema` lê e escreve os registros. `ArquivoTexto` usa `Path` e `Files` com UTF-8 e grava primeiro em arquivo temporário. |

## Persistência e divisão dos módulos

- **Gerenciamento do cinema (Lucas):** `Cinema`, `Filme`, `Sala` e `Sessao`. O código da sessão é salvo no arquivo, independentemente da posição da linha.
- **Bilheteria e arquivos (Gabriel):** `Bilheteria`, `Venda`, `Ingresso`, suas subclasses, `AssentoOcupadoException` e `ArquivoCinema`, com o auxiliar `ArquivoTexto`. A compra do menu só é confirmada após a gravação. As vendas são carregadas depois do catálogo e restauram os assentos ocupados.
- **Atendimento e integração (Leandro):** `Atendimento`, `AtendimentoBase`, os menus, `ControladorAtendimento`, `ValidadorEntrada`, `MensagensSistema`, `Main` e `ArquivoServidor`. Apesar do nome, `ArquivoServidor` apenas coordena arquivos locais.

Os arquivos de catálogo são incluídos para demonstração e podem ser alterados pelo menu de funcionário. `dados/vendas.txt` é criado na primeira compra e não é versionado. Não execute duas instâncias sobre a mesma pasta de dados.

| Arquivo | Campos |
|---|---|
| `filmes.txt` | `id;titulo;genero;duracao;classificacao` |
| `salas.txt` | `numero;capacidade` |
| `sessoes.txt` | `id;idFilme;numeroSala;dataHoraISO` |
| `vendas.txt` | `idVenda\|nomeCliente\|idSessao\|assento\|tipo\|precoBase` |

**Atenção ao atualizar outra cópia do projeto.** Os formatos antigos de filmes e sessões não têm IDs e não são lidos automaticamente. Os arquivos de exemplo deste repositório já foram convertidos. Se vocês tiverem outros cadastros ou vendas anteriores, guardem uma cópia dos arquivos e migrem os cadastros mantendo a correspondência dos códigos das sessões antes de reaproveitar as vendas.

## UML

O [diagrama de classes completo](docs/UML-completo-final.svg) mostra todas as classes do projeto, com atributos, métodos, visibilidade e tipos. Há também a versão em [PNG](docs/UML-completo-final.png) e o código-fonte do desenho em [`UML-completo-final.dot`](docs/UML-completo-final.dot), usado para gerar as imagens. O diagrama foi gerado a partir do código-fonte final.

Está organizado em camadas, com `Main` no topo. As cores indicam o módulo: roxo para o atendimento, azul para a bilheteria e os arquivos, e verde para o catálogo.

Legenda: `+` indica público, `-` privado e `#` protegido. Membros estáticos aparecem sublinhados, operações abstratas em itálico e `{readOnly}` indica campo Java `final`. O triângulo vazio com linha contínua indica herança, e com linha tracejada indica realização de interface. O losango vazio indica agregação, e o losango cheio indica composição. A seta aberta tracejada indica dependência. O texto na linha é o nome do atributo que guarda a referência, e `0..*` indica que a classe guarda vários objetos. Sem número, é um só. A caixa `«referência»` é um tipo da biblioteca padrão.

## Testes

Com o código compilado em `out/`, execute `python3 tests/test_cinenet.py` (ou `python tests/test_cinenet.py` no Windows). Os testes usam pastas temporárias. Verificam os menus, os dois preços, a proteção de assentos, a recarga após reiniciar, a reordenação de sessões, a preservação de arquivos inválidos, falhas de gravação, validação de entradas e persistência dos cadastros.

## Uso de IA

Declaramos o uso de ferramentas de IA generativa neste projeto.

**Lucas Mendoça de Almeida.** Foi usado o **ChatGPT (OpenAI)** para tirar dúvidas sobre listas e relações entre classes em Java e para obter dicas de integração entre as classes de gerenciamento do cinema e as demais classes do projeto, desenvolvidas pelos colegas.

**Gabriel Westin Generoso Pontes.** Foi usado **ChatGPT (OpenAI)** para apoiar o planejamento, a implementação inicial da bilheteria e da persistência, a revisão da versão integrada, as correções de código, os testes de integração (`tests/test_cinenet.py`) e a atualização deste README e do diagrama UML.

**Leandro Caminha Ramos Pereira.** Foi usado o **Claude (Anthropic)** para apoiar na explicação de conceitos de POO como exception, métodos e recursos de Java como trim(), contains(), split(), Integer.parseInt() e de Java NIO, a geração do diagrama UML completo a partir do 
código-fonte final, redação das partes deste README e sobre os integrantes.

O código foi compilado, executado e testado pelos integrantes antes da entrega.