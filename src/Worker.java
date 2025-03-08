import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class Worker extends UnicastRemoteObject implements Subscriber, Serializable {
    static TaskBag stub;
    String workerName;


    Worker() throws RemoteException {
        super();
        createStub();
        getWorkerName();

    }
    public static void createStub(){
        try {
            stub = (TaskBag)Naming.lookup("rmi://localhost:1899"+"/TB");
        } catch (Exception e){
            System.out.println("Error creating worker stub:"+e);
        }
    }

    public void getWorkerName() throws RemoteException {
        int workerNumber = stub.subscribe(SubscriberTypes.Worker,this);
        workerName="Worker"+workerNumber;
    }

    public static int maxNumberInArray(int [] array){
        int max =0;
        for (Integer integer : array) {
            if (integer > max) max = integer;
        }
        return max;
    }

    public void doWork() throws RemoteException, InterruptedException {
        int[] array=stub.pairIn();
        System.out.println("Doing Work:"+ Arrays.toString(array));

        stub.updateWork();
        System.out.println(Arrays.toString(array));
        int max =  maxNumberInArray(array);

        try {
            stub.addToResults(workerName,max);
        } catch (java.rmi.RemoteException e) {
            System.out.println("Error---" +e );
        }
        System.out.println("maxNumber is:"+max);

    }

    @Override
    public synchronized void  update() throws RemoteException, InterruptedException {
        stub.unSubscribe(SubscriberTypes.Worker,this);
        doWork();
        stub.subscribe(SubscriberTypes.Worker,this);
    }


    public static void main( String [] arg) throws RemoteException, InterruptedException {
        Worker worker= new Worker();

        System.out.println("Running Worker:"+worker.workerName);

    }

}
