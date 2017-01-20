package com.semeval.task4;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

public final class SimpleGravesLstmNetworkTrainer extends AbstractNetworkTrainer {
    private final WordVectors wordVectors;
    protected final int vectorSize;
    static final File fileToSaveNetworkModel = new File("myRNN.zip");
    public static final String WORD_VECTORS_PATH = "C:\\Users\\i323283\\Downloads\\glove.twitter.27B\\glove.twitter.27B.200d.txt";
    public SimpleGravesLstmNetworkTrainer(Path trainSet, Path testSet, WordVectors wordVectors, int vectorSize) {
        super(trainSet, testSet);
        this.wordVectors = wordVectors;
        this.vectorSize = vectorSize;
    }

    @Override
    protected MultiLayerNetwork createNetwork() {
        final int nOut = Math.min(vectorSize, 200);
        final MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .iterations(1)
                .updater(Updater.RMSPROP)
                .regularization(true)
                .l2(1e-5)
                .weightInit(WeightInit.XAVIER)
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                .gradientNormalizationThreshold(1.0)
                .learningRate(0.15)
                .list()
                .layer(0, new GravesLSTM.Builder()
                        .nIn(vectorSize)
                        .nOut(nOut)
                        .activation("softsign")
                        .build())
                .layer(1, new RnnOutputLayer.Builder()
                        .activation("softmax")
                        .lossFunction(LossFunction.MCXENT)
                        .nIn(nOut)
                        .nOut(2)
                        .build())
                .pretrain(false)
                .backprop(true)
                .build();

        MultiLayerNetwork multiLayerNetwork = new MultiLayerNetwork(configuration);
        multiLayerNetwork.init();
        


        return multiLayerNetwork;
    }

    @Override
    protected DataSetIterator createTrainSetIterator(Path trainSet) throws IOException {
        return new TwitterDataIterator(trainSet, wordVectors, vectorSize, 50);
    }

    @Override
    protected DataSetIterator createTestSetIterator(Path testSet) throws IOException {
        return new TwitterDataIterator(testSet, wordVectors, vectorSize, 50);
    }

    public static void main(String[] args) throws TrainingException, IOException {
        System.out.println("Loading word vectors (" + WORD_VECTORS_PATH + ") ....");
        final int vectorSize = 200;
        WordVectors wordVectors = WordVectorSerializer.loadTxtVectors(new File(WORD_VECTORS_PATH));

        final Path trainSet = Paths.get("C:\\Users\\i323283\\Desktop\\semeval\\twitter_download\\output\\test.txt");
        final Path testSet = Paths.get("C:\\Users\\i323283\\Desktop\\semeval\\twitter_download\\output\\2016.test.bd.txt_semeval_tweets.txt");

        System.out.println("Train set: " + trainSet);
        System.out.println("Creating trainer...");
        System.out.println("Test set: " + testSet);

        final SimpleGravesLstmNetworkTrainer trainer = new SimpleGravesLstmNetworkTrainer(trainSet, testSet, wordVectors, vectorSize);
        trainer.addPreprocessor(new HashTagPreprocessor());
        trainer.addPreprocessor(new UrlRemovingPreprocessor());
        trainer.addPreprocessor(new ReplaceEmoticonsPreprocessor());
        trainer.addPreprocessor(new PunctuationRemovingPreprocessor());
        trainer.addPreprocessor(new RepeatingCharsPreprocessor());
        trainer.addPreprocessor(new ToLowerCasePreprocesor());

        for (int i = 0; i < 12; i++) {
            System.out.println("\nEpoch: " + i);
            System.out.println("Training...");
            trainer.train(1, 20);
            System.out.println("Evaluating...");
            System.out.println(trainer.evaluate(20).stats());
        }

        try (OutputStream rawOut = Files.newOutputStream(Paths.get("trainedNetwork.zip"))) {
            trainer.saveNetwork(rawOut);
        }
    }
}
