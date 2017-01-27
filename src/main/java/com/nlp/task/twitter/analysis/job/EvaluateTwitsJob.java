package com.nlp.task.twitter.analysis.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.datasets.iterator.AsyncDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nlp.task.twitter.analysis.NetworkConfig;
import com.nlp.task.twitter.analysis.dao.TweetDao;
import com.nlp.task.twitter.analysis.model.entity.Tweet;
import com.nlp.task.twitter.analysis.model.entity.Tweet.Sentiment;
import com.semeval.task4.HashTagPreprocessor;
import com.semeval.task4.PunctuationRemovingPreprocessor;
import com.semeval.task4.RepeatingCharsPreprocessor;
import com.semeval.task4.ReplaceEmoticonsPreprocessor;
import com.semeval.task4.ToLowerCasePreprocesor;
import com.semeval.task4.TweetPreprocessor;
import com.semeval.task4.UrlRemovingPreprocessor;
import com.semeval.task4.demo.SingleTweetIterator;

/**
 * Job used to retrieve events from Ariba and post them to another instance.
 *
 */
public class EvaluateTwitsJob implements Job {

	private static final Logger logger = LoggerFactory.getLogger(EvaluateTwitsJob.class);

	private static TweetDao tweetDao = new TweetDao();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			processTwits();
		} catch (Exception e) {
			logger.error("Error while fetching twits: ", e);
		}

		logger.debug("Evaluated job done");

	}

	private void processTwits() {

		tweetDao.findAllUnknown().forEach(this::evaluate);

	}

	private void evaluate(Tweet tweet) {
		try {
			String content = tweet.getContent();
			List<TweetPreprocessor> preprocessors = new ArrayList<>();
			preprocessors.add(new HashTagPreprocessor());
			preprocessors.add(new UrlRemovingPreprocessor());
			preprocessors.add(new ReplaceEmoticonsPreprocessor());
			preprocessors.add(new PunctuationRemovingPreprocessor());
			preprocessors.add(new RepeatingCharsPreprocessor());
			preprocessors.add(new ToLowerCasePreprocesor());

			for (TweetPreprocessor preprocessor : preprocessors) {
				content = preprocessor.preProcess(content);

			}
			DataSetIterator testIterator = null;

			testIterator = new AsyncDataSetIterator(
					new SingleTweetIterator(content, NetworkConfig.getWordVectors(), 100), 1);

			Evaluation evaluation = new Evaluation();

			DataSet t = testIterator.next();
			INDArray features = t.getFeatureMatrix();
			INDArray lables = t.getLabels();
			INDArray inMask = t.getFeaturesMaskArray();
			INDArray outMask = t.getLabelsMaskArray();
			INDArray predicted = NetworkConfig.getNet().output(features, false, inMask, outMask);

			evaluation.evalTimeSeries(lables, predicted, outMask);

			testIterator.reset();

			if (evaluation.getTopNCorrectCount() == evaluation.getTopNTotalCount()) {
				tweet.setSentiment(Sentiment.POSITIVE);
			} else {
				tweet.setSentiment(Sentiment.NEGATIVE);
			}

			tweetDao.update(tweet);
		} catch (Exception e) {
			logger.warn("Error while evaluating tweet: ", e.getMessage());
		}

	}

}