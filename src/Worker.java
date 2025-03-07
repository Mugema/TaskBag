import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Arrays;

public class Worker implements Subscriber{
    static TaskBag stub;
    String workerName;


    Worker() throws RemoteException {
        getWorkerName();
    }
    public static void createStub(){
        try {
            stub = (TaskBag)Naming.lookup("rmi://localhost:1899"+"/TB");
        } catch (Exception e){
            System.out.println("Error creating worker stub:"+e);
        }
    }

    public synchronized void getWorkerName() throws RemoteException {
        int workerNumber = stub.subscribe(SubsciberTypes.Worker,this);
        workerName="Worker"+workerNumber;
    }

    public static int maxNumberInArray(int [] array){
        int max =0;
        for (Integer integer : array) {
            if (integer > max) max = integer;
        }
        return max;
    }

    public synchronized void doWork() throws RemoteException {
        int[] array=stub.pairIn();

        stub.updateWork();
        System.out.println(Arrays.toString(array));
        int max =  maxNumberInArray(array);


        try {
            stub.addToResults(workerName,max);
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update() throws RemoteException {
        stub.unSubscribe(SubsciberTypes.Worker,this);
        doWork();
        stub.subscribe(SubsciberTypes.Worker,this);
    }


    public static void main( String [] arg) throws RemoteException {
        Worker.createStub();
        Worker worker= new Worker();

        System.out.println("Running Worker:"+worker.workerName);
        worker.update();

    }

}
