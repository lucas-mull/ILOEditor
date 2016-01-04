package figures;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.stream.Stream;

import figures.enums.FigureType;
import figures.enums.LineType;
import filters.EdgeColorFilter;
import filters.FigureFilters;
import filters.FillColorFilter;
import filters.LineFilter;
import filters.ShapeFilter;
import utils.PaintFactory;
import utils.StrokeFactory;

/**
 * Classe contenant l'ensemble des figures à dessiner (LE MODELE)
 *
 * @author davidroussel
 */
public class Drawing extends Observable
{
	/**
	 * Liste des figures à dessiner
	 */
	private Vector<Figure> figures;
	
	/**
	 * Le type de figure à créer
	 */
	private FigureType type;

	/**
	 * La couleur de remplissage courante
	 */
	private Paint fillPaint;

	/**
	 * La couleur de trait courante
	 */
	private Paint edgePaint;

	/**
	 * La largeur de trait courante
	 */
	private float edgeWidth;

	/**
	 * Le type de trait courant (sans trait, trait plein, trait pointillé)
	 */
	private LineType edgeType;

	/**
	 * Les caractétistique à appliquer au trait en fonction de {@link #type} et
	 * {@link #edgeWidth}
	 */
	private BasicStroke stroke;
		
	/**
	 * Figure située sous le curseur.
	 * Déterminé par {@link #getFigureAt(Point2D)}
	 */
	private Figure selectedFigure;
	
	/**
	 * Etat de filtrage des figures dans le flux de figures fournit par 
	 * {@link #stream()}
	 * Lorsque {@link #filtering} est true le dessin des figures est filtré
	 * par l'ensemble des filtres présents dans {@link #shapeFilters}, 
	 * {@link #fillColorFilter}, {@link #edgeColorFilter} et {@link #lineFilters}.
	 * Lorsque {@link #filtering} est false, toutes les figures sont fournies
	 * dans le flux des figures.
	 * @see #stream()
	 */
	private boolean filtering;
	
	/**
	 * Filtres à appliquer au flux des figures pour sélectionner les types
	 * de figures à afficher
	 * @see #stream()
	 */
	private FigureFilters<FigureType> shapeFilters;
	
	/**
	 * Filtre à appliquer au flux des figures pour sélectionner les figures
	 * ayant une couleur particulière de remplissage
	 */
	private FillColorFilter fillColorFilter;
	
	/**
	 * Filtre à appliquer au flux des figures pour sélectionner les figures
	 * ayant une couleur particulière de trait
	 */
	private EdgeColorFilter edgeColorFilter;
	
	/**
	 * Filtres à applique au flux des figures pour sélectionner les figures 
	 * ayant un type particulier de lignes
	 */
	private FigureFilters<LineType> lineFilters;
	
	/**
	 * Permet de switcher entre la création de nouvelles figures et le mode de déplacement
	 * des figures existantes
	 */
	private boolean moveMode;

	/**
	 * Constructeur de modèle de dessin
	 */
	public Drawing()
	{
		figures = new Vector<Figure>();
		shapeFilters = new FigureFilters<FigureType>();
		
		fillColorFilter = null;
		edgeColorFilter = null;
		lineFilters = new FigureFilters<LineType>();
		
		fillPaint = null;
		edgePaint = null;
		edgeWidth = 1.0f;
		edgeType = LineType.SOLID;
		stroke = StrokeFactory.getStroke(edgeType, edgeWidth);
		filtering = false;
		selectedFigure = null;

		System.out.println("Drawing model created");
	}

	/**
	 * Nettoyage avant destruction
	 */
	@Override
	protected void finalize()
	{
		// Aide au GC
		figures.clear();
	}

	/**
	 * Mise à jour du ou des {@link Observer} qui observent ce modèle. On place
	 * le modèle dans un état "changé" puis on notifie les observateurs.
	 */
	public void update()
	{
		setChanged();
		notifyObservers();
	}

	// ------------------------------------------------------------------------
	// Accesseur et Mutateurs des attributs
	// ------------------------------------------------------------------------
	/**
	 * Accesseur du type courant de figure
	 * @return le type courant de figures à créer
	 */
	public FigureType getType()
	{
		return type;
	}
	/**
	 * Mise en place d'un nouveau type de figure à générer
	 *
	 * @param type le nouveau type de figure
	 */
	public void setType(FigureType type)
	{
		this.type = type;
	}

	/**
	 * Accesseur de la couleur de remplissage courante des figures
	 * @return la couleur de remplissage courante des figures
	 */
	public Paint getFillpaint()
	{
		return fillPaint;
	}
	
	/**
	 * Mise en place d'une nouvelle couleur de remplissage
	 *
	 * @param fillPaint la nouvelle couleur de remplissage
	 */
	public void setFillPaint(Paint fillPaint)
	{
		this.fillPaint = fillPaint;
		/*
		 * Au moment où on initiera une nouvelle figure, on mettra ce paint dans
		 * la PaintFactory
		 */
	}

	/**
	 * Accesseur de la couleur de trait courante des figures
	 * @return la couleur de remplissage courante des figures
	 */
	public Paint getEdgePaint()
	{
		return edgePaint;
	}
	
	/**
	 * Mise en place d'une nouvelle couleur de trait
	 *
	 * @param edgePaint la nouvelle couleur de trait
	 */
	public void setEdgePaint(Paint edgePaint)
	{
		this.edgePaint = edgePaint;
		/*
		 * Au moment où on initiera une nouvelle figure, on mettra ce paint dans
		 * la PaintFactory
		 */
	}

	/**
	 * Mise en place d'un nouvelle épaisseur de trait
	 *
	 * @param width la nouvelle épaisseur de trait
	 */
	public void setEdgeWidth(float width)
	{
		edgeWidth = width;
		/*
		 * Au moment où on initiera une nouvelle figure, on mettra le stroke 
		 * résultant dans la StrokeFactory
		 */		
	}

	/**
	 * Mise en place d'un nouvel état de ligne pointillée
	 *
	 * @param type le nouveau type de ligne
	 */
	public void setEdgeType(LineType type)
	{
		edgeType = type;
		/*
		 * Au moment où on initiera une nouvelle figure, on mettra le stroke 
		 * résultant dans la StrokeFactory
		 */		
	}

	/**
	 * Initialisation d'une figure de type {@link #type} au point p et ajout de
	 * cette figure à la liste des {@link #figures}
	 *
	 * @param p le point où initialiser la figure
	 * @return la nouvelle figure créée à x et y avec les paramètres courants
	 */
	public Figure initiateFigure(Point2D p)
	{
		/*
		 * Maintenant que l'on s'apprête effectivement à créer une figure on
		 * ajoute les Paints et le Stroke aux factories
		 */
		fillPaint = PaintFactory.getPaint(fillPaint);
		edgePaint = PaintFactory.getPaint(edgePaint);
		stroke = StrokeFactory.getStroke(edgeType, edgeWidth);
		
		/*
		 * Obtention de la figure correspondant au type de figure choisi grâce à
		 * type.getFigure(...)
		 */
		Figure newFigure = type.getFigure(stroke,
		                                  edgePaint,
		                                  fillPaint,
		                                  p);

		/*
		 * Ajout de la figure à #figures
		 */
		if (newFigure != null)
		{
			figures.add(newFigure);
		}

		/* Notification des observers */
		update();

		return newFigure;
	}

	/**
	 * Obtention de la dernière figure (implicitement celle qui est en cours de
	 * dessin)
	 * @return la dernière figure du dessin
	 */
	public Figure getLastFigure()
	{
		if (!figures.isEmpty())
		{
			return figures.get(figures.size() - 1);
		}
		else
		{
			System.err.println("Drawing.getLastFigure : empty");
			return null;
		}
	}

	/**
	 * Obtention de la dernière figure contenant le point p.
	 * @param p le point sous lequel on cherche une figure
	 * @return une référence vers la dernière figure contenant le point p ou à
	 *         défaut null.
	 */
	public Figure getFigureAt(Point2D p)
	{
		// Première version du parcours sans itérateur
//		for (int i = figures.size() - 1; i >= 0; i--)
//		{
//			AbstractFigure figure = figures.get(i);
//			if (figure.contains(p))
//			{
//				return figure;
//			}
//		}
		
//		// Seconde version du parcours avec le ListIterator initialisé à listIterator(figures.size())
//		for (ListIterator<Figure> it = reverseIterator(); it.hasPrevious();)
//		{
//			Figure figure = it.previous();
//			if (figure.contains(p))
//			{
//				return figure;
//			}
//		}
		
		/*
		 * Recherche dans le flux des figures de la DERNIERE figure contenant
		 * le point p
		 */
		selectedFigure = null;
		
		stream().forEach((Figure f) -> 
		{
			if (f.contains(p))
			{
				selectedFigure = f;
			}
		});

		return selectedFigure;
	}

	/**
	 * Retrait de la dernière figure (sera déclencé par une action undo)
	 * @post le modèle de dessin a été mis à jour
	 */
	public void removeLastFigure()
	{
		if (!figures.isEmpty())
		{
			figures.remove(figures.size() - 1);
			update();
		}
	}

	/**
	 * Effacement de toutes les figures (sera déclenché par une action clear)
	 * @post le modèle de dessin a été mis à jour
	 */
	public void clear()
	{
		if (!figures.isEmpty())
		{
			figures.clear();
			update();
		}
	}
	
	/**
	 * Accesseur de l'état de filtrage
	 * @return l'état courant de filtrage
	 */
	public boolean getFiltering()
	{
		return filtering;
	}
	
	/**
	 * Changement d'état du filtrage
	 * @param filtering le nouveau statut de filtrage
	 * @post le modèle de dessin a été mis à jour
	 */
	public void setFiltering(boolean filtering)
	{
		this.filtering = filtering;
		update();
	}
	
	/**
	 * Ajout d'un filtre pour filtrer les types de figures
	 * @param filter le filtre à ajouter
	 * @return true si le filtre n'était pas déjà présent dans l'ensemble des
	 * filtres fitrant les types de figures, false sinon
	 * @post si le filtre a été ajouté, une mise à jour est déclenchée
	 */
	public boolean addShapeFilter(ShapeFilter filter)
	{
		boolean added = shapeFilters.add(filter);
		
		// System.out.println(shapeFilters);
		
		if (added)
		{
			update();			
		}
		
		return added;
	}
	
	/**
	 * Retrait d'un filtre filtrant les types de figures
	 * @param filter le filtre à retirer
	 * @return true si le filtre faisait partie des filtres filtrant les types
	 * de figure et a été retiré, false sinon.
	 * @post si le filtre a éré retiré, une mise à jour est déclenchée
	 */
	public boolean removeShapeFilter(ShapeFilter filter)
	{
		boolean removed = shapeFilters.remove(filter);

		// System.out.println(shapeFilters);
		
		if (removed)
		{
			update();
		}
		
		return removed;
	}
	
	/**
	 * Mise en place du filtre de couleur de remplissage
	 * @param filter le filtre de couleur de remplissage à appliquer
	 * @post le {@link #fillColorFilter} est mis en place et une mise à jour
	 * est déclenchée
	 */
	public void setFillColorFilter(FillColorFilter filter)
	{
		fillColorFilter = filter;
		update();
	}
	
	/**
	 * Mise en place du filtre de couleur de trait
	 * @param filter le filtre de couleur de trait à appliquer
	 * @post le {@link #edgeColorFilter} est mis en place et une mise à jour
	 * est déclenchée
	 */
	public void setEdgeColorFilter(EdgeColorFilter filter)
	{
		edgeColorFilter = filter;
		update();
	}
	
	/**
	 * Ajout d'un filtre pour filtrer les types de ligne des figures
	 * @param filter le filtre à ajouter
	 * @return true si le filtre n'était pas déjà présent dans l'ensemble des
	 * filtres fitrant les types de lignes, false sinon
	 * @post si le filtre a été ajouté, une mise à jour est déclenchée
	 */
	public boolean addLineFilter(LineFilter filter)
	{
		boolean added = lineFilters.add(filter);
		
		if (added)
		{
			update();			
		}
		
		return added;
	}

	/**
	 * Retrait d'un filtre filtrant les types de lignes
	 * @param filter le filtre à retirer
	 * @return true si le filtre faisait partie des filtres filtrant les types
	 * de lignes et a été retiré, false sinon.
	 * @post si le filtre a éré retiré, une mise à jour est déclenchée
	 */
	public boolean removeLineFilter(LineFilter filter)
	{
		boolean removed = lineFilters.remove(filter);
		
		if (removed)
		{
			update();
		}
		
		return removed;
	}

	/**
	 * Accès aux figures dans un stream afin que l'on puisse y appliquer
	 * de filtres
	 * @return le flux des figures éventuellement filtrés par les différents
	 * filtres
	 */
	public Stream<Figure> stream()
	{
		Stream<Figure> figuresStream = figures.stream();
		if (filtering)
		{
			if (shapeFilters.size() > 0)
			{
				figuresStream = figuresStream.filter(shapeFilters);
			}
			
			if (fillColorFilter != null)
			{
				figuresStream = figuresStream.filter(fillColorFilter);
			}

			if (edgeColorFilter != null)
			{
				figuresStream = figuresStream.filter(edgeColorFilter);
			}
			
			if (lineFilters.size() > 0)
			{
				figuresStream = figuresStream.filter(lineFilters);
			}
		}
		
		return figuresStream;
	}
	
	public boolean isMoveMode() {
		return moveMode;
	}

	public void setMoveMode(boolean moveMode) {
		this.moveMode = moveMode;
	}
}
