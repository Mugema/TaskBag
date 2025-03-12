import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class TaskBagImplementation extends UnicastRemoteObject
        implements TaskBag
{
    Hashtable<SubscriberTypes,List<Subscriber>> subscribers = new Hashtable<>(){};
    int numberOfResults;

    TaskBagImplementation() throws RemoteException {
        super();
        subscribers.put(SubscriberTypes.Worker,new ArrayList<>());
        subscribers.put(SubscriberTypes.MasterWorker,new ArrayList<>());
    }
    List<String> taskNames;
    ArrayList<Integer> results= new ArrayList<>();


    @Override
    public void subscribe(SubscriberTypes type, Subscriber sub) throws RemoteException {
        subscribers.get(type).addLast(sub);
        System.out.println("Adding " + type + " Subscriber.\n");
        if(type== SubscriberTypes.Worker) {
            try {
                workerNotification(SubscriberTypes.Worker);
            } catch (InterruptedException ignored) { }
        }
    }

    @Override
    public void unSubscribe(SubscriberTypes type, Subscriber sub) throws RemoteException {
        System.out.println("Removing "+ type + " Subscriber.\n");
        subscribers.get(type).remove(sub);
    }

    @Override
    public void workerNotification(SubscriberTypes workerType) throws RemoteException, InterruptedException {
        while (subscribers.get(workerType).isEmpty() || tasks.isEmpty())
        {
            Thread.sleep(500L);
        }
        List<Subscriber> subscriberList = subscribers.get(workerType);

        try{
            subscriberList.getFirst().update();
        } catch (RemoteException e) {
            subscribers.get(SubscriberTypes.Worker).remove(subscriberList.getFirst());
        }

    }

    @Override
    public void masterWorkerNotification() throws RemoteException, InterruptedException {
        new Thread(()->{
            try {
                subscribers.get(SubscriberTypes.MasterWorker).getFirst().update();
            } catch (RemoteException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public void newTasks() throws RemoteException {
        numberOfResults = tasks.size();
        taskNames = new ArrayList<>(List.copyOf(tasks.keySet()));
        taskNames.remove("Next");

        System.out.println("New tasks added to the Bag are "+tasks+"\n");
        try {
            results=new ArrayList<>();
            workerNotification(SubscriberTypes.Worker);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized  void pairOut(String key, int[] array) throws RemoteException {
        tasks.put(key,array);
    }

    @Override
    public synchronized  void addToResults(int value) throws RemoteException {
        if(results.add(value)) System.out.println("Added " +value+" to the results.\n");
        if (results.size()==numberOfResults) {
            try {
                masterWorkerNotification();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        else System.out.println(value);
        System.out.println("Results list: "+results+"\n");
    }

    @Override
     public synchronized  int[] pairIn() {
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
    public synchronized  int[] readPair(String key) {
        return tasks.get(key);
    }

    @Override
    public  synchronized void updateWork() throws RemoteException {
        new Thread(() -> {
            if (!tasks.isEmpty()) {
                if(!taskNames.isEmpty()) {
                    String key = taskNames.getFirst();
                    taskNames.removeFirst();
                    int[] array = tasks.get(key);

                    tasks.remove(key);
                    tasks.put("Next",array);

                    try {
                        workerNotification(SubscriberTypes.Worker);
                    } catch (RemoteException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    @Override
    public ArrayList<Integer> returnResults() throws RemoteException {
        return results;
    }

    public static void createServer() {
        try {
            TaskBagImplementation taskBag = new TaskBagImplementation();
            LocateRegistry.createRegistry(1899);
            Naming.bind("rmi://localhost:1899"+"/TB",taskBag);
        }
        catch (Exception ignored){
        }
    }

    public static void main(String [] arg){
        System.out.println("---------------------------Running the TaskBag---------------------------");
        TaskBagImplementation.createServer();
    }


}
