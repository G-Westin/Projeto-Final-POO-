// Packages
package br.inatel.projetocinema.arquivoservidor;

// Imports
import br.inatel.projetocinema.bilheteria.Bilheteria;
import br.inatel.projetocinema.cinema.Cinema;
import br.inatel.projetocinema.filme.Filme;
import br.inatel.projetocinema.mensagenssistema.MensagensSistema;
import br.inatel.projetocinema.sala.Sala;
import br.inatel.projetocinema.sessao.Sessao;
import br.inatel.projetocinema.validadorentrada.ValidadorEntrada;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ArquivoServidor {

    // Atributos
    public static final String PASTA = "dados";
    public static final String ARQUIVO_FILMES = PASTA + "/filmes.txt";
    public static final String ARQUIVO_SALAS = PASTA + "/salas.txt";
    public static final String ARQUIVO_SESSOES = PASTA + "/sessoes.txt";
    public static final String ARQUIVO_VENDAS = PASTA + "/vendas.txt";
    public static final String SEPARADOR = ";";

    // Métodos
    public static void carregarDados(Cinema cinema, Bilheteria bilheteria) {
        try {
            ArrayList<Filme> filmes = carregarFilmes();
            ArrayList<Sala> salas = carregarSalas();
            ArrayList<Sessao> sessoes = carregarSessoes(filmes, salas);

            for (Filme filme : filmes) {
                cinema.cadastrarFilme(filme);
            }
            for (Sala sala : salas) {
                cinema.cadastrarSala(sala);
            }
            for (Sessao sessao : sessoes) {
                cinema.cadastrarSessao(sessao);
            }

            bilheteria.carregarVendas(Paths.get(ARQUIVO_VENDAS), cinema.getSessoes());
        } catch (IOException erro) {
            MensagensSistema.erro("Não foi possível carregar os dados: " + erro.getMessage());
        }
    }

    public static void salvarVendas(Bilheteria bilheteria) {
        // Sem vendas não há o que gravar (o arquivo anterior fica preservado)
        if (bilheteria.getVendas().isEmpty()) {
            return;
        }

        try {
            bilheteria.salvarVendas(Paths.get(ARQUIVO_VENDAS));
        } catch (IOException erro) {
            MensagensSistema.erro("Não foi possível salvar as vendas: " + erro.getMessage());
        }
    }

    public static ArrayList<Filme> carregarFilmes() throws IOException {
        ArrayList<Filme> filmes = new ArrayList<>();

        for (String linha : lerLinhas(ARQUIVO_FILMES)) {
            String[] campos = linha.split(SEPARADOR);

            if (campos.length == 4 && ValidadorEntrada.ehNumero(campos[2])) {
                int duracao = Integer.parseInt(campos[2].trim());
                filmes.add(new Filme(campos[0].trim(), campos[1].trim(), duracao, campos[3].trim()));
            }
        }
        return filmes;
    }

    public static ArrayList<Sala> carregarSalas() throws IOException {
        ArrayList<Sala> salas = new ArrayList<>();

        for (String linha : lerLinhas(ARQUIVO_SALAS)) {
            String[] campos = linha.split(SEPARADOR);

            if (campos.length == 2 && ValidadorEntrada.ehNumero(campos[0])
                    && ValidadorEntrada.ehNumero(campos[1])) {
                int numero = Integer.parseInt(campos[0].trim());
                int capacidade = Integer.parseInt(campos[1].trim());
                salas.add(new Sala(numero, capacidade));
            }
        }
        return salas;
    }

    public static ArrayList<Sessao> carregarSessoes(
            ArrayList<Filme> filmes,
            ArrayList<Sala> salas
    ) throws IOException
    {
        ArrayList<Sessao> sessoes = new ArrayList<>();

        for (String linha : lerLinhas(ARQUIVO_SESSOES)) {
            String[] campos = linha.split(SEPARADOR);

            if (campos.length == 3 && ValidadorEntrada.ehNumero(campos[1])) {
                Filme filme = buscarFilme(filmes, campos[0].trim());
                Sala sala = buscarSala(salas, Integer.parseInt(campos[1].trim()));

                // Só cria a sessão se o filme e a sala existirem
                if (filme != null && sala != null) {
                    sessoes.add(new Sessao(filme, sala, campos[2].trim()));
                }
            }
        }
        return sessoes;
    }

    private static List<String> lerLinhas(String caminho) throws IOException {
        return Files.readAllLines(Paths.get(caminho));
    }

    private static Filme buscarFilme(ArrayList<Filme> filmes, String titulo) {
        for (Filme filme : filmes) {
            if (filme.getTitulo().equals(titulo)) {
                return filme;
            }
        }
        return null;
    }

    private static Sala buscarSala(ArrayList<Sala> salas, int numero) {
        for (Sala sala : salas) {
            if (sala.getNumero() == numero) {
                return sala;
            }
        }
        return null;
    }
}