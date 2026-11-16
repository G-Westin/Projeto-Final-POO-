// Packages
package br.inatel.projetocinema.menufuncionario;

// Imports
import br.inatel.projetocinema.atendimentobase.AtendimentoBase;

public class MenuFuncionario extends AtendimentoBase {

    // Construtor
    public MenuFuncionario() {
        super();
    }

    // Métodos
    @Override
    public void exibirMenu() {
        System.out.println();
        System.out.println("===> MENU DO FUNCIONÁRIO:");
        System.out.println("1 - Consultar filmes");
        System.out.println("2 - Consultar sessões");
        System.out.println("3 - Vender ingresso");
        System.out.println("4 - Consultar vendas");
        System.out.println("5 - Cadastrar filme");
        System.out.println("6 - Cadastrar sala");
        System.out.println("7 - Cadastrar sessão");
        System.out.println("0 - Encerrar atendimento");
    }

    @Override
    public int getTotalOpcoes() {
        return 7;
    }
}