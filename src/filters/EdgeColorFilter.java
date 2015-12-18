package filters;

import java.awt.Paint;

import figures.Figure;

/**
 * Filtre filtrant les figures possédant une certaine couleur de trait
 * @author davidroussel
 */
public class EdgeColorFilter extends FigureFilter<Paint>
{
	/**
	 * Constructeur d'un {@link EdgeColorFilter}
	 * @param paint la couleur à filtrer
	 */
	public EdgeColorFilter(Paint paint)
	{
		super(paint);
	}

	/**
	 * Test du prédicat
	 * @return true si la figure courante possède la même couleur de trait
	 */
	@Override
	public boolean test(Figure f)
	{
		return f.getEdgePaint().equals(element);
	}

}
