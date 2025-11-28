package pentarctagon.hmis.data.campaign.rulecmd.ui.plugin;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import pentarctagon.hmis.data.campaign.rulecmd.ui.Button;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public abstract class Selector<T extends Button>
implements CustomUIPanelPlugin
{
	private final BitSet isSelectedArray = new BitSet();
	protected List<T> items = new ArrayList<>();

	@Override
	public void buttonPressed(Object o)
	{
		for(int i = 0; i < items.size(); i++)
		{
			checkIfModified(i);
		}
	}

	/**
	 * Checks if the item at [index] has had its selection status modified; if so, call the appropriate handler.
	 */
	private void checkIfModified(int index)
	{
		T item = items.get(index);
		if(item.isSelected() && !isSelectedArray.get(index))
		{
			// Item wasn't selected and just got selected
			onSelected(index);
			// onSelected could clear the selection
			if(item.isSelected())
			{
				isSelectedArray.set(index);
			}
		}
		if(!item.isSelected() && isSelectedArray.get(index))
		{
			// It was selected and just got deselected
			onDeselected(index);
			// onDeselected could select the entry
			if(!item.isSelected())
			{
				isSelectedArray.clear(index);
			}
		}
	}

	protected void forceDeselect(int index)
	{
		items.get(index).deselect();
		isSelectedArray.clear(index);
	}

	public List<T> getSelected()
	{
		List<T> selected = new ArrayList<>();
		for(int i : getSelectedIndices())
		{
			selected.add(items.get(i));
		}
		return selected;
	}

	public List<Integer> getSelectedIndices()
	{
		List<Integer> selectedIndices = new ArrayList<>();
		for(int i = 0; i < isSelectedArray.size(); i++)
		{
			if(isSelectedArray.get(i))
			{
				selectedIndices.add(i);
			}
		}
		return selectedIndices;
	}

	protected abstract void onSelected(int index);

	protected abstract void onDeselected(int index);

	@Override
	public void advance(float amount){}

	@Override
	public void positionChanged(PositionAPI position){}

	@Override
	public void render(float alphaMult){}

	@Override
	public void renderBelow(float alphaMult){}

	@Override
	public void processInput(List<InputEventAPI> events){}
}
