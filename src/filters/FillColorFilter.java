package filters;

import java.awt.Paint;

import figures.Figure;

/**
 * Filtre filtrant les figures possédant une certaine couleur de remplissage
 * @author davidroussel
 */
public class FillColorFilter extends FigureFilter<Paint>
{
	/**
	 * Constructeur d'un {@link FillColorFilter}
	 * @param paint la couleur à filtrer
	 */
	public FillColorFilter(Paint paint)
	{
		super(paint);
	}

	/**
	 * Test du prédicat
	 * @return true si la figure courante possède la même couleur de remplissage
	 */
	@Override
	public boolean test(Figure f)
	{
		return f.getFillPaint().equals(element);
	}

}
