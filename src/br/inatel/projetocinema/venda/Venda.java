package br.inatel.projetocinema.venda;

import br.inatel.projetocinema.ingresso.Ingresso;
import br.inatel.projetocinema.sessao.Sessao;

import java.util.Locale;

public class Venda {
    private final int id;
    private final String nomeCliente;
    private final Sessao sessao;
    private final int assento;
    private final Ingresso ingresso;

    public Venda(int id, String nomeCliente, Sessao sessao, int assento, Ingresso ingresso) {
        if (id <= 0) {
            throw new IllegalArgumentException("O código da venda precisa ser positivo.");
        }
        if (nomeCliente == null || nomeCliente.isBlank() || nomeCliente.contains("|")
                || nomeCliente.contains("\n") || nomeCliente.contains("\r")) {
            throw new IllegalArgumentException("Informe um nome válido, sem a barra vertical (|).");
        }
        if (sessao == null || ingresso == null) {
            throw new IllegalArgumentException("A venda precisa de uma sessão e de um ingresso.");
        }
        if (assento < 1 || assento > sessao.getSala().getCapacidade()) {
            throw new IllegalArgumentException("O número do assento está fora do intervalo da sala.");
        }
        this.id = id;
        this.nomeCliente = nomeCliente.strip();
        this.sessao = sessao;
        this.assento = assento;
        this.ingresso = ingresso;
    }

    public int getId() {
        return id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public Sessao getSessao() {
        return sessao;
    }

    public int getAssento() {
        return assento;
    }

    public Ingresso getIngresso() {
        return ingresso;
    }

    public double getValor() {
        return ingresso.calcularPreco();
    }

    public void mostraInfo() {
        System.out.printf(Locale.forLanguageTag("pt-BR"),
                "Venda %d | Cliente: %s | Sessão: %d | Assento: %d | %s | R$ %.2f%n",
                id, nomeCliente, sessao.getId(), assento, ingresso.getTipo(), getValor());
    }
}
