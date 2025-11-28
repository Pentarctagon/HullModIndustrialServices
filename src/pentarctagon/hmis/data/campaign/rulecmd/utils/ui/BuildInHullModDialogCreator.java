package pentarctagon.hmis.data.campaign.rulecmd.utils.ui;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import pentarctagon.hmis.data.campaign.rulecmd.ui.BuildInHullModDelegate;
import pentarctagon.hmis.data.campaign.rulecmd.utils.Sizing;

/**
 * Handles showing the dialog for building in hullmods, removing built-in hullmods, or enhancing hullmods
 */
public class BuildInHullModDialogCreator
{
	public static boolean shouldRecreateShipPanel = true;

	public static void createPanel(FleetMemberAPI fleetMember, ShipVariantAPI selectedVariant, InteractionDialogAPI dialog, float shipScrollPanelY)
	{
		float dWidth = Sizing.PANEL_WIDTH + 150;
		float dHeight = Sizing.panelHeight() - 100;
		dialog.showCustomDialog(dWidth, dHeight, new BuildInHullModDelegate(fleetMember, selectedVariant, dialog, shipScrollPanelY, dWidth));
	}
}
