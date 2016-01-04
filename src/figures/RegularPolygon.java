package figures;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.Point2D;

import figures.enums.FigureType;

/**
 * Classe de Figure pour les cercles
 * 
 * @author lucasmull
 */
public class RegularPolygon extends Figure
{
	/**
	 * Le rayon par défaut pour un cercle
	 */
	public final static int DEFAULT_RAYON = 2;

	/**
	 * Le compteur d'instance des cercles. Utilisé pour donner un numéro
	 * d'instance après l'avoir incrémenté
	 */
	private static int counter = 0;

	/**
	 * Constructeur valué d'un cercle
	 * 
	 * @param stroke le type du trait de la bordure
	 * @param edge la couleur de la bordure
	 * @param fill la couleur de remplissage
	 * @param center le centre du polygone
	 * @param rayon le rayon du cercle englobant le polygone
	 * @param vertexCount le nombre de sommets du polygone
	 */
	public RegularPolygon(BasicStroke stroke, Paint edge, Paint fill, Point center,
			int rayon, int vertexCount)
	{
		super(stroke, edge, fill);
		instanceNumber = ++counter;
		createShape(center, rayon + DEFAULT_RAYON, vertexCount);
		// System.out.println("Cercle created");
	}
	
	public void createShape(Point center, int rayon, int vertexCount)
	{		
		shape = new java.awt.Polygon(
				getXCoordinates(center, rayon, vertexCount), 
				getYCoordinates(center, rayon, vertexCount), 
				vertexCount);
	}
	
	private int[] getXCoordinates(Point center, int rayon, int vertexCount)
	{
		int xcoord[] = new int[vertexCount];
        double addAngle = 2*Math.PI/vertexCount;
        double angle = 0;
        for (int i=0; i<vertexCount; i++) {
        	xcoord[i] = (int)(Math.round(rayon*Math.cos(angle)) + center.x);
            angle += addAngle;
        }
        
        return xcoord;
	}
	
	private int[] getYCoordinates(Point center, int rayon, int vertexCount)
	{
		int ycoord[] = new int[vertexCount];
        double addAngle = 2*Math.PI/vertexCount;
        double angle = 0;
        for (int i = 0; i < vertexCount; i++) {
        	ycoord[i] = (int)(Math.round(rayon * Math.sin(angle)) + center.y);
            angle += addAngle;
        }
        
        return ycoord;
	}

	/**
	 * Déplacement du dernier point du polygone
	 * @param p la position du dernier point
	 * @see lines.AbstractLine#setLastPoint(Point2D)
	 */
	@Override
	public void setLastPoint(Point2D p)
	{
		java.awt.Polygon poly = (java.awt.Polygon) shape;
		int lastIndex = poly.npoints-1;
		if (lastIndex >= 0)
		{
			poly.xpoints[lastIndex] = Double.valueOf(p.getX()).intValue();
			poly.ypoints[lastIndex] = Double.valueOf(p.getY()).intValue();
		}
	}
	
//
//	/**
//	 * Obtention du barycentre de la figure.
//	 * 
//	 * @return le point correspondant au barycentre de la figure
//	 */
	@Override
	public Point2D getCenter()
	{
		java.awt.Polygon poly = (java.awt.Polygon) shape;

		float xm = 0.0f;
		float ym = 0.0f;

		if (poly.npoints > 0)
		{
			for (int i = 0; i < poly.npoints; i++)
			{
				xm += poly.xpoints[i];
				ym += poly.ypoints[i];
			}

			xm /= poly.npoints;
			ym /= poly.npoints;
		}

		return new Point2D.Float(xm, ym);
	}
	
 	/**
 	 * Accesseur du type de figure selon {@link FigureType}
 	 * @return le type de figure
 	 */
 	@Override
	public FigureType getType()
 	{
 		return FigureType.CIRCLE;
 	}
}
