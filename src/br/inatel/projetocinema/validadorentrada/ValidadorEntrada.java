// Packages
package br.inatel.projetocinema.validadorentrada;

public final class ValidadorEntrada {

    private ValidadorEntrada() {
    }

    public static boolean ehNumero(String texto) {
        if (texto == null) {
            return false;
        }
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
        return texto != null && !texto.isBlank() && !texto.contains("|")
                && !texto.contains("\n") && !texto.contains("\r");
    }
}