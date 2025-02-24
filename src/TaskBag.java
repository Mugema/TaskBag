import java.lang.reflect.Array;
import java.rmi.*;
import java.util.Hashtable;
import java.util.Map;

public interface TaskBag extends Remote {

    Hashtable<String,int[]> tasks = new Hashtable<>();
    Hashtable<String, Integer> results = new Hashtable<>();
    Hashtable<String, Boolean> workerState = new Hashtable<>();

    /**causes a Pair (key, value) to be added to the Task Bag. The client
      process continues immediately */
    void pairOut(String key, int[] array);

    void paiOut(String key, int value);

    /**
     * causes some Pair in the Task Bag that matches key to be withdrawn
     * from the Task Bag; the value part of the Pair is returned and the client
     * process continues. If no matching Pair is available, the client waits
     * until one is and then proceeds as before
     */
    int[]  pairIn(String key);

    /**
     is the same as pairIn(key) except that the Pair remains in the
     Task Bag
     */
    int[] readPair(String key);

    void sendNotification();

    void updateWorkerState(String key,Boolean value);

    void updateWork(String key);

}
