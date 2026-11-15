package br.inatel.projetocinema.arquivocinema;

import br.inatel.projetocinema.ingresso.Ingresso;
import br.inatel.projetocinema.ingressointeira.IngressoInteira;
import br.inatel.projetocinema.ingressomeia.IngressoMeia;
import br.inatel.projetocinema.sessao.Sessao;
import br.inatel.projetocinema.venda.Venda;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ArquivoCinema {
    private final Path caminho;

    public ArquivoCinema(Path caminho) {
        this.caminho = caminho;
    }

    public void salvarVendas(List<Venda> vendas) throws IOException {
        Path pasta = caminho.getParent();
        if (pasta != null) {
            Files.createDirectories(pasta);
        }

        StringBuilder texto = new StringBuilder();
        for (Venda venda : vendas) {
            texto.append(venda.getId()).append('|')
                 .append(venda.getNomeCliente()).append('|')
                 .append(venda.getSessao().getId()).append('|')
                 .append(venda.getAssento()).append('|')
                 .append(venda.getIngresso().getTipo()).append('|')
                 .append(venda.getIngresso().getPrecoBase()).append('\n');
        }
        Files.writeString(caminho, texto.toString(), StandardCharsets.UTF_8);
    }

    public ArrayList<Venda> carregarVendas(List<Sessao> sessoes) throws IOException {
        ArrayList<Venda> vendas = new ArrayList<>();
        List<String> linhas;
        try {
            linhas = Files.readAllLines(caminho, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            return vendas;
        }

        for (int i = 0; i < linhas.size(); i++) {
            String linha = linhas.get(i);
            if (linha.isBlank()) {
                continue;
            }
            try {
                vendas.add(lerVenda(linha, sessoes));
            } catch (IllegalArgumentException e) {
                throw new IOException("Venda inválida na linha " + (i + 1) + ": " + e.getMessage(), e);
            }
        }

        return vendas;
    }

    private Venda lerVenda(String linha, List<Sessao> sessoes) {
        String[] campos = linha.split("\\|", -1);
        if (campos.length != 6) {
            throw new IllegalArgumentException("Esperados seis campos separados por |.");
        }

        int id = Integer.parseInt(campos[0]);
        int idSessao = Integer.parseInt(campos[2]);
        int assento = Integer.parseInt(campos[3]);
        double precoBase = Double.parseDouble(campos[5]);

        Sessao sessao = buscarSessao(sessoes, idSessao);
        Ingresso ingresso;
        switch (campos[4]) {
            case IngressoInteira.TIPO:
                ingresso = new IngressoInteira(precoBase);
                break;
            case IngressoMeia.TIPO:
                ingresso = new IngressoMeia(precoBase);
                break;
            default:
                throw new IllegalArgumentException("Tipo de ingresso inválido: " + campos[4]);
        }

        return new Venda(id, campos[1], sessao, assento, ingresso);
    }

    private Sessao buscarSessao(List<Sessao> sessoes, int idSessao) {
        for (Sessao sessao : sessoes) {
            if (sessao.getId() == idSessao) {
                return sessao;
            }
        }
        throw new IllegalArgumentException("Sessão não encontrada: " + idSessao);
    }
}
