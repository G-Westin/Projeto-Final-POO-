package br.inatel.projetocinema.excecao;

public class AssentoOcupadoException extends Exception {
    private static final long serialVersionUID = 1L;

    public AssentoOcupadoException(String mensagem) {
        super(mensagem);
    }
}
