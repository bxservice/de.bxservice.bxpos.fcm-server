package de.bxservice.process;

import org.compiere.process.SvrProcess;

import de.bxservice.bxpos.server.NotificationContent;
import de.bxservice.bxpos.server.POST2GCM;

public class NotificationProcess extends SvrProcess {
	
	public NotificationContent content;

	@Override
	protected void prepare() {
			
	}

	@Override
	protected String doIt() throws Exception {
		
		System.out.println( "Sending POST to GCM" );

        String apiKey = "AIzaSyC2Vwvpq2cQl4_nsUO2xbHpmUIm2Uv2GiY";
        content = createContent();

        POST2GCM.post(apiKey, content);
		
		
		
        return null;
	}
	
	public static NotificationContent createContent() {

		NotificationContent c = new NotificationContent();

        c.addRegId("evNEIEiPy1I:APA91bHA6yIE3MB5srAoSX_A0dZaNxmnRyvP_O5nVrqC5dsKwBbZ-SGtamkMdy2Qp2VNravfokIP8lGLY3JiTzA9JGB-nmneV2sY0lQYmVsVO0uJWRLdk5cCSdD4BKc5Cuj_kvQ0YWuL");//Bx phone
        c.addRegId("e-PbprbswBg:APA91bEzGmu1qkO9_gUWITmnEbqSBfCaJzyJKwmWUYQvmxEb-EiGSHHv7Q1mL2h9tsaEyXVvC0mHMrAJbLlUbIZOueDOKWfoEMxOb_xP6utwUWwCa5pigvc02lQCChzhY2GngN1KwGve");// Vanessa's
        c.createData("Test Title", "Test Message");
        c.createNotification("Title notification", "Notification message");

        return c;
    }

}
