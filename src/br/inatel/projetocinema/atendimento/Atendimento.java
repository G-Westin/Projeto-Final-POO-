// Packages
package br.inatel.projetocinema.atendimento;

public interface Atendimento {

    void exibirMenu();

    int getTotalOpcoes();

    int receberOpcao(String mensagem, int minimo, int maximo);

    String receberTexto(String mensagem);

    boolean continuarAtendimento();
}