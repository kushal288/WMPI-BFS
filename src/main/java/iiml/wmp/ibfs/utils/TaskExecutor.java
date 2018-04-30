package iiml.wmp.ibfs.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class TaskExecutor
{

	private final static Logger logger = LoggerFactory.getLogger(TaskExecutor.class);
	private static ExecutorService serviceExecutor;

	private TaskExecutor()
	{
		serviceExecutor = Executors.newFixedThreadPool(4);
	}

	public static ExecutorService getServiceExecutor()
	{
		return serviceExecutor;
	}

	public static TaskExecutor getInstance()
	{
		return TaskExecutorHelper.INSTANCE;
	}

	public void submit(Runnable task)
	{
		serviceExecutor.submit(task);
	}

	public <T> Future<T> submit(Callable<T> task)
	{
		return serviceExecutor.submit(task);
	}

	public void shutdown()
	{

		serviceExecutor.shutdownNow();
		try
		{
			serviceExecutor.awaitTermination(120, TimeUnit.SECONDS);
		}
		catch (InterruptedException e)
		{
			logger.error("Error In shutdown of executor!!!", e);
		}
		serviceExecutor.shutdown();
	}

	private static class TaskExecutorHelper
	{
		private static final TaskExecutor INSTANCE = new TaskExecutor();
	}
}
