package exodecorateur_angryballs.maladroit.modele;

import java.awt.Color;
import java.util.Vector;

import mesmaths.cinematique.Collisions;
import mesmaths.geometrie.base.Vecteur;
import mesmaths.mecanique.MecaniquePoint;

/**
 * 
 * Bille rebondissant sur les murs, subissant un mouvement rectiligne (mouvement en ligne droite) et subissant le frottement dans l'air
 * 
 * Soumis à un vecteur pesanteur
 * 
 * 
 *  A MODIFIER
 *  
 *  */
public class BilleMvtPesanteurFrottementRebond extends Bille
{
Vecteur pesanteur;

public BilleMvtPesanteurFrottementRebond(Vecteur position, double rayon,
        Vecteur vitesse, Vecteur pesanteur, Color couleur)
{
super(position, rayon, vitesse, couleur);
this.pesanteur = pesanteur;
}



/* (non-Javadoc)
 * @see decorateur_angryballs.modele.Bille#gestionAccélération(java.util.Vector)
 */
@Override
public void gestionAccélération(Vector<Bille> billes)
{
super.gestionAccélération(billes);          // remise à zéro du vecteur accélération
this.getAccélération().ajoute(this.pesanteur);          // contribution du champ de pesanteur (par exemple)
this.getAccélération().ajoute(MecaniquePoint.freinageFrottement(this.masse(), this.getVitesse())); // contribution de l'accélération due au frottement dans l'air
}



@Override
public void collisionContour(double abscisseCoinHautGauche,
        double ordonnéeCoinHautGauche, double largeur, double hauteur)
{
Collisions.collisionBilleContourAvecRebond(this.getPosition(), this.getRayon(), this.getVitesse(), abscisseCoinHautGauche, ordonnéeCoinHautGauche, largeur, hauteur);
}

}
