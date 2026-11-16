package br.inatel.projetocinema.sala;

public class Sala {
    public static final int CAPACIDADE_MAXIMA = 1000;
    private final int numero;
    private final int capacidade;

    public Sala(int numero, int capacidade) {
        if (numero < 1) {
            throw new IllegalArgumentException("O número da sala deve ser positivo.");
        }
        if (capacidade < 1 || capacidade > CAPACIDADE_MAXIMA) {
            throw new IllegalArgumentException("A capacidade deve estar entre 1 e " + CAPACIDADE_MAXIMA + ".");
        }
        this.numero = numero;
        this.capacidade = capacidade;
    }

    public int getNumero() {
        return numero;
    }

    public int getCapacidade() {
        return capacidade;
    }
}
