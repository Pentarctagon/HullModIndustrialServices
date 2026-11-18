package pentarctagon.hmis.items;

import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.InstallableIndustryItemPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseInstallableItemEffect;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import pentarctagon.hmis.constants.Other;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class DeconstructionModuleItemEffect
extends BaseInstallableItemEffect
{
	public DeconstructionModuleItemEffect(String id)
	{
		super(id);
	}

	@Override
	public void apply(Industry industry)
	{
		industry.getMarket().getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyFlat(Other.HULL_MOD_SERVICES+"_deconmod_gdef", -10, "Hull Mod Services - Deconstruction Module");
	}

	@Override
	public void unapply(Industry industry)
	{
		industry.getMarket().getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyFlat(Other.HULL_MOD_SERVICES+"_deconmod_gdef");
	}

	@Override
	public List<String> getUnmetRequirements(Industry industry)
	{
		ArrayList<String> reqs = new ArrayList<>();
		if(industry == null)
		{
			return reqs;
		}

		if(!industry.getMarket().hasIndustry(Industries.HEAVYBATTERIES))
		{
			reqs.add("Heavy Batteries");
		}
		if(
			!industry.getMarket().hasIndustry(Industries.BATTLESTATION) && !industry.getMarket().hasIndustry(Industries.BATTLESTATION_MID) && !industry.getMarket().hasIndustry(Industries.BATTLESTATION_HIGH) &&
			!industry.getMarket().hasIndustry(Industries.STARFORTRESS) && !industry.getMarket().hasIndustry(Industries.STARFORTRESS_MID) && !industry.getMarket().hasIndustry(Industries.STARFORTRESS_HIGH)
		)
		{
			reqs.add("Battlestation");
		}

		return reqs;
	}

	@Override
	public List<String> getUnmetRequirements(Industry industry, boolean checkSurveyed)
	{
		return getUnmetRequirements(industry);
	}

	@Override
	public List<String> getRequirements(Industry industry)
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Heavy Batteries");
		reqs.add("Battlestation");
		return reqs;
	}

	@Override
	public Set<String> getConditionsRelatedToRequirements(Industry industry)
	{
		return new HashSet<>();
	}

	@Override
	protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data, InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad)
	{
		text.addPara(pre + "Adds domain grade parts production. Decreases colony base ground defenses by 5.", pad);
	}
}
