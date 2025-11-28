package pentarctagon.hmis.data.campaign.rulecmd.ui.plugin;

import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.ScrollPanelAPI;
import pentarctagon.hmis.data.campaign.rulecmd.utils.ui.BuildInHullModDialogCreator;
import pentarctagon.hmis.data.campaign.rulecmd.utils.ui.PanelCreator;
import pentarctagon.hmis.data.campaign.rulecmd.ui.SelectShipDelegate;
import pentarctagon.hmis.data.campaign.rulecmd.ui.SelectShipButton;

import java.util.List;

/**
 * Handles when the player clicked on a particular ship's button.
 * This is for the whole list of ship buttons - does not represent a single ship button.
 */
public class SelectShipPlugin
extends Selector<SelectShipButton>
{
	private final SelectShipDelegate selectShip;
	private ScrollPanelAPI scrollPanelAPI;

	public SelectShipPlugin(SelectShipDelegate selectShip)
	{
		this.selectShip = selectShip;
	}

	public void setData(PanelCreator.PanelCreatorData<List<SelectShipButton>> data)
	{
		// T = List<SelectShipButton>
		items = data.created();
		this.scrollPanelAPI = data.tooltipMaker().getExternalScroller();
	}

	@Override
	protected void onSelected(int index)
	{
		// dismisses ship list dialog, otherwise individual ship dialog won't get shown
		selectShip.getCallback().dismissCustomDialog(1);
		// show the single ship dialog for managing its hull mods
		FleetMemberAPI fleetMember = items.get(index).getData();
		BuildInHullModDialogCreator.createPanel(fleetMember, fleetMember.getVariant(), selectShip.getDialog(), scrollPanelAPI.getYOffset());
	}

	@Override
	protected void onDeselected(int index){}
}
