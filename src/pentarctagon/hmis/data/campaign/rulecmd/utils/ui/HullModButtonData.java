package pentarctagon.hmis.data.campaign.rulecmd.utils.ui;

import com.fs.starfarer.api.combat.HullModEffect;
import com.fs.starfarer.api.combat.ShipAPI;

public record HullModButtonData(
	String id,
	String name,
	String spriteName,
	String defaultDescription,
    String tooltipDescription,
	HullModEffect hullModEffect,
    ShipAPI.HullSize hullSize,
	int creditCost,
	int storyPointCost,
	boolean isEnhanceOnly,
	boolean isBuiltIn
)
{}
