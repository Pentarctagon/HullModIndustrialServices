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

// TODO: lunalib integration - update URLs in version file
// TODO: how to package mod and where to upload? how to add it to a github release?
//       add link to it to version file
// TODO: create forum thread and add link to version file

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
