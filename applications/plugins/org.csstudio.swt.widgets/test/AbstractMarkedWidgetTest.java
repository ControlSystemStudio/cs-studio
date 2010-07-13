

public abstract class AbstractMarkedWidgetTest extends AbstractScaledWidgetTest {

	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();	
		
		String[] myProps = new String[]{
				"showMarkers",
				"loloLevel",
				"loLevel",
				"hiLevel",
				"hihiLevel",
				"showLolo",
				"showLo",
				"showHi",
				"showHihi",
				"loloColor",
				"loColor",
				"hiColor",
				"hihiColor"
		};
		return concatenateStringArrays(superProps, myProps);
	}
	
}
