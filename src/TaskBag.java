import java.rmi.*;
import java.util.Map;

public interface TaskBag extends Remote {
    /**causes a Pair (key, value) to be added to the Task Bag. The client
      process continues immediately */
    void pairOut( Map<String,String> taskPair );

    /**
     * causes some Pair in the Task Bag that matches key to be withdrawn
     * from the Task Bag; the value part of the Pair is returned and the client
     * process continues. If no matching Pair is available, the client waits
     * until one is and then proceeds as before
     */
    String  pairIn(String key);

    /**
     is the same as pairIn(key) except that the Pair remains in the
     Task Bag
     */
    String readPair(String key);

}
