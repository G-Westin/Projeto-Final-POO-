package br.inatel.projetocinema.sessao;

import br.inatel.projetocinema.filme.Filme;
import br.inatel.projetocinema.sala.Sala;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sessao {
    private static final DateTimeFormatter FORMATO_EXIBICAO = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");
    private final int id;
    private final Filme filme;
    private final Sala sala;
    private final LocalDateTime horario;
    private final boolean[] assentos;

    public Sessao(int id, Filme filme, Sala sala, LocalDateTime horario) {
        if (id < 1 || filme == null || sala == null || horario == null) {
            throw new IllegalArgumentException("Informe um código positivo, filme, sala e horário da sessão.");
        }
        if (horario.getYear() < 1 || horario.getYear() > 9999) {
            throw new IllegalArgumentException("O ano da sessão deve estar entre 1 e 9999.");
        }
        this.id = id;
        this.filme = filme;
        this.sala = sala;
        this.horario = horario;
        this.assentos = new boolean[sala.getCapacidade()];
    }

    public int getId() {
        return id;
    }

    public Filme getFilme() {
        return filme;
    }

    public Sala getSala() {
        return sala;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public boolean assentoDisponivel(int numero) {
        return !assentos[indiceAssento(numero)];
    }

    public void ocuparAssento(int numero) {
        int indice = indiceAssento(numero);
        if (assentos[indice]) {
            throw new IllegalStateException("O assento " + numero + " já está ocupado.");
        }
        assentos[indice] = true;
    }

    private int indiceAssento(int numero) {
        if (numero < 1 || numero > assentos.length) {
            throw new IllegalArgumentException("Escolha um assento de 1 a " + assentos.length + ".");
        }
        return numero - 1;
    }

    public void mostraAssentos() {
        System.out.println("Assentos (XX = ocupado):");
        for (int i = 0; i < assentos.length; i++) {
            if (assentos[i]) {
                System.out.print("[XX] ");
            } else {
                System.out.printf("[%02d] ", i + 1);
            }
            if ((i + 1) % 10 == 0) {
                System.out.println();
            }
        }
        if (assentos.length % 10 != 0) {
            System.out.println();
        }
    }

    public void mostraInfo() {
        System.out.println("--- SESSÃO " + id + " ---");
        System.out.println("Filme: " + filme.getTitulo());
        System.out.println("Sala: " + sala.getNumero());
        System.out.println("Data e horário: " + horario.format(FORMATO_EXIBICAO));
        System.out.println();
    }
}
