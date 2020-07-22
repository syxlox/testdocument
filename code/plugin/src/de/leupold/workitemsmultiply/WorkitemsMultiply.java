package de.leupold.workitemsmultiply;


import com.polarion.alm.projects.model.IProject;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.alm.tracker.model.ITrackerProject;
import com.polarion.core.util.types.duration.DurationTime;
import com.polarion.platform.core.PlatformContext;
import com.polarion.platform.persistence.model.IPObject;
import com.polarion.platform.persistence.model.IPObjectList;
import com.polarion.platform.security.ISecurityService;
import com.polarion.core.util.exceptions.UserFriendlyRuntimeException;
import com.polarion.subterra.base.SubterraURI;
import com.polarion.subterra.base.data.identification.ContextId;
import com.polarion.alm.tracker.model.ITrackerProject;
import com.polarion.platform.ITransactionService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import org.apache.jasper.compiler.JavacErrorDetail; 

public class WorkitemsMultiply {

	private IPObjectList<IWorkItem> wis;
	private IWorkItem properties;
	private IProject pro;
	private ITrackerProject tpro;
	private ITransactionService transaction;
	private String returnvalue;
	
	
	private List<String> dcclistcp;
	private List<String> dcclistnoncp;
	private List<String> optionalcp;
	private List<String> optionalnoncp;
	private List<String> cpotcs;
	private int cpoff;
	private int noncpoff;
	
	
	private List<Object> obj;
	private EnumMap <keys, String[]> dependencies; 

	public enum keys {
		dcclistcp,cpotcs,dcclistnoncp,optionalcp,optionalnoncp
	}

	
	
	public WorkitemsMultiply(ITrackerService tracker, IWorkItem item, ITransactionService trans) {
		
		transaction = trans;
		properties = item;	//get the properties workitem
    	pro = tracker.getProjectsService().getProject(properties.getCustomField("projectid").toString());		//get the project instance, of the project within the properties wi
    	tpro = tracker.getTrackerProject(pro);	//get the project tracker instance; used for creating new wis in this explicit project
    	dependencies = new EnumMap<>(keys.class);
    	returnvalue = "initialized; " + "</br>" + properties.toString() + " " + "</br>";
		
	}
	
	public boolean getDependencies() {
		
		wis = tpro.queryWorkItems("type:testdocument AND aspectkey.KEY:(aspect_l aspect_p) AND NOT HAS_VALUE:dcc_class.KEY", null);	//query all workitems which have to be processed
		returnvalue = returnvalue + wis.toString() + "</br>";
		
		int a = 0;
		for(keys i : keys.values()) {
			Object temp = properties.getCustomField(i.toString());	//check if any required field is empty, if yes -> return false
			if(temp != null) {
				dependencies.put(i ,temp.toString().split(";"));
				a++;
			}
			else { 
				returnvalue += " " + i.toString() + " is empty" +"</br>";
				return false;
			}
		}
		
		
		
		if(properties.getCustomField("cpoff") == null || properties.getCustomField("noncpoff") == null) {
			returnvalue += "a integer field seems to be empty, look at the values" + "</br>";
			return false; 
		}
		else {
			cpoff = (int) properties.getCustomField("cpoff");
			noncpoff = (int) properties.getCustomField("noncpoff");
		}
		
		
		cpotcs = Arrays.asList(dependencies.get(keys.cpotcs));
		
		optionalcp = Arrays.asList(dependencies.get(keys.optionalcp));
		optionalnoncp = Arrays.asList(dependencies.get(keys.optionalnoncp));
		
		dcclistcp = Arrays.asList(dependencies.get(keys.dcclistcp));
		dcclistnoncp = Arrays.asList(dependencies.get(keys.dcclistnoncp));
		
		if(wis == null) {
			returnvalue += "there are no workitems to multiply" + "</br>";
			return false;
		}
		
		return true;
	}
	
	public void Multiply() throws java.lang.Exception{

		returnvalue = "";
		transaction.beginTx();	//establish transaction; needed to save workitems 
		returnvalue += "transaction began" + "</br>";
		returnvalue += "<p style=\"color:red\">multiplication starts here, following are the multiplied items" + "</br>";
		
		for(IWorkItem element : wis) {
			
			int a = 0;
			
			if(cpotcs.contains(element.getCustomField("otc").toString())) {	//workitem is a c&p object
				for(String dcc : dcclistcp) {
					IWorkItem newwi = tpro.createWorkItem("testdocument");	//create new wi	
					element.copyTo(newwi, null, null, true, null, null);	//copy all info from the queued wi to the newly generated
					newwi.setEnumerationValue("dcc_class", dcc);	
					newwi.setEnumerationValue("code_letter", "e");	//code letter e is default
					if(optionalcp.contains(dcc)) {		//see if document is optional or not
						newwi.setEnumerationValue("vsc", "optional");
						newwi.setEnumerationValue("lcc", "optional");
					}
					else {
						newwi.setEnumerationValue("vsc", "used");
						newwi.setEnumerationValue("lcc", "used");
					}
					if(a <= cpoff) {	//the value of cpoff holds the number off offsite tests matching the dccs; if a > cpoff its onsite testing
						newwi.setEnumerationValue("testingsite", "offsite");
					}
					else{
						newwi.setEnumerationValue("testingsite", "onsite");
					}
					a++;
					newwi.save();
					returnvalue += newwi.toString() + "</br>";
				}
			}
				
			else {
				for(String item : dcclistnoncp) {	//workitem is no c&p object
					IWorkItem newwi = tpro.createWorkItem("testdocument");
					element.copyTo(newwi, null, null, true, null, null);
					newwi.setEnumerationValue("dcc_class", item);
					newwi.setEnumerationValue("code_letter", "e");
					if(optionalnoncp.contains(item)) {
						newwi.setEnumerationValue("vsc", "optional");
						newwi.setEnumerationValue("lcc", "optional");
					}
					else {
						newwi.setEnumerationValue("vsc", "used");
						newwi.setEnumerationValue("lcc", "used");
					}
					if(a <= noncpoff) {
						newwi.setEnumerationValue("testingsite", "offsite");
					}
					else {
						newwi.setEnumerationValue("testingsite", "onsite");
					}
					a++;
					newwi.save();
					returnvalue += newwi.toString() + a + "</br>";
				}
			}
			element.delete();	//delete queued wi; its not required anymore
		}	
		transaction.commitTx();	//commit the transaction to save all the changes made
		returnvalue += "</p>";
		returnvalue += "transaction ended" + "</br>";
}
	
	public String getReturnvalue() {
		return returnvalue;	//get information
	}
	
	public void testfunction() {	//a testfunction if something has to be tested/changed
		
	}
}
