package filters;

import figures.Figure;
import figures.enums.FigureType;

/**
 * Filtre de figure appliqué au type de figure
 * @author davidroussel
 */
public class ShapeFilter extends FigureFilter<FigureType>
{
	/**
	 * Constructeur d'un {@link ShapeFilter} à partir d'un type de figure
	 * @param element le type de figure à tester par ce prédicat
	 */
	public ShapeFilter(FigureType element)
	{
		super(element);
	}

	/**
	 * Test du prédicat
	 * @param f la figure à tester
	 * @return vrai si la figure f est du type contenu dans element
	 * @see java.util.function.Predicate#test(java.lang.Object)
	 */
	@Override
	public boolean test(Figure f)
	{
		return f.getType() == element;
	}
}
