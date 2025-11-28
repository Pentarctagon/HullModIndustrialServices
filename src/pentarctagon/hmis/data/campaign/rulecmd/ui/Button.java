package pentarctagon.hmis.data.campaign.rulecmd.ui;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;

/**
 * Contains the Starsector ButtonAPI class. Base class for other custom buttons.
 */
public class Button
{
	protected final String text;
	protected final Color baseColor;
	protected final Color darkColor;
	protected final Color brightColor;
	protected final float width;
	protected final float height;
	protected final float pad;

	public ButtonAPI button;

	public Button(
		String text,
		Color baseColor,
		Color darkColor,
		Color brightColor,
		float width,
		float height,
		float pad
	)
	{
		this.text = text;
		this.baseColor = baseColor;
		this.darkColor = darkColor;
		this.brightColor = brightColor;
		this.width = width;
		this.height = height;
		this.pad = pad;
	}

	public void disable()
	{
		button.setEnabled(false);
	}

	public void init(TooltipMakerAPI tooltip)
	{
		// WTF is computeTextHeight doing, it's probably bugged
		// giving way too large values
		// For now just assume the height is 10 whatever
		float textHeight = 10;
		button = tooltip.addAreaCheckbox(
			text,
			"temp",
			baseColor,
			darkColor,
			brightColor,
			width,
			height,
			pad - textHeight - height / 2
		);
	}

	public boolean isSelected()
	{
		return button.isChecked();
	}

	public void deselect()
	{
		button.setChecked(false);
	}
}
