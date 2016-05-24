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

import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

import de.bxservice.bxpos.server.NotificationContent;
import de.bxservice.bxpos.server.POST2GCM;

public class NotificationProcess extends SvrProcess {
	
	private static final String API_KEY = "AIzaSyC2Vwvpq2cQl4_nsUO2xbHpmUIm2Uv2GiY";
	private NotificationContent content;
	private List<List<Object>> deviceTokens;

	@Override
	protected void prepare() {
			
	}

	@Override
	protected String doIt() throws Exception {
		
		String message = null;
		
		log.log(Level.INFO, "Sending POST to GCM");
        
        StringBuilder selectQuery = new StringBuilder("Select bxs_devicetoken FROM BXS_DeviceRegistration")
		.append(" WHERE ")
		.append("IsActive='Y' AND AD_Client_ID=? AND AD_Org_ID=?");
        
        //Bring the devices that are registered
        deviceTokens = DB.getSQLArrayObjectsEx(get_TrxName(), selectQuery.toString(), Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx()));
        
        if(deviceTokens != null && deviceTokens.size() > 0) {
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
		for (List<Object> row : deviceTokens) {        
			for(Object token : row) {
				c.addRegId((String) token);
			}
		}
			
        c.createData("Test Title", "Test Message");
        c.createNotification(Msg.getMsg(Env.getCtx(), "BXS_UpdateRequestDescription"), Msg.getMsg(Env.getCtx(), "BXS_UpdateRequestMessage"));

        return c;
    }

}
