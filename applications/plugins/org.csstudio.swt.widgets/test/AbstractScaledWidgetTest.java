import java.util.ArrayList;
import java.util.List;


public abstract class AbstractScaledWidgetTest extends AbstractWidgetTest {

	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();	
		List<String> superPropList = new ArrayList<String>();
		for(String p : superProps){
			if(!p.equals("opaque"))
				superPropList.add(p);
		}
		String[] scaleProps = new String[]{
				"transparent",
				"value",
				"minimum",
				"maximum",
				"majorTickMarkStepHint",
				"showMinorTicks",
				"showScale",
				"logScale"
		};
		return concatenateStringArrays(superPropList.toArray(new String[]{}), scaleProps);
	}
	
}
