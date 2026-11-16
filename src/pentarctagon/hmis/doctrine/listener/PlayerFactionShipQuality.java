package pentarctagon.hmis.doctrine.listener;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import pentarctagon.hmis.Utils;
import pentarctagon.hmis.constants.Other;

import java.math.BigDecimal;

public class PlayerFactionShipQuality
implements EconomyTickListener
{
	private static BigDecimal qualityOnLastTick;
	private static long lastTimestamp;

	// load from faction memory
	static
	{
		MemoryAPI memory = Global.getSector().getFaction(Factions.PLAYER).getMemory();
		if(!memory.contains(Other.HMIS_FACTION_QUALITY_LAST_TICK))
		{
			memory.set(Other.HMIS_FACTION_QUALITY_LAST_TICK, Utils.getPlayerFactionDoctrineQuality().toString());
		}
		if(!memory.contains(Other.HMIS_FACTION_QUALITY_LAST_TICK_TIMESTAMP))
		{
			memory.set(Other.HMIS_FACTION_QUALITY_LAST_TICK_TIMESTAMP, PlayerFactionShipQuality.getLastTimestamp());
		}

		qualityOnLastTick = new BigDecimal(memory.getString(Other.HMIS_FACTION_QUALITY_LAST_TICK));
		lastTimestamp = memory.getLong(Other.HMIS_FACTION_QUALITY_LAST_TICK_TIMESTAMP);
	}

	// doesn't seem to necessarily trigger with a consistent frequency
	@Override
	public void reportEconomyTick(int iterIndex)
	{
		BigDecimal factionDoctrineQuality = Utils.getPlayerFactionDoctrineQuality();
		int daysSinceLastTimestamp = (int)Global.getSector().getClock().getElapsedDaysSince(lastTimestamp);

		if(daysSinceLastTimestamp >= 7 && qualityOnLastTick.compareTo(factionDoctrineQuality) != 0)
		{
			lastTimestamp = Global.getSector().getClock().getTimestamp();
			//noinspection ComparatorResultComparison
			if(qualityOnLastTick.compareTo(factionDoctrineQuality) == -1)
			{
				qualityOnLastTick = qualityOnLastTick.add(new BigDecimal("0.025"));
			}
			else
			{
				qualityOnLastTick = qualityOnLastTick.subtract(new BigDecimal("0.025"));
			}
		}
	}

	@Override
	public void reportEconomyMonthEnd(){}

	public static long getLastTimestamp()
	{
		return lastTimestamp;
	}
	public static BigDecimal getQualityOnLastTick()
	{
		return qualityOnLastTick;
	}
}
