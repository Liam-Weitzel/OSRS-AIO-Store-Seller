package main;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.methods.skills.Skills;

@ScriptManifest(author = "Dominus & Luda", description = "AIO Store Seller", name = "AIO Store Seller", category = Category.MAGIC, version = 0.1)
public class main extends AbstractScript{
	State state;
	
	//VARIABLES
	double BuysAt = 0.55;
	double ChangePer = 0.01;
		
	//ARRAYS
	int[] CURworlds = {};
	int[] IDs = {};
	String[] ItemNames = {};
	int[] BuyAvg = {};
	int[] Prices = {};
	int[] toSell = {};
	
	@Override
	public int onLoop() {
		
		switch(getState()) {
			case Sample1:
			sleep(100);
			break;
		}
		return randomNum(400, 800);
	}
	
	private enum State{
		Sample1
	}
	
	private State getState() {
		if (1 == 1) {
			state = State.Sample1;
		}
		return state;
	}
	
	public void onStart() {
		log("Bot Started");
		updateWorlds();
		updateSP();
		updateToSell();
		removeNulls();
		Truncto27();
		IDsToItemName();
		
		//intergate pi server to display on website
		//integrate telegram api/ webhook
		
		log("Worlds: " + Arrays.toString(CURworlds));
		log("ItemNames: "+Arrays.toString(ItemNames));
		log("IDs: " + Arrays.toString(IDs));
		log("BuyAvg: " + Arrays.toString(BuyAvg));
		log("Prices: " + Arrays.toString(Prices));
		log("toSell: " + Arrays.toString(toSell));
		log("toSell.length: " + toSell.length);
		}
	
	public void onExit() {
		log("Bot Ended");
	}
	
	public int randomNum(int i, int k) {
		int num = (int)(Math.random() * (k - i + 1)) + i;
		return num;
	}
	
	public void updateSP() {
		fetchSP.Pair pair = fetchSP.Scrape();
		IDs = pair.getIDs();
		Prices = pair.getPrices();
		BuyAvg = pair.getBuyAvg();
	}
	
	public void updateToSell() {
		int profitperhopfor = 0;
		int profitperhopwhile= 0;
		for (int i = 0; i < IDs.length; i++) {
			double loopprice = BuysAt*Prices[i];
			int loopiter = 0;
			profitperhopwhile = 0;
			while ((loopprice > Prices[i]*0.1) && (loopprice > BuyAvg[i])) {
				profitperhopwhile = (int) loopprice + profitperhopwhile;
				loopprice = (int) (loopprice - (Prices[i]*ChangePer));
				loopiter = loopiter + 1;
			}
			profitperhopfor = (profitperhopfor + profitperhopwhile) - (loopiter*BuyAvg[i]);
			toSell = Arrays.copyOf(toSell, toSell.length+1);
			toSell[toSell.length-1] = loopiter;
		}
		log("Profit per hop: "+profitperhopfor+" GP");
	}
	
	public void updateWorlds() {
		final int[] nullworlds = getNullWorlds();

        for (int i = 301; i <= 535; i++) {
            if (!inArray(nullworlds, i)) {
                if (Worlds.getWorld(i).isMembers() && (Worlds.getWorld(i).getMinimumLevel() == 0 || Worlds.getWorld(i).getMinimumLevel() <= Skills.getTotalLevel()) && !Worlds.getWorld(i).isHighRisk() && !Worlds.getWorld(i).isLastManStanding() && !Worlds.getWorld(i).isTournamentWorld() && !Worlds.getWorld(i).isPVP() && !Worlds.getWorld(i).isTargetWorld() && !Worlds.getWorld(i).isDeadmanMode()) {
                	CURworlds = Arrays.copyOf(CURworlds, CURworlds.length + 1);
                    CURworlds[CURworlds.length - 1] = i;
                }
            }
        }
	}
	
	public static boolean inArray(final int[] arr, final int key) {
        return Arrays.stream(arr).anyMatch(i -> i == key);
    }

    public static int[] getNullWorlds() {
        int[] nullworlds = new int[0];

        for (int i = 301; i <= 535; i++) {
            try {
                Worlds.getWorld(i).isMembers();
            } catch (final Exception e) {
                nullworlds = Arrays.copyOf(nullworlds, nullworlds.length + 1);
                nullworlds[nullworlds.length - 1] = i;
            }
        }
        return nullworlds;
    }
	
	public void removeNulls() {
		int[] indices = {};
		for (int i = 0; i < toSell.length; i++) {
			if (toSell[i] == 0) {
				indices = Arrays.copyOf(indices, indices.length+1);
				indices[indices.length-1] = i;
			}
		} //record what values are 0
		
		List<Integer> IDList = Arrays.stream(IDs).boxed().collect(Collectors.toList());
		List<Integer> BuyAvgList = Arrays.stream(BuyAvg).boxed().collect(Collectors.toList());
		List<Integer> PricesList = Arrays.stream(Prices).boxed().collect(Collectors.toList());
		List<Integer> toSellList = Arrays.stream(toSell).boxed().collect(Collectors.toList());
		//turn all into lists
		
		for(int i = 0; i < indices.length / 2; i++) {
		    int temp = indices[i];
		    indices[i] = indices[indices.length - i - 1];
		    indices[indices.length - i - 1] = temp;
		} //flip indices array
		
		for (int i = 0; i < indices.length; i++) {
			IDList.remove(indices[i]);
			BuyAvgList.remove(indices[i]);
			PricesList.remove(indices[i]);
			toSellList.remove(indices[i]);
		} //iterate through and remove indices from all arrays
		
        IDs = IDList.stream().mapToInt(i->i).toArray();
        BuyAvg = BuyAvgList.stream().mapToInt(i->i).toArray();
        Prices = PricesList.stream().mapToInt(i->i).toArray();
        toSell = toSellList.stream().mapToInt(i->i).toArray();
        //make back into integer arrays
	}
	
	public void Truncto27() {
		if (IDs.length > 27) {
			log("Fetched too many items, truncating to 27");
		    int[] truncated = new int[27];
		    System.arraycopy(IDs, 0, truncated, 0, 27);
		    IDs = truncated;
		    
		    truncated = new int[27];
		    System.arraycopy(BuyAvg, 0, truncated, 0, 27);
		    BuyAvg = truncated;
		    
		    truncated = new int[27];
		    System.arraycopy(Prices, 0, truncated, 0, 27);
		    Prices = truncated;
		    
		    truncated = new int[27];
		    System.arraycopy(toSell, 0, truncated, 0, 27);
		    toSell = truncated;
		}
	}
	
	public void IDsToItemName() {
	    ItemNames = new String[IDs.length];
		for(int i = 0; i < IDs.length; i++) {
			Item item = new Item(IDs[i], 1);
			ItemNames[i] = item.getName();
		}
	}
}