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
    public void pairOut(String key, int[] array) throws RemoteException {
        tasks.put(key,array);
    }

    @Override
    public void paiOut(String key, int value) throws RemoteException {
        results.put(key,value);
    }

    @Override
    public int[] pairIn(String key) {
        if (tasks.get(key) == null){
            return null;
        }
        else  {
            tasks.remove(key);
            needWorkValue = true;
            return tasks.get(key);
        }
    }

    @Override
    public int[] readPair(String key) {
        return tasks.get(key);
    }

    @Override
    public void sendNotification() {

    }

    @Override
    public void updateWorkerState(String key, Boolean value) {
        workerState.put(key,value);
    }

    @Override
    public void updateWork(String key) {
        int[] array = tasks.get(key);
        tasks.remove(key);
        tasks.put("Next",array);
        needWorkValue = false;
    }

    public boolean needWork(){
        return needWorkValue;
    }

    @Override
    public Hashtable<String, Integer> returnResults() throws RemoteException {
        return results;
    }


}
