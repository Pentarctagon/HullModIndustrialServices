package pentarctagon.hmis;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URL;
import java.net.URLClassLoader;

@SuppressWarnings("unused")
public class HullModIndustrialServices
extends BaseModPlugin
{
	private static final String[] reflectionWhitelist = new String[] {
		"pentarctagon.hmis.data.campaign.rulecmd.ui.refit.RefitTabListenerAndScript",
		"pentarctagon.hmis.data.campaign.rulecmd.ui.refit.ReflectionStuff"
	};

	public static ReflectionEnabledClassLoader getClassLoader()
	{
		URL url = HullModIndustrialServices.class.getProtectionDomain().getCodeSource().getLocation();
		return new ReflectionEnabledClassLoader(url, HullModIndustrialServices.class.getClassLoader());
	}

	@Override
	public void onGameLoad(boolean newGame)
	{
		try
		{
			Class<?> cls = getClassLoader().loadClass("pentarctagon.hmis.data.campaign.rulecmd.ui.refit.RefitTabListenerAndScript");
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			MethodHandle mh = lookup.findConstructor(cls, MethodType.methodType(void.class));
			EveryFrameScript refitScript = (EveryFrameScript) mh.invoke();
			ListenerManagerAPI listeners = Global.getSector().getListenerManager();
			listeners.addListener(refitScript, true);
			Global.getSector().addTransientScript(refitScript);
		}
		catch(Throwable e)
		{
			throw new RuntimeException("Failed to add refit tab listener", e);
		}
	}

	public static class ReflectionEnabledClassLoader
	extends URLClassLoader
	{
		public ReflectionEnabledClassLoader(URL url, ClassLoader parent)
		{
			super(new URL[]{url}, parent);
		}

		@Override
		public Class<?> loadClass(String name)
		throws ClassNotFoundException
		{
			if(name.startsWith("java.lang.reflect"))
			{
				return ClassLoader.getSystemClassLoader().loadClass(name);
			}
			return super.loadClass(name);
		}

		@Override
		public Class<?> loadClass(String name, boolean resolve)
		throws ClassNotFoundException
		{
			Class<?> c = findLoadedClass(name);
			if(c != null)
			{
				return c;
			}
			// Be the defining classloader for all classes in the reflection whitelist
			// For classes defined by this loader, classes in java.lang.reflect will be loaded directly
			// by the system classloader, without the intermediate delegations.
			for(String str : reflectionWhitelist)
			{
				if(name.startsWith(str))
				{
					return findClass(name);
				}
			}
			return super.loadClass(name, resolve);
		}
	}
}
