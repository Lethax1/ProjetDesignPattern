package exodecorateur_angryballs.maladroit.modele;

import java.awt.Color;
import java.util.Vector;

import mesmaths.cinematique.Collisions;
import mesmaths.geometrie.base.Vecteur;
import mesmaths.mecanique.MecaniquePoint;

/**
 * 
 * Bille rebondissant sur les bords, subissant le frottement dans l'air et subissant l'attraction des autres billes
 * 
 * 
 *  A MODIFIER
 *  
 *  */
public class BilleMvtNewtonFrottementRebond extends Bille
{

public BilleMvtNewtonFrottementRebond(Vecteur centre, double rayon,
        Vecteur vitesse, Color couleur)
{
super(centre, rayon, vitesse, couleur);
}




/* (non-Javadoc)
 * @see decorateur_angryballs.modele.Bille#gestionAccélération(java.util.Vector)
 */
@Override
public void gestionAccélération(Vector<Bille> billes)
{
super.gestionAccélération(billes);                              // remise à zéro du vecteur accélération
this.getAccélération().ajoute(OutilsBille.gestionAccélérationNewton(this, billes));     // contribution de l'accélération due à l'attraction des autres billes
this.getAccélération().ajoute(MecaniquePoint.freinageFrottement(this.masse(), this.getVitesse()));      // contribution de l'accélération due au frottement dans l'air
}




@Override
public void collisionContour(double abscisseCoinHautGauche,
        double ordonnéeCoinHautGauche, double largeur, double hauteur)
{
Collisions.collisionBilleContourAvecRebond(this.getPosition(), this.getRayon(), this.getVitesse(), abscisseCoinHautGauche, ordonnéeCoinHautGauche, largeur, hauteur);

}

}
