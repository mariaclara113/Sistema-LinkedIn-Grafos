package br.com.unipe;

import java.util.*;

public class LinkedInAnalyzer {

    private final Grafo grafo;

    public LinkedInAnalyzer(Grafo grafo) {
        this.grafo = grafo;
    }

    // ==================================================
    // 1. Sugestão de conexões (amigos de 2º grau)
    // ==================================================
    public List<String> sugerirConexoes(String usuario) {

        Vertice v = grafo.encontraVertice(usuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Set<String> diretos = new HashSet<>();
        Map<String, Integer> comuns = new HashMap<>();

        for (Vertice a : v.getAdjacencias()) {
            diretos.add(a.getNome());
        }

        for (Vertice amigo : v.getAdjacencias()) {
            for (Vertice amigoDoAmigo : amigo.getAdjacencias()) {

                String nome = amigoDoAmigo.getNome();

                if (nome.equals(usuario)) continue;
                if (diretos.contains(nome)) continue;

                comuns.put(nome, comuns.getOrDefault(nome, 0) + 1);
            }
        }

        List<String> resultado = new ArrayList<>(comuns.keySet());

        resultado.sort((a, b) -> comuns.get(b) - comuns.get(a));

        return resultado;
    }

    // ==================================================
    // 2. Grau de separação
    // ==================================================
    public int grauSeparacao(String origem, String destino) {
        return grafo.dijkstra(origem, destino).getCusto();
    }

    // ==================================================
    // 3. Melhor rota (afinidade)
    // ==================================================
    public Grafo.ResultadoCaminho melhorRota(String origem, String destino) {
        return grafo.dijkstra(origem, destino);
    }

    // ==================================================
    // 4. Grupos isolados (componentes conexos)
    // ==================================================
    public List<List<String>> gruposIsolados() {

        Set<String> visitados = new HashSet<>();
        List<List<String>> grupos = new ArrayList<>();

        for (String nome : grafo.getNomesVertices()) {

            if (visitados.contains(nome)) continue;

            List<String> grupo = new ArrayList<>();
            Stack<String> stack = new Stack<>();

            stack.push(nome);
            visitados.add(nome);

            while (!stack.isEmpty()) {
                String atual = stack.pop();
                grupo.add(atual);

                Vertice v = grafo.encontraVertice(atual).get();

                for (Vertice vizinho : v.getAdjacencias()) {
                    String n = vizinho.getNome();

                    if (!visitados.contains(n)) {
                        visitados.add(n);
                        stack.push(n);
                    }
                }
            }

            grupos.add(grupo);
        }

        return grupos;
    }
}