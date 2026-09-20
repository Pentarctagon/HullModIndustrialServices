package pentarctagon.hmis.industries;

import com.fs.starfarer.api.campaign.BaseCustomDialogDelegate;
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
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
	private static final Integer PLUGIN = 0;
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
		if(ships == null || Industries.HEAVYINDUSTRY.equals(ships.getId()))
		{
			return null;
		}
		else if(ships.getSpecialItem() == null || !Items.PRISTINE_NANOFORGE.equals(ships.getSpecialItem().getId()))
		{
			return null;
		}

		ArrayList<IndustryOptionData> options = new ArrayList<>();

		IndustryOptionData opt = new IndustryOptionData("Domain Grade Parts production", PLUGIN, ind, this);
		opt.color = Color.GREEN;
		options.add(opt);

		return options;
	}

	@Override
	public void optionSelected(IndustryOptionData opt, DialogCreatorUI ui)
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
						((HullModServices)opt.ind).setDomainGradePartsProduction(Integer.parseInt(buttonId.toString().substring(UNITS.length())));
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

	@Override
	public void createTooltip(IndustryOptionData opt, TooltipMakerAPI tooltip, float width)
	{
		tooltip.addPara("Set how many units of Domain Grade Parts to produce.", 0f);
	}

	@Override
	public void addToIndustryTooltip(Industry ind, Industry.IndustryTooltipMode mode, TooltipMakerAPI tooltip, float width, boolean expanded)
	{
		if (getIndustryOptions(ind) == null)
		{
			return;
		}

		float opad = 10f;
		tooltip.addSectionHeading("Domain Grade Parts", Alignment.MID, opad);
		tooltip.addPara("Allows producing one unit of domain grade parts per four units of ships, one unit of ship quality, and 15% colony fleet size.", opad);
	}
}
