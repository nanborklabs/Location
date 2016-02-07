package nanborklabs.testmodule;

/**
 * Created by nandhu on 6/2/16.
 */
public class serverHandler {

    public interface BusLocation{
        void busLocationChanged();
    }

    /**
     * THis method calls for a php code residing in Server
     * connect to server, provide credentials
     * //TODO:get server details and establish secure conection based on user credentials
     * calling it in on pause as per Design guidelines
     */
        public void connect(){}


    /**
     * calling server to disconnect
     * calling  it in  on pause/STOp as per Design Guidelines
     *
     */
    public void disconnect(){}

    /**
     * this method gets a default Bus Hardware ID
     * and stores it temporarily to listen for Busevents
     */
    public void getWhichBustoListen(){

    }

    /**
     * get a Location of bus ,based on Bus Hardware ID
     *
     */
    public void getBusLocation(){

    }




}
