package pentarctagon.hmis.data.campaign.rulecmd.ui.refit;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import org.apache.log4j.*;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionStuff
{
	private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());

	public static UIPanelAPI getCoreUI()
	{
		CampaignUIAPI campaignUI = Global.getSector().getCampaignUI();
		InteractionDialogAPI dialog = campaignUI.getCurrentInteractionDialog();

		if(dialog == null)
		{
			try
			{
				Field field = campaignUI.getClass().getDeclaredField("core");
				field.setAccessible(true);

				return (UIPanelAPI) field.get(campaignUI);
			}
			catch(Exception e)
			{
				log.error("Failed to retrieve field 'core'", e);
				return null;
			}
		}
		else
		{
			return (UIPanelAPI) ReflectionStuff.invokeMethod(dialog, "getCoreUI");
		}
	}

	public static Object invokeMethod(Object o, String methodName, Object... args)
	{
		try
		{
			return invokeMethodNoCatchExt(o, methodName, args);
		}
		catch(Exception e)
		{
			log.error("Failed to invoke method" + methodName, e);
			return null;
		}
	}

	public static Object invokeMethodNoCatch(Object o, String methodName, Object... args)
	throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		return invokeMethodNoCatchExt(o, methodName, args);
	}

	private static Object invokeMethodNoCatchExt(Object o, String methodName, Object... args)
	throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
	{
		if(o == null)
		{
			return null;
		}

		Class<?>[] argClasses = new Class<?>[args.length];
		for(int i = 0; i < args.length; i++)
		{
			argClasses[i] = args[i].getClass();
			// unbox
			if(argClasses[i] == Integer.class)
			{
				argClasses[i] = int.class;
			}
			else if(argClasses[i] == Boolean.class)
			{
				argClasses[i] = boolean.class;
			}
			else if(argClasses[i] == Float.class)
			{
				argClasses[i] = float.class;
			}
		}

		Method method = o.getClass().getMethod(methodName, argClasses);
		return method.invoke(o, args);
	}
}
