package com.nlp.task.twitter.analysis.job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scheduler handler. Manage a job triggering.
 *
 */
public class SchedulerHandler {

	private static final String DEBUG_JOBS_INIT_STARTING_MESSAGE = "Starting job initialization...";
	private static final String DEBUG_JOBS_INIT_DONE_MESSAGE = "Jobs initialization done.";
	private static final String DEBUG_JOBS_SHUTDOWN_STARTING_MESSAGE = "Starting jobs shutdown...";
	private static final String DEBUG_JOBS_SHUTDOWN_DONE_MESSAGE = "Starting jobs done.";

	private static final Logger logger = LoggerFactory.getLogger(SchedulerHandler.class);

	private static SchedulerHandler instance;
	private Scheduler scheduler;

	public static SchedulerHandler getInstance() throws SchedulerException {
		if (SchedulerHandler.instance == null) {
			synchronized (SchedulerHandler.class) {
				if (SchedulerHandler.instance == null) {
					SchedulerHandler.instance = new SchedulerHandler();
				}
			}
		}

		return SchedulerHandler.instance;
	}

	private SchedulerHandler() throws SchedulerException {
		scheduler = StdSchedulerFactory.getDefaultScheduler();
	}

	public void startAndSchedule() throws SchedulerException {
		logger.debug(DEBUG_JOBS_INIT_STARTING_MESSAGE);

		if (!scheduler.isStarted()) {
			scheduler.start();

			JobDetail job = JobBuilder.newJob(FetchTwitsJob.class).build();
			JobDetail job2 = JobBuilder.newJob(EvaluateTwitsJob.class).build();
  
			Trigger trigger = TriggerBuilder.newTrigger().startNow().withSchedule(
					SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(30).repeatForever())
					.build();
			
			Trigger trigger2 = TriggerBuilder.newTrigger().startNow().withSchedule(
					SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(30).repeatForever())
					.build();

			scheduler.scheduleJob(job, trigger);
			scheduler.scheduleJob(job2, trigger2);
		}

		logger.debug(DEBUG_JOBS_INIT_DONE_MESSAGE);
	}

	/**
	 * Stop scheduler
	 *
	 * @throws SchedulerException
	 *             if scheduler is not stopped
	 */
	public void stop() throws SchedulerException {
		logger.debug(DEBUG_JOBS_SHUTDOWN_STARTING_MESSAGE);

		scheduler.shutdown();

		logger.debug(DEBUG_JOBS_SHUTDOWN_DONE_MESSAGE);
	}
}