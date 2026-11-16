# CineNet

Sistema de cinema em Java para a disciplina de Programação Orientada a Objetos (C06) do Inatel. O programa funciona no terminal. O perfil **Cliente** consulta a programação e compra ingressos. O perfil **Funcionário** também cadastra filmes, salas e sessões e consulta o total das vendas.

O atendimento é local. Não usa sockets, protocolos de rede nem threads na aplicação.

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
| Interface e classe abstrata | `AtendimentoBase` implementa `Atendimento` e compartilha a leitura de entradas entre os menus. |
| Arrays e coleções | `Sessao` usa `boolean[]` para assentos. `Cinema` e `Bilheteria` usam `ArrayList`. A carga das vendas usa `HashSet` para detectar duplicatas. |
| Tratamento de erros | `AssentoOcupadoException` é lançada pela bilheteria e tratada no controlador. A inicialização bloqueia o atendimento se a leitura dos arquivos falhar. |
| Java NIO | `ArquivoCinema` lê e escreve os registros. `ArquivoTexto` usa `Path` e `Files` com UTF-8 e grava primeiro em arquivo temporário. |

## Persistência e divisão dos módulos

- **Gerenciamento do cinema:** `Cinema`, `Filme`, `Sala` e `Sessao`. O código da sessão é salvo no arquivo, independentemente da posição da linha.
- **Bilheteria e arquivos:** `Bilheteria`, `Venda`, `Ingresso`, suas subclasses, `AssentoOcupadoException` e `ArquivoCinema`, com o auxiliar `ArquivoTexto`. A compra do menu só é confirmada após a gravação. As vendas são carregadas depois do catálogo e restauram os assentos ocupados.
- **Atendimento e integração:** `Atendimento`, `AtendimentoBase`, os menus, `ControladorAtendimento`, `ValidadorEntrada`, `MensagensSistema`, `Main` e `ArquivoServidor`. Apesar do nome, `ArquivoServidor` apenas coordena arquivos locais.

Os arquivos de catálogo são incluídos para demonstração e podem ser alterados pelo menu de funcionário. `dados/vendas.txt` é criado na primeira compra e não é versionado. Não execute duas instâncias sobre a mesma pasta de dados.

| Arquivo | Campos |
|---|---|
| `filmes.txt` | `id;titulo;genero;duracao;classificacao` |
| `salas.txt` | `numero;capacidade` |
| `sessoes.txt` | `id;idFilme;numeroSala;dataHoraISO` |
| `vendas.txt` | `idVenda\|nomeCliente\|idSessao\|assento\|tipo\|precoBase` |

**Atenção ao atualizar outra cópia do projeto.** Os formatos antigos de filmes e sessões não têm IDs e não são lidos automaticamente. Os arquivos de exemplo deste repositório já foram convertidos. Se vocês tiverem outros cadastros ou vendas anteriores, guardem uma cópia dos arquivos e migrem os cadastros mantendo a correspondência dos códigos das sessões antes de reaproveitar as vendas.

## UML

O [diagrama completo](docs/UML-completo.svg) contém todas as classes e os membros declarados. O [diagrama geral](docs/UML-CineNet.svg) resume as relações principais para projetar na apresentação. Para consultar os membros com mais facilidade, use os três detalhamentos:

- [Catálogo e sessões](docs/UML-catalogo.svg)
- [Bilheteria e arquivos](docs/UML-bilheteria.svg)
- [Atendimento e integração](docs/UML-atendimento.svg)

Nos diagramas, `+` indica público, `-` privado, `#` protegido e `~` acesso de pacote. Membros estáticos aparecem sublinhados, operações abstratas em itálico e `{readOnly}` indica campo Java `final`. O triângulo vazio com linha contínua indica herança, e com linha tracejada indica realização de interface. As outras linhas tracejadas indicam dependência. O losango preenchido indica composição. Caixas marcadas como `«referência»` têm seu detalhamento em outro módulo ou são tipos da biblioteca padrão.

## Testes

Com o código compilado em `out/`, execute `python3 tests/test_cinenet.py` (ou `python tests/test_cinenet.py` no Windows). Os testes usam pastas temporárias. Verificam os menus, os dois preços, a proteção de assentos, a recarga após reiniciar, a reordenação de sessões, a preservação de arquivos inválidos, falhas de gravação, validação de entradas e persistência dos cadastros.

## Uso de IA

Foi usado **ChatGPT (OpenAI)** para apoiar o planejamento, a implementação inicial da bilheteria e da persistência, a revisão da versão integrada, as correções de código, os testes de integração e a atualização deste README e do diagrama UML.
