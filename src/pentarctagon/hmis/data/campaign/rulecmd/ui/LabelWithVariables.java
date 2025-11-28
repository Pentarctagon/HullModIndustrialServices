package pentarctagon.hmis.data.campaign.rulecmd.ui;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;

/**
 * Wrapper around using String.format() for labels
 */
public class LabelWithVariables
{
	public LabelAPI label;
	private final String text;
	private final Integer[] vars;
	private final String[] highlights;

	public LabelWithVariables(String text, Color highlightColor, Alignment alignment, TooltipMakerAPI tooltip, Integer... vars)
	{
		this.text = text;
		this.vars = vars;
		highlights = new String[vars.length];
		for(int i = 0; i < vars.length; i++)
		{
			highlights[i] = vars[i].toString();
		}

		label = tooltip.addPara(String.format(text, vars), 0f);
		label.setHighlight(highlights);
		label.setHighlightColor(highlightColor);
		label.setAlignment(alignment);
	}

	public Integer getVar(int index)
	{
		return index < vars.length ? vars[index] : null;
	}

	public void changeVar(int index, Integer newVar)
	{
		if(index >= vars.length)
		{
			return;
		}
		vars[index] = newVar;
		highlights[index] = newVar.toString();
		label.setText(String.format(text, vars));
		label.setHighlight(highlights);
	}
}
