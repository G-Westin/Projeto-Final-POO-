package br.inatel.projetocinema.controladoratendimento;

import br.inatel.projetocinema.arquivoservidor.ArquivoServidor;
import br.inatel.projetocinema.atendimento.Atendimento;
import br.inatel.projetocinema.bilheteria.Bilheteria;
import br.inatel.projetocinema.cinema.Cinema;
import br.inatel.projetocinema.excecao.AssentoOcupadoException;
import br.inatel.projetocinema.filme.Filme;
import br.inatel.projetocinema.mensagenssistema.MensagensSistema;
import br.inatel.projetocinema.sala.Sala;
import br.inatel.projetocinema.sessao.Sessao;
import br.inatel.projetocinema.venda.Venda;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class ControladorAtendimento {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    private final Cinema cinema;
    private final Bilheteria bilheteria;
    private final Atendimento atendimento;
    private final ArquivoServidor arquivos;

    public ControladorAtendimento(Cinema cinema, Bilheteria bilheteria, Atendimento atendimento,
                                  ArquivoServidor arquivos) {
        this.cinema = cinema;
        this.bilheteria = bilheteria;
        this.atendimento = atendimento;
        this.arquivos = arquivos;
    }

    public void executar() {
        do {
            atendimento.exibirMenu();
            int opcao = atendimento.receberOpcao("OPÇÃO: ", 0, atendimento.getTotalOpcoes());
            if (opcao == 0) {
                break;
            }
            encaminharSolicitacao(opcao);
        } while (atendimento.continuarAtendimento());
    }

    private void encaminharSolicitacao(int opcao) {
        System.out.println();
        try {
            switch (opcao) {
                case 1: consultarFilmes(); break;
                case 2: consultarSessoes(); break;
                case 3: comprarIngresso(); break;
                case 4: consultarVendas(); break;
                case 5: cadastrarFilme(); break;
                case 6: cadastrarSala(); break;
                case 7: cadastrarSessao(); break;
                default: MensagensSistema.opcaoInvalida(0, atendimento.getTotalOpcoes());
            }
        } catch (AssentoOcupadoException | IllegalArgumentException | IllegalStateException erro) {
            MensagensSistema.erro(erro.getMessage());
        } catch (DateTimeParseException erro) {
            MensagensSistema.erro("Data ou horário inválido. Use o formato DD/MM/AAAA HH:mm.");
        } catch (IOException erro) {
            MensagensSistema.erro("A operação não foi confirmada porque não foi possível gravar: "
                    + erro.getMessage());
        }
    }

    private void consultarFilmes() {
        if (cinema.getFilmes().isEmpty()) {
            MensagensSistema.listaVazia("filme");
        } else {
            cinema.listarFilmes();
        }
    }

    private void consultarSessoes() {
        if (cinema.getSessoes().isEmpty()) {
            MensagensSistema.listaVazia("sessão");
        } else {
            cinema.listarSessoes();
        }
    }

    private void comprarIngresso() throws AssentoOcupadoException, IOException {
        if (cinema.getSessoes().isEmpty()) {
            MensagensSistema.listaVazia("sessão");
            return;
        }
        cinema.listarSessoes();
        int id = atendimento.receberOpcao("Código da sessão: ", 1, Integer.MAX_VALUE);
        Sessao sessao = cinema.buscarSessao(id);
        sessao.mostraAssentos();
        int assento = atendimento.receberOpcao("Escolha o assento: ", 1, sessao.getSala().getCapacidade());
        int opcaoTipo = atendimento.receberOpcao("Tipo (1 - Inteira / 2 - Meia): ", 1, 2);
        String tipo = opcaoTipo == 1 ? Bilheteria.TIPO_INTEIRA : Bilheteria.TIPO_MEIA;
        String nomeCliente = atendimento.receberTexto("Nome do cliente: ");
        if (nomeCliente == null) {
            cancelarOperacao();
            return;
        }
        Venda venda = bilheteria.comprarIngresso(nomeCliente, sessao, assento, tipo, arquivos.getArquivoVendas());
        MensagensSistema.compraConfirmada(venda);
    }

    private void consultarVendas() {
        if (bilheteria.getVendas().isEmpty()) {
            MensagensSistema.listaVazia("venda");
        } else {
            bilheteria.listarVendas();
            MensagensSistema.totalVendas(bilheteria.calcularTotalVendas());
        }
    }

    private void cadastrarFilme() throws IOException {
        String titulo = atendimento.receberTexto("Título: ");
        if (titulo == null) {
            cancelarOperacao();
            return;
        }
        String genero = atendimento.receberTexto("Gênero: ");
        if (genero == null) {
            cancelarOperacao();
            return;
        }
        int duracao = atendimento.receberOpcao("Duração em minutos: ", 1, Integer.MAX_VALUE);
        String classificacao = atendimento.receberTexto("Classificação indicativa: ");
        if (classificacao == null) {
            cancelarOperacao();
            return;
        }
        Filme filme = new Filme(cinema.proximoIdFilme(), titulo, genero, duracao, classificacao);
        arquivos.cadastrarFilme(cinema, filme);
        System.out.println("Filme cadastrado com sucesso. Código: " + filme.getId());
    }

    private void cadastrarSala() throws IOException {
        int numero = atendimento.receberOpcao("Número da sala: ", 1, Integer.MAX_VALUE);
        int capacidade = atendimento.receberOpcao("Capacidade da sala: ", 1, Sala.CAPACIDADE_MAXIMA);
        arquivos.cadastrarSala(cinema, new Sala(numero, capacidade));
        System.out.println("Sala cadastrada com sucesso.");
    }

    private void cadastrarSessao() throws IOException {
        if (cinema.getFilmes().isEmpty() || cinema.getSalas().isEmpty()) {
            MensagensSistema.erro("Cadastre pelo menos um filme e uma sala antes da sessão.");
            return;
        }
        cinema.listarFilmes();
        int idFilme = atendimento.receberOpcao("Código do filme: ", 1, Integer.MAX_VALUE);
        Filme filme = cinema.buscarFilme(idFilme);
        cinema.listarSalas();
        int numeroSala = atendimento.receberOpcao("Número da sala: ", 1, Integer.MAX_VALUE);
        Sala sala = cinema.buscarSala(numeroSala);
        String data = atendimento.receberTexto("Data e horário (DD/MM/AAAA HH:mm): ");
        if (data == null) {
            cancelarOperacao();
            return;
        }
        LocalDateTime horario = LocalDateTime.parse(data, FORMATO_DATA);
        Sessao sessao = new Sessao(cinema.proximoIdSessao(), filme, sala, horario);
        arquivos.cadastrarSessao(cinema, sessao);
        System.out.println("Sessão cadastrada com sucesso. Código: " + sessao.getId());
    }

    private void cancelarOperacao() {
        System.out.println("Operação cancelada.");
    }
}
