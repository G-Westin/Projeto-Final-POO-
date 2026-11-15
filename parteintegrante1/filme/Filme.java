// VERSÃO DE TESTE (Integrante 1): será substituída pela classe real.
package br.inatel.projetocinema.filme;

public class Filme {

    private String titulo;
    private String genero;
    private int duracao;
    private String classificacao;

    public Filme(String titulo, String genero, int duracao, String classificacao) {
        this.titulo = titulo;
        this.genero = genero;
        this.duracao = duracao;
        this.classificacao = classificacao;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getGenero() {
        return genero;
    }

    public int getDuracao() {
        return duracao;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void mostraInfo() {
        System.out.println("Título: " + titulo);
        System.out.println("Gênero: " + genero);
        System.out.println("Duração: " + duracao + " min");
        System.out.println("Classificação: " + classificacao);
        System.out.println();
    }
}
