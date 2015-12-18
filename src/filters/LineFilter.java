package filters;

import figures.Figure;
import figures.enums.LineType;

/**
 * Filtre filtrant les figures ayant un certain type de trait
 * @author davidroussel
 */
public class LineFilter extends FigureFilter<LineType>
{
	/**
	 * Constructeur d'un {@link LineFilter}
	 * @param type le type de ligne à filtrer
	 */
	public LineFilter(LineType type)
	{
		super(type);
	}

	/**
	 * Test du prédicat
	 * @return true si la figure courante possède le même type de trait
	 */
	@Override
	public boolean test(Figure f)
	{
		return LineType.fromStroke(f.getStroke()) == element;
	}
}
