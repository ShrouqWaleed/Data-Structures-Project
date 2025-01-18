package Backend;

public interface WaitingList {

    //Method for constructing Waiting List
    public boolean createWaitingList();

    // Method for Adding into the Waiting List
    public boolean addToWaitingList(String patient);

    //Method for removing from the Waiting List
    public boolean removeFromWaitingList(String patient);

}
