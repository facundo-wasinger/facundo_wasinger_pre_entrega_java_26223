package com.techlab.servicios;

import com.techlab.excepciones.StockInsuficienteException;
import com.techlab.pedidos.LineaPedido;
import com.techlab.pedidos.Pedido;
import com.techlab.productos.Producto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedidoService {
    private final ArrayList<Pedido> pedidos = new ArrayList<>();

    public Pedido crearPedido(List<LineaPedido> lineas) throws StockInsuficienteException {
        if (lineas == null || lineas.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos una linea.");
        }

        Map<Producto, Integer> cantidadesSolicitadas = new HashMap<>();

        for (LineaPedido linea : lineas) {
            Producto producto = linea.getProducto();
            cantidadesSolicitadas.merge(producto, linea.getCantidad(), Integer::sum);
        }

        for (Map.Entry<Producto, Integer> entrada : cantidadesSolicitadas.entrySet()) {
            Producto producto = entrada.getKey();
            int cantidadSolicitada = entrada.getValue();

            if (cantidadSolicitada > producto.getStock()) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para " + producto.getNombre()
                        + ". Disponible: " + producto.getStock()
                        + ", solicitado: " + cantidadSolicitada
                );
            }
        }

        Pedido pedido = new Pedido();

        for (LineaPedido linea : lineas) {
            pedido.agregarLinea(linea);
        }

        for (Map.Entry<Producto, Integer> entrada : cantidadesSolicitadas.entrySet()) {
            Producto producto = entrada.getKey();
            producto.setStock(producto.getStock() - entrada.getValue());
        }

        pedidos.add(pedido);
        return pedido;
    }

    public List<Pedido> listarPedidos() {
        return new ArrayList<>(pedidos);
    }
}