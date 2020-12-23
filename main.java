package main;

import java.util.Arrays;

//import org.dreambot.api.*;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.methods.skills.Skills;

@ScriptManifest(author = "Dominus & Luda", description = "AIO Store Seller", name = "AIO Store Seller", category = Category.MAGIC, version = 0.1)
public class main extends AbstractScript{
	State state;
	
	/* 		ARRAYS		*/
	int[] CURworlds = {};
	int[] IDs = {};
	int[] BuyAvg = {};
	int[] Prices = {};
	/* 	END OF ARRAYS 	*/
	
	/*		 AREAS		 */
	Area GE = new Area(3161, 3489, 3164, 3486);
	/*	 END OF AREAS	*/
	
	@Override
	public int onLoop() {
		
		switch(getState()) {
			case WalkToGE:
				if (Walking.shouldWalk(randomNum(1, 5)) || (Walking.getDestination() == null)) {
					Walking.walk(GE.getRandomTile());
					sleep(randomNum(99, 269));
				}
			break;
			
			case WorldHop:
				
				break;
		}
		return randomNum(400, 800);
	}
	
	private enum State{
		WalkToGE, WorldHop,
	}
	
	private State getState() {
		if (!GE.contains(getLocalPlayer())) {
			state = State.WalkToGE;
		}else if (GE.contains(getLocalPlayer())) {
			state = State.WorldHop;
		}
		return state;
	}
	
	public void onStart() {
		log("Bot Started");

		for (int i = 301; i <= 535; i++) {
			if (i != 423) {
				if (Worlds.getWorld(i).isMembers() && (Worlds.getWorld(i).getMinimumLevel() == 0 || Worlds.getWorld(i).getMinimumLevel() <= Skills.getTotalLevel()) && !Worlds.getWorld(i).isHighRisk() && !Worlds.getWorld(i).isLastManStanding() && !Worlds.getWorld(i).isTournamentWorld() && !Worlds.getWorld(i).isPVP()) {
					CURworlds = Arrays.copyOf(CURworlds, CURworlds.length+1);
					CURworlds[CURworlds.length-1] = i;
				}
			}
		}
		
		fetchSP.Pair pair = fetchSP.Scrape();
		IDs = pair.getIDs();
		Prices = pair.getPrices();
		BuyAvg = pair.getBuyAvg();
	}
	
	public void onExit() {
		log("Bot Ended");
	}
	
	public int randomNum(int i, int k) {
		int num = (int)(Math.random() * (k - i + 1)) + i;
		return num;
	}

}