package exodecorateur_angryballs.maladroit.vues;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.Observable;
import java.util.Vector;

import exodecorateur_angryballs.maladroit.modele.Bille;


/**
 * représente un billard où l'affichage est fait par active rendering (cà-d sans passer par paint)
 * 
 * 
 * */
public class BillardAR extends Billard
{
BufferStrategy stratégie;

public BillardAR(Vector<Bille> billes)
{
super(billes);
}

public void init()
{
this.setIgnoreRepaint(true);
this.createBufferStrategy(2);

this.stratégie = this.getBufferStrategy();
}



@Override
public void miseAJour()
{


    Graphics g = this.stratégie.getDrawGraphics();
    g.clearRect(0, 0, this.getWidth(), this.getHeight());
    this.dessine(g);
    this.stratégie.show();
    

}

public void nettoie()
{
Graphics g = this.stratégie.getDrawGraphics();
g.clearRect(0, 0, this.getWidth(), this.getHeight());
this.stratégie.show();
}

public void repaint()
{
this.miseAJour();
}
}
