/**********************************************************************
 * This file is part of FreiBier ERP                                   *
 *                                                                     *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Diego Ruiz - Bx Service GmbH                                      *
 **********************************************************************/
package de.bxservice.model;

import java.io.IOException;
import java.util.List;

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventTopics;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MUser;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.osgi.service.event.Event;

import de.bxservice.bxpos.server.BXPOSDevice;
import de.bxservice.bxpos.server.BXPOSNotificationCode;
import de.bxservice.bxpos.server.BXPOSPropertyValues;
import de.bxservice.bxpos.server.NotificationContent;
import de.bxservice.bxpos.server.POST2GCM;

public class BXPOSTableStatusEventHandler extends AbstractEventHandler {

	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(BXPOSTableStatusEventHandler.class);
	private NotificationContent content;
	private List<List<Object>> deviceTokens;
	private PO po;

	@Override
	protected void doHandleEvent(Event event) {
		String type = event.getTopic();
		po = getPO(event);
		log.info(po.get_TableName() + " Type: "+type);

		// When the table status is modified in the app
		if (type.equals(IEventTopics.PO_AFTER_CHANGE) &&
				po.is_ValueChanged(X_BAY_Table.COLUMNNAME_BXS_IsBusy)) {
			
			BXPOSPropertyValues properties = new BXPOSPropertyValues();
			String apiKey = null;
			try {
				apiKey = properties.getApiKey();
			} catch (IOException e) {
				throw new AdempiereException("No property file condigured");
			}
			
			if (apiKey != null) {
				deviceTokens = BXPOSDevice.getDeviceTokens(true, po.get_TrxName(), Env.getAD_Org_ID(Env.getCtx()));
				
				if (deviceTokens != null && deviceTokens.size() > 0) {
		            content = createContent();
		            POST2GCM.post(apiKey, content);
		        }
		        else {
		    		log.info(Msg.getMsg(Env.getCtx(), "BXS_NoDeviceFound"));
		        }
				
			}			
		}
	}

	@Override
	protected void initialize() {
		log.warning("");
				
		registerTableEvent(IEventTopics.PO_AFTER_CHANGE, X_BAY_Table.Table_Name);		
	}
	
	public NotificationContent getContent() {
		return content;
	}

	public void setContent(NotificationContent content) {
		this.content = content;
	}
	
	public NotificationContent createContent() {
		NotificationContent c = new NotificationContent();

		String tableId = "";
		String tableStatus = "";
		String userName = "";
		
		if (po != null) {
			tableId = String.valueOf(((X_BAY_Table) po).getBAY_Table_ID());
			tableStatus = ((X_BAY_Table) po).isBXS_IsBusy() ? "Y" : "N"; //SQLite boolean are integers
			
			MUser user = MUser.get(Env.getCtx(), ((X_BAY_Table) po).getUpdatedBy());
			if (user != null) 
				userName = user.getName();
		}
		//Add the registered devices to notify
		c.registerDevices(deviceTokens);		
        c.createData(BXPOSNotificationCode.REQUEST_TYPE, String.valueOf(BXPOSNotificationCode.TABLE_STATUS_CHANGED_CODE), 
        		BXPOSNotificationCode.CHANGED_TABLE_ID, tableId,
        		BXPOSNotificationCode.NEW_TABLE_STATUS, tableStatus,
        		BXPOSNotificationCode.SERVER_NAME, userName);

        return c;
    }

}
