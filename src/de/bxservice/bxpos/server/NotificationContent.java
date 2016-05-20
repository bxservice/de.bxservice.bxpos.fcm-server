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

    public void addRegId(String regId){
        if(registration_ids == null)
            registration_ids = new LinkedList<String>();
        registration_ids.add(regId);
    }

    public void createData(String title, String message){
        if(data == null)
            data = new HashMap<String,String>();

        data.put("title", title);
        data.put("message", message);
        data.put("message2", message);
    }
    
    public void createNotification(String body, String title){
        if(notification == null)
        	notification = new HashMap<String,String>();

        notification.put("body", body);
        notification.put("title", title);
    }   
}
