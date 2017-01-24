package com.nlp.task.twitter.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

public class NetworkConfig {

	public static final String WORD_VECTORS_PATH = "<path>\\glove.twitter.27B.200d.txt";
	static final File fileToSaveNetworkModel = new File("<path>\\trainedNetwork.zip");

	private static WordVectors wordVectors;
	private static MultiLayerNetwork net;

	public static synchronized WordVectors getWordVectors() {
		if (wordVectors == null) {
			try {
				wordVectors = WordVectorSerializer.loadTxtVectors(new File(WORD_VECTORS_PATH));
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				throw new RuntimeException("Error while loading vectors: " + e.getMessage());
			}

		}

		return wordVectors;
	}

	public static synchronized MultiLayerNetwork getNet() {
		if (net == null) {
			try {
				net = ModelSerializer.restoreMultiLayerNetwork(fileToSaveNetworkModel);
			} catch (IOException e) {
				throw new RuntimeException("Error while loading multulayer network: " + e.getMessage());
			}
		}

		return net;
	}
}
