import java.rmi.*;
import java.util.ArrayList;
import java.util.Hashtable;

public interface TaskBag extends Remote {

    Hashtable<String,int[]> tasks = new Hashtable<>();

    /**causes a Pair (key, value) to be added to the Task Bag. The client
      process continues immediately */
    void pairOut(String key, int[] array) throws RemoteException;

    void addToResults( int value) throws RemoteException;

    /**
     * causes some Pair in the Task Bag that matches key to be withdrawn
     * from the Task Bag; the value part of the Pair is returned and the client
     * process continues. If no matching Pair is available, the client waits
     * until one is and then proceeds as before
     */
    int[]  pairIn() throws RemoteException;

    /**
     is the same as pairIn(key) except that the Pair remains in the
     Task Bag
     */
    int[] readPair(String key) throws RemoteException;

    /**
     * This function updates the work in the task map whenever a worker is done picking up there work
     * */
    void updateWork() throws RemoteException, InterruptedException;

    void subscribe(SubscriberTypes type, Subscriber sub) throws RemoteException;

    void unSubscribe(SubscriberTypes type, Subscriber sub) throws RemoteException;

    /**This function is executed whenever a work is updated and when the MasterWorker adds a new set of tasks
     * after the completion of the previous set*/
    void workerNotification(SubscriberTypes workerType) throws RemoteException, InterruptedException;

    /**This function is called when all the slaveWorkers have completed the assigned tasks*/
    void masterWorkerNotification() throws RemoteException, InterruptedException;

    void newTasks() throws RemoteException;


    ArrayList<Integer> returnResults() throws RemoteException;


}
