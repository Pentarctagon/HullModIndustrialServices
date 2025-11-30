package pentarctagon.hmis.data.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import pentarctagon.hmis.data.campaign.rulecmd.ui.SelectShipDelegate;
import pentarctagon.hmis.data.campaign.rulecmd.utils.Sizing;

import java.util.List;
import java.util.Map;

// TODO: review/document how the code removing the refit button works
// TODO: check that the pointless vanity project achievement triggers
// TODO: removing s-mods costs credits instead of a story point?
// TODO: require story point improvement to enable going beyond 2 s-mods?
// TODO: reduce d-mod removal costs - need to look at SMS
// TODO: console commands?
// TODO: lunalib integration - update URLs in version file
// TODO: somehow prevent gaming the costs by increasing faction doctrine quality -> s-mod stuff -> decrease it?
// TODO: logging?
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
		dialog.showCustomDialog(Sizing.PANEL_WIDTH, Sizing.panelHeight(), new SelectShipDelegate(dialog));
		return true;
	}
}
