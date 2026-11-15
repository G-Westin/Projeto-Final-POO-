package br.inatel.projetocinema.ingressointeira;

import br.inatel.projetocinema.ingresso.Ingresso;

public class IngressoInteira extends Ingresso {
    public static final String TIPO = "INTEIRA";

    public IngressoInteira(double precoBase) {
        super(precoBase, TIPO);
    }

    @Override
    public double calcularPreco() {
        return getPrecoBase();
    }
}
