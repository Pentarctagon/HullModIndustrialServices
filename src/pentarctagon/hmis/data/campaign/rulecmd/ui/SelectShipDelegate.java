package pentarctagon.hmis.data.campaign.rulecmd.ui;

import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import pentarctagon.hmis.data.campaign.rulecmd.ui.plugin.SelectShipPlugin;
import pentarctagon.hmis.data.campaign.rulecmd.utils.ui.PanelCreator;

import java.util.List;

/**
 * Dialog showing the list of ships in the player's fleet and allow choosing which ship to modify.
 */
public class SelectShipDelegate
implements CustomDialogDelegate
{
	private final SelectShipPlugin plugin;
	private CustomDialogDelegate.CustomDialogCallback callback;
	private final InteractionDialogAPI dialog;

	public SelectShipDelegate(InteractionDialogAPI dialog)
	{
		this.dialog = dialog;
		// needs to be here in constructor rather than createCustomDialog(), otherwise clicking the buttons doesn't do anything
		// not really sure why, but presumably it's somehow related to Selector's buttonPressed() method from CustomUIPanelPlugin
		// which is retrieved via CustomDialogDelegate's getCustomPanelPlugin()
		this.plugin = new SelectShipPlugin(this);
	}

	@Override
	public void createCustomDialog(CustomPanelAPI panel, CustomDialogCallback callback)
	{
		this.callback = callback;

		PanelCreator.createTitle(panel, "Select a ship", 30);
		PanelCreator.PanelCreatorData<List<SelectShipButton>> createdButtonsData = PanelCreator.createShipButtonList(panel);

		plugin.setData(createdButtonsData);
	}

	@Override
	public void customDialogConfirm(){}

	@Override
	public boolean hasCancelButton()
	{
		return false;
	}

	@Override
	public String getConfirmText() { return "Return"; }

	@Override
	public String getCancelText()
	{
		return null;
	}

	@Override
	public void customDialogCancel() {}

	@Override
	public CustomUIPanelPlugin getCustomPanelPlugin()
	{
		return plugin;
	}

	public CustomDialogDelegate.CustomDialogCallback getCallback(){ return callback; }

	public InteractionDialogAPI getDialog() { return dialog; }
}
