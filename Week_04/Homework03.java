import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class Homework03 {
    
    public static void main(String[] args) throws Exception {
        
        long start=System.currentTimeMillis();
        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法
        
        int result = method8(); //这是得到的返回值
        
        // 确保  拿到result 并输出
        System.out.println("异步计算结果为："+result);
         
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
        
        // 然后退出main线程
    }
    
    
    private static int sum() {
        return fibo(36);
    }
    
    private static int fibo(int a) {
        if ( a < 2) 
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
    
    /**
     * 原生线程操作
     * @throws Exception 
     */
    private static int method1() throws Exception {
    	final int[] lock = new int[1];
    	new Thread(() -> {
    		lock[0] = sum();
    		synchronized(lock) {
    			lock.notify();
    		}
    	} ).start();
    	synchronized (lock) {
    		lock.wait();
		}
    	return lock[0];
    }
    /**
     * future
     * @throws Exception 
     */
    private static int method2() throws Exception {
    	ExecutorService executor = Executors.newSingleThreadExecutor();
    	Future<Integer> future = executor.submit(Homework03::sum);
    	return future.get();
    }
    /**
     * CountDownLatch
     * @throws Exception 
     */
    private static int method3() throws Exception {
    	final int[] result = new int[1];
    	CountDownLatch latch = new CountDownLatch(1);
    	new Thread(() -> {
    		result[0] = sum();
    		latch.countDown();
    	}).start();
    	latch.await();
    	return result[0];
    }
    /**
     * CyclicBarrier
     * @throws Exception 
     */
    private static int method4() throws Exception {
    	final int[] result = new int[1];
    	CyclicBarrier barrier = new CyclicBarrier(1, () -> {
    		result[0] = sum();
    	});
    	barrier.await();
    	return result[0];
    }
    
    /**
     * Join
     * @throws Exception 
     */
    private static int method5() throws Exception {
    	final int[] result = new int[1];
    	Thread t = new Thread(() -> {
    		result[0] = sum();
    	} );
    	t.start();
    	t.join();
    	return result[0];
    }
    
    /**
     * Queue
     * @throws Exception 
     */
    private static int method6() throws Exception {
    	SynchronousQueue<Integer> queue = new SynchronousQueue<Integer>();
    	new Thread(() -> {
    		try {
				queue.put(sum());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}).start();
    	return queue.take();
    }
    
    /**
     * ExecutorService
     * @throws Exception 
     */
    private static int method7() throws Exception {
    	final int[] result = new int[1];
    	ExecutorService executor = Executors.newSingleThreadExecutor();
    	executor.execute(() -> result[0] = sum());
    	executor.shutdown();
    	executor.awaitTermination(1L, TimeUnit.HOURS);
    	return result[0];
    }
    
    /**
     * 自旋
     * @throws Exception 
     */
    private static int method8() throws Exception {
    	final Integer[] result = new Integer[1];
    	new Thread(() -> result[0] = sum()).start();
    	while(result[0] == null) {
    		Thread.sleep(50);
    	}
    	return result[0];
    }
    
}