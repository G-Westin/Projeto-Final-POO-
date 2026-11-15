// Packages
package br.inatel.projetocinema.atendimentobase;

// Imports
import br.inatel.projetocinema.atendimento.Atendimento;
import br.inatel.projetocinema.mensagenssistema.MensagensSistema;
import br.inatel.projetocinema.validadorentrada.ValidadorEntrada;
import java.util.Scanner;

public abstract class AtendimentoBase implements Atendimento {

    // Atributos
    private static final Scanner input = new Scanner(System.in);

    // Construtor
    public AtendimentoBase() {
    }

    // Métodos
    @Override
    public abstract void exibirMenu();

    @Override
    public abstract int getTotalOpcoes();

    @Override
    public int receberOpcao(String mensagem, int minimo, int maximo) {
        return lerOpcao(mensagem, minimo, maximo);
    }

    @Override
    public String receberTexto(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String entrada = input.nextLine();

            if (ValidadorEntrada.textoValido(entrada)) {
                return entrada.trim();
            }
            MensagensSistema.textoInvalido();
            int resposta = receberOpcao("Digitar novamente? (1 - Sim / 0 - Cancelar): ", 0, 1);

            if (resposta == 0) {
                return null;    // cancelou
            }
        }
    }

    @Override
    public boolean continuarAtendimento() {
        int resposta = receberOpcao("Deseja continuar? (1 - Sim / 0 - Não): ", 0, 1);
        return resposta == 1;
    }

    public static int escolherPerfil() {
        System.out.println("===> QUEM ESTÁ USANDO O SISTEMA ?");
        System.out.println("1 - Cliente");
        System.out.println("2 - Funcionário");
        return lerOpcao("PERFIL: ", 1, 2);
    }

    public static int lerOpcao(String mensagem, int minimo, int maximo) {
        while (true) {
            System.out.print(mensagem);
            String entrada = input.nextLine();

            if (ValidadorEntrada.ehNumero(entrada)) {
                int opcao = Integer.parseInt(entrada.trim());

                if (ValidadorEntrada.opcaoValida(opcao, minimo, maximo)) {
                    return opcao;
                }
            }
            MensagensSistema.opcaoInvalida(minimo, maximo);
        }
    }

    public static void closeScanner() {
        input.close();
    }
}