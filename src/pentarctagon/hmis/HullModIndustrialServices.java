package pentarctagon.hmis;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import pentarctagon.hmis.dmods.RestorationCostListener;

@SuppressWarnings("unused")
public class HullModIndustrialServices
extends BaseModPlugin
{
	@Override
	public void onGameLoad(boolean newGame)
	{
		Global.getSector().getListenerManager().addListener(new RestorationCostListener(), true);
	}
}
