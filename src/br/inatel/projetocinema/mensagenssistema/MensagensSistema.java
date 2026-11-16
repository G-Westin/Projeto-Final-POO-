// Packages
package br.inatel.projetocinema.mensagenssistema;

// Imports
import br.inatel.projetocinema.venda.Venda;

import java.util.Locale;

public final class MensagensSistema {

    private MensagensSistema() {
    }

    public static void boasVindas(String nomeCinema) {
        System.out.println("==================================================");
        System.out.printf("SEJA BEM VINDO AO %s%n", nomeCinema.toUpperCase());
        System.out.println("==================================================");
    }

    public static void despedida() {
        System.out.println("Obrigado pela visita. Volte sempre!");
    }

    public static void opcaoInvalida(int minimo, int maximo) {
        System.out.printf("ERRO: Opção inválida. Digite um número de %d a %d.%n", minimo, maximo);
    }

    public static void textoInvalido() {
        System.out.println("ERRO: Texto inválido. Não deixe em branco e não use \"|\".");
    }

    public static void listaVazia(String item) {
        System.out.printf("AVISO: Nenhum(a) %s cadastrado(a).%n", item);
    }

    public static void erro(String mensagem) {
        System.out.printf("ERRO: %s%n", mensagem);
    }

    public static void compraConfirmada(Venda venda) {
        System.out.println("===> COMPRA REALIZADA COM SUCESSO !!!");
        venda.mostraInfo();
    }

    public static void totalVendas(double total) {
        System.out.printf(Locale.forLanguageTag("pt-BR"), "Total de vendas: R$ %.2f%n", total);
    }
}