import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Scanner;

public class MasterWorker extends UnicastRemoteObject implements Subscriber, Serializable {
    static TaskBag stub=null;

    MasterWorker() throws RemoteException{
        super();
    }


    @Override
    public  void update() throws RemoteException{
        System.out.println("Workers Finished the previous tasks. Getting results or adding new tasks.\n");

        if (stub.returnResults().size()==1) {
            System.out.println("The max number in the array is " + stub.returnResults().getFirst());
            stub.unSubscribe(SubscriberTypes.MasterWorker,this);
            System.out.println("___________________END___________________");
        }
        else if (stub.returnResults().size()>1){
            int count =stub.returnResults().size();
            Object[] object= stub.returnResults().toArray();
            int[] resultsArray =new int[count];

            for (int i=0;i<count;i++){
                resultsArray[i]= (int) object[i];
            }
            System.out.println("New Array is ---> "+ Arrays.toString(resultsArray)+"\n");
            masterCore(resultsArray,this);
        }
    }

    public  static void createStub()  {
        try {
            stub = (TaskBag) Naming.lookup("rmi://localhost:1899"+"/TB");
        }catch (Exception e){
            System.out.println("Error occurred whilst creating the stub:"+e);
        }
    }

    public  static void addWork(String key,int[] arraySlice){
        if (stub==null){
            System.out.println("The stub was never initialized");
        }
        else try {
            stub.pairOut(key,arraySlice);
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public int[] getArray(){
        Scanner input = new Scanner(System.in);
        int size = 0;

        System.out.println("Enter the size of the array");
        if (input.hasNextInt()) size = input.nextInt();
        int[] array = new int[size];

        System.out.println("Enter the elements of the array: ");
        for (int i = 0; i < size; i++) {
            if (input.hasNextInt()) array[i] = input.nextInt();
        }
        System.out.println("The array is:"+ Arrays.toString(array));
        input.close();
        return array;
    }

    public int[] sliceArray(int[] arr,int start, int end){
        System.out.println(Arrays.toString(Arrays.copyOfRange(arr, start, end)));
        return Arrays.copyOfRange(arr,start,end);
    }

    public  void masterCore(int[] array, MasterWorker masterWorker) throws RemoteException {
        int numberOfSubArrays;

        if(array.length%3==0)
            numberOfSubArrays = array.length / 3;
        else
            numberOfSubArrays = (array.length / 3) + 1;

        if (array.length<=3)  addWork("Next", array);
        else {
            for(int i =0; i<numberOfSubArrays; i+=1){
                if (i==0)
                    addWork("Next", masterWorker.sliceArray(array,0,3) );
                else if (array.length-i*3<3)
                    addWork("Task"+i, masterWorker.sliceArray(array,i*3,array.length) );
                else
                    addWork("Task"+i, masterWorker.sliceArray(array,i*3,(i*3)+3) );
            }
        }
        stub.newTasks();
    }

    public  static void main (String[] arg) throws RemoteException{
        System.out.println("---------------------------Running the MasterWorker---------------------------");
        MasterWorker.createStub();
        MasterWorker masterWorker = new MasterWorker();

        stub.subscribe(SubscriberTypes.MasterWorker,masterWorker);

        int[] array = masterWorker.getArray();
        masterWorker.masterCore(array,masterWorker);
    }

}
