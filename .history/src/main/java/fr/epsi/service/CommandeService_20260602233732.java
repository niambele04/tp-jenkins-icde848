package fr.epsi.service;

import java.util.Map;

import fr.epsi.model.Article;
import fr.epsi.model.Panier;
import fr.epsi.repository.StockRepository;

/**
 * Service métier de gestion des commandes.
 * ICDE848 – TP Jenkins
 */
public class CommandeService {

    private StockRepository stockRepository;

    public CommandeService() {
        this.stockRepository = null;
    }

    public CommandeService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * Calcule le total d'un panier.
     */
    public double calculerTotal(Panier panier) {
        if (panier == null || panier.estVide()) {
            throw new IllegalArgumentException("Panier vide ou null");
        }
        double total = 0;
        for (Map.Entry<Article, Integer> entry : panier.getArticles().entrySet()) {
            total += entry.getKey().getPrix() * entry.getValue();
        }
        return total;
    }

    /**
     * Applique une remise en pourcentage sur un total.
     */
    public double appliquerRemise(double total, int pourcentage) {
        if (pourcentage < 0 || pourcentage > 100) {
            throw new IllegalArgumentException("Remise invalide : " + pourcentage);
        }
        return total * (1 - pourcentage / 100.0);
    }

    /**
     * Catégorise une commande selon son montant.
     */
    public String categoriserCommande(double total) {
        if (total < 50)       return "PETITE";
        else if (total < 200) return "MOYENNE";
        else                  return "GRANDE";
    }

    /**
     * Calcule la TVA à 20% sur un montant donné.
     */
    public double calculerTVA(double montant) {
        if (montant < 0) {
            throw new IllegalArgumentException("Montant négatif : " + montant);
        }
        double tva = montant * 0.20;
        return Math.round(tva * 100.0) / 100.0;
    }

    /**
     * Vérifie si la commande est réalisable selon le stock disponible.
     */
    public boolean commandeRealisable(Article article, int quantite) {
        if (stockRepository == null) {
            throw new IllegalStateException("StockRepository non configuré");
        }
        int stockDisponible = stockRepository.getStock(article);
        return stockDisponible >= quantite;
    }
}