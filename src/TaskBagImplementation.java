import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class TaskBagImplementation extends UnicastRemoteObject
        implements TaskBag
{
    TaskBagImplementation() throws RemoteException {
        super();
    }

    @Override
    public void pairOut(String key, int[] array) {
        tasks.put(key,array);
    }

    @Override
    public void paiOut(String key, int value) {
        results.put(key,value);
    }

    @Override
    public int[] pairIn(String key) {
        if (tasks.get(key) == null){
            return tasks.get(key);
        }
        else return tasks.get(key);
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
        tasks.put(key,array);
    }

}
