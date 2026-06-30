package br.com.unipe;

import java.util.List;

public class LinkedInApp {

    public static void main(String[] args) {

        Grafo grafo = montaRedeSocial();
        LinkedInAnalyzer analyzer = new LinkedInAnalyzer(grafo);

        System.out.println("=========================================");
        System.out.println(" LINKEDIN ANALYZER - UNIPE 2026");
        System.out.println("=========================================\n");

        testaSugestaoDeConexoes(analyzer, "Ana");
        testaGrauDeSeparacao(analyzer, "Ana", "Fernanda");
        testaRotaDeAfinidade(analyzer, grafo, "Ana", "Fernanda");
        testaMapeamentoDeSubRedes(analyzer);
    }

    private static Grafo montaRedeSocial() {

        Grafo grafo = new Grafo(false, true);

        grafo.adicionaVertices(
                "Ana", "Bruno", "Carlos",
                "Daniela", "Eduardo", "Fernanda",
                "Gabriel", "Hugo",
                "Igor", "Juliana"
        );

        grafo.addAresta("Ana", "Bruno", 1);
        grafo.addAresta("Ana", "Carlos", 2);
        grafo.addAresta("Ana", "Daniela", 8);
        grafo.addAresta("Bruno", "Eduardo", 1);
        grafo.addAresta("Carlos", "Eduardo", 1);
        grafo.addAresta("Daniela", "Fernanda", 5);
        grafo.addAresta("Eduardo", "Fernanda", 1);

        grafo.addAresta("Gabriel", "Hugo", 1);
        grafo.addAresta("Igor", "Juliana", 1);

        return grafo;
    }

    // MISSÃO 2
    private static void testaSugestaoDeConexoes(LinkedInAnalyzer analyzer, String pessoa) {

        System.out.println("--- Missao 2: Sugestao de Conexoes para " + pessoa + " ---");

        List<String> sugestoes = analyzer.sugerirConexoes(pessoa);

        if (sugestoes.isEmpty()) {
            System.out.println("Nenhuma sugestao encontrada.");
        } else {
            sugestoes.forEach(s -> System.out.println("  -> " + s));
        }

        System.out.println();
    }

    // MISSÃO 3
    private static void testaGrauDeSeparacao(LinkedInAnalyzer analyzer, String origem, String destino) {

        System.out.println("--- Missao 3: Grau de Separacao entre " + origem + " e " + destino + " ---");

        int grau = analyzer.grauSeparacao(origem, destino);

        System.out.println("  Grau de separacao: " + grau +
                (grau == -1 ? " (isolados)" : " salto(s)"));

        System.out.println();
    }

    // MISSÃO 4 (corrigida)
    private static void testaRotaDeAfinidade(LinkedInAnalyzer analyzer, Grafo grafo,
                                             String origem, String destino) {

        System.out.println("--- Missao 4: Rota de Afinidade entre " + origem + " e " + destino + " ---");

        Grafo.ResultadoCaminho resultado = grafo.dijkstra(origem, destino);

        if (resultado.existeCaminho()) {
            System.out.println("  Caminho: " + String.join(" -> ", resultado.getCaminho()));
            System.out.println("  Custo total: " + resultado.getCusto());
        } else {
            System.out.println("  Sem caminho possível.");
        }

        System.out.println();
    }

    // MISSÃO 5
    private static void testaMapeamentoDeSubRedes(LinkedInAnalyzer analyzer) {

        System.out.println("--- Missao 5: Sub-redes ---");

        List<List<String>> grupos = analyzer.gruposIsolados();

        for (int i = 0; i < grupos.size(); i++) {
            System.out.println("  Sub-rede " + (i + 1) + ": " + grupos.get(i));
        }

        System.out.println();
    }
}