package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.InvLote;
import com.mariastaff.Inventario.backend.data.entity.InvProducto;
import com.mariastaff.Inventario.backend.data.entity.InvProductoVariante;
import com.mariastaff.Inventario.backend.data.repository.InvLoteRepository;
import com.mariastaff.Inventario.backend.data.repository.InvProductoRepository;
import com.mariastaff.Inventario.backend.data.repository.InvProductoVarianteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private InvProductoRepository productoRepository;

    @Mock
    private InvProductoVarianteRepository varianteRepository;

    @Mock
    private InvLoteRepository loteRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    public void testCreateProductWithInitialBatch() {
        // Arrange
        InvProducto product = new InvProducto();
        product.setNombre("Test Product");
        product.setCodigoInterno("TEST-001");

        String batchCode = "BATCH-001";
        LocalDateTime expiryDate = LocalDateTime.now().plusMonths(6);
        String batchObs = "Initial batch";

        when(productoRepository.save(any(InvProducto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(varianteRepository.save(any(InvProductoVariante.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(loteRepository.save(any(InvLote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        InvProducto createdProduct = productoService.createProductWithInitialBatch(product, null, null, batchCode,
                expiryDate, batchObs);

        // Assert
        assertNotNull(createdProduct);
        assertEquals("Test Product", createdProduct.getNombre());

        verify(productoRepository, times(1)).save(product);
        verify(varianteRepository, times(1)).save(any(InvProductoVariante.class)); // Verifies variant creation
        verify(loteRepository, times(1)).save(any(InvLote.class)); // Verifies batch creation
    }

}
