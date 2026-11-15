package br.inatel.projetocinema.bilheteria;

import br.inatel.projetocinema.arquivo.ArquivoCinema;
import br.inatel.projetocinema.excecao.AssentoOcupadoException;
import br.inatel.projetocinema.ingresso.Ingresso;
import br.inatel.projetocinema.ingressointeira.IngressoInteira;
import br.inatel.projetocinema.ingressomeia.IngressoMeia;
import br.inatel.projetocinema.sessao.Sessao;
import br.inatel.projetocinema.venda.Venda;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Bilheteria {
    public static final String TIPO_INTEIRA = IngressoInteira.TIPO;
    public static final String TIPO_MEIA = IngressoMeia.TIPO;
    private static final double PRECO_BASE = 30.0;

    private final ArrayList<Venda> vendas = new ArrayList<>();
    private int ultimoId;

    public Venda comprarIngresso(String nomeCliente, Sessao sessao, int assento, String tipo)
            throws AssentoOcupadoException {
        if (ultimoId == Integer.MAX_VALUE) {
            throw new IllegalStateException("Não há mais códigos de venda disponíveis.");
        }
        Ingresso ingresso = criarIngresso(tipo);
        Venda venda = new Venda(ultimoId + 1, nomeCliente, sessao, assento, ingresso);

        if (!sessao.assentoDisponivel(assento)) {
            throw new AssentoOcupadoException("O assento " + assento + " já está ocupado.");
        }

        sessao.ocuparAssento(assento);
        vendas.add(venda);
        ultimoId = venda.getId();
        return venda;
    }

    private Ingresso criarIngresso(String tipo) {
        if (tipo != null) {
            if (TIPO_INTEIRA.equalsIgnoreCase(tipo.strip())) {
                return new IngressoInteira(PRECO_BASE);
            }
            if (TIPO_MEIA.equalsIgnoreCase(tipo.strip())) {
                return new IngressoMeia(PRECO_BASE);
            }
        }
        throw new IllegalArgumentException("Tipo inválido. Escolha INTEIRA ou MEIA.");
    }

    public ArrayList<Venda> getVendas() {
        return new ArrayList<>(vendas);
    }

    public void listarVendas() {
        if (vendas.isEmpty()) {
            System.out.println("Nenhuma venda registrada.");
        }
        for (Venda venda : vendas) {
            venda.mostraInfo();
        }
    }

    public double calcularTotalVendas() {
        double total = 0;
        for (Venda venda : vendas) {
            total += venda.getValor();
        }
        return total;
    }

    public void salvarVendas(Path caminho) throws IOException {
        new ArquivoCinema(caminho).salvarVendas(vendas);
    }

    // Chamar na inicialização, após cadastrar ou carregar as sessões.
    public void carregarVendas(Path caminho, List<Sessao> sessoes) throws IOException {
        if (!vendas.isEmpty()) {
            throw new IllegalStateException("Carregue as vendas antes de começar o atendimento.");
        }
        ArrayList<Venda> recuperadas = new ArquivoCinema(caminho).carregarVendas(sessoes);
        HashSet<Integer> ids = new HashSet<>();
        HashSet<String> assentos = new HashSet<>();

        // Valida todas as vendas antes de alterar as sessões.
        for (Venda venda : recuperadas) {
            Sessao sessao = venda.getSessao();
            int assento = venda.getAssento();
            if (!ids.add(venda.getId())) {
                throw new IOException("Código de venda duplicado: " + venda.getId());
            }
            String chave = sessao.getId() + "|" + assento;
            if (!assentos.add(chave) || !sessao.assentoDisponivel(assento)) {
                throw new IOException("O assento " + assento + " já está ocupado na sessão "
                        + sessao.getId() + ".");
            }
        }

        for (Venda venda : recuperadas) {
            venda.getSessao().ocuparAssento(venda.getAssento());
            vendas.add(venda);
            ultimoId = Math.max(ultimoId, venda.getId());
        }
    }
}
