package pentarctagon.hmis.data.campaign.rulecmd.intro;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import pentarctagon.hmis.constants.Other;

import java.util.Map;
import java.util.Optional;

public class AlphaRequestDialog
implements InteractionDialogPlugin
{
	private InteractionDialogAPI dialog;
	private final MarketAPI market;

	enum OptionId {
		CLOSE,
		REQ1_ACCEPT,
		REQ1_RESTRICT
	}

	public AlphaRequestDialog(MarketAPI market)
	{
		this.market = market;
	}

	@Override
	public void init(InteractionDialogAPI dialog)
	{
		this.dialog = dialog;
		Optional<PersonAPI> engineer = market.getPeopleCopy().stream().filter(person -> person.getMemory().contains(Other.HULL_ENGINEER)).findFirst();
		dialog.getVisualPanel().showPersonInfo(engineer.get());

		TextPanelAPI text = dialog.getTextPanel();
		text.addPara(getEngineerIntro());

		OptionPanelAPI options = dialog.getOptionPanel();
		options.addOption(getAcceptText(), OptionId.REQ1_ACCEPT);
		options.addOption(getRestrictText(), OptionId.REQ1_RESTRICT);
	}

	@Override
	public void optionSelected(String optionText, Object optionData)
	{
		OptionId option = (OptionId) optionData;
		TextPanelAPI text = dialog.getTextPanel();

		if(!market.getMemory().contains(Other.ALPHA_FIRST_REQUEST))
		{
			switch(option)
			{
				case REQ1_ACCEPT:
					market.getMemory().set(Other.ALPHA_FIRST_REQUEST, true);
					text.addPara("");
					break;
				case REQ1_RESTRICT:
					market.getMemory().set(Other.ALPHA_FIRST_REQUEST, false);
					text.addPara("");
					break;
			}
		}
		else
		{
			dialog.dismiss();
		}

		OptionPanelAPI options = dialog.getOptionPanel();
		options.clearOptions();
		options.addOption("Close comm link", OptionId.CLOSE);
	}

	@Override
	public void optionMousedOver(String optionText, Object optionData)
	{

	}

	@Override
	public void advance(float amount)
	{

	}

	@Override
	public void backFromEngagement(EngagementResultAPI battleResult)
	{

	}

	@Override
	public Object getContext()
	{
		return null;
	}

	@Override
	public Map<String, MemoryAPI> getMemoryMap()
	{
		return null;
	}

	private String getAcceptText()
	{
		MemoryAPI memory = market.getMemory();
		if(!memory.getBoolean(Other.ALPHA_FIRST_REQUEST) && memory.getInt(Other.ALPHA_MONTH_COUNTER) == 1)
		{
			return "That sounds useful, give it access (+10% ship quality)";
		}
		return "Error: something went wrong";
	}

	private String getRestrictText()
	{
		MemoryAPI memory = market.getMemory();
		if(!memory.getBoolean(Other.ALPHA_FIRST_REQUEST) && memory.getInt(Other.ALPHA_MONTH_COUNTER) == 1)
		{
			return "No, keep it restricted";
		}
		return "Error: something went wrong";
	}

	private String getEngineerIntro()
	{
		MemoryAPI memory = market.getMemory();
		if(!memory.getBoolean(Other.ALPHA_FIRST_REQUEST) && memory.getInt(Other.ALPHA_MONTH_COUNTER) == 1)
		{
			return """
					
					""";
		}
		return "Error: something went wrong";
	}
}
