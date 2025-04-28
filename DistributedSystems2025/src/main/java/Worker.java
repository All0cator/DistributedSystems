public class Worker extends Thread{

    @Override
    public void run() 
	{
        try {
            // Displaying the thread that is running
            System.out.println("Thread " + Thread.currentThread().threadId() + " is running");
        }
        catch (Exception e) {
            // Throwing an exception
            System.out.println("Exception is caught");
        }
    }

}