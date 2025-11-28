package pentarctagon.hmis.data.campaign.rulecmd.utils;

import com.fs.starfarer.api.Global;

public class Sizing
{
	public static final float PANEL_WIDTH = 450;
	public static float panelHeight()
	{
		// Already has UI scaling applied to it
		float screenHeight = Global.getSettings().getScreenHeight();
		float minHeight = 700f;
		// 75% of the screen should be good for all monitors, except for very small ones
		return Math.max(screenHeight * 0.75f, minHeight);
	}
}
