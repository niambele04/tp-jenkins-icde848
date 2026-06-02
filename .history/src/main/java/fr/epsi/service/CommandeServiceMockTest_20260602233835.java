package fr.epsi.service;

import fr.epsi.model.Article;
import fr.epsi.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeServiceMockTest {

    @Mock
    private StockRepository stockRepository;

    private CommandeService service;
    private Article article;

    @BeforeEach
    void setUp() {
        service = new CommandeService(stockRepository);
        article = new Article("Stylo", 2.0);
    }

    @Test
    @DisplayName("Stock suffisant → commande réalisable")
    void commandeRealisable_StockSuffisant_RetourneTrue() {
        when(stockRepository.getStock(article)).thenReturn(10);
        boolean resultat = service.commandeRealisable(article, 5);
        assertTrue(resultat, "La commande devrait être réalisable (stock=10, demande=5)");
        verify(stockRepository, times(1)).getStock(article);
    }

    @Test
    @DisplayName("Stock insuffisant → commande non réalisable")
    void commandeRealisable_StockInsuffisant_RetourneFalse() {
        when(stockRepository.getStock(article)).thenReturn(2);
        boolean resultat = service.commandeRealisable(article, 5);
        assertFalse(resultat, "La commande ne devrait pas être réalisable (stock=2, demande=5)");
    }

    @Test
    @DisplayName("Stock exactement égal à la demande → réalisable")
    void commandeRealisable_StockEgalDemande_RetourneTrue() {
        when(stockRepository.getStock(article)).thenReturn(5);
        boolean resultat = service.commandeRealisable(article, 5);
        assertTrue(resultat, "Commande égale au stock devrait être réalisable");
    }

    @Test
    @DisplayName("Stock à zéro → commande non réalisable")
    void commandeRealisable_StockZero_RetourneFalse() {
        when(stockRepository.getStock(article)).thenReturn(0);
        boolean resultat = service.commandeRealisable(article, 1);
        assertFalse(resultat, "Stock à zéro : commande non réalisable");
    }

    @Test
    @DisplayName("Sans StockRepository → IllegalStateException")
    void commandeRealisable_SansRepository_LeveException() {
        CommandeService serviceNonConfigure = new CommandeService();
        assertThrows(
            IllegalStateException.class,
            () -> serviceNonConfigure.commandeRealisable(article, 1)
        );
    }
}