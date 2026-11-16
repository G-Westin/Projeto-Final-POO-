// Packages
package br.inatel.projetocinema.menucliente;

// Imports
import br.inatel.projetocinema.atendimentobase.AtendimentoBase;

public class MenuCliente extends AtendimentoBase {

    public MenuCliente() {
        super();
    }

    @Override
    public void exibirMenu() {
        System.out.println();
        System.out.println("===> MENU DO CLIENTE:");
        System.out.println("1 - Consultar filmes");
        System.out.println("2 - Consultar sessões");
        System.out.println("3 - Comprar ingresso");
        System.out.println("0 - Encerrar atendimento");
    }

    @Override
    public int getTotalOpcoes() {
        return 3;
    }
}