package br.inatel.projetocinema.main;

import br.inatel.projetocinema.arquivoservidor.ArquivoServidor;
import br.inatel.projetocinema.atendimento.Atendimento;
import br.inatel.projetocinema.atendimentobase.AtendimentoBase;
import br.inatel.projetocinema.bilheteria.Bilheteria;
import br.inatel.projetocinema.cinema.Cinema;
import br.inatel.projetocinema.controladoratendimento.ControladorAtendimento;
import br.inatel.projetocinema.mensagenssistema.MensagensSistema;
import br.inatel.projetocinema.menucliente.MenuCliente;
import br.inatel.projetocinema.menufuncionario.MenuFuncionario;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

public class Main {
    public static void main(String[] args) {
        Cinema cinema = new Cinema("CineNet");
        Bilheteria bilheteria = new Bilheteria();
        ArquivoServidor arquivos = new ArquivoServidor(Paths.get("dados"));

        try {
            arquivos.carregarDados(cinema, bilheteria);
        } catch (IOException erro) {
            MensagensSistema.erro("Não foi possível carregar os dados: " + erro.getMessage());
            System.out.println("O atendimento não foi iniciado. Os arquivos não foram alterados.");
            System.exit(1);
            return;
        }

        MensagensSistema.boasVindas(cinema.getNome());
        try {
            int perfil = AtendimentoBase.escolherPerfil();
            Atendimento menu = perfil == 2 ? new MenuFuncionario() : new MenuCliente();
            new ControladorAtendimento(cinema, bilheteria, menu, arquivos).executar();
        } catch (NoSuchElementException erro) {
            System.out.println("\nEntrada encerrada.");
        } finally {
            AtendimentoBase.closeScanner();
            MensagensSistema.despedida();
        }
    }
}
