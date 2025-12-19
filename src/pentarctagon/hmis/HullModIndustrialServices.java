package pentarctagon.hmis;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;
import pentarctagon.hmis.dmods.RestorationCostListener;
import pentarctagon.hmis.industries.HullModServices;

@SuppressWarnings("unused")
public class HullModIndustrialServices
extends BaseModPlugin
{
	@Override
	public void onGameLoad(boolean newGame)
	{
		Global.getSector().getListenerManager().addListener(new RestorationCostListener(), true);

		Misc.getFactionMarkets(Factions.HEGEMONY)
		    .stream()
		    .filter(market -> market.getId().equals("chicomoztoc") && !market.hasIndustry(HullModServices.ID))
		    .findFirst()
		    .ifPresent(market -> market.addIndustry(HullModServices.ID));

		Misc.getFactionMarkets(Factions.PERSEAN)
		    .stream()
		    .filter(market -> market.getId().equals("kazeron") && !market.hasIndustry(HullModServices.ID))
		    .findFirst()
		    .ifPresent(market -> market.addIndustry(HullModServices.ID));

		Misc.getFactionMarkets(Factions.TRITACHYON)
		    .stream()
		    .filter(market -> market.getId().equals("culann") && !market.hasIndustry(HullModServices.ID))
		    .findFirst()
		    .ifPresent(market -> {
			    market.addIndustry(HullModServices.ID);
			    market.getIndustry(HullModServices.ID).setAICoreId(Commodities.ALPHA_CORE);
		    });
	}
}
