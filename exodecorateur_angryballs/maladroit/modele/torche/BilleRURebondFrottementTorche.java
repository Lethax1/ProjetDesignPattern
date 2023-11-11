package exodecorateur_angryballs.maladroit.modele.torche;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

import exodecorateur_angryballs.maladroit.modele.Bille;
import mesmaths.cinematique.Collisions;
import mesmaths.geometrie.base.Vecteur;
import mesmaths.mecanique.MecaniquePoint;

/**
 * Représente une bille-torche.
 * 
 * Si la bille est immobile, la flamme est verticale, les étincelles se déplacent du bas vers le haut.
 * Plus la bille va vite, plus la position de la flamme est influencée par la trajectoire de la bille
 * 
 *  A MODIFIER
 * */

public class BilleRURebondFrottementTorche extends Bille
{
/**
 * conditionne le retour à la position verticale de la flamme
 * doit être tel que 0 < K.
 * 
 * Plus K est proche de zéro, plus la queue est verticale
 * Plus K s'éloigne de zéro, plus la queue suit fidèlement la trajectoire de la bille
 * 
 * */
public final static double K = 5.0e2;

/**
 * echine : echine actuelle de la flamme de la bille (= échine-vitesse)
 * echineVerticale : position de l'échine dans sa position verticale, 
 * plus la bille ralentit, plus son échine tend à se confondre avec echineVerticale
 * */
Echine echine, echineVerticale;

/** positions du nuage d'étincelles */
Vecteur etincelles[];

/** couleurs du nuage d'étincelles au format RGB 24 bits */
int couleursEtincelles[];

/**
 * tel que mNuage +1 = (mEtincelles+1)*(mRangees+1)
 * tel que mNuage +1 = etincelles.length = couleursEtincelles.length
 * */
int mEtincelles, mRangees, mNuage;

/**
 * rayon d'une étincelle en coordonnées écran
 * 
 * */
double rayonEtincelle;


/**
 * 
 * l'objet qui sait créer une flamme, c-à-d le nuage d'étincelles (positions et couleurs) à partir de l'échine 
 * */
CreateurFlammes createurFlammes;

/**paramètres par défaut pour créer la bille torche */

public BilleRURebondFrottementTorche(Vecteur position,  double rayon, Vecteur vitesse, Color couleur)
{
this( position, rayon, vitesse, couleur, 20, 0.18, 1.12, 25, 20, 1.5);
}

/** les paramètres sont à définir expérimentalement
 * 
 * @param nombreSommets : nombre de sommets de l'échine (centre de la bille exclu)
 * @param coeffR0 : ratio distance (centre Bille, S0) / rayon de la bille   où S0 est le sommet du début de l'échine
 * @param coeffRaison : les longueurs des vertèbres de l'échine sont en progression géométrique. coeffRaison est la raison de cette suite.
 * @param mEtincelles : mEtincelles définit la densité d'étincelles dans la direction définie par l'échine
 * @param mRangees :  mRangées définit la densité d'étincelles dans la direction de la largeur de la flamme
 * 
 * @param rayonEtincelle : rayon d'une étincelle en coordonnées écran
 * 
 */
public BilleRURebondFrottementTorche( Vecteur position,  double rayon, Vecteur vitesse, Color couleur,
                                      final int nombreSommets,  
                                      final double coeffR0, 
                                      final double coeffRaison, 
                                      final int mEtincelles, 
                                      final int mRangees, 
                                      final double rayonEtincelle)

{
super(position, rayon, vitesse, couleur);

Vecteur u = new Vecteur(0,-1);  // vecteur unitaire vertical orienté vers le haut, en coordonnées écran

/* l'échine est créée verticale, orientée du bas vers le haut */
this.echine = new Echine(getPosition().somme(u.produit(getRayon())), coeffR0*this.getRayon(), u, coeffRaison, nombreSommets);
//this.echineVerticale = new Echine(getPosition().somme(u.produit(getRayon())), 0.65*coeffR0*this.getRayon(), u, coeffRaison, nombreSommets);
this.echineVerticale = this.echine.copie();

/* on tente de lancer le créateur de belles flammes (client vers le serveur de Flammes C++) */
try
    {
    this.mEtincelles = mEtincelles;
    this.mRangees = mRangees;
    this.createurFlammes = new CreateurBellesFlammes(this.mRangees,this.mEtincelles);
    
    this.rayonEtincelle = rayonEtincelle;
    System.out.println("CréateurBellesFlammes créé");
    }
catch (Exception e)         /* échec de connexion vers le serveur de flammes, on crée un créateur de flammes minimaliste */
    {
    this.createurFlammes = new CreateurFlammesMock();
    this.mEtincelles = this.echine.length()-1;
    this.mRangees = 0;
    this.rayonEtincelle = 4;
    }


/* pour optimiser les temps d'exécution, le nuage d'étincelles est déjà créé ( c-à-d tableau des positions et tableau des couleurs) */

this.mNuage = (this.mEtincelles+1)*(this.mRangees+1) - 1;
this.etincelles = new Vecteur[this.mNuage+1];
int i; for ( i = 0 ; i < this.etincelles.length; ++i) this.etincelles[i] = new Vecteur();
this.couleursEtincelles = new int[this.etincelles.length];
}

@Override
public void déplacer(double deltaT)
{
Vecteur p = this.getPosition().copie();         // on sauvegarde l'ancienne position du centre de la bille
super.déplacer(deltaT);                         // on met à jour position et vitesse de la bille

miseAJour2Echines(p);
}

/* (non-Javadoc)
 * @see decorateur_angryballs.modele.Bille#gestionAccélération(java.util.Vector)
 */
@Override
public void gestionAccélération(Vector<Bille> billes)
{
super.gestionAccélération(billes);          // remise à zéro du vecteur accélération

this.getAccélération().ajoute(MecaniquePoint.freinageFrottement(this.masse(), this.getVitesse())); // contribution de l'accélération due au frottement dans l'air
}


/**
 * Notons i-1 l'instant précédent et i l'instant actuel
 * 
 * Notons échine(i-1) = position et forme de l'échine-vitesse à l'instant i-1
 * Notons échineVerticale(i-1) = position et forme de l'échine verticale à l'instant i-1
 * Notons échine(i) = position et forme de l'échine-vitesse à l'instant i
 * Notons échineVerticale(i) = position et forme de l'échine verticale à l'instant i
 * 
 * alors echineVerticale(i) = fct(echineVerticale(i-1))
 * alors echine(i) = fct(echine(i-1),echineVerticale(i-1))
 * 
 * mise à jour de l'échine vitesse et de l'échine verticale : attributs this.echine et this.echineVerticale
 * @param p : position du centre de la bille à l'instant i-1
 */
public void miseAJour2Echines(Vecteur p)
{
this.echine.miseAJour(p,this.getPosition());    // on met à jour la position de l'échine de la torche (échine-vitesse)
/*ok = this.createurFlammes.creeFlamme(this.getPosition(), this.getRayon(),
        this.echine, this.mRangees, this.mEtincelles, this.etincelles,this.couleursEtincelles);*/
//System.err.println(this.echine);

Vecteur trans = this.getPosition().difference(p);           /* translation à appliquer à l'échine verticale */

for (Vecteur v : echineVerticale.sommets) v.ajoute(trans);          // on met à jour l'échine verticale

double alfa0 = Math.exp(-K*this.getVitesse().normeCarrée());        // définit l'influence de l'échine verticale, l'influence dépend de la vitesse de la bille
                                                                    // plus la bille est rapide, plus alfa0. se rapproche de zéro
                                                                    // on a toujours 0 < alfa0 <= 1


//-------- la nouvelle position de l'échine est un mélange entre l'échine-vitesse et l'échine verticale ----
//--------- on fait une combinaison linéaire convexe de telle sorte que plus on va vers la pointe de la flamme, plus l'échine verticale a de l'influence

double d = echine.sommets.length;

alfa0/=d;
int i,j;
for ( i = 0, j = 1; i < echine.sommets.length; i = j++) 
    {
    double alfa = alfa0*j;          /* astuce pour que les sommets situés près de la pointe de la flamme soient plus attirés par l'échine verticale
                                         que les sommets situés près de la bille */
    double alfabar = 1-alfa;
    echine.sommets[i].combinaisonLineaire(alfabar, alfa, this.echineVerticale.sommets[i]);
    }
}


@Override
public void dessine(Graphics g)
{
/* on dessine d'abord la bille */
super.dessine(g);

/* puis on dessine la flamme par au dessus  à l'aide de l'échine vitesse */

/* on remplit d'abord le tableau des positions des étincelles et le tableau des couleurs des étincelles */

boolean ok = this.createurFlammes.creeFlamme(this.getPosition(), this.getRayon(),
                                             this.echine, this.etincelles,this.couleursEtincelles);


if (ok)
    {
    int i;
    for (i = 0; i < this.etincelles.length; ++i) /* dessine les étincelles une par une */
        {
        int couleur = this.couleursEtincelles[i];
        Color co = new Color(couleur);
        Bille.dessineDisque(g, this.etincelles[i], this.rayonEtincelle, co, co);
        } 
    }
else            /* échec du remplissage des tableaux de positions et de couleurs alors on dessine l'échine sans la flamme */
    {
    Bille.dessineSegment(g, this.getPosition(), this.echine.sommets[0], Echine.COULEUR_ECHINE);
    this.echine.dessine(g); 
    }
}

   

@Override
public void collisionContour(double abscisseCoinHautGauche,
        double ordonnéeCoinHautGauche, double largeur, double hauteur)
{
Collisions.collisionBilleContourAvecRebond(this.getPosition(), this.getRayon(), this.getVitesse(), abscisseCoinHautGauche, ordonnéeCoinHautGauche, largeur, hauteur);
}






}
