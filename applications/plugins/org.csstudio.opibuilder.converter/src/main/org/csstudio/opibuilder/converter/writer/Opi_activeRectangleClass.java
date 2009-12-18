package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmLineStyle;
import org.csstudio.opibuilder.converter.model.Edm_activeRectangleClass;

/**
 * XML conversion class for Edm_activeRectangleClass
 * @author Matevz
 */
public class Opi_activeRectangleClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeRectangleClass");
	private static final String typeId = "Rectangle";
	private static final String name = "EDM Rectangle";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.  
	 */
	public Opi_activeRectangleClass(Context con, Edm_activeRectangleClass r) {
		super(con);
		setTypeId(typeId);

		context.getElement().setAttribute("version", version);

		/* It is not clear how rectangle attributes are mapped precisely since the
		 * mapping is not one to one.
		 * 
		 * For now, the mapping is as follows:
		 * 
		 * line* Edm attributes are converted into border* Opi attributes.
		 * fill* Edm attributes are converted into background* Opi attributes.
		 * 
		 * It is not yet defined what to do with visibility* Edm attributes and
		 * line* /foreground* Opi attributes.
		 */

		new OpiString(context, "name", name);

		new OpiInt(context, "x", r.getX() - context.getX());
		new OpiInt(context, "y", r.getY() - context.getY());
		new OpiInt(context, "width", r.getW());
		new OpiInt(context, "height", r.getH());

		new OpiColor(context, "border_color", r.getLineColor());
		if (r.getFillColor().isInitialized()) {
			new OpiColor(context, "color_background", r.getFillColor());
		}

		if (r.getAttribute("lineWidth").isInitialized()) {
			new OpiInt(context, "border_width", r.getLineWidth());
		}

		/* It is not clear when there is no border for EDM display. 
		 * 
		 * Here it is assumed there is no border (border style == 0) when line style
		 * is not set. 
		 * 
		 * The alternative would be to use lineWidth? 
		 */
		int borderStyle = 0;
		if (r.getLineStyle().isInitialized()) {

			switch (r.getLineStyle().get()) {
			case EdmLineStyle.SOLID: {
				borderStyle = 1;
			} break;
			case EdmLineStyle.DASH: {
				borderStyle = 9;
			} break;
			}

		}
		new OpiInt(context, "border_style", borderStyle);

		/**
		 * Of the visibility parameters only boolean visibility output specified.
		 * If invisible, the visibility is false, if not specified it is true.
		 * 
		 * visPv, visMin, visMax and visInvert are not output yet.  
		 */
		boolean visible = !r.getAttribute("invisible").isInitialized() || !r.isInvisible();
		new OpiBoolean(context, "visible", visible);

		log.debug("Edm_activeRectangleClass written.");

	}

}