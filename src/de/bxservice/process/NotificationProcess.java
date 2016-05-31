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
package de.bxservice.process;

import java.util.List;
import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import de.bxservice.bxpos.server.BXPOSDevice;
import de.bxservice.bxpos.server.BXPOSNotificationCode;
import de.bxservice.bxpos.server.NotificationContent;
import de.bxservice.bxpos.server.POST2GCM;

public class NotificationProcess extends SvrProcess {
	
	private static final String API_KEY = "AIzaSyC2Vwvpq2cQl4_nsUO2xbHpmUIm2Uv2GiY";
	private NotificationContent content;
	private List<List<Object>> deviceTokens;
	private String notificationType = "";

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("BXS_NotificationType"))
				notificationType = (String)para[i].getParameter();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}

	@Override
	protected String doIt() throws Exception {
		
		String message = null;
		
		log.log(Level.INFO, "Sending POST to GCM");
		
		deviceTokens = BXPOSDevice.getDeviceTokens(true, get_TrxName());
        
        if (deviceTokens != null && deviceTokens.size() > 0) {
            content = createContent();
            int responseCode = POST2GCM.post(API_KEY, content);
            
            //HTTP Code : OK 200. The request was fulfilled.
            if (responseCode == 200)
            	message = Msg.getMsg(Env.getCtx(), "BXS_NotificationSent");
            else
            	message = Msg.getMsg(Env.getCtx(), "BXS_NotificationFailed");
        }
        else {
        	message = Msg.getMsg(Env.getCtx(), "BXS_NoDeviceFound");
        }
		
        return message;
	}
	
	public NotificationContent getContent() {
		return content;
	}

	public void setContent(NotificationContent content) {
		this.content = content;
	}

	public NotificationContent createContent() {
		NotificationContent c = new NotificationContent();

		//Add the registered devices to notify
		c.registerDevices(deviceTokens);
		
		String requestType = "";
		String actionCode = "";
		String notificationMessage = "";
		String notificationDescription = "";
		if ("RE".equals(notificationType)) {
			requestType = String.valueOf(BXPOSNotificationCode.RECOMMENDED_REQUEST_CODE);
			actionCode = BXPOSNotificationCode.RECOMMENDED_UPDATE_ACTION;
			notificationMessage = Msg.getMsg(Env.getCtx(), "BXS_UpdateRequestMessage");
			notificationDescription = Msg.getMsg(Env.getCtx(), "BXS_UpdateRequestDescription");
		} else if ("MA".equals(notificationType)) {
			requestType = String.valueOf(BXPOSNotificationCode.MANDATORY_REQUEST_CODE);
			actionCode = BXPOSNotificationCode.MANDATORY_UPDATE_ACTION;
			notificationMessage = Msg.getMsg(Env.getCtx(), "BXS_MandatoryUpdateRequestMessage");
		}

        c.createData(BXPOSNotificationCode.REQUEST_TYPE, requestType);
        c.createNotification(notificationDescription, notificationMessage, actionCode);

        return c;
    }

}
