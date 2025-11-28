package pentarctagon.hmis.data.campaign.rulecmd.ui;

import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.Collections;

/**
 * Represents a single ship option that the player can click
 */
public class SelectShipButton
extends Button
{
	private final FleetMemberAPI data;

	public SelectShipButton(FleetMemberAPI data, CustomPanelAPI panel, TooltipMakerAPI tooltip)
	{
		// adding the button itself
		super("", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(), panel.getPosition().getWidth() * 0.95f, 65f, 10f);
		this.data = data;

		button = tooltip.addAreaCheckbox(
			text,
			"temp",
			baseColor,
			darkColor,
			brightColor,
			width,
			height,
			pad
		);
		button.setEnabled(true);

		// adding the ship image and text for the button via tooltip.addCustom()
		float innerHeight = height * 0.9f;
		float xPad = 10f;
		float textWidth = width - innerHeight - (xPad * 2);

		CustomPanelAPI customPanel = panel.createCustomPanel(width, height, null);
		TooltipMakerAPI shipElement = customPanel.createUIElement(innerHeight, innerHeight, false);
		TooltipMakerAPI textElement = customPanel.createUIElement(textWidth, innerHeight, false);

		shipElement.addShipList(1, 1, innerHeight, Misc.getBasePlayerColor(), Collections.singletonList(data), 0f);
		String shipName = data.getShipName() + " (" + data.getHullSpec().getNameWithDesignationWithDashClass() + ")";

		textElement.setParaFontOrbitron();
		LabelAPI text = textElement.addPara(shipName, Misc.getBasePlayerColor(), 5f);
		text.setHighlightColor(Color.WHITE);
		text.setHighlight(data.getHullSpec().getHullName());

		customPanel.addUIElement(shipElement).inLMid(0);
		customPanel.addUIElement(textElement).rightOfTop(shipElement, xPad);
		tooltip.addCustom(customPanel, -height);
	}

	public FleetMemberAPI getData(){ return data; }
}
