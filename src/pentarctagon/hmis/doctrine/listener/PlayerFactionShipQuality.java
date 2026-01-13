package pentarctagon.hmis.doctrine.listener;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import org.apache.log4j.Logger;
import pentarctagon.hmis.data.campaign.rulecmd.utils.Costs;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;

public class PlayerFactionShipQuality
implements EconomyTickListener
{
	private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass());

	private static long lastTimestamp;
	private static BigDecimal qualityOnLastTick;
	static
	{
		// set a value if none exists yet
		try
		{
			String[] values = Global.getSettings().readTextFileFromCommon("hmis_values").split("\n");
			qualityOnLastTick = new BigDecimal(values[0]);
			lastTimestamp = Long.parseLong(values[1]);
		}
		catch(Exception e)
		{
			qualityOnLastTick = Costs.getPlayerFactionDoctrineQuality();
			lastTimestamp = Global.getSector().getClock().getTimestamp();
			log.error("[HMIS]: failed to read from hmis_values");
		}
	}

	// doesn't seem to necessarily trigger with a consistent frequency
	@Override
	public void reportEconomyTick(int iterIndex)
	{
		BigDecimal factionDoctrineQuality = Costs.getPlayerFactionDoctrineQuality();
		int daysSinceLastTimestamp = (int)Global.getSector().getClock().getElapsedDaysSince(lastTimestamp);

		if(daysSinceLastTimestamp >= 7 && qualityOnLastTick.compareTo(factionDoctrineQuality) != 0)
		{
			lastTimestamp = Global.getSector().getClock().getTimestamp();
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
