package pentarctagon.hmis.data.campaign.rulecmd.utils;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;

public class LunaHelper
{
	private static final boolean enabled = Global.getSettings().getModManager().isModEnabled("lunalib");
	private static final String mod = "pentarctagon_HullModIndustrialServices";

	public static boolean getBoolean(String key, boolean def)
	{
		if(!enabled)
		{
			return def;
		}

		Boolean value = LunaSettings.getBoolean(mod, key);
		return value != null ? value : def;
	}

	public static double getDouble(String key, double def)
	{
		if(!enabled)
		{
			return def;
		}

		Double value = LunaSettings.getDouble(mod, key);
		return value != null ? value : def;
	}

	public static float getFloat(String key, float def)
	{
		if(!enabled)
		{
			return def;
		}

		Float value = LunaSettings.getFloat(mod, key);
		return value != null ? value : def;
	}
}
