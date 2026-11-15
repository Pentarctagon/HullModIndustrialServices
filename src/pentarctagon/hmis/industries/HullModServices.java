package pentarctagon.hmis.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import pentarctagon.hmis.Utils;
import pentarctagon.hmis.constants.Luna;
import pentarctagon.hmis.constants.Other;
import pentarctagon.hmis.data.campaign.rulecmd.utils.LunaHelper;
import pentarctagon.hmis.items.DeconstructionModuleItemEffect;

import java.awt.*;

public class HullModServices
extends BaseIndustry
{
	static {
		ItemEffectsRepo.ITEM_EFFECTS.put(Other.HMIS_DECONSTRUCTION_MODULE, new DeconstructionModuleItemEffect(Other.HMIS_DECONSTRUCTION_MODULE));
	}

    @Override
    public void apply()
    {
        super.apply(true);

		if(isBuilding())
		{
			return;
		}

	    int shipsValue = Utils.findShipIndustryValue(market);
		if(shipsValue < Other.ORBITAL_INDUSTRY && market.hasIndustry(Other.HULL_MOD_SERVICES))
        {
	        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(Other.HULL_MOD_SERVICES, 0f, "Hull Mod Services - no Orbital Works");
        }
        else if(market.getPrevStability() < 7)
        {
            market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(Other.HULL_MOD_SERVICES, 0f, "Hull Mod Services - low stability");
        }
		else
		{
			market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(Other.HULL_MOD_SERVICES, 0.2f, "Hull Mod Services");
		}

		if(Commodities.ALPHA_CORE.equals(getAICoreId()))
		{
			market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(Other.HULL_MOD_SERVICES+"_alphacore", 0.1f, "Hull Mod Services - Alpha Core");
		}
		else
		{
			market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodifyFlat(Other.HULL_MOD_SERVICES+"_alphacore");
		}

		// only if orbital works or better
	    Industry ships = Utils.findShipIndustry(market);

		if(ships != null && !Industries.HEAVYINDUSTRY.equals(ships.getId()))
		{
			demand(Commodities.METALS, ships.getDemand(Commodities.METALS).getQuantity().getModifiedInt()+1);
			demand(Commodities.RARE_METALS, ships.getDemand(Commodities.RARE_METALS).getQuantity().getModifiedInt());
			demand(Commodities.FUEL, ships.getDemand(Commodities.METALS).getQuantity().getModifiedInt()+1);
			demand(Commodities.HEAVY_MACHINERY, ships.getDemand(Commodities.METALS).getQuantity().getModifiedInt()+2);

			market.getMemory().set(Other.HMIS_ACTIVE, true);
		}
		else if(market.getMemoryWithoutUpdate().contains(Other.HMIS_ACTIVE))
		{
			market.getMemory().unset(Other.HMIS_ACTIVE);
		}

	    // for every 10% ship quality, export a unit that improves other factions' ship quality by 5%, rounded down by integer division
	    float marketQuality = market.getShipQualityFactor();
		if(marketQuality >= 1.1f && LunaHelper.getBoolean(Luna.HMIS_QUALITY_EXPORT, true) && shipsValue >= Other.ORBITAL_INDUSTRY)
		{
			int qualityExported = (int)((marketQuality-1)*10);
			supply(Other.SHIP_QUALITY, qualityExported);
		}
		else
		{
			supply(Other.SHIP_QUALITY, 0);
		}
    }

    @Override
    public void unapply()
    {
        super.unapply();
	    market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodifyFlat(Other.HULL_MOD_SERVICES);
	    market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodifyFlat(Other.HULL_MOD_SERVICES+"_alphacore");
    }

    @Override
    public boolean isAvailableToBuild()
    {
        return super.isAvailableToBuild() && Utils.findShipIndustryValue(market) >= Other.ORBITAL_INDUSTRY && market.getSize() >= Other.HMIS_MIN_MARKET_SIZE;
    }

    @Override
    public String getUnavailableReason()
    {
		int shipsValue = Utils.findShipIndustryValue(market);
	    if(shipsValue < Other.ORBITAL_INDUSTRY && market.getSize() >= Other.HMIS_MIN_MARKET_SIZE)
	    {
		    return "Requires Orbital Works";
	    }
	    if(shipsValue >= Other.ORBITAL_INDUSTRY && market.getSize() < Other.HMIS_MIN_MARKET_SIZE)
	    {
		    return "Requires at least size 5 colony";
	    }
	    if(shipsValue < Other.ORBITAL_INDUSTRY && market.getSize() < Other.HMIS_MIN_MARKET_SIZE)
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
                info.addPara("Reduced hullmod costs by an additional %s%%.", initPad, com.fs.starfarer.api.util.Misc.getHighlightColor(), "20");
            }
            else
            {
                info.addPara("Reduces hullmod costs by an additional %s%%.", initPad, com.fs.starfarer.api.util.Misc.getHighlightColor(), "20");
            }
            initPad = pad;
            addedSomething = true;
        }

        if(mode != ImprovementDescriptionMode.INDUSTRY_TOOLTIP)
        {
            info.addPara("Each improvement made at a colony doubles the number of " + com.fs.starfarer.api.util.Misc.STORY + " points required to make an additional improvement.", initPad,
                    com.fs.starfarer.api.util.Misc.getStoryOptionColor(), com.fs.starfarer.api.util.Misc.STORY + " points");
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
		Color highlight = com.fs.starfarer.api.util.Misc.getHighlightColor();

		String pre = "Alpha-level AI core currently assigned. ";
		if(mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP)
		{
			pre = "Alpha-level AI core. ";
		}

		if(mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP || mode == AICoreDescriptionMode.MANAGE_CORE_TOOLTIP)
		{
			CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(aiCoreId);
			TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48);
			text.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s%% unit. Increases ship quality by %s%%.", pad, highlight, String.valueOf((int)((1f - UPKEEP_MULT) * 100f)), String.valueOf(DEMAND_REDUCTION), "10");
			tooltip.addImageWithText(pad);
			return;
		}

		tooltip.addPara(pre + "Reduces upkeep cost by %s%%. Reduces demand by %s unit. Increases ship quality by %s%%.", pad, highlight, String.valueOf((int)((1f - UPKEEP_MULT) * 100f)), String.valueOf(DEMAND_REDUCTION), "10");
	}
}
