package pentarctagon.hmis.data.campaign.rulecmd.utils.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import pentarctagon.hmis.data.campaign.rulecmd.ui.*;
import pentarctagon.hmis.data.campaign.rulecmd.ui.Button;
import pentarctagon.hmis.data.campaign.rulecmd.ui.plugin.BuildInPlugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PanelCreator
{
	private static final String BUTTON_MANAGE_MODULE = "BUTTON_MANAGE_MODULE";

	/**
	 * Creates and adds a title with the provided text to the provided panel.
	 */
	public static PanelCreatorData<LabelAPI> createTitle(CustomPanelAPI panel, String titleText, float titleHeight)
	{
		float width = panel.getPosition().getWidth();
		CustomPanelAPI titlePanel = panel.createCustomPanel(width, titleHeight, null);
		TooltipMakerAPI titleElement = titlePanel.createUIElement(width - 10f, titleHeight, false);

		titleElement.setTitleOrbitronLarge();
		LabelAPI title = titleElement.addTitle(titleText);
		title.setAlignment(Alignment.MID);

		titlePanel.addUIElement(titleElement).inTMid(0f);
		panel.addComponent(titlePanel).inTMid(0);

		return new PanelCreatorData<>(titlePanel, titleElement, title);
	}

	/**
	 * Adds the buttons for the list of ships in the player's fleet
	 */
	public static PanelCreatorData<List<SelectShipButton>> createShipButtonList(CustomPanelAPI panel)
	{
		Sizes sizes = getSizes(panel, 0);

		TooltipMakerAPI tooltipMaker = panel.createUIElement(sizes.width(), sizes.buttonListHeight(), true);
		panel.addUIElement(tooltipMaker).inTL(sizes.buttonListHorizontalPadding(), 30);

		List<SelectShipButton> buttons = new ArrayList<>();
		for(FleetMemberAPI data : Global.getSector().getPlayerFleet().getFleetData().getMembersInPriorityOrder())
		{
			buttons.add(new SelectShipButton(data, panel, tooltipMaker));
		}

		return new PanelCreatorData<>(panel, tooltipMaker, buttons);
	}

	public static PanelCreatorData<LabelWithVariables> createLabelWithVariables(CustomPanelAPI panel, String text, Color highlightColor, float pad, Alignment align, Integer... vars)
	{
		float width = panel.getPosition().getWidth();
		float labelWidth = width * 0.85f;
		TooltipMakerAPI labelElement = panel.createUIElement(labelWidth, 20f, false);
		labelElement.setParaFontOrbitron();
		LabelWithVariables label = new LabelWithVariables(text, highlightColor, align, labelElement, vars);
		panel.addUIElement(labelElement).inTMid(pad);
		return new PanelCreatorData<>(panel, labelElement, label);
	}

	/**
	 * Create a panel containing the ship image, name, S-mod limit and the button to increase the limit.
	 * Returns the panel, so the next panel can be positioned below it.
	 */
	public static CustomPanelAPI createShipInfoPanel(CustomPanelAPI panel, FleetMemberAPI ship, ShipVariantAPI selectedVariant, CustomDialogDelegate.CustomDialogCallback callback, float shipScrollPanelY)
	{
		Sizes sizes = getSizes(panel, 0);
		float shipSize = 80f;
		boolean shipHasModules = !SelectShipModuleDialogCreator.getModuleVariantsWithOP(ship.getVariant()).isEmpty();

		CustomUIPanelPlugin panelPlugin = null;

		// If ship has modules, add a button for switching modules and a listener plugin
		// When the button is pressed, dismiss this dialog and open a new module selection dialog
		// If a module is selected in that dialog, the hullmod panel will be recreated with a different selectedVariant parameter
		if(shipHasModules)
		{
			panelPlugin = new BaseCustomUIPanelPlugin()
			{
				@Override
				public void buttonPressed(Object buttonId)
				{
				if(buttonId.equals(BUTTON_MANAGE_MODULE))
				{
					BuildInHullModDialogCreator.shouldRecreateShipPanel = false;
					callback.dismissCustomDialog(1);
					InteractionDialogAPI dialog = Global.getSector().getCampaignUI().getCurrentInteractionDialog();
					SelectShipModuleDialogCreator.createPanel(ship, selectedVariant, dialog, shipScrollPanelY);
				}
				}
			};
		}

		CustomPanelAPI shipInfoPanel = panel.createCustomPanel(sizes.width(), shipSize, panelPlugin);

		TooltipMakerAPI shipElement = shipInfoPanel.createUIElement(shipSize, shipSize, false);
		// If editing a non-modular ship or a modular ship's base, add a fancy ship list image thing
		if(ship.getVariant() == selectedVariant)
		{
			shipElement.addShipList(1, 1, shipSize, Misc.getBasePlayerColor(), Collections.singletonList(ship), 0f);
		}
		// Otherwise manually grab the module's image
		else
		{
			String moduleSprite = selectedVariant.getHullSpec().getSpriteName();
			// This might be rotated the wrong way, but it's hard to fix
			// 1. Determining the correct rotation is tricky, have to iterate over weapon slots
			// 2. Drawing the image rotated is tricky, have to create a temp file and rotate and draw that
			// So the module will just be rotated incorrectly for now
			shipElement.addImage(moduleSprite, shipSize, shipSize, 0f);
		}
		shipInfoPanel.addUIElement(shipElement).inTL(sizes.buttonListHorizontalPadding(), 0f);

		float infoTextPad = 10f;
		float infoTextWidth = sizes.buttonWidth() - shipSize - infoTextPad;
		TooltipMakerAPI infoTextElement = shipInfoPanel.createUIElement(infoTextWidth, 30f, false);
		String shipName = ship.getShipName() + " (" + ship.getHullSpec().getNameWithDesignationWithDashClass() + ")";
		if(ship.getVariant() != selectedVariant)
		{
			// If a module of the ship is selected, add it to the name
			shipName += " - " + selectedVariant.getHullSpec().getHullName();
		}
		infoTextElement.setParaOrbitronLarge();
		LabelAPI text = infoTextElement.addPara(shipName, Misc.getBasePlayerColor(), 0f);
		text.setHighlightColor(Color.WHITE);
		text.setHighlight(ship.getHullSpec().getHullName());

		// If ship has multiple modules, add a button to select a different one
		if(shipHasModules)
		{
			infoTextElement.addButton("Manage a different module", BUTTON_MANAGE_MODULE, infoTextWidth, 30f, 5f);
		}

		shipInfoPanel.addUIElement(infoTextElement).rightOfTop(shipElement, infoTextPad);
		panel.addComponent(shipInfoPanel);

		return shipInfoPanel;
	}

	public static PanelCreatorData<List<Button>> createButtonList(CustomPanelAPI panel, List<String> buttonText, float buttonHeight, float buttonPadding, float distanceFromTop)
	{
		Sizes sizes = getSizes(panel, distanceFromTop);
		List<Button> buttons = new ArrayList<>();
		TooltipMakerAPI buttonsElement = panel.createUIElement(sizes.width(), sizes.buttonListHeight(), true);
		for(String text : buttonText)
		{
			Button button = new Button(text, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(), sizes.buttonWidth(), buttonHeight, buttonPadding);
			button.init(buttonsElement);
			buttons.add(button);
		}
		panel.addUIElement(buttonsElement).inTL(sizes.buttonListHorizontalPadding(), distanceFromTop);
		return new PanelCreatorData<>(panel, buttonsElement, buttons);
	}

	public static PanelCreatorData<List<HullModButton>> createHullModPanel(CustomPanelAPI panel, float height,  float width, List<HullModButtonData> hullModButtonData, BuildInPlugin plugin)
	{
		Sizes sizes = getSizes(panel, 0);

		CustomPanelAPI hullmodPanel = panel.createCustomPanel(width, height, plugin);
		TooltipMakerAPI element = hullmodPanel.createUIElement(width, height, true);

		List<HullModButton> buttons = new ArrayList<>();

		for(HullModButtonData buttonData : hullModButtonData)
		{
			element.setParaFontOrbitron();
			if(plugin.isNeedRemoveText() && buttonData.isBuiltIn() && !buttonData.isEnhanceOnly())
			{
				plugin.setNeedRemoveText(false);
				element.addPara("Select built-in hull mods to remove", 3f);
			}
			if(plugin.isNeedEnhanceText() && !buttonData.isBuiltIn() && buttonData.isEnhanceOnly())
			{
				plugin.setNeedEnhanceText(false);
				element.addSpacer(7f);
				element.addPara("Select built-in hull mods to enhance (does not contribute to the S-mod limit)", 3f);
			}
			if(plugin.isNeedBuildInText() && !buttonData.isBuiltIn() && !buttonData.isEnhanceOnly())
			{
				plugin.setNeedBuildInText(false);
				element.setParaFontOrbitron();
				element.addSpacer(7f);
				element.addPara("Select hull mods to build in", 3f);
			}
			buttons.add(addHullModButtonToElement(element, buttonData, sizes.buttonWidth()));
		}

		hullmodPanel.addUIElement(element).inTL(sizes.buttonListHorizontalPadding(), 0f);
		panel.addComponent(hullmodPanel);

		return new PanelCreatorData<>(hullmodPanel, element, buttons);
	}

	public static HullModButton addHullModButtonToElement(TooltipMakerAPI tooltipMaker, HullModButtonData data, float buttonWidth)
	{
		HullModButton button = new HullModButton(data,
			data.isBuiltIn() ? Misc.getStoryOptionColor() : Misc.getBasePlayerColor(),
			data.isBuiltIn() ? Misc.getStoryDarkColor() : Misc.getDarkPlayerColor(),
			Misc.getBrightPlayerColor(),
			data.isBuiltIn() ? Misc.getStoryOptionColor() : Color.WHITE,
			buttonWidth,
			45f,
			10f,
			tooltipMaker
		);

		// Add the hull mod's effect description to a tooltip on mouse hover
		tooltipMaker.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator()
		{
			@Override
			public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam)
			{
				if(data.hullModEffect().shouldAddDescriptionToTooltip(data.hullSize(), null, true))
				{
					List<String> highlights = new ArrayList<>();
					String descParam;
					// hard cap at 100 just in case getDescriptionParam for some reason doesn't default to null
					for(int i = 0; i < 100 && (descParam = data.hullModEffect().getDescriptionParam(i, data.hullSize(), null)) != null; i++)
					{
						highlights.add(descParam);
					}
					tooltip.addPara(data.tooltipDescription().replaceAll("%", "%%"), 0f, Misc.getHighlightColor(), highlights.toArray(new String[0]));
				}
				data.hullModEffect().addPostDescriptionSection(tooltip, data.hullSize(), null, getTooltipWidth(tooltipParam), true);
				if(data.hullModEffect().hasSModEffectSection(data.hullSize(), null, false))
				{
					data.hullModEffect().addSModSection(tooltip, data.hullSize(), null, getTooltipWidth(tooltipParam), true, true);
				}
			}

			@Override
			public float getTooltipWidth(Object tooltipParam)
			{
				return 500f;
			}

			@Override
			public boolean isTooltipExpandable(Object tooltipParam)
			{
				return false;
			}

		}, TooltipMakerAPI.TooltipLocation.RIGHT);

		return button;
	}

	private static Sizes getSizes(CustomPanelAPI panel, float distanceFromTop)
	{
		float width = panel.getPosition().getWidth();
		float buttonWidth = width * 0.95f;
		// There seems to be a base horizontal padding of 10f
		// need to account for this for true centering
		float buttonListHorizontalPadding = 0.5f * (width - buttonWidth - 10f);
		float buttonListHeight = panel.getPosition().getHeight() - distanceFromTop;

		return new Sizes(width, buttonWidth, buttonListHorizontalPadding, buttonListHeight);
	}

	/**
	 * @param panel        The panel that was acted on
	 * @param tooltipMaker The TooltipMakerAPI that was used to create the element
	 * @param created      Whatever [tooltipMaker] created
	 */
	public record PanelCreatorData<T>(CustomPanelAPI panel, TooltipMakerAPI tooltipMaker, T created)
	{}
}
