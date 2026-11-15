// Packages
package br.inatel.projetocinema.controladoratendimento;

// Imports
import br.inatel.projetocinema.atendimento.Atendimento;
import br.inatel.projetocinema.bilheteria.Bilheteria;
import br.inatel.projetocinema.cinema.Cinema;
import br.inatel.projetocinema.excecao.AssentoOcupadoException;
import br.inatel.projetocinema.mensagenssistema.MensagensSistema;
import br.inatel.projetocinema.sessao.Sessao;
import br.inatel.projetocinema.venda.Venda;

public class ControladorAtendimento {

    // Atributos
    private final Cinema cinema;
    private final Bilheteria bilheteria;
    private final Atendimento atendimento;

    // Construtor
    public ControladorAtendimento(
            Cinema cinema,
            Bilheteria bilheteria,
            Atendimento atendimento
    )
    {
        this.cinema = cinema;
        this.bilheteria = bilheteria;
        this.atendimento = atendimento;
    }

    // Métodos
    public void executar() {
        MensagensSistema.boasVindas(cinema.getNome());

        boolean continua;

        do {
            atendimento.exibirMenu();
            int opcao = atendimento.receberOpcao("OPÇÃO: ", 0, atendimento.getTotalOpcoes());

            if (opcao == 0) {
                continua = false;
            } else {
                encaminharSolicitacao(opcao);
                continua = atendimento.continuarAtendimento();
            }
        } while (continua);

        MensagensSistema.despedida();
    }

    private void encaminharSolicitacao(int opcao) {
        System.out.println();

        if (opcao == 1) {
            consultarFilmes();
        } else if (opcao == 2) {
            consultarSessoes();
        } else if (opcao == 3) {
            comprarIngresso();
        } else if (opcao == 4) {
            consultarVendas();
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

    private void comprarIngresso() {
        if (cinema.getSessoes().isEmpty()) {
            MensagensSistema.listaVazia("sessão");
            return;
        }

        // Escolha da sessão
        cinema.listarSessoes();
        int numeroSessao = atendimento.receberOpcao(
                "Escolha a sessão: ",
                1,
                cinema.getSessoes().size()
        );
        Sessao sessao = cinema.getSessoes().get(numeroSessao - 1);

        // Escolha do assento
        System.out.println();
        sessao.mostraAssentos();
        int assento = atendimento.receberOpcao(
                "Escolha o assento: ",
                1,
                sessao.getSala().getCapacidade()
        );

        int opcaoTipo = atendimento.receberOpcao("Tipo (1 - Inteira / 2 - Meia): ", 1, 2);
        String tipo = Bilheteria.TIPO_INTEIRA;

        if (opcaoTipo == 2) {
            tipo = Bilheteria.TIPO_MEIA;
        }

        String nomeCliente = atendimento.receberTexto("Nome do cliente: ");

        if (nomeCliente == null) {
            MensagensSistema.erro("Compra cancelada.");
            return;
        }

        try {
            Venda venda = bilheteria.comprarIngresso(nomeCliente, sessao, assento, tipo);
            MensagensSistema.compraConfirmada(venda);
        } catch (AssentoOcupadoException erro) {
            MensagensSistema.erro(erro.getMessage());
        }
    }

    private void consultarVendas() {
        if (bilheteria.getVendas().isEmpty()) {
            MensagensSistema.listaVazia("venda");
        } else {
            bilheteria.listarVendas();
            MensagensSistema.totalVendas(bilheteria.calcularTotalVendas());
        }
    }
}