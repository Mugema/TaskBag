import java.io.IOException;
import java.nio.CharBuffer;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class TaskBagImplementation extends UnicastRemoteObject
        implements TaskBag
{
    TaskBagImplementation() throws RemoteException {
        super();
    }

    @Override
    public void pairOut(Map<String, String> pair) {

    }

    @Override
    public String pairIn(String key) {
        return "";
    }

    @Override
    public String readPair(String key) {
        return "";
    }

}
