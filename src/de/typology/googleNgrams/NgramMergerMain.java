package de.typology.googleNgrams;

import java.io.IOException;

import de.typology.utils.Config;
import de.typology.utils.SystemHelper;

public class NgramMergerMain {

	/**
	 * @param args
	 * @throws IOException
	 * 
	 * @author Martin Koerner
	 */
	public static void main(String[] args) throws IOException {
		SystemHelper
				.runUnixCommand("rm " + Config.get().googleNgramsMergedPath);
		NgramMerger merger = new NgramMerger();
		merger.merge(Config.get().googleNgramsPath,
				Config.get().googleNgramsMergedPath);

	}

}
