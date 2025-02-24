import java.rmi.Naming;
import java.util.Arrays;
import java.util.Scanner;

public class MasterWorker {
    static TaskBag stub=null;

    public static void createStub()  {
        stub = null;
        try {
            stub = (TaskBag) Naming.lookup("rmi://localhost:999"+"/TB");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void addWork(String key,int[] arraySlice){
        if (stub==null){
            System.out.println("The stub was never initialized");
        }
        else stub.pairOut(key,arraySlice);
    }

    public void updateWork(String key){
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
        return Arrays.copyOfRange(arr,start,end);
    }

    public static void main (String[] arg){
        MasterWorker.createStub();
        MasterWorker masterWorker = new MasterWorker();
        int[] array = masterWorker.getArray();
        String[] keys;
        int range=0;

        if(array.length%3==0) {
            keys = new String[array.length / 3];
            range = array.length / 3;
        }
        else keys = new String[(array.length/3)+1]; range = array.length/3;


        if (array.length<=3) addWork("Next",array);

        else if(array.length >= 6){
            for(int i =0; i<range; i=i+3){
                if (range-i<3){
                    addWork("Master"+i, masterWorker.sliceArray(array,i,range-i) );
                }
                else addWork("Master"+i, masterWorker.sliceArray(array,i,i+3) );
            }
        }

        if(true){
            int currentJob=0;
            masterWorker.updateWork(keys[currentJob]);
        }

    }
}
