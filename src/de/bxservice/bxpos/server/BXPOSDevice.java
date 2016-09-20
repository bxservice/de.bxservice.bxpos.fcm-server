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

import java.util.List;

import org.compiere.util.DB;
import org.compiere.util.Env;

public class BXPOSDevice {
	
	private static List<List<Object>> deviceTokens;

	public static List<List<Object>> getDeviceTokens(boolean reQuery, String trxName, int AD_Org_ID) {
		if(deviceTokens == null || reQuery)
			readDeviceTokens(trxName, AD_Org_ID);
		return deviceTokens;
	}
	
	private static void readDeviceTokens(String trxName, int AD_Org_ID) {
		StringBuilder selectQuery = new StringBuilder("Select bxs_devicetoken FROM BXS_DeviceRegistration")
		.append(" WHERE ")
		.append("IsActive='Y' AND AD_Client_ID=? AND AD_Org_ID=?");
        
        //Bring the devices that are registered
        deviceTokens = DB.getSQLArrayObjectsEx(trxName, selectQuery.toString(), Env.getAD_Client_ID(Env.getCtx()), AD_Org_ID);
	}

}
