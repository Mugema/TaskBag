import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class TaskBagImplementation extends UnicastRemoteObject
        implements TaskBag
{
    Hashtable<SubscriberTypes,List<Subscriber>> subscribers = new Hashtable<>(){};

    TaskBagImplementation() throws RemoteException {
        super();
        subscribers.put(SubscriberTypes.Worker,new ArrayList<>());
        subscribers.put(SubscriberTypes.MasterWorker,new ArrayList<>());
    }
    List<String> taskNames;


    @Override
    public int subscribe(SubscriberTypes type, Subscriber sub) throws RemoteException {
        subscribers.get(type).add(sub);
        System.out.println("Adding Subscriber: "+sub.toString());
        if(type== SubscriberTypes.Worker)
            return subscribers.get(SubscriberTypes.Worker).size()-1;
        else
            return 0;
    }

    @Override
    public void unSubscribe(SubscriberTypes type, Subscriber sub) throws RemoteException {
        System.out.println("Removing Subscriber: "+ sub);
        subscribers.get(type).remove(sub);
    }

    @Override
    public void workerNotification(SubscriberTypes workerType) throws RemoteException, InterruptedException {
        while (subscribers.get(workerType).isEmpty())
        {
            Thread.sleep(2000L);
        }
        List<Subscriber> subscriberList = subscribers.get(workerType);

        try{
            System.out.println(subscriberList);
            subscriberList.getFirst().update();
        } catch (RemoteException e) {
            subscribers.get(SubscriberTypes.Worker).remove(subscriberList.getFirst());
        }

    }

    @Override
    public void masterWorkerNotification() throws RemoteException, InterruptedException {
        subscribers.get(SubscriberTypes.MasterWorker).getFirst().update();
    }

    @Override
    public void newTasks() throws RemoteException {
        taskNames = new ArrayList<>(List.copyOf(tasks.keySet()));
        System.out.println("The size of the taskBag is"+tasks.size());
        System.out.println("The sliced arrays are:"+ Arrays.toString(tasks.values().toArray()));
        System.out.println("The taskNames are:"+tasks.keys());
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
    public synchronized void updateWork() throws RemoteException, InterruptedException {
        taskNames.remove("Next");

        if (!tasks.isEmpty()){
            String key = taskNames.getFirst();
            int[] array = tasks.get(key);

            tasks.remove(key);
            tasks.put("Next",array);

            workerNotification(SubscriberTypes.Worker);
        }
        else {
            masterWorkerNotification();
        }

    }

    @Override
    public Hashtable<String, int[]> returnResults() throws RemoteException {
        return tasks;
    }

    public static void createServer() {
        try {
            TaskBagImplementation taskBag = new TaskBagImplementation();
            LocateRegistry.createRegistry(1899);
            Naming.bind("rmi://localhost:1899"+"/TB",taskBag);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public static void main(String [] arg){
        System.out.println("Running the TaskBag");
        System.out.println("-----------------------------------------------------");
        TaskBagImplementation.createServer();
    }


}
