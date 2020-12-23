package main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class fetchSP {
	
	private static URLConnection con;
	private static InputStream is;
	private static InputStreamReader isr;
	private static BufferedReader br;
	
	/* USAGE
	public static void main(String[] args) {
		fetchSP.Pair pair = fetchSP.Scrape();
		int[] IDs = pair.getIDs();
		int[] Prices = pair.getPrices();
		int[] BuyAvg = pair.getBuyAvg();
	}
	*/
	
	public static Pair Scrape() {
		try {
			URL url = new URL("http://83.85.215.128:8080/storesales.php");
			con = url.openConnection();	        
			is = con.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			String line = br.readLine();	
			
			ArrayList<Integer> itemIDs = new ArrayList<Integer>();  
			ArrayList<Integer> itemPrices = new ArrayList<Integer>();
			ArrayList<Integer> itemBuyAvg = new ArrayList<Integer>();

			while (line != null){  
				if (line.indexOf("id: ") != -1) {
					int beginningofid = line.indexOf("id: ");
					int endofid = line.indexOf(",", beginningofid);
					int begginingofsp = line.indexOf("store_price: ");
					int endofsp = line.indexOf(",", begginingofsp);
					int beginningofbuyavg = line.indexOf("buy_average: ");
					int endofbuyavg = line.indexOf(",", beginningofbuyavg);
					
					int begginningofspreal = begginingofsp+13;
					int begginningofidreal = beginningofid+4;
					int beginningofbuyavgreal = beginningofbuyavg+13;
					String sp = line.substring(begginningofspreal, endofsp);
					String id = line.substring(begginningofidreal, endofid);
					String buyAVG = line.substring(beginningofbuyavgreal, endofbuyavg);
					itemPrices.add(Integer.parseInt(sp));
					itemIDs.add(Integer.parseInt(id));
					itemBuyAvg.add(Integer.parseInt(buyAVG));
				}
				line = br.readLine();
			}
			
			int[] ArrItemIDs = new int[itemIDs.size()]; 
			int[] ArrItemPrices = new int[itemPrices.size()];
			int[] ArrItemBuyAvg = new int[itemBuyAvg.size()];
			for (int i = 0; i < ArrItemIDs.length; i++){
				ArrItemIDs[i] = itemIDs.get(i);
				ArrItemPrices[i] = itemPrices.get(i);
				ArrItemBuyAvg[i] = itemBuyAvg.get(i);
			}
			
			return new Pair(ArrItemIDs, ArrItemPrices, ArrItemBuyAvg);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				} else if (isr != null) {
					isr.close();
				} else if (is != null) {
					is.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static class Pair
	{
	    private int[] array1;
	    private int[] array2;
	    private int[] array3;
	    public Pair(int[] array1, int[] array2, int[] array3)
	    {
	        this.array1 = array1;
	        this.array2 = array2;
	        this.array3 = array3;
	    }
	    public int[] getIDs() { return array1; }
	    public int[] getPrices() { return array2; }
	    public int[] getBuyAvg() { return array3; }
	}
}
