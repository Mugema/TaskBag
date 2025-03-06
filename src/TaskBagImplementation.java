import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

public class TaskBagImplementation extends UnicastRemoteObject
        implements TaskBag
{

    Boolean needWorkValue = true;

    TaskBagImplementation() throws RemoteException {
        super();
    }

    @Override
    public synchronized void pairOut(String key, int[] array) throws RemoteException {
        tasks.put(key,array);
    }

    @Override
    public synchronized void pairOut(String key, int value) throws RemoteException {
        results.put(key,value);
    }

    @Override
    public synchronized int[] pairIn() {
        if (tasks.get("Next") == null){
            return null;
        }
        else  {
            int[] result = tasks.get("Next");
            tasks.remove("Next");
            needWorkValue = true;
            return result;
        }
    }

    @Override
    public synchronized int[] readPair(String key) {
        return tasks.get(key);
    }

    @Override
    public synchronized void sendNotification() {

    }

    @Override
    public synchronized void updateWorkerState(String key, Boolean value) {
        workerState.put(key,value);
    }

    @Override
    public synchronized void updateWork(String key) {
        int[] array = tasks.get(key);
        tasks.remove(key);
        tasks.put("Next",array);
        needWorkValue = false;
    }

    public synchronized boolean needWork(){
        return needWorkValue;
    }

    @Override
    public Hashtable<String, int[]> returnResults() throws RemoteException {
        return tasks;
    }


}
