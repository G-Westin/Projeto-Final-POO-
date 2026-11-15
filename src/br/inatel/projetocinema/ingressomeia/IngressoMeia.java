package br.inatel.projetocinema.ingressomeia;

import br.inatel.projetocinema.ingresso.Ingresso;

public class IngressoMeia extends Ingresso {
    public static final String TIPO = "MEIA";

    public IngressoMeia(double precoBase) {
        super(precoBase, TIPO);
    }

    @Override
    public double calcularPreco() {
        return getPrecoBase() / 2;
    }
}
