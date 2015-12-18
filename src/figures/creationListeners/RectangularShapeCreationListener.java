package figures.creationListeners;

import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import figures.Figure;
import figures.Drawing;

/**
 * Listener permettant d'enchainer les actions souris pour créer des formes
 * rectangulaires comme des rectangles ou des ellipse (evt des cercles):
 * <ol>
 * 	<li>bouton 1 pressé et maintenu enfoncé</li>
 * 	<li>déplacement de la souris avec le bouton enfoncé</li>
 * 	<li>relachement du bouton</li>
 * </ol>
 * @author davidroussel
 */
public class RectangularShapeCreationListener extends AbstractCreationListener
{
	/**
	 * Constructeur d'un listener à deux étapes: pressed->drag->release pour
	 * toutes les figures à caractère rectangulaire (Rectangle, Ellipse, evt
	 * Cercle)
	 *
	 * @param model le modèle de dessin à modifier par ce creationListener
	 * @param tipLabel le label dans lequel afficher les conseils utilisateur
	 */
	public RectangularShapeCreationListener(Drawing model, JLabel tipLabel)
	{
		super(model, tipLabel, 2);

		tips[0] = new String("Cliquez et maintenez enfoncé pour initier la figure");
		tips[1] = new String("Relâchez pour terminer la figure");

		updateTip();

		System.out.println("RectangularShapeCreationListener created");
	}

	/**
	 * Création d'une nouvelle figure rectangulaire de taille 0 au point de
	 * l'évènement souris, si le bouton appuyé est le bouton gauche.
	 *
	 * @param e l'évènement souris
	 * @see AbstractCreationListener#startFigure(MouseEvent)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			startFigure(e);
		}
	}

	/**
	 * Terminaison de la nouvelle figure rectangulaire si le bouton appuyé
	 * était le bouton gauche
	 * @param e l'évènement souris
	 * @see AbstractCreationListener#endFigure(MouseEvent)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			endFigure(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e)
	{
		// Rien
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e)
	{
		// Rien
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e)
	{
		// Rien
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e)
	{
		// Rien
	}

	/**
	 * Déplacement du point en bas à droite de la figure rectangulaire, si
	 * l'on se trouve à l'étape 1 (après initalisation de la figure) et que
	 * le bouton enfoncé est bien le bouton gauche.
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (currentStep == 1)
		{
			// AbstractFigure figure = drawingModel.getLastFigure();
			Figure figure = currentFigure;
			if (figure != null)
			{
				figure.setLastPoint(e.getPoint());
			}
			drawingModel.update();
		}
	}
}
