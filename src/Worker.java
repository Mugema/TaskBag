import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class Worker extends UnicastRemoteObject implements Subscriber, Serializable {
    static TaskBag stub;


    Worker() throws RemoteException {
        super();
        System.out.println("---------------------------Running Worker---------------------------");
        createStub();
        stub.subscribe(SubscriberTypes.Worker,this);
    }
    public static void createStub(){
        try {
            stub = (TaskBag)Naming.lookup("rmi://localhost:1899"+"/TB");
        } catch (Exception e){
            System.out.println("Error creating worker stub:"+e);
        }
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

        if(array!=null) {
            System.out.println("Doing Work:"+ Arrays.toString(array));
            stub.updateWork();

            int max =  maxNumberInArray(array);
            System.out.println("maxNumber in "+ Arrays.toString(array) +" is "+max);

            try {
                stub.addToResults(max);
            } catch (Exception e) {
                System.out.println("Error---" +e );
            }
        }
    }

    @Override
    public  void  update() throws RemoteException, InterruptedException {
        stub.unSubscribe(SubscriberTypes.Worker,this);
        doWork();
        Thread.sleep(2000L);
        stub.subscribe(SubscriberTypes.Worker,this);
    }


    public static void main( String [] arg) throws RemoteException{
        Worker worker= new Worker();
    }

}
