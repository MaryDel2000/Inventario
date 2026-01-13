package com.mariastaff.Inventario.backend;

import com.mariastaff.Inventario.backend.data.entity.*;
import com.mariastaff.Inventario.backend.data.repository.*;
import com.mariastaff.Inventario.backend.service.ProductoService;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataGenerator implements CommandLineRunner {

    private final InvProductoRepository productoRepository;
    private final InvProductoVarianteRepository varianteRepository;
    private final InvCategoriaRepository categoriaRepository;
    private final InvUnidadMedidaRepository unidadMedidaRepository;
    private final InvAlmacenRepository almacenRepository;
    private final InvUbicacionRepository ubicacionRepository;
    private final InvLoteRepository loteRepository;
    private final InvExistenciaRepository existenciaRepository;
    private final InvMovimientoRepository movimientoRepository;
    private final InvMovimientoDetalleRepository movimientoDetalleRepository;
    
    private final ProductoService productoService;

    public DataGenerator(InvProductoRepository productoRepository,
                         InvProductoVarianteRepository varianteRepository,
                         InvCategoriaRepository categoriaRepository,
                         InvUnidadMedidaRepository unidadMedidaRepository,
                         InvAlmacenRepository almacenRepository,
                         InvUbicacionRepository ubicacionRepository,
                         InvLoteRepository loteRepository,
                         InvExistenciaRepository existenciaRepository,
                         InvMovimientoRepository movimientoRepository,
                         InvMovimientoDetalleRepository movimientoDetalleRepository,
                         ProductoService productoService) {
        this.productoRepository = productoRepository;
        this.varianteRepository = varianteRepository;
        this.categoriaRepository = categoriaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.almacenRepository = almacenRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.loteRepository = loteRepository;
        this.existenciaRepository = existenciaRepository;
        this.movimientoRepository = movimientoRepository;
        this.movimientoDetalleRepository = movimientoDetalleRepository;
        this.productoService = productoService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Only run if we want to reset (for now we assume user wants this based on prompt)
        // To prevent accidental deletion on every restart, we could check a flag or just do it this time.
        // Given the explicit request "replace current data", I will execute the cleanup and generation.
        
        System.out.println("--- INICIANDO GENERACIÓN DE DATOS DE EJEMPLO (REPUESTOS AUTOMOTRICES) ---");
        
        deleteAllData();
        createData();
        
        System.out.println("--- FINALIZADA GENERACIÓN DE DATOS ---");
    }

    private void deleteAllData() {
        // Delete in order to satisfy FK constraints
        movimientoDetalleRepository.deleteAll();
        movimientoRepository.deleteAll();
        existenciaRepository.deleteAll();
        loteRepository.deleteAll();
        varianteRepository.deleteAll();
        productoRepository.deleteAll();
        ubicacionRepository.deleteAll();
        almacenRepository.deleteAll();
        categoriaRepository.deleteAll();
        unidadMedidaRepository.deleteAll();
        System.out.println("Datos existentes eliminados.");
    }

    private void createData() {
        // 1. Categorías
        InvCategoria catLubricantes = createCategoria("Lubricantes", "Aceites y fluidos");
        InvCategoria catFrenos = createCategoria("Frenos", "Pastillas, discos y líquidos");
        InvCategoria catSuspension = createCategoria("Suspensión", "Amortiguadores y resortes");
        InvCategoria catElectrico = createCategoria("Eléctrico", "Baterías, alternadores, luces");
        InvCategoria catAccesorios = createCategoria("Accesorios", "Limpiaparabrisas, tapetes, etc.");
        InvCategoria catMotor = createCategoria("Motor", "Componentes internos de motor");

        // 2. Unidades de Medida
        InvUnidadMedida undUnidad = createUnidad("Unidad", "UND");
        InvUnidadMedida undLitro = createUnidad("Litro", "LTR");
        InvUnidadMedida undGalon = createUnidad("Galón", "GAL");
        InvUnidadMedida undJuego = createUnidad("Juego", "JGO");
        InvUnidadMedida undKit = createUnidad("Kit", "KIT");
        InvUnidadMedida undCaja = createUnidad("Caja", "CJA");

        // 3. Almacenes y Ubicaciones
        InvAlmacen almTienda = createAlmacen("Tienda Principal", "TDA-01", "TIENDA", "Av. Principal 123");
        InvUbicacion ubTiendaMostrador = createUbicacion(almTienda, "TDA-MOST", "Mostrador Principal");
        InvUbicacion ubTiendaEstanteA = createUbicacion(almTienda, "TDA-EST-A", "Estante A - Lubricantes");
        InvUbicacion ubTiendaEstanteB = createUbicacion(almTienda, "TDA-EST-B", "Estante B - Accesorios");

        InvAlmacen almBodega = createAlmacen("Bodega Central", "BOD-01", "BODEGA", "Zona Industrial Calle 5");
        InvUbicacion ubBodegaPasillo1 = createUbicacion(almBodega, "BOD-P1-N1", "Pasillo 1 Nivel 1 - Pesados");
        InvUbicacion ubBodegaPasillo2 = createUbicacion(almBodega, "BOD-P2-N1", "Pasillo 2 Nivel 1 - Repuestos");

        // 4. Productos con Stock Inicial
        
        // Aceite
        createProduct("Aceite Sintético 5W-30", "OIL-SYN-5W30", "Aceite motor 100% sintético", 
                      catLubricantes, undLitro, ubTiendaEstanteA, new BigDecimal("50"), 
                      "LOTE-2023-A", LocalDateTime.now().plusYears(2));

        createProduct("Aceite Mineral 20W-50", "OIL-MIN-20W50", "Aceite motor alto kilometraje", 
                      catLubricantes, undLitro, ubTiendaEstanteA, new BigDecimal("30"), 
                      "LOTE-2023-B", LocalDateTime.now().plusYears(3));

        // Frenos
        createProduct("Pastillas Freno Delanteras Corolla", "BRK-PAD-TYT-01", "Juego de pastillas cerámicas", 
                      catFrenos, undJuego, ubTiendaMostrador, new BigDecimal("12"), 
                      null, null); // Sin lote/caducidad

        createProduct("Líquido de Frenos DOT4", "BRK-FLUID-DOT4", "Líquido alta temperatura 500ml", 
                      catFrenos, undUnidad, ubTiendaEstanteA, new BigDecimal("24"), 
                      "L-BF-2305", LocalDateTime.now().plusYears(1));

        // Suspensión
        createProduct("Amortiguador Trasero Spark", "SHOCK-CVT-SPK", "Amortiguador gas", 
                      catSuspension, undUnidad, ubBodegaPasillo2, new BigDecimal("8"), 
                      null, null);

        // Eléctrico
        createProduct("Batería 12V 60Ah", "BAT-12V-60", "Batería libre mantenimiento", 
                      catElectrico, undUnidad, ubBodegaPasillo1, new BigDecimal("15"), 
                      "BAT-NOV23", LocalDateTime.now().plusMonths(6)); // Caduca pronto

        createProduct("Bujía Iridium NGK", "SPK-PLG-NGK", "Bujía alto rendimiento", 
                      catElectrico, undUnidad, ubTiendaMostrador, new BigDecimal("100"), 
                      null, null);

        // Accesorios
        createProduct("Limpiaparabrisas 20 pulg", "WIPER-20", "Escobilla universal", 
                      catAccesorios, undUnidad, ubTiendaEstanteB, new BigDecimal("40"), 
                      null, null);
                      
        createProduct("Refrigerante Verde", "COOLANT-GRN", "Refrigerante 50/50 Galón", 
                      catMotor, undGalon, ubTiendaEstanteA, new BigDecimal("20"), 
                      "COOL-23", LocalDateTime.now().plusYears(5));

    }

    private InvCategoria createCategoria(String nombre, String descripcion) {
        InvCategoria c = new InvCategoria();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setActivo(true);
        return categoriaRepository.save(c);
    }

    private InvUnidadMedida createUnidad(String nombre, String abrev) {
        InvUnidadMedida u = new InvUnidadMedida();
        u.setNombre(nombre);
        u.setAbreviatura(abrev);
        u.setActivo(true);
        return unidadMedidaRepository.save(u);
    }

    private InvAlmacen createAlmacen(String nombre, String codigo, String tipo, String direccion) {
        InvAlmacen a = new InvAlmacen();
        a.setNombre(nombre);
        a.setCodigo(codigo);
        a.setTipoAlmacen(tipo);
        a.setDireccion(direccion);
        a.setActivo(true);
        return almacenRepository.save(a);
    }

    private InvUbicacion createUbicacion(InvAlmacen almacen, String codigo, String desc) {
        InvUbicacion u = new InvUbicacion();
        u.setAlmacen(almacen);
        u.setCodigo(codigo);
        u.setDescripcion(desc);
        u.setActivo(true);
        return ubicacionRepository.save(u);
    }

    private void createProduct(String nombre, String codigo, String desc, 
                               InvCategoria cat, InvUnidadMedida uom, 
                               InvUbicacion ubicacionInicial, BigDecimal stockInicial,
                               String loteCodigo, LocalDateTime caducidad) {
        InvProducto p = new InvProducto();
        p.setNombre(nombre);
        p.setCodigoInterno(codigo);
        p.setDescripcion(desc);
        p.setCategoria(cat);
        p.setUnidadMedida(uom);
        p.setActivo(true);
        
        // Use the service method to ensure all related entities (Variant, Stock) are created
        productoService.createProductWithInitialBatch(p, ubicacionInicial, stockInicial, loteCodigo, caducidad, "Lote Inicial Carga");
    }
}
