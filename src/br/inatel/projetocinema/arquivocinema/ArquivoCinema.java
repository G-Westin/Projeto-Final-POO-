package br.inatel.projetocinema.arquivocinema;

import br.inatel.projetocinema.arquivotexto.ArquivoTexto;
import br.inatel.projetocinema.cinema.Cinema;
import br.inatel.projetocinema.filme.Filme;
import br.inatel.projetocinema.ingresso.Ingresso;
import br.inatel.projetocinema.ingressointeira.IngressoInteira;
import br.inatel.projetocinema.ingressomeia.IngressoMeia;
import br.inatel.projetocinema.sala.Sala;
import br.inatel.projetocinema.sessao.Sessao;
import br.inatel.projetocinema.venda.Venda;

import java.io.IOException;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArquivoCinema {
    private final Path caminho;

    public ArquivoCinema(Path caminho) {
        this.caminho = caminho;
    }

    public void carregarFilmes(Cinema cinema) throws IOException {
        List<String> linhas = ArquivoTexto.lerLinhas(caminho);
        for (int i = 0; i < linhas.size(); i++) {
            if (!linhas.get(i).isBlank()) {
                try {
                    String[] campos = separar(linhas.get(i), ";", 5);
                    cinema.cadastrarFilme(new Filme(Integer.parseInt(campos[0]), campos[1], campos[2],
                            Integer.parseInt(campos[3]), campos[4]));
                } catch (IllegalArgumentException erro) {
                    throw erroNaLinha(i, erro);
                }
            }
        }
    }

    public void carregarSalas(Cinema cinema) throws IOException {
        List<String> linhas = ArquivoTexto.lerLinhas(caminho);
        for (int i = 0; i < linhas.size(); i++) {
            if (!linhas.get(i).isBlank()) {
                try {
                    String[] campos = separar(linhas.get(i), ";", 2);
                    cinema.cadastrarSala(new Sala(Integer.parseInt(campos[0]), Integer.parseInt(campos[1])));
                } catch (IllegalArgumentException erro) {
                    throw erroNaLinha(i, erro);
                }
            }
        }
    }

    public void carregarSessoes(Cinema cinema) throws IOException {
        List<String> linhas = ArquivoTexto.lerLinhas(caminho);
        for (int i = 0; i < linhas.size(); i++) {
            if (!linhas.get(i).isBlank()) {
                try {
                    String[] campos = separar(linhas.get(i), ";", 4);
                    Filme filme = cinema.buscarFilme(Integer.parseInt(campos[1]));
                    Sala sala = cinema.buscarSala(Integer.parseInt(campos[2]));
                    cinema.cadastrarSessao(new Sessao(Integer.parseInt(campos[0]), filme, sala,
                            LocalDateTime.parse(campos[3])));
                } catch (IllegalArgumentException | DateTimeException erro) {
                    throw erroNaLinha(i, erro);
                }
            }
        }
    }

    public void salvarFilmes(List<Filme> filmes) throws IOException {
        ArrayList<String> linhas = new ArrayList<>();
        for (Filme filme : filmes) {
            linhas.add(filme.getId() + ";" + filme.getTitulo() + ";" + filme.getGenero()
                    + ";" + filme.getDuracao() + ";" + filme.getClassificacao());
        }
        ArquivoTexto.gravarLinhas(caminho, linhas);
    }

    public void salvarSalas(List<Sala> salas) throws IOException {
        ArrayList<String> linhas = new ArrayList<>();
        for (Sala sala : salas) {
            linhas.add(sala.getNumero() + ";" + sala.getCapacidade());
        }
        ArquivoTexto.gravarLinhas(caminho, linhas);
    }

    public void salvarSessoes(List<Sessao> sessoes) throws IOException {
        ArrayList<String> linhas = new ArrayList<>();
        for (Sessao sessao : sessoes) {
            linhas.add(sessao.getId() + ";" + sessao.getFilme().getId() + ";"
                    + sessao.getSala().getNumero() + ";" + sessao.getHorario());
        }
        ArquivoTexto.gravarLinhas(caminho, linhas);
    }

    public void salvarVendas(List<Venda> vendas) throws IOException {
        ArrayList<String> linhas = new ArrayList<>();
        for (Venda venda : vendas) {
            linhas.add(venda.getId() + "|" + venda.getNomeCliente() + "|" + venda.getSessao().getId()
                    + "|" + venda.getAssento() + "|" + venda.getIngresso().getTipo() + "|"
                    + venda.getIngresso().getPrecoBase());
        }
        ArquivoTexto.gravarLinhas(caminho, linhas);
    }

    public ArrayList<Venda> carregarVendas(List<Sessao> sessoes) throws IOException {
        ArrayList<Venda> vendas = new ArrayList<>();
        List<String> linhas = ArquivoTexto.lerLinhas(caminho);
        for (int i = 0; i < linhas.size(); i++) {
            if (!linhas.get(i).isBlank()) {
                try {
                    vendas.add(lerVenda(linhas.get(i), sessoes));
                } catch (IllegalArgumentException erro) {
                    throw erroNaLinha(i, erro);
                }
            }
        }
        return vendas;
    }

    private Venda lerVenda(String linha, List<Sessao> sessoes) {
        String[] campos = separar(linha, "\\|", 6);
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

    private String[] separar(String linha, String separador, int quantidade) {
        String[] campos = linha.split(separador, -1);
        if (campos.length != quantidade) {
            throw new IllegalArgumentException("Esperados " + quantidade + " campos.");
        }
        for (int i = 0; i < campos.length; i++) {
            campos[i] = campos[i].strip();
        }
        return campos;
    }

    private IOException erroNaLinha(int indice, RuntimeException causa) {
        return new IOException(caminho + ", linha " + (indice + 1) + ": " + causa.getMessage(), causa);
    }
}
