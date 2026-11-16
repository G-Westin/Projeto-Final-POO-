package br.inatel.projetocinema.cinema;

import br.inatel.projetocinema.filme.Filme;
import br.inatel.projetocinema.sala.Sala;
import br.inatel.projetocinema.sessao.Sessao;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Cinema {
    private final String nome;
    private final ArrayList<Filme> filmes = new ArrayList<>();
    private final ArrayList<Sala> salas = new ArrayList<>();
    private final ArrayList<Sessao> sessoes = new ArrayList<>();

    public Cinema(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe o nome do cinema.");
        }
        this.nome = nome.strip();
    }

    public String getNome() {
        return nome;
    }

    public ArrayList<Filme> getFilmes() {
        return new ArrayList<>(filmes);
    }

    public ArrayList<Sala> getSalas() {
        return new ArrayList<>(salas);
    }

    public ArrayList<Sessao> getSessoes() {
        return new ArrayList<>(sessoes);
    }

    public Filme buscarFilme(int id) {
        for (Filme filme : filmes) {
            if (filme.getId() == id) {
                return filme;
            }
        }
        throw new IllegalArgumentException("Filme " + id + " não cadastrado.");
    }

    public Sala buscarSala(int numero) {
        for (Sala sala : salas) {
            if (sala.getNumero() == numero) {
                return sala;
            }
        }
        throw new IllegalArgumentException("Sala " + numero + " não cadastrada.");
    }

    public Sessao buscarSessao(int id) {
        for (Sessao sessao : sessoes) {
            if (sessao.getId() == id) {
                return sessao;
            }
        }
        throw new IllegalArgumentException("Sessão " + id + " não cadastrada.");
    }

    public int proximoIdFilme() {
        int maior = 0;
        for (Filme filme : filmes) {
            maior = Math.max(maior, filme.getId());
        }
        return gerarProximoId(maior);
    }

    public int proximoIdSessao() {
        int maior = 0;
        for (Sessao sessao : sessoes) {
            maior = Math.max(maior, sessao.getId());
        }
        return gerarProximoId(maior);
    }

    private int gerarProximoId(int ultimoId) {
        if (ultimoId == Integer.MAX_VALUE) {
            throw new IllegalStateException("Não há mais códigos de cadastro disponíveis.");
        }
        return ultimoId + 1;
    }

    public void validarNovoFilme(Filme filme) {
        if (filme == null) {
            throw new IllegalArgumentException("Informe o filme.");
        }
        for (Filme existente : filmes) {
            if (existente.getId() == filme.getId()) {
                throw new IllegalArgumentException("Código de filme repetido: " + filme.getId() + ".");
            }
        }
    }

    public void validarNovaSala(Sala sala) {
        if (sala == null) {
            throw new IllegalArgumentException("Informe a sala.");
        }
        for (Sala existente : salas) {
            if (existente.getNumero() == sala.getNumero()) {
                throw new IllegalArgumentException("Sala " + sala.getNumero() + " já cadastrada.");
            }
        }
    }

    public void validarNovaSessao(Sessao sessao) {
        if (sessao == null) {
            throw new IllegalArgumentException("Informe a sessão.");
        }
        if (buscarFilme(sessao.getFilme().getId()) != sessao.getFilme()
                || buscarSala(sessao.getSala().getNumero()) != sessao.getSala()) {
            throw new IllegalArgumentException("Use o filme e a sala já cadastrados no cinema.");
        }
        LocalDateTime inicio = sessao.getHorario();
        LocalDateTime fim = inicio.plusMinutes(sessao.getFilme().getDuracao());
        for (Sessao existente : sessoes) {
            if (existente.getId() == sessao.getId()) {
                throw new IllegalArgumentException("Código de sessão repetido: " + sessao.getId() + ".");
            }
            LocalDateTime outroInicio = existente.getHorario();
            LocalDateTime outroFim = outroInicio.plusMinutes(existente.getFilme().getDuracao());
            if (existente.getSala() == sessao.getSala() && inicio.isBefore(outroFim)
                    && outroInicio.isBefore(fim)) {
                throw new IllegalArgumentException("Conflito de horário com a sessão " + existente.getId() + ".");
            }
        }
    }

    public void cadastrarFilme(Filme filme) {
        validarNovoFilme(filme);
        filmes.add(filme);
    }

    public void cadastrarSala(Sala sala) {
        validarNovaSala(sala);
        salas.add(sala);
    }

    public void cadastrarSessao(Sessao sessao) {
        validarNovaSessao(sessao);
        sessoes.add(sessao);
    }

    public void listarFilmes() {
        for (Filme filme : filmes) {
            filme.mostraInfo();
        }
    }

    public void listarSalas() {
        for (Sala sala : salas) {
            System.out.printf("Sala %d | %d assentos%n", sala.getNumero(), sala.getCapacidade());
        }
    }

    public void listarSessoes() {
        for (Sessao sessao : sessoes) {
            sessao.mostraInfo();
        }
    }
}
