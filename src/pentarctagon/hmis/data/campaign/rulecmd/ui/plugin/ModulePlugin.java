package pentarctagon.hmis.data.campaign.rulecmd.ui.plugin;

import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import pentarctagon.hmis.data.campaign.rulecmd.utils.ui.BuildInHullModDialogCreator;
import pentarctagon.hmis.data.campaign.rulecmd.utils.ui.PanelCreator;
import pentarctagon.hmis.data.campaign.rulecmd.ui.Button;

import java.util.List;

public class ModulePlugin
extends Selector<Button>
{
	private boolean shouldRecreateHullmodPanel = true;

	private final FleetMemberAPI fleetMember;
	private final List<ShipVariantAPI> modulesWithOP;
	private final InteractionDialogAPI dialog;
	private CustomDialogDelegate.CustomDialogCallback callback;
	private final float scrollPanelY;

	public ModulePlugin(FleetMemberAPI fleetMember, List<ShipVariantAPI> modulesWithOP, InteractionDialogAPI dialog, float scrollPanelY)
	{
		this.fleetMember = fleetMember;
		this.modulesWithOP = modulesWithOP;
		this.dialog = dialog;
		this.scrollPanelY = scrollPanelY;
	}

	public void init(PanelCreator.PanelCreatorData<List<Button>> data, CustomDialogDelegate.CustomDialogCallback callback)
	{
		items = data.created();
		this.callback = callback;
	}

	public void recreateHullmodPanel(ShipVariantAPI selectedVariant)
	{
		// Needed because dismissCustomDialog() triggers customDialogCancel(), which is supposed to
		// recreate the hullmod panel if the user presses escape
		if(!shouldRecreateHullmodPanel)
		{
			return;
		}
		shouldRecreateHullmodPanel = false;
		callback.dismissCustomDialog(1);
		BuildInHullModDialogCreator.createPanel(fleetMember, selectedVariant, dialog, scrollPanelY);
	}

	@Override
	protected void onSelected(int index)
	{
		ShipVariantAPI variant = fleetMember.getVariant();
		if(index > 0 && modulesWithOP.size() >= index)
		{
			variant = modulesWithOP.get(index - 1);
		}
		recreateHullmodPanel(variant);
	}

	@Override
	protected void onDeselected(int index){}

	public void disableItem(int index)
	{
		items.get(index).disable();
	}
}
