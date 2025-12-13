package pentarctagon.hmis.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import pentarctagon.hmis.data.campaign.rulecmd.utils.Constants;

import java.awt.*;

public class HullModServices
extends BaseIndustry
{
	public static final String ID = "hullmodservices";

    @Override
    public void apply()
    {
        super.apply(true);

        if(market.getPrevStability() >= 7)
        {
            market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(0), 0.2f, "Hull Mod Services");
        }
		else if(market.hasIndustry(Industries.ORBITALWORKS) && market.hasIndustry(ID) && market.getIndustry(Industries.ORBITALWORKS).isDisrupted())
        {
	        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(0), 0f, "Hull Mod Services - Orbital Works disrupted");
        }
        else
        {
            market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(0), 0f, "Hull Mod Services - low stability");
        }

		if(Commodities.ALPHA_CORE.equals(getAICoreId()))
		{
			market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(1), 0.1f, "Hull Mod Services - Alpha Core");
		}
		else
		{
			market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodify(getModId(1));
		}

		if(market.hasIndustry(Industries.ORBITALWORKS))
		{
			demand(Commodities.METALS, market.getIndustry(Industries.ORBITALWORKS).getDemand(Commodities.METALS).getQuantity().getModifiedInt()+2);
			demand(Commodities.RARE_METALS, market.getIndustry(Industries.ORBITALWORKS).getDemand(Commodities.METALS).getQuantity().getModifiedInt());
			demand(Commodities.FUEL, market.getIndustry(Industries.ORBITALWORKS).getDemand(Commodities.METALS).getQuantity().getModifiedInt()+2);
		}
    }

    @Override
    public void unapply()
    {
        super.unapply();
        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodifyFlat(getModId(0));
    }

    @Override
    public boolean isAvailableToBuild()
    {
        return super.isAvailableToBuild() && market.hasIndustry(Industries.ORBITALWORKS) && market.getSize() >= Constants.HMIS_MIN_MARKET_SIZE;
    }

    @Override
    public String getUnavailableReason()
    {
	    if(!market.hasIndustry(Industries.ORBITALWORKS) && market.getSize() >= Constants.HMIS_MIN_MARKET_SIZE)
	    {
		    return "Requires Orbital Works";
	    }
	    if(market.hasIndustry(Industries.ORBITALWORKS) && market.getSize() < Constants.HMIS_MIN_MARKET_SIZE)
	    {
		    return "Requires at least size 5 colony";
	    }
	    if(!market.hasIndustry(Industries.ORBITALWORKS) && market.getSize() < Constants.HMIS_MIN_MARKET_SIZE)
	    {
		    return "Requires Orbital Works and at least a size 5 colony";
	    }

        if(!super.isAvailableToBuild()){
            return "Can not be built";
        }

        return "this should never happen, please report";
    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }

    @Override
    public int getImproveProductionBonus()
    {
        return 0;
    }

    @Override
    public void addImproveDesc(TooltipMakerAPI info, ImprovementDescriptionMode mode)
    {
        float initPad = 0f;
        float pad = 10f;
        boolean addedSomething = false;
        if(canImproveToIncreaseProduction())
        {
            if(mode == ImprovementDescriptionMode.INDUSTRY_TOOLTIP)
            {
                info.addPara("Reduced hullmod costs by an additional %s%%.", initPad, Misc.getHighlightColor(), "20");
            }
            else
            {
                info.addPara("Reduces hullmod costs by an additional %s%%.", initPad, Misc.getHighlightColor(), "20");
            }
            initPad = pad;
            addedSomething = true;
        }

        if(mode != ImprovementDescriptionMode.INDUSTRY_TOOLTIP)
        {
            info.addPara("Each improvement made at a colony doubles the number of " + Misc.STORY + " points required to make an additional improvement.", initPad,
                    Misc.getStoryOptionColor(), Misc.STORY + " points");
            addedSomething = true;
        }

        if(!addedSomething)
        {
            info.addSpacer(-pad);
        }
    }

	@Override
	public void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode)
	{
		float pad = 10f;
		Color highlight = Misc.getHighlightColor();

		String pre = "Alpha-level AI core currently assigned. ";
		if(mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP)
		{
			pre = "Alpha-level AI core. ";
		}

		if(mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP || mode == AICoreDescriptionMode.MANAGE_CORE_TOOLTIP)
		{
			CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(aiCoreId);
			TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48);
			text.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s%% unit. Increases ship quality by 10%%.", pad, highlight, String.valueOf((int)((1f - UPKEEP_MULT) * 100f)), String.valueOf(DEMAND_REDUCTION));
			tooltip.addImageWithText(pad);
			return;
		}

		tooltip.addPara(pre + "Reduces upkeep cost by %s%%. Reduces demand by %s unit. Increases ship quality by 10%%.", pad, highlight, String.valueOf((int)((1f - UPKEEP_MULT) * 100f)), String.valueOf(DEMAND_REDUCTION));
	}
}
