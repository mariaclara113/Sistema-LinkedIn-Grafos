package br.com.unipe;

import java.util.ArrayList;
import java.util.List;

public class Vertice {

    private String nome;
    private int grau;
    private int inDegree;
    private int outDegree;

    private List<Vertice> adjacencias; // saída
    private List<Vertice> adjacentes;  // entrada

    public Vertice(String nome) {
        this.nome = nome;
        adjacencias = new ArrayList<>();
        adjacentes = new ArrayList<>();
    }

    // =========================
    // GETTERS (ESSENCIAIS)
    // =========================
    public String getNome() {
        return nome;
    }

    public List<Vertice> getAdjacencias() {
        return adjacencias;
    }

    public List<Vertice> getAdjacentes() {
        return adjacentes;
    }

    public int getInDegree() {
        return inDegree;
    }

    public int getOutDegree() {
        return outDegree;
    }

    public int getGrau() {
        return grau;
    }

    // =========================
    // MÉTODOS DE CONTROLE
    // =========================
    public void resetaGraus() {
        grau = inDegree = outDegree = 0;
    }

    public void resetaAdjacenciasEAdjacentes() {
        adjacencias.clear();
        adjacentes.clear();
    }

    public void aumentaGrau() {
        grau++;
    }

    public void aumentaInDegree() {
        grau++;
        inDegree++;
    }

    public void aumentaOutDegree() {
        grau++;
        outDegree++;
    }

    public void adicionaAdjacencia(Vertice vertice) {
        adjacencias.add(vertice);
    }

    public void adicionaAdjacente(Vertice vertice) {
        adjacentes.add(vertice);
    }

    public String exibeGraus() {
        return "\n%s: grau %d (%d in | %d out)"
                .formatted(nome, grau, inDegree, outDegree);
    }

    @Override
    public String toString() {
        return nome;
    }
}