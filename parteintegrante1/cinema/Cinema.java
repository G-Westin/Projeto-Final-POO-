// VERSÃO DE TESTE (Integrante 1): será substituída pela classe real.
package br.inatel.projetocinema.cinema;

import br.inatel.projetocinema.filme.Filme;
import br.inatel.projetocinema.sala.Sala;
import br.inatel.projetocinema.sessao.Sessao;
import java.util.ArrayList;

public class Cinema {

    private String nome;
    private ArrayList<Filme> filmes = new ArrayList<>();
    private ArrayList<Sala> salas = new ArrayList<>();
    private ArrayList<Sessao> sessoes = new ArrayList<>();

    public Cinema(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public ArrayList<Filme> getFilmes() {
        return filmes;
    }

    public ArrayList<Sala> getSalas() {
        return salas;
    }

    public ArrayList<Sessao> getSessoes() {
        return sessoes;
    }

    public void cadastrarFilme(Filme filme) {
        filmes.add(filme);
    }

    public void cadastrarSala(Sala sala) {
        salas.add(sala);
    }

    public void cadastrarSessao(Sessao sessao) {
        sessoes.add(sessao);
    }

    public void listarFilmes() {
        for (Filme filme : filmes) {
            filme.mostraInfo();
        }
    }

    public void listarSessoes() {
        for (int i = 0; i < sessoes.size(); i++) {
            System.out.println("--- SESSÃO " + (i + 1) + " ---");
            sessoes.get(i).mostraInfo();
        }
    }
}
