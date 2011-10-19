package com.twinsoft.convertigo.beans.steps;

import java.beans.PropertyDescriptor;

import com.twinsoft.convertigo.beans.core.MySimpleBeanInfo;

public class MoveStepBeanInfo extends MySimpleBeanInfo{
	
	public MoveStepBeanInfo() {
		try {
			beanClass = MoveStep.class;
			additionalBeanClass = com.twinsoft.convertigo.beans.core.Step.class;

			iconNameC16 = "/com/twinsoft/convertigo/beans/steps/images/move_16x16.gif";
			iconNameC32 = "/com/twinsoft/convertigo/beans/steps/images/move_32x32.gif";
			
			resourceBundle = java.util.ResourceBundle.getBundle("com/twinsoft/convertigo/beans/steps/res/MoveStep");
			
			displayName = resourceBundle.getString("display_name");
			shortDescription = resourceBundle.getString("short_description");	          
		
			properties = new PropertyDescriptor[2];
			
			properties[0] = new PropertyDescriptor("sourcePath", beanClass, "getSourcePath", "setSourcePath");
			properties[0].setExpert(true);
			properties[0].setDisplayName(getExternalizedString("property.sourcePath.display_name"));
	        properties[0].setShortDescription(getExternalizedString("property.sourcePath.short_description"));            
	        properties[0].setValue("scriptable", Boolean.TRUE);
	        
	        properties[1] = new PropertyDescriptor("destinationPath", beanClass, "getDestinationPath", "setDestinationPath");
			properties[1].setExpert(true);
			properties[1].setDisplayName(getExternalizedString("property.destinationPath.display_name"));
	        properties[1].setShortDescription(getExternalizedString("property.destinationPath.short_description"));    
	        properties[1].setValue("scriptable", Boolean.TRUE);
		}
		catch(Exception e) {
			com.twinsoft.convertigo.engine.Engine.logBeans.error("Exception with bean info; beanClass=" + beanClass.toString(), e);
		}
	}

}
