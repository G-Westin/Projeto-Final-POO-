package br.inatel.projetocinema.arquivotexto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

public final class ArquivoTexto {
    private ArquivoTexto() {
    }

    public static List<String> lerLinhas(Path caminho) throws IOException {
        try {
            return Files.readAllLines(caminho, StandardCharsets.UTF_8);
        } catch (NoSuchFileException erro) {
            return Collections.emptyList();
        }
    }

    // O arquivo antigo só é substituído depois que o novo foi inteiramente escrito.
    public static void gravarLinhas(Path caminho, List<String> linhas) throws IOException {
        Path destino = caminho.toAbsolutePath();
        Path pasta = destino.getParent();
        Files.createDirectories(pasta);
        Path temporario = Files.createTempFile(pasta, "cinenet-", ".tmp");

        try {
            Files.write(temporario, linhas, StandardCharsets.UTF_8);
            try {
                Files.move(temporario, destino, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException erro) {
                Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException erro) {
            try {
                Files.deleteIfExists(temporario);
            } catch (IOException erroLimpeza) {
                erro.addSuppressed(erroLimpeza);
            }
            throw erro;
        }
    }
}
