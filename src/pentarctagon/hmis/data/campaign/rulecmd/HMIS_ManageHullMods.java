package pentarctagon.hmis.data.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import pentarctagon.hmis.data.campaign.rulecmd.ui.SelectShipDelegate;
import pentarctagon.hmis.data.campaign.rulecmd.utils.Sizing;

import java.util.List;
import java.util.Map;

// TODO: more lunalib configurations for costs?
// TODO: lunalib integration - update URLs in version file
// TODO: somehow prevent gaming the costs by increasing faction doctrine quality -> s-mod stuff -> decrease it
// TODO: logging?
//       also do something about the Logger bits copied over in the reflection stuff
// TODO: how to handle best of the best skill?
// TODO: parade piece explosion radius+effects

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
		System.out.println(Global.getSettings().getFloat("baseRestoreCostMult"));
		dialog.showCustomDialog(Sizing.PANEL_WIDTH, Sizing.panelHeight(), new SelectShipDelegate(dialog));
		return true;
	}
}
