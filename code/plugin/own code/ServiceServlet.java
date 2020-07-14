/*
 * Copyright (C) 2004-2014 Polarion Software
 * All rights reserved.
 * Email: dev@polarion.com
 *
 *
 * Copyright (C) 2004-2014 Polarion Software
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Polarion Software.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * POLARION SOFTWARE MAKES NO REPRESENTATIONS OR WARRANTIES 
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESSED OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. POLARION SOFTWARE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT
 * OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package de.leupold.workitemsmultiply;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*; 

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import sun.invoke.empty.Empty;

import com.polarion.alm.tracker.model.ITrackerProject;
import com.polarion.platform.ITransactionService;

/**
 * 
 * This extension, superclass is a servlet, rendered as wikipage, processes Workitems and
 * multiply a base data to get several more workitems, based on the originally workitem.
 * The algorhythm and the use case is documented.
 * 
 * 
 *          
 * @author Stepan Roh for base code (doGet, do Post, .jsp file)
 */
public class ServiceServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    	
    	Object[] returnvalue = new Object[5];
        ITrackerService tracker = (ITrackerService) PlatformContext.getPlatform().lookupService(ITrackerService.class);
        ISecurityService securityService = (ISecurityService) PlatformContext.getPlatform().lookupService(ISecurityService.class);
        ITransactionService transaction = (ITransactionService) PlatformContext.getPlatform().lookupService(ITransactionService.class);
        
        returnvalue[0] = (String) "";
        
        IPObjectList items = tracker.queryWorkItems("type:properties", null);	//search for the properties workitem
        
        if(items.size() <= 1){	//if there is more than 1 properties workitem -> quit
			if(!(items.isEmpty())) {	//if there is no properties workitem -> quit procedure 
				
				WorkitemsMultiply toworkwith = new WorkitemsMultiply(tracker, (IWorkItem) items.get(0), transaction); //new workspace for multiplying
				
				Boolean check = toworkwith.getDependencies();
				returnvalue[0] += toworkwith.getReturnvalue();
				
				
				if(check) {
					try{
						returnvalue[0] += "multiply call" + "</br>";
						toworkwith.Multiply();
					}
					catch(Exception e) {
						StringWriter sw = new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						String exceptionAsString = sw.toString();
						returnvalue[0] += "transaction failed" + exceptionAsString + "</br" ;
					}
				}
				else returnvalue[0] += "getdependecies failed";
				
				returnvalue[0] += toworkwith.getReturnvalue();
				
				toworkwith = null;	//set references to null to collect the garbage
				items = null;
			}
		
			else returnvalue[0] = "no properties workitem found";
		}
		else returnvalue[0] = "more than one properties item found, only one per project is allowed";
        
        
        req.setAttribute("properties", returnvalue);
        
        getServletContext().getRequestDispatcher("/rendered.jsp").forward(req, resp);	//forward data to the servlet
    }
    
    
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
    
}
