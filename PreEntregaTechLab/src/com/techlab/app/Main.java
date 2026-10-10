package com.techlab.app;

import com.techlab.excepciones.StockInsuficienteException;
import com.techlab.pedidos.LineaPedido;
import com.techlab.pedidos.Pedido;
import com.techlab.productos.Producto;
import com.techlab.servicios.PedidoService;
import com.techlab.servicios.ProductoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ProductoService productoService = new ProductoService();
        PedidoService pedidoService = new PedidoService();
        Scanner scanner = new Scanner(System.in);
        boolean ejecutando = true;

        while (ejecutando) {
            mostrarMenu();
            System.out.print("Elija una opcion: ");
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    agregarProducto(scanner, productoService);
                    break;
                case "2":
                    listarProductos(productoService);
                    break;
                case "3":
                    buscarYActualizarProducto(scanner, productoService);
                    break;
                case "4":
                    eliminarProducto(scanner, productoService);
                    break;
                case "5":
                    crearPedido(scanner, productoService, pedidoService);
                    break;
                case "6":
                    listarPedidos(pedidoService);
                    break;
                case "7":
                    ejecutando = false;
                    System.out.println("Programa finalizado.");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n--- SISTEMA TECHLAB ---");
        System.out.println("1. Agregar producto");
        System.out.println("2. Listar productos");
        System.out.println("3. Buscar/Actualizar producto");
        System.out.println("4. Eliminar producto");
        System.out.println("5. Crear pedido");
        System.out.println("6. Listar pedidos");
        System.out.println("7. Salir");
    }

    private static void agregarProducto(Scanner scanner, ProductoService productoService) {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        Double precio = leerDouble(scanner, "Precio: ");
        Integer stock = leerEntero(scanner, "Stock: ");

        if (precio == null || stock == null) {
            return;
        }

        try {
            Producto producto = new Producto(nombre, precio, stock);
            productoService.agregarProducto(producto);
            System.out.println("Producto agregado con ID: " + producto.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listarProductos(ProductoService productoService) {
        List<Producto> productos = productoService.listarProductos();

        if (productos.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }

        System.out.println("\n--- PRODUCTOS ---");
        for (Producto producto : productos) {
            mostrarProducto(producto);
        }
    }

    private static void buscarYActualizarProducto(Scanner scanner, ProductoService productoService) {
        System.out.println("1. Buscar por ID");
        System.out.println("2. Buscar por nombre");
        System.out.print("Seleccione una opcion: ");
        String criterio = scanner.nextLine().trim();
        Producto producto;

        if (criterio.equals("1")) {
            Integer id = leerEntero(scanner, "ID: ");
            if (id == null) {
                return;
            }
            producto = productoService.buscarPorId(id);
        } else if (criterio.equals("2")) {
            System.out.print("Nombre: ");
            producto = productoService.buscarPorNombre(scanner.nextLine());
        } else {
            System.out.println("Opcion invalida.");
            return;
        }

        if (producto == null) {
            System.out.println("Producto no encontrado.");
            return;
        }

        mostrarProducto(producto);
        System.out.print("Desea actualizarlo? (s/n): ");

        if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
            return;
        }

        System.out.print("Nuevo nombre: ");
        String nombre = scanner.nextLine();
        Double precio = leerDouble(scanner, "Nuevo precio: ");
        Integer stock = leerEntero(scanner, "Nuevo stock: ");

        if (precio == null || stock == null) {
            return;
        }

        if (productoService.actualizarProducto(producto.getId(), nombre, precio, stock)) {
            System.out.println("Producto actualizado correctamente.");
        } else {
            System.out.println("No se pudo actualizar el producto. Revise los datos.");
        }
    }

    private static void eliminarProducto(Scanner scanner, ProductoService productoService) {
        Integer id = leerEntero(scanner, "ID del producto a eliminar: ");

        if (id == null) {
            return;
        }

        if (productoService.eliminarProducto(id)) {
            System.out.println("Producto eliminado correctamente.");
        } else {
            System.out.println("Producto no encontrado.");
        }
    }

    private static void crearPedido(
            Scanner scanner,
            ProductoService productoService,
            PedidoService pedidoService
    ) {
        Integer cantidadDeLineas = leerEntero(scanner, "Cuantos productos desea agregar al pedido? ");

        if (cantidadDeLineas == null || cantidadDeLineas <= 0) {
            System.out.println("Debe ingresar al menos un producto.");
            return;
        }

        List<LineaPedido> lineas = new ArrayList<>();

        for (int i = 1; i <= cantidadDeLineas; i++) {
            System.out.println("\nProducto " + i + ":");
            Integer idProducto = leerEntero(scanner, "ID del producto: ");
            Integer cantidad = leerEntero(scanner, "Cantidad: ");

            if (idProducto == null || cantidad == null) {
                return;
            }

            Producto producto = productoService.buscarPorId(idProducto);

            if (producto == null) {
                System.out.println("Producto no encontrado. Pedido cancelado.");
                return;
            }

            try {
                lineas.add(new LineaPedido(producto, cantidad));
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                return;
            }
        }

        try {
            Pedido pedido = pedidoService.crearPedido(lineas);
            System.out.println("Pedido " + pedido.getId() + " creado correctamente.");
            System.out.println("Total: $" + pedido.calcularTotal());
        } catch (StockInsuficienteException e) {
            System.out.println("No se pudo crear el pedido: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo crear el pedido: " + e.getMessage());
        }
    }

    private static void listarPedidos(PedidoService pedidoService) {
        List<Pedido> pedidos = pedidoService.listarPedidos();

        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos registrados.");
            return;
        }

        System.out.println("\n--- PEDIDOS ---");
        for (Pedido pedido : pedidos) {
            System.out.println("Pedido ID: " + pedido.getId());

            for (LineaPedido linea : pedido.getLineas()) {
                Producto producto = linea.getProducto();
                System.out.println(
                        "  - " + producto.getNombre()
                        + " | Cantidad: " + linea.getCantidad()
                        + " | Subtotal: $" + linea.calcularSubtotal()
                );
            }

            System.out.println("Total del pedido: $" + pedido.calcularTotal());
        }
    }

    private static Integer leerEntero(Scanner scanner, String mensaje) {
        System.out.print(mensaje);

        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Debe ingresar un numero entero valido.");
            return null;
        }
    }

    private static Double leerDouble(Scanner scanner, String mensaje) {
        System.out.print(mensaje);

        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Debe ingresar un numero valido.");
            return null;
        }
    }

    private static void mostrarProducto(Producto producto) {
        System.out.println(
                "ID: " + producto.getId()
                + " | Nombre: " + producto.getNombre()
                + " | Precio: $" + producto.getPrecio()
                + " | Stock: " + producto.getStock()
        );
    }
}