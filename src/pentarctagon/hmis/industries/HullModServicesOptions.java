package pentarctagon.hmis.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.listeners.BaseIndustryOptionProvider;
import com.fs.starfarer.api.campaign.listeners.DialogCreatorUI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import pentarctagon.hmis.Utils;
import pentarctagon.hmis.constants.Other;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HullModServicesOptions
extends BaseIndustryOptionProvider
{
	private static final Integer PROD_OPTION = 0;
	private static final Integer DECON_OPTION = 1;
	private static final String UNITS = "UNITS";

	@Override
	public List<IndustryOptionData> getIndustryOptions(Industry ind)
	{
		if(isUnsuitable(ind, false))
		{
			return null;
		}
		if(!ind.getId().equals(Other.HULL_MOD_SERVICES))
		{
			return null;
		}

		Industry ships = Utils.findShipIndustry(ind.getMarket());
		if(ships == null)
		{
			return null;
		}

		ArrayList<IndustryOptionData> options = new ArrayList<>();

		if(ships.getSpecialItem() != null && Items.PRISTINE_NANOFORGE.equals(ships.getSpecialItem().getId()))
		{
			IndustryOptionData opt = new IndustryOptionData("Domain Grade Parts production", PROD_OPTION, ind, this);
			opt.color = Color.GREEN;
			options.add(opt);
		}

		if(Global.getSector().getPlayerMemoryWithoutUpdate().getBoolean("$encounteredThreat"))
		{
			IndustryOptionData opt = new IndustryOptionData("Create Deconstruction Module", DECON_OPTION, ind, this);
			opt.color = Color.RED;
			options.add(opt);
		}

		return options;
	}

	@Override
	public void optionSelected(IndustryOptionData opt, DialogCreatorUI ui)
	{
		if(opt.id == PROD_OPTION)
		{
			CustomDialogDelegate delegate = new BaseCustomDialogDelegate()
			{
				@Override
				public void createCustomDialog(CustomPanelAPI panel, CustomDialogCallback callback)
				{
					final CustomUIPanelPlugin panelPlugin = new BaseCustomUIPanelPlugin()
					{
						@Override
						public void buttonPressed(Object buttonId)
						{
							((HullModServices) opt.ind).setDomainGradePartsProduction(Integer.parseInt(buttonId.toString().substring(UNITS.length())));
							callback.dismissCustomDialog(1);
						}
					};

					CustomPanelAPI newpanel = panel.createCustomPanel(350f, 30f, panelPlugin);
					TooltipMakerAPI info = newpanel.createUIElement(350f, 30f, false);
					info.addPara("Units of domain grade parts to produce:", 0f).getPosition().inBL(0, 35);

					Industry ships = Utils.findShipIndustry(opt.ind.getMarket());
					if(ships != null)
					{
						int originalShipsProduction = (int) ships.getSupply(Commodities.SHIPS).getQuantity().getModifiedValue() + (4 * ((HullModServices) opt.ind).getDomainGradePartsProduction());
						for(int i = 0; i <= originalShipsProduction / 4; i++)
						{
							info.addButton(String.valueOf(i), UNITS + i, 30f, 30f, 0).getPosition().inBL((i * 30) + (i * 5), 0);
						}
					}

					newpanel.addUIElement(info);
					panel.addComponent(newpanel);
				}
			};

			ui.showDialog(350f, 50f, delegate);
		}

		if(opt.id == DECON_OPTION)
		{
			int fabs = 0;
			int tpus = 0;

			CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
			List<CargoStackAPI> cargoList = cargo.getStacksCopy();
			for(CargoStackAPI cargoStack : cargoList)
			{
				if(cargoStack.getType().equals(CargoAPI.CargoItemType.SPECIAL))
				{
					SpecialItemData data = (SpecialItemData) cargoStack.getData();
					if(data.getId().equals(Items.FRAGMENT_FABRICATOR))
					{
						fabs = (int)cargoStack.getSize();
					}
					if(data.getId().equals(Items.THREAT_PROCESSING_UNIT))
					{
						tpus = (int)cargoStack.getSize();
					}
				}
			}

			if(fabs >= Other.DECOMOD_FAB_COST && tpus >= Other.DECONMOD_TPU_COST)
			{
				cargo.addSpecial(new SpecialItemData(Other.HMIS_DECONSTRUCTION_MODULE, null), 1);
				cargo.removeItems(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(Items.FRAGMENT_FABRICATOR, null), Other.DECOMOD_FAB_COST);
				cargo.removeItems(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(Items.THREAT_PROCESSING_UNIT, null), Other.DECONMOD_TPU_COST);
			}
		}
	}

	@Override
	public void createTooltip(IndustryOptionData opt, TooltipMakerAPI tooltip, float width)
	{
		if(opt.id == PROD_OPTION)
		{
			tooltip.addPara("Set how many units of Domain Grade Parts to produce.", 0f);
		}
		else if(opt.id == DECON_OPTION)
		{
			tooltip.addPara("Costs "+Other.DECOMOD_FAB_COST+" Fragment Fabricators and "+Other.DECONMOD_TPU_COST+" Threat Processing Units.", 0f);
		}
	}

	@Override
	public void addToIndustryTooltip(Industry ind, Industry.IndustryTooltipMode mode, TooltipMakerAPI tooltip, float width, boolean expanded)
	{
		List<IndustryOptionData> opts = getIndustryOptions(ind);
		if(opts == null)
		{
			return;
		}

		float opad = 10f;
		tooltip.addSectionHeading("Domain Grade Parts", Alignment.MID, opad);
		tooltip.addPara("Allows producing one unit of domain grade parts per four units of ships and 15% colony fleet size.", opad);
		if(Global.getSector().getPlayerMemoryWithoutUpdate().getBoolean("$encounteredThreat"))
		{
			tooltip.addSectionHeading("Deconstruction Module", Alignment.MID, opad);
			tooltip.addPara("Increases production of Domain Grade Parts by 1. Costs "+Other.DECOMOD_FAB_COST+" Fragment Fabricators and "+Other.DECONMOD_TPU_COST+" Threat Processing Units to produce.", opad);
		}
	}
}
