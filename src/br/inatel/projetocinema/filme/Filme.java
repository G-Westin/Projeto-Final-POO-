package br.inatel.projetocinema.filme;

public class Filme {
    private final int id;
    private final String titulo;
    private final String genero;
    private final int duracao;
    private final String classificacao;

    public Filme(int id, String titulo, String genero, int duracao, String classificacao) {
        if (id < 1 || duracao < 1) {
            throw new IllegalArgumentException("O código e a duração do filme devem ser positivos.");
        }
        this.id = id;
        this.titulo = validarCampo(titulo, "título");
        this.genero = validarCampo(genero, "gênero");
        this.duracao = duracao;
        this.classificacao = validarCampo(classificacao, "classificação");
    }

    private static String validarCampo(String valor, String campo) {
        if (valor == null || valor.isBlank() || valor.contains(";")
                || valor.contains("\n") || valor.contains("\r")) {
            throw new IllegalArgumentException("Preencha o campo " + campo + " sem ponto e vírgula.");
        }
        return valor.strip();
    }

    public int getId() {
        return id;
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
        System.out.println("Filme " + id + ": " + titulo);
        System.out.println("Gênero: " + genero);
        System.out.println("Duração: " + duracao + " min");
        System.out.println("Classificação: " + classificacao);
        System.out.println();
    }
}
