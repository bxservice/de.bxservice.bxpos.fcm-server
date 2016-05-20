package de.bxservice.process;
import org.adempiere.base.IProcessFactory;
import org.compiere.process.ProcessCall;


public class NotificationProcessFactory implements IProcessFactory {

	@Override
	public ProcessCall newProcessInstance(String className) {
		ProcessCall process = null;
		if ("de.bxservice.process.NotificationProcess".equals(className)) {
			try {
				process =  NotificationProcess.class.newInstance();
			} catch (Exception e) {}
		}

		return process;
	}

}
