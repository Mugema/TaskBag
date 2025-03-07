import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.List;

public class TaskBagImplementation extends UnicastRemoteObject
        implements TaskBag
{

    TaskBagImplementation() throws RemoteException {
        super();
    }

    @Override
    public int subscribe(SubsciberTypes type,Subscriber sub) throws RemoteException {
        subscribers.get(type).add(sub);
        if(type==SubsciberTypes.Worker)
            return subscribers.get(SubsciberTypes.Worker).size()-1;
        else
            return 0;
    }

    @Override
    public void unSubscribe(SubsciberTypes type,Subscriber sub) throws RemoteException {
        subscribers.get(type).remove(sub);
    }

    @Override
    public void workerNotification(SubsciberTypes workerType) throws RemoteException {
        List<Subscriber> sub = subscribers.get(workerType);
        if (!sub.isEmpty()) sub.getFirst().update();
    }

    @Override
    public void masterWorkerNotification() throws RemoteException {
        subscribers.get(SubsciberTypes.MasterWorker).getFirst().update();
    }

    @Override
    public synchronized void pairOut(String key, int[] array) throws RemoteException {
        tasks.put(key,array);
    }

    @Override
    public synchronized void addToResults(String key, int value) throws RemoteException {
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
            return result;
        }
    }

    @Override
    public synchronized int[] readPair(String key) {
        return tasks.get(key);
    }

    @Override
    public synchronized void updateWork() throws RemoteException {
        taskNames.remove("Next");

        String key = taskNames.getFirst();
        int[] array = tasks.get(key);

        tasks.remove(key);
        tasks.put("Next",array);

        if (tasks.isEmpty())
            masterWorkerNotification();
        else
            workerNotification(SubsciberTypes.Worker);
    }

    @Override
    public Hashtable<String, int[]> returnResults() throws RemoteException {
        return tasks;
    }


}
