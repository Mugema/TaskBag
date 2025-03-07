import java.rmi.RemoteException;

public interface Subscriber {
    void update() throws RemoteException;
}
