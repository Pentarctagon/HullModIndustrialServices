package pentarctagon.hmis.data.campaign.rulecmd.intro;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import pentarctagon.hmis.Utils;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class IntroManager
extends BaseCommandPlugin
{
	@Override
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap)
	{
		dialog.getOptionPanel().removeOption("cutCommLink");
		dialog.getOptionPanel().addOption("Thank you, that's all for now", "cutCommLink");
		return true;
	}
}
