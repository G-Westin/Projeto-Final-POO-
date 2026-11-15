// Packages
package br.inatel.projetocinema.main;

// Imports
import br.inatel.projetocinema.arquivoservidor.ArquivoServidor;
import br.inatel.projetocinema.atendimento.Atendimento;
import br.inatel.projetocinema.atendimentobase.AtendimentoBase;
import br.inatel.projetocinema.bilheteria.Bilheteria;
import br.inatel.projetocinema.cinema.Cinema;
import br.inatel.projetocinema.controladoratendimento.ControladorAtendimento;
import br.inatel.projetocinema.mensagenssistema.MensagensSistema;
import br.inatel.projetocinema.menucliente.MenuCliente;
import br.inatel.projetocinema.menufuncionario.MenuFuncionario;

public class Main {
    public static void main(String[] args) {

        Cinema cinema = new Cinema("CineNet");
        Bilheteria bilheteria = new Bilheteria();

        ArquivoServidor.carregarDados(cinema, bilheteria);

        MensagensSistema.boasVindas(cinema.getNome());
        int perfil = AtendimentoBase.escolherPerfil();
        Atendimento menuEscolhido;

        if (perfil == 2) {
            menuEscolhido = new MenuFuncionario();
        } else {
            menuEscolhido = new MenuCliente();
        }

        ControladorAtendimento controlador = new ControladorAtendimento(
                cinema,
                bilheteria,
                menuEscolhido
        );

        controlador.executar();

        ArquivoServidor.salvarVendas(bilheteria);

        AtendimentoBase.closeScanner();
    }
}