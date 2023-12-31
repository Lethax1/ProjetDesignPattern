package exodecorateur_angryballs.maladroit.vues;

import java.awt.Canvas;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Vector;

import exodecorateur_angryballs.maladroit.modele.Bille;



/*import exodecorateur_angryballs.mieux.modele.Dessinateur;
import exodecorateur_angryballs.mieux.modele.awt.DessinateurBilleAWT;*/




/**
 * responsable du dessin des billes
 * 
 * 
 * */
public class Billard extends Canvas implements VueBillard
{
Vector<Bille> billes;
public Billard(Vector<Bille> billes)
{
this.billes = billes;
}

public void init() {}

/* (non-Javadoc)
 * @see java.awt.Canvas#paint(java.awt.Graphics)
 */
@Override
public void paint(Graphics graphics)
{
this.dessine(graphics);
}
@Override
public double largeurBillard()
{
return this.getWidth();
}
@Override
public double hauteurBillard()
{
return this.getHeight();
}
@Override
public void miseAJour()
{
this.repaint();
}
@Override
public void montrer()
{
this.setVisible(true);
}

public  void dessine(Graphics graphics)
{

int i;

for ( i = 0; i < this.billes.size(); ++i)
    this.billes.get(i).dessine(graphics);
}
 
public void setImage() {}

}
