package pentarctagon.hmis.industries;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.*;

import java.lang.invoke.MethodHandles;

// RefitHandler.java:modifyBuildInButton, updateMasteryButton

public class HullModServices
extends BaseIndustry
{
    // TODO: check what this does when launching the game normally
    private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
    static
    {
        ConsoleAppender console = new ConsoleAppender();
        String PATTERN = "%m%n";
        console.setLayout(new PatternLayout(PATTERN));
        console.setThreshold(Level.DEBUG);
        console.activateOptions();
        log.setAdditivity(false);
        log.addAppender(console);
    }

    @Override
    public void apply()
    {
        super.apply(true);

        if (market.getPrevStability() >= 7)
        {
            market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(0), 0.2f, "Hull Mod Services");
        }
        else
        {
            market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(0), 0f, "Hull Mod Services - low stability");
        }

		if(market.hasIndustry(Industries.ORBITALWORKS))
		{
			demand(Commodities.METALS, market.getIndustry(Industries.ORBITALWORKS).getDemand(Commodities.METALS).getQuantity().getModifiedInt()+2);
			demand(Commodities.RARE_METALS, market.getIndustry(Industries.ORBITALWORKS).getDemand(Commodities.METALS).getQuantity().getModifiedInt());
			demand(Commodities.HEAVY_MACHINERY, market.getIndustry(Industries.ORBITALWORKS).getDemand(Commodities.METALS).getQuantity().getModifiedInt()+2);
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
        return super.isAvailableToBuild() && market.hasIndustry(Industries.ORBITALWORKS) && market.getSize() > 4;
    }

    @Override
    public String getUnavailableReason()
    {
	    if(!market.hasIndustry(Industries.ORBITALWORKS) && market.getSize() > 4)
	    {
		    return "Requires Orbital Works";
	    }
	    if(market.hasIndustry(Industries.ORBITALWORKS) && market.getSize() <= 4)
	    {
		    return "Requires at least size 5 colony";
	    }
	    if(!market.hasIndustry(Industries.ORBITALWORKS) && market.getSize() <= 4)
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
                info.addPara("Reduced hullmod costs by an additional %s percent.", initPad, Misc.getHighlightColor(), "20");
            }
            else
            {
                info.addPara("Reduces hullmod costs by an additional %s percent.", initPad, Misc.getHighlightColor(), "20");
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
}
