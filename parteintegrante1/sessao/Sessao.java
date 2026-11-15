// VERSÃO DE TESTE (Integrante 1): será substituída pela classe real.
package br.inatel.projetocinema.sessao;

import br.inatel.projetocinema.filme.Filme;
import br.inatel.projetocinema.sala.Sala;

public class Sessao {

    // VERSÃO DE TESTE: o id é gerado em ordem de criação (a mesma ordem do sessoes.txt)
    private static int proximoId = 1;

    private int id;
    private Filme filme;
    private Sala sala;
    private String horario;
    private boolean[] assentos;   // posição 0 = assento 1

    public Sessao(Filme filme, Sala sala, String horario) {
        this.id = proximoId++;
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

    public String getHorario() {
        return horario;
    }

    public boolean assentoDisponivel(int numero) {
        return !assentos[numero - 1];
    }

    public void ocuparAssento(int numero) {
        assentos[numero - 1] = true;
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
        System.out.println();
    }

    public void mostraInfo() {
        System.out.println("Filme: " + filme.getTitulo());
        System.out.println("Sala: " + sala.getNumero());
        System.out.println("Horário: " + horario);
        System.out.println();
    }
}
