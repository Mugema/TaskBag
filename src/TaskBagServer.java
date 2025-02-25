import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class TaskBagServer {
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

    public static void main(String[] arg){
        TaskBagServer.createServer();
    }
}
