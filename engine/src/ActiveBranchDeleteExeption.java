public class ActiveBranchDeleteExeption extends Exception{

    private String message = "can not delete active branch";

    // Overrides Exception's getMessage()
    @Override
    public String getMessage(){
        return message;
    }
}
