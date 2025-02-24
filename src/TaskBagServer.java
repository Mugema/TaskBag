import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class TaskBagServer {
    public static void createServer() throws RemoteException {
        TaskBagImplementation taskBag = new TaskBagImplementation();
        try {
            LocateRegistry.createRegistry(999);
            Naming.bind("rmi://localhost:999"+"/TB",taskBag);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
