package com.techlab.servicios;

import com.techlab.productos.Producto;

import java.util.ArrayList;
import java.util.List;

public class ProductoService {
    private final ArrayList<Producto> productos = new ArrayList<>();

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public List<Producto> listarProductos() {
        return new ArrayList<>(productos);
    }

    public Producto buscarPorId(int id) {
        for (Producto producto : productos) {
            if (producto.getId() == id) {
                return producto;
            }
        }
        return null;
    }

    public Producto buscarPorNombre(String nombre) {
        if (nombre == null) {
            return null;
        }

        for (Producto producto : productos) {
            if (producto.getNombre().equalsIgnoreCase(nombre.trim())) {
                return producto;
            }
        }
        return null;
    }

    public boolean actualizarProducto(int id, String nombre, double precio, int stock) {
        Producto producto = buscarPorId(id);

        if (producto == null) {
            return false;
        }

        if (nombre == null || nombre.trim().isEmpty() || precio < 0 || stock < 0) {
            return false;
        }

        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        return true;
    }

    public boolean eliminarProducto(int id) {
        Producto producto = buscarPorId(id);

        if (producto == null) {
            return false;
        }

        productos.remove(producto);
        return true;
    }
}