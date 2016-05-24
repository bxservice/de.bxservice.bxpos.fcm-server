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
package de.bxservice.bxpos.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NotificationContent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5654341364305784569L;
	private List<String> registration_ids;
    private Map<String,String> data;
    private Map<String,String> notification;

    public void addRegId(String regId) {
        if (registration_ids == null)
            registration_ids = new LinkedList<String>();
        registration_ids.add(regId);
    }

    public void createData(String title, String message, String requestCode) {
        if (data == null)
            data = new HashMap<String,String>();

        if (title != null && !title.isEmpty())
        	data.put("title", title);
        if (message != null && !message.isEmpty())
        	data.put("message", message);
        if (requestCode != null && !requestCode.isEmpty())
        	data.put(BXPOSNotificationCode.REQUEST_TYPE, requestCode);

    }
    
    public void createNotification(String body, String title, String actionCode) {
        if (notification == null)
        	notification = new HashMap<String,String>();
        
        if (actionCode != null && !actionCode.isEmpty())
        	notification.put("click_action", actionCode);
        if (body != null && !body.isEmpty())
        	notification.put("body", body);
        if (title != null && !title.isEmpty())
        	notification.put("title", title);
    }   
}
