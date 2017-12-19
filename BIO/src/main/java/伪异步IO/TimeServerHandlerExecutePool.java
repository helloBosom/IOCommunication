package 伪异步IO;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimeServerHandlerExecutePool {
	ExecutorService executorService;

	public TimeServerHandlerExecutePool(int maxPoolSize, int queueSize) {
		executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSize, 120L,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize));
	}

	void execute(Runnable task) {
		executorService.execute(task);
	}

}
