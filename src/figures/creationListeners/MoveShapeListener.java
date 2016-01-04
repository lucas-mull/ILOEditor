package figures.creationListeners;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.JLabel;

import figures.Drawing;

/**
 * Listener permettant de déplacer une figure
 * <ul>
 * </ul>
 *
 * @author lucasmull
 */
public class MoveShapeListener extends AbstractCreationListener implements KeyListener
{

	/**
	 * Constructeur d'un Listener pour créer un polygon en plusieurs clicks
	 *
	 * @param model le modèle de dessin à modifier
	 * @param infoLabel le label dans lequel afficher les conseils utilisateurs
	 */
	public MoveShapeListener(Drawing model, JLabel infoLabel)
	{
		super(model, infoLabel, 2);

		tips[0] = new String("Maintenez clic gauche pour déplacer");
		tips[1] = new String("Relachez pour...relacher");

		updateTip();

		System.out.println("MoveShapeListener created");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * figures.creationListeners.AbstractCreationListener#mousePressed(java.
	 * awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{		
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			currentFigure = drawingModel.getFigureAt(e.getPoint());
			startPoint = e.getPoint();
			
			drawingModel.update();
			nextStep();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * figures.creationListeners.AbstractCreationListener#mouseReleased(java
	 * .awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e)
	{
		// Rien
		currentFigure = null;
		nextStep();
	}

	/**
	 * Actions à réaliser lorsqu'un bouton de la souris est cliqué.
	 * Si l'on se trouve à l'étape 0 et que le bouton cliqué est
	 * {@link MouseEvent#BUTTON1}, on initie la figure et on passe à l'étape suivante.
	 * Dans l'étape suivante si c'est {@link MouseEvent#BUTTON1} qui est cliqué
	 * on ajoute un point, si c'est le {@link MouseEvent#BUTTON2} on supprime le
	 * dernier point ajouté et si c'est le bouton {@link MouseEvent#BUTTON3},
	 * on termine la figure.
	 * @param e l'évènement souris associé
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e)
	{
		currentFigure = drawingModel.getFigureAt(e.getPoint());
		
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e)
	{
		// Rien
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e)
	{
		// Rien
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
	 * )
	 */
	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (currentFigure != null)
		{
			Point p = e.getPoint();
			Point2D center = currentFigure.getCenter();
			try {
				currentFigure.getTransformation().inverseTransform(p, p);
			} catch (NoninvertibleTransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			currentFigure.translate(p.x - center.getX(), p.y - center.getY());
			drawingModel.update();
		}		
	}

	/*
	 * 
	 * @param e L'évènement souris associé
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e)
	{
		// Rien
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
