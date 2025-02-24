public class Worker {
    Boolean workAvailable = false;

    public static Integer maxNumberInArray(Integer [] array){
        int max =0;
        for (Integer integer : array) {
            if (integer > max) max = integer;
        }
        return max;
    }

}
