package pentarctagon.hmis.data.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import pentarctagon.hmis.data.campaign.rulecmd.ui.SelectShipDelegate;
import pentarctagon.hmis.data.campaign.rulecmd.utils.Sizing;

import java.util.List;
import java.util.Map;

// TODO: fix cost calculations and display
//       implemented s-mods, need to handle removal and enhancement
// TODO: prevent gaming the costs by increasing faction doctrine quality -> s-mod stuff -> decrease it
// TODO: hullmods
// TODO: achievements
// TODO: lunalib integration
// TODO: logging?
// TODO: how to handle best of the best skill

/**
 * Entry point for hull mod management dialog - see rules.csv
 */
@SuppressWarnings("unused")
public class HMIS_ManageHullMods
extends BaseCommandPlugin
{
	@Override
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap)
	{
		dialog.showCustomDialog(Sizing.PANEL_WIDTH, Sizing.panelHeight(), new SelectShipDelegate(dialog));
		return true;
	}
}
