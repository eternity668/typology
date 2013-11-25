package de.typology.executables;

import java.io.File;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.typology.indexes.WordIndex;
import de.typology.indexes.WordIndexer;
import de.typology.patterns.PatternBuilder;
import de.typology.smoother.KneserNeySmoother;
import de.typology.splitter.AbsoluteSplitter;
import de.typology.splitter.DataSetSplitter;
import de.typology.splitter.SmoothingSplitter;
import de.typology.utils.Config;

public class KneserNeyBuilder {

	static Logger logger = LogManager.getLogger(KneserNeyBuilder.class
			.getName());

	public static void main(String[] args) {

		// TODO: parameters as arguments
		File inputDirectory = new File(Config.get().outputDirectory
				+ Config.get().inputDataSet);
		File inputFile = new File(inputDirectory.getAbsolutePath()
				+ "/training.txt");
		File indexFile = new File(inputDirectory.getAbsolutePath()
				+ "/index.txt");
		File absoluteOutputDirectory = new File(
				inputDirectory.getAbsolutePath() + "/absolute");
		if (Config.get().splitData) {
			DataSetSplitter dss = new DataSetSplitter(inputDirectory,
					"normalized.txt");
			dss.split("training.txt", "learning.txt", "testing.txt",
					Config.get().modelLength);
			// dss.splitIntoSequences(new
			// File(inputDirectory.getAbsolutePath()+"/learning.txt"),
			// Config.get().modelLength,Config.get().numberOfQueries);
			// dss.splitIntoSequences(new
			// File(inputDirectory.getAbsolutePath()+"/testing.txt"),
			// Config.get().modelLength,Config.get().numberOfQueries);
		}
		if (Config.get().buildIndex) {
			logger.info("build word index: " + indexFile.getAbsolutePath());
			WordIndexer wordIndexer = new WordIndexer();
			wordIndexer.buildIndex(inputFile, indexFile,
					Config.get().maxCountDivider, "<fs> <s> ", " </s>");
		}
		if (Config.get().buildGLM) {
			ArrayList<boolean[]> glmForSmoothingPatterns = PatternBuilder
					.getReverseGLMForSmoothingPatterns(Config.get().modelLength);
			AbsoluteSplitter absolteSplitter = new AbsoluteSplitter(inputFile,
					indexFile, absoluteOutputDirectory,
					Config.get().maxCountDivider, "\t",
					Config.get().deleteTempFiles, "<fs> <s> ", " </s>");
			logger.info("split into GLM sequences: "
					+ inputFile.getAbsolutePath());
			absolteSplitter.split(glmForSmoothingPatterns,
					Config.get().numberOfCores);
		}
		if (Config.get().build_absoluteGLM) {
			ArrayList<boolean[]> glmForSmoothingPatterns = PatternBuilder
					.getReverseGLMForSmoothingPatterns(Config.get().modelLength);
			SmoothingSplitter smoothingSplitter = new SmoothingSplitter(
					absoluteOutputDirectory, indexFile,
					Config.get().maxCountDivider, "\t",
					Config.get().deleteTempFiles);

			logger.info("split into GLM sequences: "
					+ inputFile.getAbsolutePath());
			smoothingSplitter.split(glmForSmoothingPatterns,
					Config.get().numberOfCores);
		}
		File _absoluteDirecory = new File(inputDirectory.getAbsolutePath()
				+ "/_absolute");
		File _absolute_Direcory = new File(inputDirectory.getAbsolutePath()
				+ "/_absolute_");
		File absolute_Direcory = new File(inputDirectory.getAbsolutePath()
				+ "/absolute_");
		if (Config.get().buildKneserNey) {
			File kneserNeyOutputDirectory = new File(
					inputDirectory.getAbsolutePath() + "/kneser-ney");
			// TODO:delete old kneser ney files
			KneserNeySmoother kns = new KneserNeySmoother(
					absoluteOutputDirectory, _absoluteDirecory,
					_absolute_Direcory, absolute_Direcory,
					kneserNeyOutputDirectory, new WordIndex(indexFile), "\t",
					Config.get().decimalPlaces);
			kns.deleteResults();
			for (int i = 1; i <= Config.get().modelLength; i++) {
				// call KneserNeySmoother
				kns.smoothComplex(PatternBuilder.getGLMPatterns(i));
			}
		}
		logger.info("done");
	}
}