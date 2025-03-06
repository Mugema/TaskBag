import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Arrays;

public class Worker {
    Boolean workAvailable = false;
    static TaskBag stub;
    String workerName;


    Worker() throws RemoteException {
        getWorkerName();
        stub.updateWorkerState(workerName,true);
        System.out.println(workerName);
    }
    public static void createStub(){
        try {
            stub = (TaskBag)Naming.lookup("rmi://localhost:1899"+"/TB");
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public synchronized void getWorkerName(){
        if (TaskBag.workerState.isEmpty()) workerName = "Worker"+0 ;
        else workerName = "Worker"+TaskBag.workerState.size()+1;
    }

    public static Integer maxNumberInArray(int [] array){
        int max =0;
        for (Integer integer : array) {
            if (integer > max) max = integer;
        }
        return max;
    }

    public synchronized int getWork() throws RemoteException {
        stub.updateWorkerState(workerName,false);
        int[] array=stub.pairIn();
        System.out.println(Arrays.toString(array));
        if(array==null){
            try {
                Thread.sleep(1000L);
                return getWork();
            } catch (InterruptedException e){ return 0;}
        }
        else return maxNumberInArray(array);

    }

    public void addResult(int result){
        try {
            stub.pairOut(workerName,result);
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main( String [] arg) throws RemoteException {
        System.out.println("Running Worker");
        Worker.createStub();
        int result;
        Worker worker= new Worker();

        while(true){
            result= worker.getWork();
            worker.addResult(result);

        }
    }

}
