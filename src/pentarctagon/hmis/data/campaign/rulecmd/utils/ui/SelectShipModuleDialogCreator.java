package pentarctagon.hmis.data.campaign.rulecmd.utils.ui;

import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import pentarctagon.hmis.data.campaign.rulecmd.ui.plugin.ModulePlugin;
import pentarctagon.hmis.data.campaign.rulecmd.utils.Sizing;

import java.util.ArrayList;
import java.util.List;

public class SelectShipModuleDialogCreator
{
	public static void createPanel(FleetMemberAPI fleetMember, ShipVariantAPI selectedVariant, InteractionDialogAPI dialog, float shipScrollPanelY)
	{
		// Guaranteed to not be empty since the button is only created when a ship has multiple modules
		List<ShipVariantAPI> modulesWithOP = getModuleVariantsWithOP(fleetMember.getVariant());

		List<String> moduleNameStrings = new ArrayList<>();
		int maxSMods = 11;
		moduleNameStrings.add("Base ship" + String.format("  (%s/%s S-mods)", fleetMember.getVariant().getSMods().size(), maxSMods));
		int currentVariantIndex = 0;

		for(int i = 0; i < modulesWithOP.size(); i++)
		{
			ShipVariantAPI moduleVariant = modulesWithOP.get(i);
			moduleNameStrings.add("Module: " + moduleVariant.getHullSpec().getHullName() + String.format("  (%s/%s S-mods)", moduleVariant.getSMods().size(), maxSMods));
			if(selectedVariant == moduleVariant)
			{
				currentVariantIndex = i + 1;
			}
		}
		ModulePlugin plugin = new ModulePlugin(fleetMember, modulesWithOP, dialog, shipScrollPanelY);

		final int finalCurrentVariantIndex = currentVariantIndex;
		dialog.showCustomDialog(Sizing.PANEL_WIDTH, Sizing.panelHeight(), new CustomDialogDelegate()
		{
			@Override
			public void createCustomDialog(CustomPanelAPI panel, CustomDialogCallback callback)
			{
				float titleHeight = 25f;
				PanelCreator.createTitle(panel, "Select a module", titleHeight);
				plugin.init(PanelCreator.createButtonList(panel, moduleNameStrings, 45f, 10f, titleHeight), callback);
				plugin.disableItem(finalCurrentVariantIndex);
			}

			@Override
			public void customDialogConfirm()
			{
				// Logic is in plugin.onSelected() without needing to press confirm.
				// Confirm is used as return button, just recreate the hullmod manage panel with the previously selected variant
				plugin.recreateHullmodPanel(selectedVariant);
			}

			@Override
			public void customDialogCancel()
			{
				// Called when user hits escape even if button is disabled. Same handling as confirm
				plugin.recreateHullmodPanel(selectedVariant);
			}

			@Override
			public String getCancelText()
			{
				return "Cancel";
			}

			@Override
			public String getConfirmText()
			{
				return "Return";
			}

			@Override
			public CustomUIPanelPlugin getCustomPanelPlugin()
			{
				return plugin;
			}

			@Override
			public boolean hasCancelButton()
			{
				return false;
			}
		});
	}

	/**
	 * Returns a list of the module variants of a base variant that have positive OP.
	 */
	public static List<ShipVariantAPI> getModuleVariantsWithOP(ShipVariantAPI base)
	{
		List<ShipVariantAPI> withOP = new ArrayList<>();

		if(base.getModuleSlots() == null)
		{
			return withOP;
		}

		for(int i = 0; i < base.getModuleSlots().size(); i++)
		{
			ShipVariantAPI moduleVariant = base.getModuleVariant(base.getModuleSlots().get(i));
			if(moduleVariant.getHullSpec().getOrdnancePoints(null) <= 0)
			{
				continue;
			}
			withOP.add(moduleVariant);
		}

		return withOP;
	}
}
