package br.inatel.projetocinema.ingresso;

public abstract class Ingresso {
    private final double precoBase;
    private final String tipo;

    protected Ingresso(double precoBase, String tipo) {
        if (!Double.isFinite(precoBase) || precoBase <= 0) {
            throw new IllegalArgumentException("O preço-base precisa ser positivo.");
        }
        this.precoBase = precoBase;
        this.tipo = tipo;
    }

    public double getPrecoBase() {
        return precoBase;
    }

    public String getTipo() {
        return tipo;
    }

    public abstract double calcularPreco();
}
