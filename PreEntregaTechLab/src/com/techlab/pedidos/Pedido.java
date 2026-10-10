package com.techlab.pedidos;

import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private static int proximoId = 1;

    private final int id;
    private final ArrayList<LineaPedido> lineas = new ArrayList<>();

    public Pedido() {
        this.id = proximoId++;
    }

    public int getId() {
        return id;
    }

    public List<LineaPedido> getLineas() {
        return new ArrayList<>(lineas);
    }

    public void agregarLinea(LineaPedido linea) {
        lineas.add(linea);
    }

    public double calcularTotal() {
        double total = 0;

        for (LineaPedido linea : lineas) {
            total += linea.calcularSubtotal();
        }

        return total;
    }
}