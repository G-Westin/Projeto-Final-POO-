"""Testes de integração do CineNet sem dependências externas."""
import shutil
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path


RAIZ = Path(__file__).resolve().parents[1]
CLASSES = (RAIZ / "out").resolve()
MAIN = "br.inatel.projetocinema.main.Main"


class CineNetTest(unittest.TestCase):
    def setUp(self):
        pasta = tempfile.TemporaryDirectory()
        self.addCleanup(pasta.cleanup)
        self.raiz = Path(pasta.name)
        self.dados = self.raiz / "dados"
        self.dados.mkdir()
        for nome in ("filmes.txt", "salas.txt", "sessoes.txt"):
            shutil.copyfile(RAIZ / "dados" / nome, self.dados / nome)

    def executar(self, *linhas, sucesso=True):
        p = subprocess.run(
            ["java", "-Dfile.encoding=UTF-8", "-cp", str(CLASSES), MAIN],
            cwd=self.raiz, input="".join(f"{linha}\n" for linha in linhas),
            encoding="utf-8", capture_output=True, timeout=10,
        )
        self.assertEqual(p.returncode, 0 if sucesso else 1, p.stdout + p.stderr)
        self.assertEqual(p.stderr, "")
        return p.stdout

    def ler(self, nome="vendas.txt"):
        return (self.dados / nome).read_text(encoding="utf-8")

    def escrever(self, nome, texto):
        (self.dados / nome).write_text(texto, encoding="utf-8")

    def test_01_vendas_validacao_e_reinicio(self):
        saida = self.executar(
            "foo", 2, 1, 1, 2, 1,
            3, 1, 0, 1, 1, "Pessoa|Teste", 1, "João", 1,
            3, 1, 1, 2, "Nome Repetido", 1,
            3, 2, 1, 2, "Maria", 1, 4, 0,
        )
        for trecho in ("O Resgate do Soldado Ryan", "Lágrimas do Sol", "Opção inválida",
                       "Texto inválido", "R$ 30,00", "R$ 15,00", "já está ocupado", "R$ 45,00"):
            self.assertIn(trecho, saida)
        self.assertEqual(self.ler().splitlines(), ["1|João|1|1|INTEIRA|30.0", "2|Maria|2|1|MEIA|30.0"])
        saida = self.executar(2, 4, 1, 3, 1, 1, 2, "Tentativa", 0)
        for trecho in ("João", "Maria", "R$ 45,00", "[XX]", "já está ocupado"):
            self.assertIn(trecho, saida)

    def test_02_cliente_e_cancelamento(self):
        saida = self.executar(1, 4, 3, 1, 2, 2, "\u2003", 0, 0)
        for trecho in ("MENU DO CLIENTE", "Opção inválida", "Texto inválido", "Operação cancelada"):
            self.assertIn(trecho, saida)
        self.assertFalse((self.dados / "vendas.txt").exists())

    def test_03_id_estavel_ao_reordenar_sessoes(self):
        self.executar(1, 3, 1, 1, 1, "Ana", 0)
        sessoes = self.ler("sessoes.txt").splitlines()
        self.escrever("sessoes.txt", "\n".join([sessoes[1], sessoes[0], sessoes[2]]) + "\n")
        saida = self.executar(1, 3, 1, 1, 1, "Bruno", 1, 3, 2, 1, 1, "Carlos", 0)
        self.assertIn("já está ocupado", saida)
        self.assertEqual(self.ler().splitlines(), ["1|Ana|1|1|INTEIRA|30.0", "2|Carlos|2|1|INTEIRA|30.0"])

    def test_04_arquivo_invalido_preservado(self):
        original = "1|Ana|1|1|INTEIRA|30.0\nlinha inválida\n"
        self.escrever("vendas.txt", original)
        saida = self.executar(1, 3, 1, 2, 2, "Lia", 0, sucesso=False)
        self.assertIn("Não foi possível carregar os dados", saida)
        self.assertNotIn("MENU DO CLIENTE", saida)
        self.assertEqual(self.ler(), original)

    def test_05_compra_persiste_mesmo_sem_saida_normal(self):
        saida = self.executar(1, 3, 1, 1, 1, "Pessoa")
        self.assertIn("COMPRA REALIZADA COM SUCESSO", saida)
        self.assertIn("Entrada encerrada", saida)
        self.assertEqual(self.ler().splitlines(), ["1|Pessoa|1|1|INTEIRA|30.0"])

    def test_06_capacidade_invalida(self):
        original = "1;-2\n2;30\n3;40\n"
        self.escrever("salas.txt", original)
        saida = self.executar(2, 0, sucesso=False)
        self.assertIn("salas.txt, linha 1", saida)
        self.assertEqual(self.ler("salas.txt"), original)

    def test_07_cadastro_e_recarga(self):
        saida = self.executar(2, 5, "História de Amanhã", "Ficção", 120, "Livre", 1,
                              6, 4, 25, 1, 7, 4, 4, "30/11/2026 19:30", 0)
        for trecho in ("Filme cadastrado", "Sala cadastrada", "Sessão cadastrada"):
            self.assertIn(trecho, saida)
        self.assertIn("4;História de Amanhã;Ficção;120;Livre", self.ler("filmes.txt"))
        self.assertIn("4;25", self.ler("salas.txt"))
        self.assertIn("4;4;4;2026-11-30T19:30", self.ler("sessoes.txt"))
        saida = self.executar(1, 2, 1, 3, 4, 25, 2, "Beatriz", 0)
        self.assertIn("História de Amanhã", saida)
        self.assertIn("30/11/2026 19:30", saida)
        self.assertEqual(self.ler().splitlines(), ["1|Beatriz|4|25|MEIA|30.0"])

    def test_08_horario_invalido_e_conflito(self):
        sessoes = self.ler("sessoes.txt")
        saida = self.executar(2, 7, 1, 1, "31/11/2026 19:30", 1,
                              7, 2, 1, "20/11/2026 19:00", 0)
        self.assertIn("Data ou horário inválido", saida)
        self.assertIn("Conflito de horário", saida)
        self.assertEqual(self.ler("sessoes.txt"), sessoes)

    def test_09_primeira_execucao_e_cadastro(self):
        shutil.rmtree(self.dados)
        saida = self.executar(2, 5, "Primeiro filme", "Aventura", 95, "Livre", 1,
                              6, 1, 10, 1, 7, 1, 1, "30/11/2026 20:30", 1,
                              3, 1, 10, 1, "Cliente", 0)
        self.assertIn("COMPRA REALIZADA COM SUCESSO", saida)
        self.assertEqual(self.ler().splitlines(), ["1|Cliente|1|10|INTEIRA|30.0"])

    def test_10_falha_de_gravacao_nao_confirma_nem_ocupa(self):
        # Simula a troca do destino por uma pasta após o início do atendimento.
        p = subprocess.Popen(["java", "-Dfile.encoding=UTF-8", "-cp", str(CLASSES), MAIN],
                             cwd=self.raiz, stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                             stderr=subprocess.PIPE, encoding="utf-8")
        self.addCleanup(self.finalizar, p)
        self.assertIn("====", p.stdout.readline())
        destino = self.dados / "vendas.txt"
        destino.mkdir()
        saida, erros = p.communicate("2\n3\n1\n1\n1\nAna\n1\n4\n1\n3\n1\n1\n2\nBia\n0\n", timeout=10)
        self.assertEqual(p.returncode, 0, erros)
        self.assertNotIn("COMPRA REALIZADA COM SUCESSO", saida)
        self.assertNotIn("já está ocupado", saida)
        self.assertIn("Nenhum(a) venda cadastrado(a)", saida)
        self.assertEqual(saida.count("não foi possível gravar"), 2)
        self.assertEqual(list(self.dados.glob("cinenet-*.tmp")), [])

    @staticmethod
    def finalizar(p):
        if p.poll() is None:
            p.kill()
            p.communicate()


if __name__ == "__main__":
    if not (CLASSES / "br/inatel/projetocinema/main/Main.class").exists():
        sys.exit("Compile primeiro o projeto em out/ conforme o README.md.")
    unittest.main(verbosity=2)
