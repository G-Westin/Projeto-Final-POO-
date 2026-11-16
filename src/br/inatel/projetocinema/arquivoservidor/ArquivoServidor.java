package br.inatel.projetocinema.arquivoservidor;

import br.inatel.projetocinema.arquivocinema.ArquivoCinema;
import br.inatel.projetocinema.bilheteria.Bilheteria;
import br.inatel.projetocinema.cinema.Cinema;
import br.inatel.projetocinema.filme.Filme;
import br.inatel.projetocinema.sala.Sala;
import br.inatel.projetocinema.sessao.Sessao;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

// Coordena arquivos locais do cinema. Não há servidor de rede.
public class ArquivoServidor {
    private final ArquivoCinema arquivoFilmes;
    private final ArquivoCinema arquivoSalas;
    private final ArquivoCinema arquivoSessoes;
    private final Path arquivoVendas;

    public ArquivoServidor(Path pasta) {
        this.arquivoFilmes = new ArquivoCinema(pasta.resolve("filmes.txt"));
        this.arquivoSalas = new ArquivoCinema(pasta.resolve("salas.txt"));
        this.arquivoSessoes = new ArquivoCinema(pasta.resolve("sessoes.txt"));
        this.arquivoVendas = pasta.resolve("vendas.txt");
    }

    public void carregarDados(Cinema cinema, Bilheteria bilheteria) throws IOException {
        arquivoFilmes.carregarFilmes(cinema);
        arquivoSalas.carregarSalas(cinema);
        arquivoSessoes.carregarSessoes(cinema);
        bilheteria.carregarVendas(arquivoVendas, cinema.getSessoes());
    }

    public Path getArquivoVendas() {
        return arquivoVendas;
    }

    public void cadastrarFilme(Cinema cinema, Filme filme) throws IOException {
        cinema.validarNovoFilme(filme);
        ArrayList<Filme> filmes = cinema.getFilmes();
        filmes.add(filme);
        arquivoFilmes.salvarFilmes(filmes);
        cinema.cadastrarFilme(filme);
    }

    public void cadastrarSala(Cinema cinema, Sala sala) throws IOException {
        cinema.validarNovaSala(sala);
        ArrayList<Sala> salas = cinema.getSalas();
        salas.add(sala);
        arquivoSalas.salvarSalas(salas);
        cinema.cadastrarSala(sala);
    }

    public void cadastrarSessao(Cinema cinema, Sessao sessao) throws IOException {
        cinema.validarNovaSessao(sessao);
        ArrayList<Sessao> sessoes = cinema.getSessoes();
        sessoes.add(sessao);
        arquivoSessoes.salvarSessoes(sessoes);
        cinema.cadastrarSessao(sessao);
    }
}
