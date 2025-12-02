package pentarctagon.hmis.data.campaign.rulecmd.utils;

public class Constants
{
	public static final boolean LOG_MORE = System.getProperty("fromide", "").equals("yes");

	public final static int MAX_SAFE_SMODS = 4;
	public final static int MAX_SMODS = 11;
	public final static int HMIS_MIN_MARKET_SIZE = 5;
	public final static String MAINTENANCE_NIGHTMARE = "hmis_maintenance_nightmare";
	public final static String OVERBURDENED = "hmis_overburdened";
	public final static String PARADE_PIECE = "hmis_parade_piece";
}
