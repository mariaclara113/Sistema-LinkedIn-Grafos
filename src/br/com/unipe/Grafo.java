package br.com.unipe;
import java.util.*;

public class Grafo {
    private final List<Aresta> arestas;
    private final List<Vertice> vertices;
    private boolean eDirigido;
    private int ordem;
    private int tamanho;
    private final boolean ePonderado;

    public Grafo() {
        this(false, false);
    }

    public Grafo(boolean eDirigido, boolean ePonderado) {
        this.eDirigido = eDirigido;
        this.ePonderado = ePonderado;
        arestas = new ArrayList<>();
        vertices = new ArrayList<>();
    }

    public void adicionaVertices(String... nomes) {
        for (String nome : nomes) {
            vertices.add(new Vertice(nome));
            ordem++;
        }
    }

    public void addAresta(String v1, String v2) {
        arestas.add(criaAresta("", v1, v2, null));
    }

    public void addAresta(String v1, String v2, int peso) {
        arestas.add(criaAresta("", v1, v2, peso));
    }

    public void addAresta(String nome, String v1, String v2) {
        arestas.add(criaAresta(nome, v1, v2, null));
    }

    public void addAresta(String nome, String v1, String v2, int peso) {
        arestas.add(criaAresta(nome, v1, v2, peso));
    }

    private Aresta criaAresta(String nomeAresta, String nomeVertice1, String nomeVertice2, Integer peso) {
        Vertice v1 = encontraVertice(nomeVertice1).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + nomeVertice1 + " não encontrado."));
        Vertice v2 = encontraVertice(nomeVertice2).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + nomeVertice2 + " não encontrado."));
        if (!eDirigido) {
            infereSeGrafoEDirecionado(v1, v2);
        }
        aumentaGrauDosVertices(v1, v2);
        resolveAdjacencias(v1, v2);
        tamanho++;
        return new Aresta(nomeAresta, v1, v2, peso);
    }

    private void resolveAdjacencias(Vertice v1, Vertice v2) {
        v1.adicionaAdjacencia(v2); // v1 envia p v2
        v2.adicionaAdjacente(v1); // v2 recebe de v1
        if (!eDirigido) {
            v1.adicionaAdjacente(v2);
            v2.adicionaAdjacencia(v1);
        }
    }

    private void aumentaGrauDosVertices(Vertice v1, Vertice v2) {
        if (eDirigido) {
            v1.aumentaOutDegree();
            v2.aumentaInDegree();
        } else {
            v1.aumentaGrau();
            v2.aumentaGrau();
        }
    }

    private void infereSeGrafoEDirecionado(Vertice v1, Vertice v2) {
        if (eSelfLoop(v1, v2)) {
            reprocessamentoParaDigrafo();
        } else {
            for (Aresta aresta : arestas) {
                if (eViaMaoDupla(v1, v2, aresta) || eArestaDuplicada(v2, v1, aresta)) {
                    reprocessamentoParaDigrafo();
                    break;
                }
            }
        }
    }

    private static boolean eArestaDuplicada(Vertice v1, Vertice v2, Aresta aresta) {
        return aresta.getVerticeOrigem().equals(v1) && aresta.getVerticeDestino().equals(v2);
    }

    private static boolean eViaMaoDupla(Vertice v1, Vertice v2, Aresta aresta) {
        return aresta.getVerticeOrigem().equals(v2) && aresta.getVerticeDestino().equals(v1);
    }

    private static boolean eSelfLoop(Vertice v1, Vertice v2) {
        return v1.getNome().equals(v2.getNome());
    }

    public Optional<Vertice> encontraVertice(String nome) {
        for (Vertice vertice : vertices) {
            if (vertice.getNome().equalsIgnoreCase(nome)) {
                return Optional.of(vertice);
            }
        }
        return Optional.empty();
    }

    private void reprocessamentoParaDigrafo() {
        eDirigido = true;
        System.out.println("Reprocessamento para digrafo necessário. O grafo agora é direcionado.");
        limpezaGrausEAdjacencias();
        recalculaGrausEAdjacencias();
    }

    private void recalculaGrausEAdjacencias() {
        arestas.forEach(aresta -> {
            Vertice origem = aresta.getVerticeOrigem();
            Vertice destino = aresta.getVerticeDestino();
            aumentaGrauDosVertices(origem, destino);
            resolveAdjacencias(origem, destino);
        });
    }

    private void limpezaGrausEAdjacencias() {
        vertices.forEach(vertice -> {
            vertice.resetaGraus();
            vertice.resetaAdjacenciasEAdjacentes();
        });
    }

    public String exibeGrausDosVertices() {
        StringBuilder graus = new StringBuilder();
        for (Vertice vertice : vertices) {
            graus.append(vertice.exibeGraus());
        }
        return graus.toString();
    }

    public String exibeAdjacencias() {
        StringBuilder adjacencias = new StringBuilder();
        for (Vertice vertice : vertices) {
            adjacencias.append("\n").append(vertice.getNome()).append(": ").append(vertice.getAdjacencias());
        }
        return adjacencias.toString();
    }

    public String exibeAdjacentes() {
        StringBuilder adjacencias = new StringBuilder();
        for (Vertice vertice : vertices) {
            adjacencias.append("\n").append(vertice.getNome()).append(": ").append(vertice.getAdjacentes());
        }
        return adjacencias.toString();
    }

    public void exibeMatrizAdjacencia() {
        List<Vertice> verticesOrdenados = vertices.stream().sorted(Comparator.comparing(Vertice::getNome)).toList();

        StringBuilder matriz = new StringBuilder("\nMatriz de Adjacência\n");
        matriz.append("\t");
        verticesOrdenados.forEach(v -> matriz.append(v.getNome()).append("\t"));
        matriz.append("\n");

        for (Vertice vertice : verticesOrdenados) { // read-only
            matriz.append(vertice.getNome()).append("\t");
            List<Vertice> adjacencias = vertice.getAdjacencias();
            for (Vertice outroVertice : verticesOrdenados) {
                matriz.append(adjacencias.contains(outroVertice) ? "1" : "0").append("\t");
            }
            matriz.append("\n");
        }

        System.out.println(matriz);
    }

    public void exibeMatrizIncidencia() {
        List<Vertice> verticesOrdenados = vertices.stream().sorted(Comparator.comparing(Vertice::getNome)).toList();
        StringBuilder matriz = new StringBuilder("\nMatriz de Incidência\n\t");
        arestas.forEach(a -> matriz.append(a.getNome()).append("\t"));
        matriz.append("\n");
        for (Vertice vertice : verticesOrdenados) {
            matriz.append(vertice.getNome()).append("\t");
            for (Aresta aresta : arestas) {
                Vertice origem = aresta.getVerticeOrigem();
                Vertice destino = aresta.getVerticeDestino();
                String valor;
                if (origem.equals(vertice) && destino.equals(vertice)) {
                    valor = " 2";
                } else if (origem.equals(vertice)) {
                    valor = eDirigido ? "-1" : "1";
                } else if (destino.equals(vertice)) {
                    valor = " 1";
                } else { // caso contrário
                    valor = " 0";
                }
                matriz.append(valor).append("\t");
            }
            matriz.append("\n");
        }
        System.out.println(matriz);
    }

    public List<String> dfsIterativo(String origem, String destino) {
        Vertice verticeOrigem = encontraVertice(origem).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + origem + " não encontrado."));
        Vertice verticeDestino = destino == null ? null
                : encontraVertice(destino).orElseThrow(
                        () -> new IllegalArgumentException("Vertice " + destino + " não encontrado."));

        Stack<Vertice> pilha = new Stack<>();
        List<Vertice> visitados = new ArrayList<>();
        StringBuilder percurso = new StringBuilder("Percurso = ");

        visitados.add(verticeOrigem);
        pilha.push(verticeOrigem);

        percurso.append(verticeOrigem.getNome()).append(", ");

        while (!pilha.isEmpty()) {
            Vertice atual = pilha.peek();

            if (atual.equals(verticeDestino)) {
                break;
            }

            List<Vertice> adjacencias = atual.getAdjacencias();
            List<Vertice> adjacenciasOrdenadas = adjacencias.stream().sorted(Comparator.comparing(Vertice::getNome))
                    .toList();

            // Pegue a primeira adjacência não visitada
            Optional<Vertice> proximo = adjacenciasOrdenadas.stream().filter(a -> !visitados.contains(a)).findFirst();

            if (proximo.isPresent()) {
                Vertice adjacencia = proximo.get();
                visitados.add(adjacencia);
                percurso.append(adjacencia.getNome()).append(", ");
                pilha.push(adjacencia); // avança para o primeiro vizinho não visitado
            } else {
                pilha.pop(); // vértice esgotado: remove da pilha
            }
        }

        System.out.println(percurso);
        return visitados.stream().map(Vertice::getNome).toList();
    }

    public List<String> dfsRecursivo(String origem, String destino, List<Vertice> visitados) {
        final List<Vertice> visitadosAtual = visitados != null ? visitados : new ArrayList<>();

        Vertice v = encontraVertice(origem).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + origem + " não encontrado."));
        visitadosAtual.add(v);

        if (origem.equals(destino)) {
            return visitadosAtual.stream().map(Vertice::getNome).toList();
        }

        // itera os vizinhos um a um — após backtrack, os já visitados são pulados pelo
        // contains()
        // espelhando o peek() + findFirst() do iterativo
        for (Vertice adj : v.getAdjacencias()) {
            if (visitadosAtual.contains(adj)) {
                continue;
            }

            dfsRecursivo(adj.getNome(), destino, visitadosAtual);

            // se destino foi encontrado em algum ramo, propaga o resultado
            if (destino != null && visitadosAtual.stream().anyMatch(x -> x.getNome().equals(destino))) {
                return visitadosAtual.stream().map(Vertice::getNome).toList();
            }
        }

        // vértice esgotado (sem vizinhos não visitados): retorna o percurso até aqui
        return visitadosAtual.stream().map(Vertice::getNome).toList();
    }

    public int encontraComprimentoCaminho(String... caminho) {
        if (!ePonderado) {
            return caminho.length - 1; // qtd de arestas percorridas
        }
        int comprimento = 0;
        List<Aresta> arestasPercorridas = new ArrayList<>();

        for (int i = 0; i < caminho.length - 1; i++) {
            int indiceAtual = i;
            Vertice origem = encontraVertice(caminho[indiceAtual]).orElseThrow(
                    () -> new IllegalArgumentException("Vertice " + caminho[indiceAtual] + " não encontrado."));
            Vertice destino = encontraVertice(caminho[indiceAtual + 1]).orElseThrow(
                    () -> new IllegalArgumentException("Vertice " + caminho[indiceAtual + 1] + " não encontrado."));
            Optional<Aresta> aresta = arestas.stream()
                    .filter(a -> a.getVerticeOrigem().equals(origem) && a.getVerticeDestino().equals(destino))
                    .findFirst();
            if (aresta.isPresent()) {
                if (arestasPercorridas.contains(aresta.get())) {
                    throw new IllegalArgumentException("Aresta repetida!");
                }
                arestasPercorridas.add(aresta.get());
                comprimento += aresta.get().getPeso();
            }
        }
        return comprimento;
    }

    public boolean eConexo() {
        for (Vertice v : vertices)
            if (v.getInDegree() == 0 || v.getOutDegree() == 0) {
                return false;
            }

        for (Vertice v : vertices) {
            List<String> caminho = dfsIterativo(v.getNome(), null);
            if (caminho.size() < vertices.size()) {
                return false;
            }
        }
        return true;
    }

    public boolean eConexoSimplificado() {
        if (vertices.stream().anyMatch(v -> v.getInDegree() == 0 || v.getOutDegree() == 0)) {
            return false;
        }
        return vertices.stream().noneMatch(v -> dfsIterativo(v.getNome(), null).size() < vertices.size());
    }

    public List<String> greedySearch(String nomeVerticeOrigem, String nomeVerticeDestino) {
        List<Vertice> verticesVisitados = new ArrayList<>();
        int comprimentoCaminho = 0;

        Vertice verticeOrigem = encontraVertice(nomeVerticeOrigem).orElseThrow();
        Vertice verticeDestino = encontraVertice(nomeVerticeDestino).orElseThrow();

        verticesVisitados.add(verticeOrigem);
        Vertice atual = verticeOrigem;

        while (!atual.equals(verticeDestino)) {
            Vertice verticeAlvo = atual;

            // Otimização: Pegamos direto os vizinhos sem iterar sobre arestas do grafo
            // inteiro
            List<Vertice> adjacencias = verticeAlvo.getAdjacencias();
            if (adjacencias == null || adjacencias.isEmpty()) {
                System.out.println("Caminho não encontrado. Busca falhou em: " + atual.getNome());
                return null;
            }

            // Busca a aresta não percorrida com o menor peso baseada nos vizinhos do
            // vértice atual
            List<Aresta> arestasVizinhas = new ArrayList<>();
            for (Vertice vizinho : adjacencias) {
                if (!verticesVisitados.contains(vizinho)) {
                    arestasVizinhas.addAll(obtemArestasParaVizinho(verticeAlvo, vizinho));
                }
            }

            // Se não houver arestas vizinhas, significa que não há caminho para o destino
            if (arestasVizinhas.isEmpty()) {
                System.out.println("Caminho não encontrado. Busca falhou em: " + atual.getNome());
                return null;
            }

            // Pega a aresta com o menor peso
            Aresta melhorAresta = arestasVizinhas.stream()
                    .min(Comparator.comparing(Aresta::getPeso))
                    .orElseThrow();

            comprimentoCaminho += melhorAresta.getPeso() != null ? melhorAresta.getPeso() : 0;
            atual = obtemVerticeOposto(melhorAresta, verticeAlvo);
            verticesVisitados.add(atual);

            System.out.println("Percorrendo aresta " + melhorAresta.getNome() +
                    " (peso " + melhorAresta.getPeso() +
                    ") para o vértice " + atual.getNome());
        }

        List<String> nomesVisitados = verticesVisitados.stream().map(Vertice::getNome).toList();

        System.out.println("Destino " + verticeDestino.getNome() + " encontrado! Busca concluída com sucesso.");
        System.out.println("Caminho: " + String.join(" -> ", nomesVisitados));
        System.out.println("Comprimento do caminho: " + comprimentoCaminho);

        return nomesVisitados;
    }


    private List<Aresta> obtemArestasParaVizinho(Vertice atual, Vertice vizinho) {
        return arestas.stream()
                .filter(a -> (a.getVerticeOrigem().equals(atual) && a.getVerticeDestino().equals(vizinho)) ||
                        (!eDirigido && a.getVerticeDestino().equals(atual) && a.getVerticeOrigem().equals(vizinho)))
                .toList();
    }

    private Vertice obtemVerticeOposto(Aresta aresta, Vertice vertice) {
        return aresta.getVerticeOrigem().equals(vertice) ? aresta.getVerticeDestino() : aresta.getVerticeOrigem();
    }

    /**
     * Retorna os nomes de todos os vértices cadastrados no grafo.
     * Usado, por exemplo, para varrer a rede inteira em busca de componentes
     * conexas (sub-redes isoladas).
     */
    public List<String> getNomesVertices() {
        return vertices.stream().map(Vertice::getNome).toList();
    }

    /**
     * Algoritmo de Dijkstra: encontra o caminho de menor custo acumulado (soma
     * de pesos) entre dois vértices em um grafo ponderado. Caso não haja peso
     * definido para alguma aresta (grafo não ponderado), assume-se peso 1.
     *
     * @param nomeOrigem  nome do vértice de origem
     * @param nomeDestino nome do vértice de destino
     * @return um {@link ResultadoCaminho} contendo a sequência de nomes do
     *         caminho encontrado e o custo total. Se não houver caminho possível,
     *         retorna caminho vazio e custo -1.
     */
    public ResultadoCaminho dijkstra(String nomeOrigem, String nomeDestino) {
        Vertice origem = encontraVertice(nomeOrigem).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + nomeOrigem + " não encontrado."));
        Vertice destino = encontraVertice(nomeDestino).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + nomeDestino + " não encontrado."));

        Map<Vertice, Integer> distancias = new HashMap<>();
        Map<Vertice, Vertice> predecessores = new HashMap<>();
        for (Vertice v : vertices) {
            distancias.put(v, Integer.MAX_VALUE);
        }
        distancias.put(origem, 0);

        // Fila de prioridade com deleção preguiçosa (lazy deletion): em vez de tentar
        // atualizar a posição de um elemento já inserido (Java não suporta
        // decrease-key nativamente), inserimos uma nova entrada a cada relaxamento e
        // ignoramos entradas "desatualizadas" quando retiradas da fila.
        PriorityQueue<Map.Entry<Vertice, Integer>> fila = new PriorityQueue<>(Map.Entry.comparingByValue());
        fila.add(Map.entry(origem, 0));

        Set<Vertice> visitados = new HashSet<>();

        while (!fila.isEmpty()) {
            Map.Entry<Vertice, Integer> entradaAtual = fila.poll();
            Vertice atual = entradaAtual.getKey();

            if (visitados.contains(atual)) {
                continue; // entrada desatualizada, já processamos esse vértice com distância melhor
            }
            visitados.add(atual);

            if (atual.equals(destino)) {
                break;
            }

            for (Vertice vizinho : atual.getAdjacencias()) {
                if (visitados.contains(vizinho)) {
                    continue;
                }

                int pesoAresta = obtemArestasParaVizinho(atual, vizinho).stream()
                        .map(Aresta::getPeso)
                        .filter(Objects::nonNull)
                        .min(Integer::compareTo)
                        .orElse(1);

                int novaDistancia = distancias.get(atual) + pesoAresta;
                if (novaDistancia < distancias.get(vizinho)) {
                    distancias.put(vizinho, novaDistancia);
                    predecessores.put(vizinho, atual);
                    fila.add(Map.entry(vizinho, novaDistancia));
                }
            }
        }

        if (distancias.get(destino) == Integer.MAX_VALUE) {
            return new ResultadoCaminho(new ArrayList<>(), -1);
        }

        LinkedList<String> caminho = new LinkedList<>();
        Vertice passo = destino;
        while (passo != null) {
            caminho.addFirst(passo.getNome());
            passo = predecessores.get(passo);
        }

        return new ResultadoCaminho(caminho, distancias.get(destino));
    }

    /**
     * Estrutura simples para carregar o resultado de uma busca de caminho:
     * a sequência de vértices (nomes) percorrida e o custo total acumulado.
     */
    public static class ResultadoCaminho {
        private final List<String> caminho;
        private final int custo;

        public ResultadoCaminho(List<String> caminho, int custo) {
            this.caminho = caminho;
            this.custo = custo;
        }

        public List<String> getCaminho() {
            return caminho;
        }

        public int getCusto() {
            return custo;
        }

        public boolean existeCaminho() {
            return custo != -1 && !caminho.isEmpty();
        }

        @Override
        public String toString() {
            return existeCaminho()
                    ? String.join(" -> ", caminho) + " (custo: " + custo + ")"
                    : "Sem caminho possível";
        }
    }

    @Override
    public String toString() {
        return """
                direcionado = %s,
                ordem = %d,
                tamanho = %d,
                vertices = %s,
                arestas = %s,
                graus = %s,
                adjacencias = %s,
                adjacentes = %s
                }""".formatted(eDirigido ? "sim" : "não", ordem, tamanho, vertices, arestas, exibeGrausDosVertices(),
                exibeAdjacencias(), exibeAdjacentes());
    }
}
