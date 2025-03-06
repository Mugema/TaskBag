import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Scanner;

public class MasterWorker {
    static TaskBag stub=null;
    String[] taskNames;

    public static void createStub()  {
        try {
            stub = (TaskBag) Naming.lookup("rmi://localhost:1899"+"/TB");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void addWork(String key,int[] arraySlice){
        if (stub==null){
            System.out.println("The stub was never initialized");
        }
        else try {
            stub.pairOut(key,arraySlice);
        } catch (java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateWork(String key) throws RemoteException {
        stub.updateWork(key);
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

    public void generateTaskNames(int limit){
        for(int i=0;i<limit;i++){
            taskNames[i]="Master"+i;
        }
    }

    public void masterCore(int[] array, MasterWorker masterWorker) throws RemoteException {
        int numberOfSubArrays;

        if(array.length%3==0) numberOfSubArrays = array.length / 3;
        else numberOfSubArrays = (array.length / 3) + 1;

        masterWorker.taskNames= new String[numberOfSubArrays];
        masterWorker.generateTaskNames(numberOfSubArrays);

        if (array.length<=3)  addWork("Next", array);
        else {
            for(int i =0; i<numberOfSubArrays; i+=1){
                if (array.length-i*3<3) addWork(masterWorker.taskNames[i], masterWorker.sliceArray(array,i*3,array.length) );
                else addWork("Master"+i, masterWorker.sliceArray(array,i*3,(i*3)+3) );
                if (i==0) masterWorker.taskNames[i]="Master"+i;
            }
        }
        for(int i=0;i<numberOfSubArrays;i++){
            if(stub.needWork()) {
                masterWorker.updateWork(masterWorker.taskNames[i]);
                System.out.println("NeedWork");
                System.out.println(stub.returnResults());
            }
        }

    }

    public static void main (String[] arg) throws RemoteException {
        MasterWorker.createStub();
        MasterWorker masterWorker = new MasterWorker();
        int[] array = masterWorker.getArray();

        masterWorker.masterCore(array,masterWorker);

        if (stub.returnResults().isEmpty())
            masterWorker.masterCore(array,masterWorker);

        while(true){
            if (stub.returnResults().size()==1 && !stub.needWork()) {
                System.out.println("The max number in the array is :" + stub.returnResults().get("result"));
                break;
            }
            else if (stub.returnResults().size()>1){
                int count =stub.returnResults().size();
                Object[] object= stub.returnResults().values().toArray();
                int[] aray=new int[count];

                for (int i=0;i<count;i++){
                    aray[i]= (int) object[i];
                }
                masterWorker.masterCore(aray,masterWorker);
            }
        }

    }
}
