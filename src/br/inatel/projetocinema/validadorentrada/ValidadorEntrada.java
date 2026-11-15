// Packages
package br.inatel.projetocinema.validadorentrada;

public class ValidadorEntrada {

    // Construtor
    public ValidadorEntrada() {
    }

    // Métodos
    public static boolean ehNumero(String texto) {
        try {
            Integer.parseInt(texto.trim());
            return true;
        } catch (NumberFormatException erro) {
            return false;
        }
    }

    public static boolean opcaoValida(int opcao, int minimo, int maximo) {
        return opcao >= minimo && opcao <= maximo;
    }

    public static boolean textoValido(String texto) {
        return texto != null && !texto.trim().isEmpty() && !texto.contains("|");
    }
}