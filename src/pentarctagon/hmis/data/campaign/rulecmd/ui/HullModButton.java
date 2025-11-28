package pentarctagon.hmis.data.campaign.rulecmd.ui;

import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import pentarctagon.hmis.data.campaign.rulecmd.utils.ui.HullModButtonData;

import java.awt.*;

public class HullModButton
extends Button
{
	protected final String spriteName;
	protected final String titleText;
	protected final String descriptionText;
	protected final String defaultDescription;
	protected final Color titleColor;

	private final LabelAPI title;
	private final LabelAPI description;

	private final HullModButtonData data;

	public HullModButton(
		HullModButtonData data,
		Color baseColor,
		Color darkColor,
		Color brightColor,
		Color titleColor,
		float width,
		float height,
		float pad,
		TooltipMakerAPI tooltipMaker
	)
	{
		super("", baseColor, darkColor, brightColor, width, height, pad);
		this.spriteName = data.spriteName();
		this.titleText = data.name();
		this.descriptionText = data.defaultDescription();
		this.defaultDescription = data.defaultDescription();
		this.titleColor = titleColor;

		this.data = data;

		button = tooltipMaker.addAreaCheckbox(
			text,
			"temp",
			baseColor,
			darkColor,
			brightColor,
			width,
			height,
			pad
		);

		TooltipMakerAPI imageAndText = tooltipMaker.beginImageWithText(spriteName, height);

		imageAndText.setTitleOrbitronLarge();
		title = imageAndText.addTitle(titleText);

		title.setHighlight(titleText);
		title.setHighlightColor(titleColor);

		imageAndText.setParaFontOrbitron();
		description = imageAndText.addPara(descriptionText, 0f);

		tooltipMaker.addImageWithText(-height);
	}

	public HullModButtonData getData(){ return data; }

	public String getDefaultDescription()
	{
		return defaultDescription;
	}

	/**
	 * If [reason] is null, will not change the description.
	 */
	public void enable(String reason)
	{
		button.setEnabled(true);
		button.setClickable(true);
		if(reason != null)
		{
			description.setText(reason);
			description.setHighlight(reason);
			description.setHighlightColor(Color.WHITE);
			title.setHighlightColor(Color.WHITE);
		}
	}

	/**
	 * If [reason] is null, will not change the description.
	 * If [highlight] is set, text color will be set to orange.
	 * Otherwise, it is set to gray.
	 */
	public void disable(String reason, boolean highlight)
	{
		button.setEnabled(false);
		if(reason != null)
		{
			description.setText(reason);
			description.setHighlight(reason);
			description.setHighlightColor(highlight ? Misc.getNegativeHighlightColor() : Color.GRAY);
			title.setHighlightColor(Color.GRAY);
		}
	}

	@Override
	public void disable()
	{
		disable(null, false);
	}

	public LabelAPI getDescription() { return description; }
}
