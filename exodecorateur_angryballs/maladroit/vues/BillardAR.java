package exodecorateur_angryballs.maladroit.vues;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.Observable;
import java.util.Vector;

import exodecorateur_angryballs.maladroit.modele.Bille;


/**
 * repr�sente un billard o� l'affichage est fait par active rendering (c�-d sans passer par paint)
 * 
 * 
 * */
public class BillardAR extends Billard
{
BufferStrategy strat�gie;

public BillardAR(Vector<Bille> billes)
{
super(billes);
}

public void init()
{
this.setIgnoreRepaint(true);
this.createBufferStrategy(2);

this.strat�gie = this.getBufferStrategy();
}



@Override
public void miseAJour()
{


    Graphics g = this.strat�gie.getDrawGraphics();
    g.clearRect(0, 0, this.getWidth(), this.getHeight());
    this.dessine(g);
    this.strat�gie.show();
    

}

public void nettoie()
{
Graphics g = this.strat�gie.getDrawGraphics();
g.clearRect(0, 0, this.getWidth(), this.getHeight());
this.strat�gie.show();
}

public void repaint()
{
this.miseAJour();
}
}
