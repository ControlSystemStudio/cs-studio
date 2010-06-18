package org.csstudio.opibuilder.script;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.platform.util.StringUtil;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**Data of a rule.
 * @author Xihui Chen
 *
 */
public class RuleData implements IAdaptable{

	private static final String QUOTE = "\""; //$NON-NLS-1$

	/**
	 * The name of the rule.
	 */
	private String name;
	
	/**
	 * Id of the property which the rule will apply to. 
	 */
	private String propId;	
	
	private AbstractWidgetModel widgetModel;
	
	/**
	 * Output expression value.
	 */
	private boolean outputExpValue;
	
	/**
	 * List of expressions.
	 */
	private List<Expression> expressionList;
		
	/**
	 * The input PVs of the rule. Which can be accessed in the rule and trigger the rule execution.
	 */
	private List<PVTuple> pvList;
	
	public RuleData(AbstractWidgetModel widgetModel) {
		this.widgetModel = widgetModel;
		expressionList = new ArrayList<Expression>();
		pvList = new ArrayList<PVTuple>();
		name = "Rule";
		propId = "name"; //$NON-NLS-1$
	}
		
	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the propId
	 */
	public final String getPropId() {
		return propId;
	}
	
	/**
	 * @param propId the propId to set
	 */
	public final void setPropId(String propId) {
		this.propId = propId;
	}
	
	public void setOutputExpValue(boolean outputExpValue) {
		this.outputExpValue = outputExpValue;
	}
	
	public boolean isOutputExpValue() {
		return outputExpValue;
	}
	

	public List<Expression> getExpressionList(){
		return expressionList;
	}
	
	public void addExpression(Expression exp){
		if(!expressionList.contains(exp))
			expressionList.add(exp);
	}
	
	public void removeExpression(Expression exp){
		expressionList.remove(exp);
	}
	
	/**Get the input PVs of the script 
	 * @return
	 */
	public List<PVTuple> getPVList() {
		return pvList;
	}
	
	public void addPV(PVTuple pvTuple){
		if(!pvList.contains(pvTuple)){
			pvList.add(pvTuple);
		}			
	}
	
	public void removePV(String pv){
		pvList.remove(pv);
	}	
	
	public String generateScript(){
		if(expressionList.size() <=0)
			return ""; //$NON-NLS-1$
		StringBuilder sb = new StringBuilder(
				"importPackage(Packages.org.csstudio.opibuilder.scriptUtil); \n"); //$NON-NLS-1$
		
		AbstractWidgetProperty property = widgetModel.getProperty(propId);
		boolean needDbl = false, needStr = false, needSev=false;
		for(Expression exp : expressionList){
			if(!needDbl)
				needDbl = StringUtil.containRegex(exp.getBooleanExpression(), "pv\\d") || //$NON-NLS-1$
						(outputExpValue && StringUtil.containRegex(exp.getValue().toString(), "pv\\d")); //$NON-NLS-1$
			if(!needStr){
				if(exp.getBooleanExpression().contains("pvStr")) //$NON-NLS-1$
					needStr = true;
				if(outputExpValue && exp.getValue().toString().contains("pvStr")) //$NON-NLS-1$
					needStr = true;
			}
			if(!needSev){
				if(exp.getBooleanExpression().contains("pvSev")) //$NON-NLS-1$
					needSev = true;
				if(outputExpValue && exp.getValue().toString().contains("pvSev")) //$NON-NLS-1$
					needSev = true;
			}
				
		}
		for(int i=0; i<pvList.size(); i++){
			if(needDbl)
				sb.append("var pv" + i + " = PVUtil."+ "getDouble(pvArray[" + i + "]);\n");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if(needStr)
				sb.append("var pvStr" + i + " = PVUtil."+ "getString(pvArray[" + i + "]);\n");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if(needSev)
				sb.append("var pvSev" + i + " = PVUtil.getSeverity(pvArray[" + i + "]);\n");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		int i=0;
		for(Expression exp : expressionList){
			sb.append(i == 0 ? "if(" : "else if(");	//$NON-NLS-1$ //$NON-NLS-2$
			sb.append(expressionList.get(i++).getBooleanExpression());
			sb.append(")\n");//$NON-NLS-1$
			
			sb.append("\twidgetController.setPropertyValue(\"" + propId + "\","); //$NON-NLS-1$ //$NON-NLS-2$
			
			String propValue = generatePropValueString(property, exp);
			sb.append(propValue + ");\n"); //$NON-NLS-1$			
		}
		sb.append("else\n"); //$NON-NLS-1$
		sb.append("\twidgetController.setPropertyValue(\"" + propId + "\"," + //$NON-NLS-1$ //$NON-NLS-2$
				generatePropValueString(property, null)+");\n"); //$NON-NLS-1$
		
		return sb.toString();
	}

	public RuleData getCopy(){
		RuleData result = new RuleData(widgetModel);
		result.setName(name);
		result.setOutputExpValue(outputExpValue);
		result.setPropId(propId);
		for(Expression expression : expressionList){
			result.addExpression(expression.getCopy());
		}
		for(PVTuple pvTuple : pvList){
			result.addPV(pvTuple.getCopy());
		}
		return result;
	}
	
	/**
	 * @param property
	 * @param exp
	 * @param propValue
	 * @return
	 */
	private String generatePropValueString(AbstractWidgetProperty property,
			Expression exp) {
		Object value;
		String propValue;
		if(exp != null && outputExpValue){
			propValue = exp.getValue().toString();
			return propValue;
		}
		else{
			if(exp != null)
				value = exp.getValue();
			else
				value = property.getPropertyValue();
			
			
			if(property instanceof BooleanProperty){			
				propValue = (Boolean)value? "true" : "false"; //$NON-NLS-1$ //$NON-NLS-2$
			}else if(property instanceof ColorProperty){
				OPIColor opiColor = (OPIColor)value;
				if(opiColor.isPreDefined())
					propValue = QUOTE + opiColor.getColorName()+QUOTE;
				else{
					RGB rgb = opiColor.getRGBValue();
					propValue = "ColorFontUtil.getColorFromRGB("+ //$NON-NLS-1$
						rgb.red + "," + rgb.green + "," + rgb.blue + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}else if(property instanceof ComboProperty || property instanceof IntegerProperty)
				propValue = ((Integer)value).toString();
			else if(property instanceof DoubleProperty)
				propValue = ((Double)value).toString();
			else
				propValue = value.toString();		
		}
	
		if(property instanceof StringProperty || property instanceof FilePathProperty)
			propValue = QUOTE + propValue + QUOTE;
		return propValue;
	}

	public AbstractWidgetProperty getProperty() {
		return widgetModel.getProperty(propId);
	}
	
	/**Convert this {@link RuleData} to {@link RuleScriptData} so 
	 * that the scriptEngine code can be reused for running rules.
	 * @return
	 */
	public RuleScriptData convertToScriptData(){
		RuleScriptData ruleScriptData  = new RuleScriptData(this);
		ruleScriptData.setPVList(pvList);
		ruleScriptData.setScriptString(generateScript());
		return ruleScriptData;
	}
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if(adapter == IWorkbenchAdapter.class)
			return new IWorkbenchAdapter() {
				
				public Object getParent(Object o) {
					return null;
				}
				
				public String getLabel(Object o) {
					return name;
				}
				
				public ImageDescriptor getImageDescriptor(Object object) {
					return CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
							OPIBuilderPlugin.PLUGIN_ID, "icons/js.gif");
				}
				
				public Object[] getChildren(Object o) {
					return new Object[0];
				}
			};
		
		return null;
	}

	public AbstractWidgetModel getWidgetModel() {
		return widgetModel;
	}
}
